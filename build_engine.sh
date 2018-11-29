#!/bin/sh
# This Shell Script will build and use the latest jMonkeyEngine git version, so there might be some undiscovered engine bugs, watch out!
# Also if you want to revert to releases and bintray builds, you need to uninstall them from your local maven repo...

if [ ! -d "engine" ]; then
    echo "Downloading the Engine, this may take some time"
    if [ "x$TRAVIS" != "x" ] && [ "x$TRAVIS_TAG" != "x" ]; then
        # Extract the engine version from the sdk branch tag.
        git clone -b $(echo "$TRAVIS_TAG" | sed -n 's/\(v.\+\)-sdk.\+/\1/p') --single-branch --depth 10 https://github.com/jMonkeyEngine/jMonkeyEngine/ engine # single-branch requires git > 1.7.10, if you see an error, just leave it out.
    else
        git clone -b master --single-branch --depth 10 https://github.com/jMonkeyEngine/jMonkeyEngine/ engine # single-branch requires git > 1.7.10, if you see an error, just leave it out.
    fi
    cd engine
else
    echo "Engine already cloned, pulling updates."
    cd engine
    git pull
fi
# git checkout tags/v3.1.0-beta2 # To use this, leave out depth and change -b to a branch.

#echo "Patching the Engine...."
#patch -s -N -p 1 < ../patches/FixHWSkinningSerialization.diff

echo "Building the Engine and installing them to your local maven repo...."
./gradlew -PbuildJavaDoc=true install # Depends on jarJavadoc, jarSourcecode, assemble, dist etc.

cd ../
