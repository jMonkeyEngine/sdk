#!/bin/bash
#(c) jmonkeyengine.org
#Author MeFisto94

# This script is build up like a gradle build script. It contains many functions, each for it's distinctive task and every function is calling it's dependency functions.
# This means in order for "unpack" to work, it will first launch "download" etc. While each task is self-explanatory, here's the process in short:
# 1. Download JDK, 2. Unpack JDK (this used to be more work, with SFX Installers from Oracle etc), 3. Compile (this zips the unpacked and processed jdk and
# creates a SFX Installer again from the zip), 4. Build (Build is the more general code to call compile (which calls unpack which calls download) and links the currently
# most up to date JDK version into the main directory (because several old jdk versions are stored as well).

set -e # Quit on Error

jdk_major_version="11"
jdk_version="0.6"
jdk_build_version="10"
platforms=( "x64_linux" "x86-32_windows" "x64_windows" "x64_mac" )

# DEPRECATED (not required anymore)
function install_xar {
    # This is needed to open Mac OS .pkg files on Linux...
    echo ">> Compiling xar, just for you..."
    wget -q https://storage.googleapis.com/google-code-archive-downloads/v2/code.google.com/xar/xar-1.5.2.tar.gz
    tar xf xar-1.5.2.tar.gz
    cd xar-1.5.2
    ./configure -q > /dev/null
    make -s > /dev/null
    cd ../
    echo "<< OK!"
}

# DEPRECATED (not required anymore)
function install_seven_zip {
    # This is due to not having root privilegs for apt-get
    if [ -x "$(command -v 7z)" ]; then
        return 0
    fi

    echo "> Installing 7zip"

    if [ -x "7zip/bin/7z" ]; then
        echo ">> Found cached 7zip, adjusting path"
        cd 7zip/bin
        PATH=`pwd`:$PATH
        cd ../../
        return 0
    fi

    echo ">> Compiling 7zip from source"
    mkdir -p 7zip/bin
    mkdir -p 7zip/lib
    cd 7zip
    wget -q http://downloads.sourceforge.net/project/p7zip/p7zip/15.09/p7zip_15.09_src_all.tar.bz2
    tar xf p7zip*
    rm *.bz2
    cd p7zip*
    make all3 > /dev/null
    ./install.sh ../bin ../lib /dev/null /dev/null
    #mv -v bin/ ../
    cd ../
    rm -rf p7zip*
    cd bin
    PATH=`pwd`:$PATH
    cd ../lib
    PATH=`pwd`:$PATH
    cd ../../
}

function download_jdk {
    echo ">>> Downloading the JDK for $1"

    if [ -f downloads/jdk-$1$2 ];
    then
        echo "<<< Already existing, SKIPPING."
    else
        if [ "$jdk_major_version" == "8" ];
        then
            curl -s -o downloads/jdk-$1$2 -L https://github.com/AdoptOpenJDK/openjdk8-binaries/releases/download/jdk$jdk_version-$jdk_build_version/OpenJDK8U-jdk_$1_hotspot_$jdk_version$jdk_build_version$2
        else
            curl -s -o downloads/jdk-$1$2 -L https://github.com/AdoptOpenJDK/openjdk$jdk_major_version-binaries/releases/download/jdk-$jdk_major_version.$jdk_version+$jdk_build_version/OpenJDK$jdk_major_version\U-jdk_$1_hotspot_$jdk_major_version.$jdk_version\_$jdk_build_version$2
        fi
        echo "<<< OK!"
    fi
}

function unpack_mac_jdk {
    echo ">> Extracting the Mac JDK..."
    #cd local/$jdk_version-$jdk_build_version/

    if [ -f "compiled/jdk-macosx.zip" ];
    then
        echo "< Already existing, SKIPPING."
        #cd ../../
        return 0
    fi

    download_jdk x64_mac .tar.gz
    tar xf downloads/jdk-x64_mac.tar.gz
    if [ "$jdk_major_version" == "8" ];
    then
        cd jdk$jdk_version-$jdk_build_version/Contents/
    else
        cd jdk-$jdk_major_version.$jdk_version+$jdk_build_version/Contents/
    fi
    # FROM HERE: build-osx-zip.sh by normen (with changes)
    mv Home jdk # rename folder
    rm -rf jdk/man jdk/legal # ANT got stuck at the symlinks (https://bz.apache.org/bugzilla/show_bug.cgi?id=64053)
    zip -9 -r -y -q ../../compiled/jdk-macosx.zip jdk
    cd ../../
    
    if [ "$jdk_major_version" == "8" ];
    then
        rm -r jdk$jdk_version-$jdk_build_version
    else
        rm -rf jdk-$jdk_major_version.$jdk_version+$jdk_build_version
    fi

    if [ "$TRAVIS" == "true" ]; then
        rm -rf downloads/jdk-x64_mac.tar.gz
    fi
    #cd ../../

    echo "<< OK!"
}

function build_mac_jdk {
    echo "> Building the Mac JDK"
    if ! [ -f "compiled/jdk-macosx.zip" ];
    then
        unpack_mac_jdk # Depends on "unpack" which depends on "download" (Unpack includes what compile is to other archs)
    fi

    rm -rf ../../jdk-macosx.zip
    ln -rs compiled/jdk-macosx.zip ../../
    echo "< OK!"
}

