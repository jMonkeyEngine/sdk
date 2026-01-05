#!/bin/bash
#(c) jmonkeyengine.org
#Author MeFisto94

set -e # Quit on Error

jdk_major_version="21"
jvm_impl="hotspot"
jdk_vendor="eclipse"

function download_jdk {
    echo ">>> Downloading the JDK for $1_$2$3"

    if [ -f $2-$1/jdk-$1_$2$3 ];
    then
        echo "<<< Already existing, SKIPPING."
    else
        curl -# -o $2-$1/jdk-$1_$2$3 -L https://api.adoptium.net/v3/binary/latest/$jdk_major_version/ga/$2/$1/jdk/$jvm_impl/normal/$jdk_vendor?project=jdk
        echo "<<< OK!"
    fi
}

function get_jdk_macos {
    echo "> Getting the JDK for MacOS-$1"

    download_jdk "$1" macos .tar.gz

    echo "< OK!"
}

function get_jdk_windows {
    echo ">> Getting the JDK for Windows-$1"

    download_jdk "$1" windows .zip
    
    echo "<< OK!"
}

function get_jdk_linux {
    echo ">> Getting the JDK for Linux-$1"

    download_jdk "$1" linux .tar.gz

    echo "<< OK!"
}


# PARAMS: os arch arch_unzipsfx
function get_jdk {
    echo "> Getting JDK for $1-$2"

    if [[ $1 != "windows" && $1 != "linux" && $1 != "macos" ]]; then
        echo "Unknown Platform $1. ERROR!!!"
        exit 1
    fi

    # Depends on UNPACK and thus DOWNLOAD
    if [ $1 == "windows" ]; then
        get_jdk_windows $2
    elif [ $1 == "linux" ]; then
        get_jdk_linux $2
    elif [ $1 == "macos" ]; then
        get_jdk_macos $2
    fi

    echo "< OK!"
}

get_jdk linux x64
get_jdk linux aarch64
get_jdk windows x64
#get_jdk macos x64
#get_jdk macos aarch64
