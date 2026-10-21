# Flatpak build

The SDK can be packaged as a Flatpak (app ID `org.jmonkeyengine.SDK`). All the files are in `installers/linux-flatpak/`:

- `org.jmonkeyengine.SDK.yml` is the manifest
- `jmonkeyengine-sdk.sh` is the entry point, it sets up the sandbox and then starts the normal launcher
- `org.jmonkeyengine.SDK.desktop` and `org.jmonkeyengine.SDK.metainfo.xml` are the menu entry and the AppStream data
- `icon-48.png`, `icon-256.png` and `screenshots/`

The manifest doesn't build the SDK from source. It just repackages `dist/jmonkeyplatform.zip` on top of `org.freedesktop.Platform//25.08`, and copies the full JDK from the `openjdk25` extension into `/app/jdk` (a JRE is not enough, the SDK has to compile projects).

### Building it locally

One time setup:
```bash
sudo dnf install flatpak-builder        # or apt install flatpak-builder
flatpak remote-add --user --if-not-exists flathub https://dl.flathub.org/repo/flathub.flatpakrepo
flatpak install --user flathub org.freedesktop.Platform//25.08 org.freedesktop.Sdk//25.08 \
    org.freedesktop.Sdk.Extension.openjdk25//25.08
```

Then build the zip, build the flatpak and run it:
```bash
./gradlew buildSdk && ant build-zip
cp dist/jmonkeyplatform.zip installers/linux-flatpak/
cd installers/linux-flatpak
flatpak-builder --user --install --force-clean build-dir org.jmonkeyengine.SDK.yml
flatpak run org.jmonkeyengine.SDK
```

If you want a single file to share instead:
```bash
flatpak-builder --user --force-clean --repo=repo build-dir org.jmonkeyengine.SDK.yml
flatpak build-bundle repo jmonkeyengine-sdk.flatpak org.jmonkeyengine.SDK \
    --runtime-repo=https://dl.flathub.org/repo/flathub.flatpakrepo
flatpak install --user ./jmonkeyengine-sdk.flatpak
```

The zip, `build-dir/`, `.flatpak-builder/`, `repo/` and `*.flatpak` are already in `.gitignore`.

Be careful before committing: the build changes a lot of tracked files (versions in `nbproject/project.properties`, in every module's `nbproject/project.properties` and `project.xml`, and in the branding `Bundle.properties`) and leaves new files in `JME3TestsTemplate*/src/` and `jme3-project-testdata/release/`. None of that should be committed, `git restore -- . ':!.gitignore'` puts the tracked files back.

To check the metadata and the manifest:
```bash
appstreamcli validate --no-net installers/linux-flatpak/org.jmonkeyengine.SDK.metainfo.xml
flatpak run --command=flatpak-builder-lint org.flatpak.Builder manifest installers/linux-flatpak/org.jmonkeyengine.SDK.yml
```

### Where the files go

The flatpak has no access to the home folder. Everything the SDK writes for itself stays in `~/.var/app/org.jmonkeyengine.SDK/`:

```
config/jmonkeyplatform/   NetBeans userdir (settings, plugins, logs in var/log/messages.log)
cache/jmonkeyplatform/    NetBeans cachedir
data/home/                fake home: .gradle, .m2, .java, .jmonkeyplatform, .gitconfig
                          plus links to the real Documents, Pictures and Downloads
```

The wrapper does this by setting `HOME`, `GRADLE_USER_HOME` and `-Duser.home` in `JAVA_TOOL_OPTIONS`. Java doesn't read `$HOME`, so the property is needed, and `JAVA_TOOL_OPTIONS` is picked up by every JVM started inside (IDE, Gradle/Ant, the games you run). That's where the `Picked up JAVA_TOOL_OPTIONS` line in the terminal comes from, it can be ignored.

Documents is read-write, so projects should go there (e.g. `~/Documents/jMonkeyProjects`). Pictures and Downloads are read-only, to import assets. If you need another folder:
```bash
flatpak override --user --filesystem=~/gamedev org.jmonkeyengine.SDK
```
(or use Flatseal). The sandbox also has its own git config, to use yours copy it with `cp ~/.gitconfig ~/.var/app/org.jmonkeyengine.SDK/data/home/`.

The other permissions are X11, the GPU, PulseAudio and network (Gradle, Maven and the update center). Plugin updates go into the userdir inside the sandbox.

### Workarounds in the wrapper

`-DLWJGL_DISABLE_XRANDR=true`: LWJGL 2 calls the `xrandr` program to list the screens, and it isn't in the runtime. Without this flag the 3D view fails with a `NullPointerException` in `XRandR.getResolutions`.

`_JAVA_AWT_WM_NONREPARENTING=1`: with non-reparenting window managers Java only shows a gray window. The wrapper sets it when `XDG_CURRENT_DESKTOP` is niri, sway, Hyprland, river or i3. It's not set everywhere because on GNOME/KDE it can mess up window placement. For other window managers with the same problem:
```bash
flatpak override --user --env=_JAVA_AWT_WM_NONREPARENTING=1 org.jmonkeyengine.SDK
```

### Known problems

The Scene Composer can say "OpenGL context not started" on Mesa drivers, with `Could not find GLX 1.3 config from peer info` in the log. The editor uses LWJGL 2 and its offscreen buffer doesn't work with current Mesa/Xwayland. It happens without flatpak too, so it's not a packaging issue, and games using LWJGL 3 are fine. On laptops with an NVIDIA card you can get around it with:
```bash
flatpak run --env=__NV_PRIME_RENDER_OFFLOAD=1 --env=__GLX_VENDOR_LIBRARY_NAME=nvidia org.jmonkeyengine.SDK
```

Programs on the host can't be seen from inside, so Blender import, nvcompress and the Android SDK don't work out of the box (the Android SDK can be installed inside the sandbox home).

`JFileChooser` doesn't go through the XDG portal, so you can only reach the folders above and the ones added with `flatpak override`.

There's no native Wayland in AWT, the SDK runs on XWayland.

### Releases

- Every release needs a `<release version="..." date="..."/>` line in the metainfo (newest first), in the tagged commit.
- The screenshot URL points to `master`. Before sending it to Flathub change it to a release tag, keeping in mind that a tag only has the files committed before it.
- `StartupWMClass` in the `.desktop` file is `Apache NetBeans Platform` (from `xprop WM_CLASS`), check it again if the branding changes.
- Not done yet: `release.yml` doesn't build the flatpak, and it's not on Flathub.
