/*
 * Copyright (c) 2022 jMonkeyEngine
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

package com.jme3.gde.templates.gradledesktop.options;

/**
 * Enum representing a jMonkeyEngine version to be used when creating a new
 * Gradle based project.
 *
 * <p>To add a new version of the engine to the options for a Gradle project:
 * </p>
 * <ul>
 * <li>
 * Create a new .html file in the
 * <code>com.jme3.gde.templates.files.patchnotes</code> package containing the
 * Patch Notes copied from GitHub. The <code>class=""</code> attributes should
 * be removed using a regex like: class="[a-zA-Z0-9:;\.\s\(\)\-,]*"
 * </li>
 * <li>
 * Add a new entry to this enum. The label should match the Maven/Gradle
 * version. The patchNotesPath should point to the .html file containing the
 * Patch Notes
 * </li>
 * </ul>
 *
 * @author peedeeboy
 */
public enum JMEVersion implements LibraryVersion {

    JME_3_8_1("3.8.1-stable",
            "/com/jme3/gde/templates/files/patchnotes/381-stable.html"),
    JME_3_8_0("3.8.0-stable",
            "/com/jme3/gde/templates/files/patchnotes/380-stable.html"),
    JME_3_7_0("3.7.0-stable",
            "/com/jme3/gde/templates/files/patchnotes/370-stable.html"),
    JME_3_6_1("3.6.1-stable",
            "/com/jme3/gde/templates/files/patchnotes/361-stable.html"),
    JME_3_6_0("3.6.0-stable",
            "/com/jme3/gde/templates/files/patchnotes/360-stable.html"),
    JME_3_5_2("3.5.2-stable",
            "/com/jme3/gde/templates/files/patchnotes/352-stable.html"),
    JME_3_5_1("3.5.1-stable",
            "/com/jme3/gde/templates/files/patchnotes/351-stable.html"),
    JME_3_5_0("3.5.0-stable",
            "/com/jme3/gde/templates/files/patchnotes/350-stable.html"),
    JME_3_4_1("3.4.1-stable",
            "/com/jme3/gde/templates/files/patchnotes/341-stable.html"),
    JME_3_4_0("3.4.0-stable",
            "/com/jme3/gde/templates/files/patchnotes/340-stable.html"),
    JME_3_3_2("3.3.2-stable",
            "/com/jme3/gde/templates/files/patchnotes/332-stable.html"),
    JME_3_3_0("3.3.0-stable",
            "/com/jme3/gde/templates/files/patchnotes/330-stable.html");

    /**
     * Default artifact ID for jME that we use to check i.e. versions from
     */
    public static final String JME_ARTIFACT_ID = "jme3-core";

    /**
     * Patch notes for versions that are not hard coded like versions in this
     * class
     */
    public static final String DEFAULT_PATCH_NOTES_PATH = "/com/jme3/gde/templates/files/patchnotes/default.html";

    /**
     * Name of the jMonkeyEngine version. This should match the Maven/Gradle
     * version.
     */
    private final String label;
    /**
     * Path to a .html file containing the Patch Notes for this version of the
     * Engine.
     */
    private final String patchNotesPath;

    private final VersionInfo versionInfo;

    /**
     * Private constructor to create an instance of this enum.
     *
     * @param label Name of the jMonkeyEngine version.
     * @param patchNotesPath Path to a .html file containing the Patch Notes for
     * this version of the Engine.
     */
    JMEVersion(String label, String patchNotesPath) {
        this.label = label;
        this.patchNotesPath = patchNotesPath;
        this.versionInfo = new SemanticPlusTagVersionInfo(label);
    }

    /**
     * Get the label for this jMonkeyEngineVersion.
     *
     * @return the label for this jMonkeyEngineVersion
     */
    public String getLabel() {
        return label;
    }

    @Override
    public String getPatchNotesPath() {
        return patchNotesPath;
    }

    /**
     * Override the <code>toString()</code> method to return the label, so that
     * this enum will display nicely in a jComboBox.
     *
     * @return <code>label</code> as a String
     */
    @Override
    public String toString() {
        return label;
    }

    @Override
    public String getGroupId() {
        return MavenArtifact.JME_GROUP_ID;
    }

    @Override
    public String getArtifactId() {
        return "core";
    }

    @Override
    public VersionInfo getVersionInfo() {
        return versionInfo;
    }
}
