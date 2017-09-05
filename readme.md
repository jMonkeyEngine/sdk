# jMonkeyEngine Software Development Kit (SDK) [![Build Status](https://travis-ci.org/jMonkeyEngine/sdk.svg?branch=master)](https://travis-ci.org/jMonkeyEngine/sdk)

Welcome to the repository of the jMonkeyEngine Software Development Kit (SDK).
This SDK simplifies developing 3D Applications with the jMonkeyEngine. It contains everything ever needed to develop a full application.  
  
Our SDK (or jmonkeyplatform) is based on the Netbeans IDE but includes additional features like:
- A Scene Editor (SceneExplorer: Preview your scene, adjust all positions, add `Controls` and `AppStates` (WYSIWYG) and then just save the scene)
- A Material Editor
- A Filter Editor (combine multiple filters and just load them in-game)
- Bundled JDK for each platform (so you don't need to install java first)
- Bundled [Blender](https://www.blender.org) (so you can start 3d modelling and also use it's import capabilities)
and many more!  

It is important to know that the SDK is not coupled with the engine itself, so if you have issues which are unrelated to the IDE, report them [here](https://github.com/jMonkeyEngine/jmonkeyengine).
This however also means, that the SDK can have a different pace than the engine (at the time of writing, the SDK is on 3.1.0-stable whereas the engine team is already working on 3.2) but you can nonetheless work on a different engine version than the one which is bundled.

__!!! Note: You are currently watching the master branch which is the active development for JMonkeyEngine 3.2. For the current release candidate, see the v3.1 branch! !!!__

## Getting Started / Downloading the SDK
Just have a look at our [releases](https://github.com/jMonkeyEngine/sdk/releases) section.
There you can download the version you desire. The SDK will generally follow the convention that it's version number is lined up with the matching engine version
plus some suffixes for different SDK releases on the same engine version.

There are multiple files from which you can choose:  
You can take the platform agnostic `jmonkeyplatform.zip` which contains the full SDK able to be run on "any" (X86/x86_64) platform (Windows, Mac OS, Linux) however it lacks the jdk as well as blender.  
Thus the prefered download is `jmonkeyplatform-windows-x64.exe` which essentially __is__ `jmonkeyplatform.zip` and the correct version of the JDK and Blender (hence the ~250MiB size increase).  
If you don't know whether your system is 64 bits (x64) or 32 bits (x86), you can type `uname -i` on Linux or look into the system informations (right click on Computer->Properties) on Windows.

__Note:__ The Tag `stable` is refering to 3.0-stable, which is an ancient version. You should not start any new projects based of 3.0 but use 3.1 instead. Also the tag is pointing to the wrong commit since we had to re-upload those files but the repo doesn't really contain any 3.0 commits anymore.

## Building the SDK
Building the SDK is an easy process basically, but it depends on what kind of distribution you want to build.
You also have to know that the build process changes from time to time, so have a look at the `.travis.yml` file, or related, you will see how we build our releases then.  

__Note:__ Currently, the SDK has to build the engine on it's own, because not all needed dependencies are in mavenCentral/jCenter. Thus you have to call `build_engine.sh` first on a Linux System, Inside your Windows-Git-Shell or you can manually checkout the [jMonkeyEngine](https://github.com/jMonkeyEngine/jmonkeyengine) repository and then invoke `gradlew.bat -PbuildJavaDoc=true install`. This will add _all_ jMonkeyEngine Libraries into your local maven "server".

Technically the gradle task `buildSdk` is the main task which builds the sdk (Invoking `./gradlew buildSdk` or `gradlew.bat buildSdk` on Windows).

__If you just want to run the SDK on your machine:__   
    call `./gradlew run` (`gradlew.bat run`), which internally calls `buildSdk` and then uses `ant` to run the SDK.
    
__If you want to build the platform agnostic zip:__   
    call `./gradlew buildSdk` and then you have the file in `dist/jmonkeyplatform.zip`  

__If you want to build the platform installers (like we do):__   
    call `./download-jdks.sh`, `./gradlew buildSdk` and then `ant build-installers`
    You have to install ant for this to work (for linux consult the internet, for mac os look into homebrew).
    Note that this takes a long time (more than one hour), because a huge amount of data is compressed and such.
    
__If you want to build the platform installers (On Windows -> without having ant):__   
    See above. You can bypass ant by declaring a gradle task, just like it has been done for run. There, the gradle included ant will be used).
    Alternatively you can launch netbeans in `netbeans/`, open the SDK as Netbeans Project Collection and select __package as__:
    ![Package as...](http://i.imgur.com/5V2uBHf.png).
    Note that you still have to download and unpack the jdks somehow, which is a cumbersome process, which is why it is discouraged to
    build the installers on Windows (Linux and Mac OS are supported).
    
__If you want to debug the SDK inside an IDE:__   
    See above. You want to open the SDK as Netbeans Project from within Netbeans (you can use the nb in `netbeans/`) and you can then treat it as regular project with the difference that there are several subproject.

A note about the `netbeans/` folder: To save bandwidth `buildSdk` downloads netbeans once to said folder and uses it over and over again. Even when the download URL is changed (i.e. when you update the netbeans version), you have to delete the netbeans folder and remove it from any cache (`Travis!`), so it can be reloaded.

## Developing/Contributing
First of all, I suggest you to take a look at [docs/](https://github.com/jMonkeyEngine/sdk/tree/master/docs). Those docs are a loose collection of things I came across during development, but they prevent you from re-doing the same experiences. 
Other than that, `Netbeans Platform` is your google keyword for any NB related issues.
Basically the only tricky thing is how we handle custom entries in the SceneExplorer. This is called the Netbeans Nodes API and is somewhat unintuitive.
Just take a look at the `Motion Event Pull Request`, which should've been added around `March 2016`, there you can see what was needed to add MotionEvents to the SDK.

Other than that, we are more than happy to help, even if your addition is incomplete. Make sure you use the Netbeans formatting, obvious variable naming and commented and especially documented code, though.
Before you open an issue, make sure that it is not an engine bug, but a real sdk bug. If you are uncertain whether it is a real bug or misuse, you can either open an issue or ask [on the hub](https://hub.jmonkeyengine.org) using the `Troubleshooting | jmonkeyplatform` category.

## Issues when building
__Could not find `jme3.1.0-stable`__ :  
    When you occur this issue, you have to go back to the engine build step and see how the engine is called. Technically the engine version is collected out from git tags, or called SNAPSHOT. Then there are cases where this recognition fails. For example when the SDK has a tag but the engine is snapshot. Then take a look at `build.gradle` in the sdk repo. It contains a field called `ext.jmeVersion` or something, where you can specify the version.
    
__Could not find `jme3-jbullet`__ :  
    jBullet is not part of jCenter, which means you forgot to build the engine first. See above.