# PARAMS arch
function unpack_windows {
    echo ">> Extracting the JDK for windows-$1"
    #cd local/$jdk_version-$jdk_build_version/

    if [ -d windows-$1 ];
    then
        echo "<< Already existing, SKIPPING."
        # cd ../../
        return 0
    fi

    download_jdk $1_windows .zip

    mkdir -p windows-$1
    unzip -qq downloads/jdk-$1_windows.zip -d windows-$1
    cd windows-$1/
    
    if [ "$jdk_major_version" == "8" ];
    then
        mv jdk$jdk_version-$jdk_build_version/* .
        rm -r jdk$jdk_version-$jdk_build_version
        # TODO: Why?
        rm src.zip
    else
        mv jdk-$jdk_major_version.$jdk_version+$jdk_build_version/* .
        rm -rf jdk-$jdk_major_version.$jdk_version+$jdk_build_version
    fi    

    # This seems to be replaced by lib/tools.jar in openJDK
    #unzip -qq tools.zip -d .
    #rm tools.zip

    find . -exec chmod u+w {} \; # Make all file writable to allow uninstaller's cleaner to remove file    
    
    find . -type f \( -name "*.exe" -o -name "*.dll" \) -exec chmod u+rwx {} \; # Make them executable

    find . -type f -name "*.pack" | while read eachFile; do
        echo ">> Unpacking $eachFile ...";
        unpack200 $eachFile ${eachFile%.pack}.jar;
        rm $eachFile;
    done
    
    cd ../

    if [ "$TRAVIS" == "true" ]; then
        rm -rf downloads/jdk-$1_windows.zip
    fi
    
    echo "<< OK!"
}

function unpack_linux {
    echo ">> Extracting the JDK for linux-$1"
    #cd local/$jdk_version-$jdk_build_version/

    if [ -d linux-$1 ];
    then
        echo "<< Already existing, SKIPPING."
        #cd ../../
        return 0
    fi

    download_jdk $1_linux .tar.gz

    mkdir -p linux-$1
    cd linux-$1
    tar -xf "../downloads/jdk-$1_linux.tar.gz"
    if [ "$jdk_major_version" == "8" ];
    then
        mv jdk$jdk_version-$jdk_build_version/* .
        rm -r jdk$jdk_version-$jdk_build_version
        # TODO: Why?
        rm src.zip
    else
        mv jdk-$jdk_major_version.$jdk_version+$jdk_build_version/* .
        rm -rf jdk-$jdk_major_version.$jdk_version+$jdk_build_version
    fi
    
    cd ../

    if [ "$TRAVIS" == "true" ]; then
        rm -rf downloads/jdk-$1.tar.gz
    fi

    echo "<< OK!"
}

# PARAMS: os arch arch_unzipsfx
function compile_other {
    echo "> Compiling JDK for $1-$2"

    if [ $1 == "windows" ]; then
        name="jdk-$1-$3.exe"
    elif [ $1 == "linux" ]; then
        name="jdk-$1-$3.bin"
    else
        echo "Unknown Platform $1. ERROR!!!"
        exit 1
    fi

    if [ -f "compiled/$name" ]; then
        echo "< Already existing, SKIPPING."
        return 0
    fi

    # Depends on UNPACK and thus DOWNLOAD
    if [ $1 == "windows" ]; then
        unpack_windows $2
    elif [ $1 == "linux" ]; then
        unpack_linux $2
    fi

    unzipsfxname="../../unzipsfx/unzipsfx-$1-$3"
    if [ ! -f "$unzipsfxname" ]; then
        echo "No unzipsfx for platform $1-$3 found at $unzipsfxname, cannot continue"
        exit 1
    fi

    echo "> Creating SFX JDK package $name"
    if [ -f "$1-$2/jre/lib/rt.jar" ]; then # Already packed?
        echo "> PACK200 rt.jar"
        pack200 -J-Xmx1024m $1-$2/jre/lib/rt.jar.pack.gz $1-$2/jre/lib/rt.jar
        rm -rf $1-$2/jre/lib/rt.jar
    fi

    echo "> Zipping JDK"
    cd $1-$2 # zip behaves differently between 7zip and Info-Zip, so simply change wd
    zip -9 -qry ../jdk_tmp_sfx.zip *
    cd ../
    echo "> Building SFX"
    cat $unzipsfxname jdk_tmp_sfx.zip > compiled/$name
    chmod +x compiled/$name
    rm -rf jdk_tmp_sfx.zip

    if [ "$TRAVIS" == "true" ]; then
        rm -rf $1-$2
    fi

    echo "< OK!"
}

# PARAMS: os arch arch_unzipsfx
function build_other_jdk {
    echo "> Building Package for $1-$2"
    compile_other $1 $2 $3 # Depend on Compile

    if [ $1 == "windows" ]; then
        name="jdk-$1-$3.exe"
    elif [ $1 == "linux" ]; then
        name="jdk-$1-$3.bin"
    fi

    rm -rf ../../$name
    ln -rs compiled/$name ../../
    echo "< OK!"
}

mkdir -p local/$jdk_major_version-$jdk_version-$jdk_build_version/downloads
mkdir -p local/$jdk_major_version-$jdk_version-$jdk_build_version/compiled

cd local/$jdk_major_version-$jdk_version-$jdk_build_version

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
        build_other_jdk windows x86-32 x86 &
        build_other_jdk windows x64 x64 &
    else
        build_mac_jdk
        build_other_jdk linux x64 x64
        build_other_jdk windows x86-32 x86
        build_other_jdk windows x64 x64
        #Linux 32bit not supported... build_other_jdk linux x86-32
    fi
    
fi

if [ "x$PARALLEL" != "x" ];
then
    wait
fi
cd ../../
