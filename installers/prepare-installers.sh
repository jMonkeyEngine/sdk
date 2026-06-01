#!/bin/bash
#(c) jmonkeyengine.org

# This script simply downloads NBPackage for creating the SDK installers

set -e # Quit on Error

nbpackage_version="1.0"
nbpackage_url="https://archive.apache.org/dist/netbeans/netbeans-nbpackage/$nbpackage_version/nbpackage-$nbpackage_version-bin.zip"

function download_nbpackage {
    echo "> Downloading the nbpackage"


    if [ -f "downloads/nbpackage.zip" ];
    then
        echo "< Already existing, SKIPPING."
    else
        mkdir -p downloads
        
        curl -f -# -o downloads/nbpackage.zip -L $nbpackage_url
        echo "< OK!"
    fi
}

function prepare_nbpackage {
    echo "> Extracting the nbpackage"


    if [ -d "nbpackage" ];
    then
        echo "< Already existing, SKIPPING."
    else
        unzip -qq downloads/nbpackage.zip -d downloads/nbpackage
        mkdir -p nbpackage
        mv downloads/nbpackage/nbpackage-$nbpackage_version/* nbpackage/
        echo "< OK!"
    fi
}

download_nbpackage
prepare_nbpackage