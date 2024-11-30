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

import org.openide.util.NbBundle;

/**
 * Enum representing a Physics library supported by jMonkeyEngine.
 *
 * <p>To add a new GUI library:</p>
 * <ul>
 * <li>
 * Add a new entry to this enum.
 * </li>
 * <li>
 * The label is what will be displayed in the jComboBox.
 * </li>
 * <li>
 * The description should be added to the <code>bundle.properties</code>, and
 * referenced in the 2nd parameter.
 * </li>
 * <li>
 * If the library is 3rd party, the artifact should contain the version number.
 * If the library is Core, then the artifact should not contain the version
 * number, as the correct jMonkeyEngine version number will be automatically
 * appended during project creation.
 * </li>
 * <li>
 * isCoreJmeLibrary should be set to <code>true</code> if this is a core
 * jMonkeyEngine library, or <code>false</code> if it is 3rd party.
 * </li>
 * </ul>
 *
 * @author peedeeboy
 */
public enum PhysicsLibrary implements TemplateLibrary {

    NONE("", NbBundle.getMessage(GUILibrary.class,
            "physicslibrary.none.description"), null, null,
            null, false),
    JBULLET("jBullet", NbBundle.getMessage(GUILibrary.class,
            "physicslibrary.jbullet.description"),
            null, "jme3-jbullet",
            null, true),
    MINIE("Minie", NbBundle.getMessage(PhysicsLibrary.class,
            "physicslibrary.minie.description"),
            "com.github.stephengold", "Minie",
            "8.2.0", false);

    /**
     * The name of the library. This will be displayed in the jComboBox in the
     * New Project wizard.
     */
    private final String label;
    /**
     * Long description of the library. This should be stored in the
     * <code>bundle.properties</code> file.
     */
    private final String description;
    /**
     * Maven artifact ID
     */
    private final String artifactId;
    /**
     * Maven group ID
     */
    private final String groupId;
    /**
     * Default artifact version to be used
     */
    private final VersionInfo defaultVersion;
    /**
     * Is this library a core jMonkeyEngine library? True if the library is a
     * part of jMonkeyengine, false if it is 3rd party.
     */
    private final boolean isCoreJmeLibrary;

    /**
     * Private constructor to create an instance of this enum.
     *
     * @param label The name of the library.
     * @param description Long description of the library.
     * @param groupId Maven group ID.
     * @param artifactId Maven artifact ID.
     * @param defaultVersion Default version is used if no version info is found
     * from Maven
     * @param isCoreJmeLibrary Is this library a core jMonkeyEngine library?
     */
    PhysicsLibrary(String label, String description, String groupId,
            String artifactId, String defaultVersion, boolean isCoreJmeLibrary) {
        this.label = label;
        this.description = description;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.defaultVersion = defaultVersion != null ? SemanticPlusTagVersionInfo.of(defaultVersion) : null;
        this.isCoreJmeLibrary = isCoreJmeLibrary;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Override the <code>toString()</code> method to return the label, so that
     * this enum will display nicely in a jComboBox.
     *
     * @return <code>label</code> as a String
     */
    @Override
    public String toString() {
        return this.label;
    }

    @Override
    public String getGroupId() {
        return isCoreJmeLibrary ? JME_GROUP_ID : groupId;
    }

    @Override
    public String getArtifactId() {
        return artifactId;
    }

    @Override
    public boolean getIsCoreJmeLibrary() {
        return isCoreJmeLibrary;
    }

    @Override
    public VersionInfo getVersionInfo() {
        return defaultVersion;
    }
}
