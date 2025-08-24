#!/bin/bash
#(c) jmonkeyengine.org
#Author MeFisto94

set -e # Quit on Error

jdk_major_version="21"
jvm_impl="hotspot"
jdk_vendor="eclipse"

function download_jdk {
    echo ">>> Downloading the JDK for $1_$2$3"

    if [ -f downloads/jdk-$1_$2$3 ];
    then
        echo "<<< Already existing, SKIPPING."
    else
        curl -# -o downloads/jdk-$1_$2$3 -L https://api.adoptium.net/v3/binary/latest/$jdk_major_version/ga/$2/$1/jdk/$jvm_impl/normal/$jdk_vendor?project=jdk
        echo "<<< OK!"
    fi
}

function build_mac_jdk {
    echo "> Getting the Mac JDK"
    if [ -f "downloads/jdk-x64_mac.tar.gz" ];
    then
        echo "< Already existing, SKIPPING."
        #cd ../../
        return 0
    fi

    download_jdk x64 mac .tar.gz

    echo "< OK!"
}

# PARAMS arch
function unpack_windows {
    echo ">> Getting the JDK for windows-$1"
    #cd local/$jdk_version-$jdk_build_version/

    if [ -f downloads/jdk-$1_windows.zip ];
    then
        echo "<< Already existing, SKIPPING."
        # cd ../../
        return 0
    fi

    download_jdk "$1" windows .zip
    
    echo "<< OK!"
}

function unpack_linux {
    echo ">> Getting the JDK for linux-$1"
    #cd local/$jdk_version-$jdk_build_version/

    if [ -f downloads/jdk-$1.tar.gz ];
    then
        echo "<< Already existing, SKIPPING."
        #cd ../../
        return 0
    fi

    download_jdk "$1" linux .tar.gz

    echo "<< OK!"
}


# PARAMS: os arch arch_unzipsfx
function compile_other {
    echo "> Getting JDK for $1-$2"

    if [[ $1 != "windows" && $1 != "linux" ]]; then
        echo "Unknown Platform $1. ERROR!!!"
        exit 1
    fi

    # Depends on UNPACK and thus DOWNLOAD
    if [ $1 == "windows" ]; then
        unpack_windows $2
    elif [ $1 == "linux" ]; then
        unpack_linux $2
    fi

    echo "< OK!"
}


# PARAMS: os arch arch_unzipsfx
function build_other_jdk {
    echo "> Getting Package for $1-$2"
    compile_other $1 $2 $3 # Depend on Compile

    echo "< OK!"
}


mkdir -p downloads

if [ "x$TRAVIS" != "x" ]; then
    if [ "x$BUILD_X64" != "x" ]; then
        build_other_jdk windows x64 x64
        build_other_jdk linux x64 x64
    else
        # We have to save space at all cost, so force-delete x64 jdks, which might come from the build cache.
        # that's bad because they won't be cached anymore, but we have to trade time for space.
        rm -rf compiled/jdk-windows-x64.exe compiled/jdk-linux-x64.bin
    fi
    if [ "x$BUILD_X86" != "x" ]; then
        build_other_jdk windows x86-32 x86
        #build_other_jdk linux x86 i586
    else
        rm -rf compiled/jdk-windows-x86.exe compiled/jdk-linux-x86.bin
    fi
    if [ "x$BUILD_OTHER" != "x" ]; then
        build_mac_jdk
    else
        rm -rf compiled/jdk-macosx.zip
    fi
else
    if [ "x$PARALLEL" != "x" ];
    then
        build_mac_jdk &
        build_other_jdk linux x64 x64 &
        # Windows 32bit not by default build_other_jdk windows x86-32 x86 &
        build_other_jdk windows x64 x64 &
    else
        build_mac_jdk
        build_other_jdk linux x64 x64
        ## Windows 32bit not by default build_other_jdk windows x86-32 x86
        build_other_jdk windows x64 x64
        # Linux 32bit not supported... build_other_jdk linux x86-32
    fi
    
fi

if [ "x$PARALLEL" != "x" ];
then
    wait
fi
cd ../../
