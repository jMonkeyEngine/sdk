#!/bin/bash
#(c) jmonkeyengine.org

# Turns the .deb installers produced by build-installers.sh into a signed APT
# repository and publishes it to the GitHub Pages site of this repository.
#
# Prerequisites:
# - .deb files must exist in <repo-root>/dist/installers (downloaded from the
#   installers-* release artifacts)
# - secrets APT_REPO_GPG_PRIVATE_KEY and APT_REPO_GPG_PASSPHRASE must be set
#   (see .github/actions/publish-apt-repo/action.yml)
# - the `pages` environment on the repo must allow deployment from this workflow

set -e # Quit on Error

repo_root="$(cd "$(dirname "$0")/.." && pwd)"
input_dir="$repo_root/dist/installers"
out_dir="$repo_root/dist/apt-repo"
suite="stable"
component="main"
key_name="jMonkeyEngine APT Repository"
key_email="contact@jmonkeyengine.org"

if [ -z "$APT_REPO_GPG_PRIVATE_KEY" ]; then
    echo "APT_REPO_GPG_PRIVATE_KEY secret is not set, cannot sign the repository. ERROR!!!"
    exit 1
fi

debs=$(find "$input_dir" -maxdepth 1 -name '*.deb' | sort)
if [ -z "$debs" ]; then
    echo "No .deb files found in $input_dir. ERROR!!!"
    exit 1
fi

echo ">> Building APT repository in $out_dir"
rm -rf "$out_dir"
mkdir -p "$out_dir/pool/$component" "$out_dir/dists/$suite/$component"

for deb in $debs; do
    cp "$deb" "$out_dir/pool/$component/"
done

archs=$(for deb in $debs; do dpkg-deb -f "$deb" Architecture; done | sort -u | tr '\n' ' ')
echo ">> Architectures: $archs"

for arch in $archs; do
    arch_dir="$out_dir/dists/$suite/$component/binary-$arch"
    mkdir -p "$arch_dir"
    (cd "$out_dir" && dpkg-scanpackages --arch "$arch" "pool/$component" /dev/null > "dists/$suite/$component/binary-$arch/Packages")
    gzip -9kf "$arch_dir/Packages"
done

apt-ftparchive \
    -o APT::FTPArchive::Release::Origin="jMonkeyEngine" \
    -o APT::FTPArchive::Release::Label="jMonkeyEngine" \
    -o APT::FTPArchive::Release::Suite="$suite" \
    -o APT::FTPArchive::Release::Codename="$suite" \
    -o APT::FTPArchive::Release::Architectures="$archs" \
    -o APT::FTPArchive::Release::Components="$component" \
    -o APT::FTPArchive::Release::Description="jMonkeyEngine SDK APT repository" \
    release "$out_dir/dists/$suite" > "$out_dir/dists/$suite/Release"

echo ">> Importing signing key"
export GNUPGHOME="$(mktemp -d)"
trap 'rm -rf "$GNUPGHOME"' EXIT
echo "$APT_REPO_GPG_PRIVATE_KEY" | gpg --batch --import
gpg --list-secret-keys

echo ">> Signing Release file"
gpg --batch --yes --pinentry-mode loopback --passphrase "$APT_REPO_GPG_PASSPHRASE" \
    --local-user "$key_email" --armor --detach-sign \
    -o "$out_dir/dists/$suite/Release.gpg" "$out_dir/dists/$suite/Release"
gpg --batch --yes --pinentry-mode loopback --passphrase "$APT_REPO_GPG_PASSPHRASE" \
    --local-user "$key_email" --clearsign \
    -o "$out_dir/dists/$suite/InRelease" "$out_dir/dists/$suite/Release"

gpg --batch --yes --armor --export "$key_email" > "$out_dir/jmonkeyengine-repo.asc"

echo ">> Publishing to GitHub Pages"
mkdir -p "$repo_root/dist/pages"
cp -r "$out_dir/." "$repo_root/dist/pages/"

echo "< OK!"
