#!/bin/sh
# Flatpak entry point for the jMonkeyEngine SDK.
# Keeps every file the SDK writes for itself inside ~/.var/app/org.jmonkeyengine.SDK.

REAL_HOME="$HOME"
SANDBOX_HOME="$XDG_DATA_HOME/home"
mkdir -p "$SANDBOX_HOME"

# Link the exposed XDG user folders into the fake home so file choosers can reach them.
link_user_dir() {
    # $1 = XDG name (DOCUMENTS, PICTURES, DOWNLOAD), $2 = fallback folder name
    target=""
    if command -v xdg-user-dir >/dev/null 2>&1; then
        target="$(xdg-user-dir "$1")"
    fi
    if [ -z "$target" ] || [ "$target" = "$REAL_HOME" ]; then
        target="$REAL_HOME/$2"
    fi
    if [ -d "$target" ] && [ ! -e "$SANDBOX_HOME/$2" ]; then
        ln -s "$target" "$SANDBOX_HOME/$2"
    fi
}
link_user_dir DOCUMENTS Documents
link_user_dir PICTURES  Pictures
link_user_dir DOWNLOAD  Downloads

export HOME="$SANDBOX_HOME"
export GRADLE_USER_HOME="$SANDBOX_HOME/.gradle"
# Read by every JVM started in the sandbox (IDE, Gradle/Ant, user games)
# LWJGL_DISABLE_XRANDR: LWJGL 2 runs the xrandr binary, which the runtime doesn't ship
export JAVA_TOOL_OPTIONS="-Duser.home=$SANDBOX_HOME -DLWJGL_DISABLE_XRANDR=true ${JAVA_TOOL_OPTIONS:-}"

case "${XDG_CURRENT_DESKTOP:-}" in
    *niri*|*sway*|*Hyprland*|*river*|*i3*)
        export _JAVA_AWT_WM_NONREPARENTING=1 ;;
esac

exec /app/jmonkeyplatform/bin/jmonkeyplatform \
    --jdkhome /app/jdk \
    --userdir "$XDG_CONFIG_HOME/jmonkeyplatform" \
    --cachedir "$XDG_CACHE_HOME/jmonkeyplatform" \
    "$@"
