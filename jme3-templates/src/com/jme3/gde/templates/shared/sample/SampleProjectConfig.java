/*
 * Copyright (c) 2026 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme3.gde.templates.shared.sample;

import java.util.Objects;

/**
 * Configuration for a sample project template that is downloaded from a GitHub
 * repository. Adding a new sample project template only requires adding an
 * entry here plus a small factory class registered in layer.xml.
 */
public final class SampleProjectConfig {

    /**
     * The URL of the source zip to download, e.g.
     * "https://github.com/jMonkeyEngine/monkeyzone/archive/refs/tags/v1.1.3.zip".
     * Should point to a tag so the generated project is reproducible.
     */
    private final String downloadUrl;
    /**
     * Name of the downloaded zip file, used for the file cached in the
     * temporary directory.
     */
    private final String zipName;
    /**
     * Default project name suggested in the wizard.
     */
    private final String defaultProjectName;
    /**
     * Resource path of the icon shown in the download panel, e.g.
     * "/com/jme3/gde/templates/monkeyzone/monkeyzone.png".
     */
    private final String iconPath;

    public SampleProjectConfig(String downloadUrl, String zipName, String defaultProjectName, String iconPath) {
        this.downloadUrl = Objects.requireNonNull(downloadUrl);
        this.zipName = Objects.requireNonNull(zipName);
        this.defaultProjectName = Objects.requireNonNull(defaultProjectName);
        this.iconPath = Objects.requireNonNull(iconPath);
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String getZipName() {
        return zipName;
    }

    public String getDefaultProjectName() {
        return defaultProjectName;
    }

    public String getIconPath() {
        return iconPath;
    }

    /**
     * Cached file name including the project name, so different sample
     * projects never share a cached download.
     */
    public String getCacheFileName() {
        return defaultProjectName + "-" + zipName;
    }
}
