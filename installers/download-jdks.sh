#!/bin/bash
#(c) jmonkeyengine.org
#Author MeFisto94

set -e # Quit on Error

jdk_major_version="21"
jvm_impl="hotspot"
jdk_vendor="eclipse"

function download_jdk {
    echo ">>> Downloading the JDK for $1_$2$3"

    # Translate our OS names to Adoptium API OS names
    jdkOsString=$2
    case "$jdkOsString" in
      macos)   jdkOsString="mac" ;;
      *)
    esac

    if [ -f "$2-$1/jdk-$1_$2$3" ];
    then
        echo "<<< Already existing, SKIPPING."
    else
        curl -f -# -o "$2-$1/jdk-$1_$2$3" -L "https://api.adoptium.net/v3/binary/latest/$jdk_major_version/ga/$jdkOsString/$1/jdk/$jvm_impl/normal/$jdk_vendor?project=jdk"
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

    if [[ $1 != "Windows" && $1 != "Linux" && $1 != "macOS" ]]; then
        echo "Unknown Platform $1. ERROR!!!"
        exit 1
    fi

    arch_raw="${2:-}"

    case "$arch_raw" in
      X86)   arch="x86" ;;
      X64)   arch="x64" ;;
      ARM)   arch="arm" ;;
      ARM64) arch="aarch64" ;;
      *)
        echo "Unknown Architecture $arch_raw. ERROR!!!"
        exit 1
    esac

    # Depends on UNPACK and thus DOWNLOAD
    if [ "$1" == "Windows" ]; then
        get_jdk_windows "$arch"
    elif [ "$1" == "Linux" ]; then
        get_jdk_linux "$arch"
    elif [ "$1" == "macOS" ]; then
        get_jdk_macos "$arch"
    fi

    echo "< OK!"
}

get_jdk "$1" "$2"
