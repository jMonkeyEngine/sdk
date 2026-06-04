#!/bin/bash
#(c) jmonkeyengine.org

# Uses NBPackage to create installers for different platforms.
# Prerequisites for running this script:
# - The SDK ZIP build must already exist
# - JDKs must already been downloaded
# - NBPackage must exist

set -e # Quit on Error

inno_setup_url="https://github.com/jrsoftware/issrc/releases/download/is-6_7_3/innosetup-6.7.3.exe"

function build_nbpackage {
    echo ">> Building the NBPackage installer for $1-$2"

    mkdir -p ../dist/installers
    ./nbpackage/bin/nbpackage --input ../dist/jmonkeyplatform.zip --config "$1-$2/$3" --output ../dist/installers/ -v -Ppackage.version="$4"

    echo "<< OK!"
}

function build_nbpackage_on_cmd {
    echo ">> Building the NBPackage installer in CMD for $1-$2"

    # Github runners have InnoSetup in PATH etc. we need to get it and write to the configuration
    isccPath=$(cygpath -w "$(command -v ISCC.exe)")
    echo ">> ISCC found at $isccPath"
    isccPathEscaped="${isccPath//\\/\\\\\\\\}"
    sed -i "s|^package\.innosetup\.tool=.*|package.innosetup.tool=${isccPathEscaped}|" "$1-$2/$3"

    mkdir -p ../dist/installers
    cmd.exe //c call .\\nbpackage\\bin\\nbpackage.cmd --input ../dist/jmonkeyplatform.zip --config "$1-$2/$3" --output ../dist/installers/ -v -Ppackage.version="$4"

    echo "<< OK!"
}

function build_linux_deb {
    echo "> Building the Linux DEB"

    build_nbpackage linux "$2" jmonkeyengine-"$2"-deb.properties "$1"

    echo "< OK!"
}

function build_windows_installer {
    echo "> Building the Windows installer"

    # Innosetup comes readily installed on GitHub, and either way maybe better to use its package manager to get it
    if [ -n "$3" ];
    then
      setup_inno_setup
      build_nbpackage windows "$2" jmonkeyengine-windows-"$2".properties "$1"
    else
      build_nbpackage_on_cmd windows "$2" jmonkeyengine-windows-"$2".properties "$1"
    fi

    echo "< OK!"
}

function setup_inno_setup {
    echo ">> Setting up Inno Setup"
    
    download_inno_setup
    ./downloads/innosetup.exe /VERYSILENT

    echo "<< OK!"
}

function download_inno_setup {
    echo ">>> Downloading Inno Setup"


    if [ -f "downloads/innosetup.exe" ];
    then
        echo "<<< Already existing, SKIPPING."
    else
        mkdir -p downloads
        
        curl -f -# -o downloads/innosetup.exe -L $inno_setup_url
        echo "<<< OK!"
    fi
}

function build_macos_pgk {
    echo "> Building the MacOS pgk"
    
    build_nbpackage macos "$2" jmonkeyengine-macos-"$2".properties "$1"

    echo "< OK!"
}

echo "Building installers with version tag $1 on $2 architecture $3"

versionString=$1
if [[ $versionString != [[:digit:]]* ]];
then
    versionString=${versionString:1}
    echo "Stripped version tag to $versionString"
fi

arch_raw="${3:-}"

case "$arch_raw" in
  X86)   arch="x86" ;;
  X64)   arch="x64" ;;
  ARM)   arch="arm" ;;
  ARM64) arch="aarch64" ;;
  *)
    echo "Unknown Architecture $arch_raw. ERROR!!!"
    exit 1
esac

case "$2" in
  Windows)   build_windows_installer "$versionString" "$arch" "$4" ;;
  Linux)     build_linux_deb "$versionString" "$arch" ;;
  macOS)     build_macos_pgk "$versionString" "$arch" ;;
  *)
    echo "Unknown Platform $2. ERROR!!!"
    exit 1
esac
