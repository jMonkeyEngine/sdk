#!/bin/bash
#(c) jmonkeyengine.org
#Author MeFisto94
set -e # Quit on Error

jdk_version="8u161"
jdk_build_version="b12"
jdk_hash=2f38c3b165be4555a1fa6e98c45e0808
platforms=( "linux-x64.tar.gz" "linux-i586.tar.gz" "windows-i586.exe" "windows-x64.exe" "macosx-x64.dmg" )

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

    if [ -f downloads/jdk-$1 ];
    then
        echo "<<< Already existing, SKIPPING."
    else
        curl -s -o downloads/jdk-$1 -L -b oraclelicense=accept-securebackup-cookie http://download.oracle.com/otn-pub/java/jdk/$jdk_version-$jdk_build_version/$jdk_hash/jdk-$jdk_version-$1 #--progress-bar
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

    download_jdk macosx-x64.dmg
	
    mkdir -p MacOS
    cd MacOS

    # MacOS
    if [ "$(uname)" == "Darwin" ]; then
        hdiutil attach ../downloads/jdk-macosx-x64.dmg
        xar -xf /Volumes/JDK*/JDK*.pkg
        hdiutil detach /Volumes/JDK*
    else # Linux
        7z x ../downloads/jdk-macosx-x64.dmg > /dev/null
        # The following seems dependent of the 7zip version. Travis on Version 9.20 extracts all partitions, where as at least version 16.02 is automatically extracting 4.hfs
        if [ -f 4.hfs ]; then
            7z x 4.hfs > /dev/null
        fi
        #install_xar
        #./xar-1.5.2/src/xar -xf JDK*/JDK*.pkg
        7z x JDK*/JDK*.pkg  > /dev/null
        cd jdk1*.pkg
    fi

    #cd JDK*

    cat Payload | gunzip -dc | cpio -i
    #mkdir -p Contents/jdk/
    cd Contents/
    # FROM HERE: build-osx-zip.sh by normen (with changes)
    mv Home jdk # rename folder
    zip -9 -r -y -q ../../../compiled/jdk-macosx.zip jdk
    cd ../../../
    rm -rf MacOS/

    if [ "$TRAVIS" == "true" ]; then
        rm -rf downloads/jdk-macosx-x64.dmg
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
    ln -s ./local/$jdk_version-$jdk_build_version/compiled/jdk-macosx.zip ../../ # Note that the first part is seen relative to the second one.
    echo "< OK!"
}

# PARAMS windows-arch_oracle arch_other
function unpack_windows {
    echo ">> Extracting the JDK for $1"
    #cd local/$jdk_version-$jdk_build_version/

    if [ -d $1 ];
    then
        echo "<< Already existing, SKIPPING."
        # cd ../../
        return 0
    fi

    download_jdk $1.exe

    mkdir -p $1
    7z x -o$1 "downloads/jdk-$1.exe" > /dev/null
    
    if [ $2 == "x64" ]; then
        cabextract $1/.rsrc/1033/JAVA_CAB*/* -d $1
        rm $1/src.zip
        rm $1/jre.exe # This is the JRE installer, however the jre is already in jre/ ?? 
    fi
    
    unzip -qq $1/tools.zip -d $1/
    rm $1/tools.zip

    find $1 -type f \( -name "*.exe" -o -name "*.dll" \) -exec chmod u+rwx {} \; # Make them executable

    find $1 -type f -name "*.pack" | while read eachFile; do
        echo ">> Unpacking $eachFile ...";
        unpack200 $eachFile ${eachFile%.pack}.jar;
        rm $eachFile;
    done

    if [ "$TRAVIS" == "true" ]; then
        rm -rf downloads/jdk-$1.exe
    fi
    # cd ../../
    echo "<< OK!"
}

function unpack_linux {
    echo ">> Extracting the JDK for $1"
    #cd local/$jdk_version-$jdk_build_version/

    if [ -d $1 ];
    then
        echo "<< Already existing, SKIPPING."
        #cd ../../
        return 0
    fi

    download_jdk $1.tar.gz

    mkdir -p $1
    cd $1
    tar -xf "../downloads/jdk-$1.tar.gz"
    cd jdk1*
    mv * ../
    cd ../
    rm -rf jdk1*
    cd ../

    if [ "$TRAVIS" == "true" ]; then
        rm -rf downloads/jdk-$1.tar.gz
    fi

    echo "<< OK!"
}

# PARAMS: os arch_usual arch_oracle
function compile_other {
    echo "> Compiling JDK for $1-$2"

    if [ $1 == "windows" ]; then
        name="jdk-$1-$2.exe"
    elif [ $1 == "linux" ]; then
        name="jdk-$1-$2.bin"
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
        unpack_windows windows-$3 $2
    elif [ $1 == "linux" ]; then
        unpack_linux linux-$3
    fi

    unzipsfxname="../../unzipsfx/unzipsfx-$1-$2"
    if [ ! -f "$unzipsfxname" ]; then
        echo "No unzipsfx for platform $1-$2 found at $unzipsfxname, cannot continue"
        exit 1
    fi

    echo "> Creating SFX JDK package $name"
    if [ -f "$1-$3/jre/lib/rt.jar" ]; then # Already packed?
        echo "> PACK200 rt.jar"
        pack200 -J-Xmx1024m $1-$3/jre/lib/rt.jar.pack.gz $1-$3/jre/lib/rt.jar
        rm -rf $1-$3/jre/lib/rt.jar
    fi

    echo "> Zipping JDK"
    cd $1-$3 # zip behaves differently between 7zip and Info-Zip, so simply change wd
    zip -9 -qry ../jdk_tmp_sfx.zip *
    cd ../
    echo "> Building SFX"
    cat $unzipsfxname jdk_tmp_sfx.zip > compiled/$name
    chmod +x compiled/$name
    rm -rf jdk_tmp_sfx.zip

    if [ "$TRAVIS" == "true" ]; then
        rm -rf $1-$3
    fi

    echo "< OK!"
}

# PARAMS: os arch_usual arch_oracle
function build_other_jdk {
    echo "> Building Package for $1-$2"
    compile_other $1 $2 $3 # Depend on Compile

    if [ $1 == "windows" ]; then
        name="jdk-$1-$2.exe"
    elif [ $1 == "linux" ]; then
        name="jdk-$1-$2.bin"
    fi

    rm -rf ../../$name
    ln -s ./local/$jdk_version-$jdk_build_version/compiled/$name ../../ # Note that the first part is seen relative to the second one.
    echo "< OK!"
}

mkdir -p local/$jdk_version-$jdk_build_version/downloads
mkdir -p local/$jdk_version-$jdk_build_version/compiled

cd local/$jdk_version-$jdk_build_version

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
        build_other_jdk windows x86 i586
        build_other_jdk linux x86 i586
    else
        rm -rf compiled/jdk-windows-x86.exe compiled/jdk-linux-x86.bin
    fi
    if [ "x$BUILD_OTHER" != "x" ]; then
        build_mac_jdk
    else
        rm -rf compiled/jdk-macosx.zip
    fi
else
    build_mac_jdk
    build_other_jdk windows x86 i586
    build_other_jdk windows x64 x64
    build_other_jdk linux x86 i586
    build_other_jdk linux x64 x64
fi
cd ../../
