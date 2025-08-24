#!/bin/bash
#(c) jmonkeyengine.org

# Uses NBPackage to create installers for different platforms.
# Prequisites for running this script:
# - The SDK ZIP build must already exist
# - JDKs must already been downloaded
# Some quirks exist with the different platform installers:
# - Linux DEPs are only created with current architecture
# - Windows installer requires Inno Setup, this seems like an easy thing to break in this chain

set -e # Quit on Error

nbpackage_version="1.0-beta6"
nbpackage_url="https://archive.apache.org/dist/netbeans/netbeans-nbpackage/$nbpackage_version/nbpackage-$nbpackage_version-bin.zip"
inno_setup_url="https://files.jrsoftware.org/is/6/innosetup-6.5.1.exe"

function download_nbpackage {
    echo "> Downloading the nbpackage"


    if [ -f "downloads/nbpackage.zip" ];
    then
        echo "< Already existing, SKIPPING."
    else
        mkdir -p downloads
        
        curl -# -o downloads/nbpackage.zip -L $nbpackage_url
        echo "< OK!"
    fi
}

function prepare_nbpackage {
    echo "> Extracting the nbpackage"


    if [ -d "nbpackage" ];
    then
        echo "< Already existing, SKIPPING."
    else
        unzip -qq downloads/nbpackage.zip -d nbpackage
        echo "< OK!"
    fi
}

function build_linux_deb {
    echo "> Building the Linux DEB"

    ./nbpackage/nbpackage-$nbpackage_version/bin/nbpackage --input ../dist/jmonkeyplatform.zip --config linux-x64/jmonkeyengine-x64-deb.properties --output ../dist/ -Ppackage.version=$1

    echo "< OK!"
}

function build_windows_installer {
    echo "> Building the Windows installer"
    
    setup_inno_setup

    ./nbpackage/nbpackage-$nbpackage_version/bin/nbpackage --input ../dist/jmonkeyplatform.zip --config windows-x64/jmonkeyengine-windows-x64.properties --output ../dist/ -Ppackage.version=$1

    echo "< OK!"
}

function setup_inno_setup {
    echo ">> Setting up Inno Setup"
    
    download_inno_setup
    
    # Needs Wine!!!
    wine downloads/innosetup.exe /VERYSILENT

    echo "<< OK!"
}

function download_inno_setup {
    echo ">>> Downloading Inno Setup"


    if [ -f "downloads/innosetup.exe" ];
    then
        echo "<<< Already existing, SKIPPING."
    else
        mkdir -p downloads
        
        curl -# -o downloads/innosetup.exe -L $inno_setup_url
        echo "<<< OK!"
    fi
}

function build_macos_pgk {
    echo "> Building the MacOS pgk"
    
    build_macos_x64_pgk $1

    echo "< OK!"
}

function build_macos_x64_pgk {
    echo ">> Building the MacOS x64 pgk"
    
    setup_inno_setup

    ./nbpackage/nbpackage-$nbpackage_version/bin/nbpackage --input ../dist/jmonkeyplatform.zip --config macos-x64/jmonkeyengine-macos-x64.properties --output ../dist/ -Ppackage.version=$1

    echo "<< OK!"
}

echo "Building installers with version tag $1"

download_nbpackage
prepare_nbpackage
build_linux_deb $1
build_windows_installer $1
build_macos_pgk $1
