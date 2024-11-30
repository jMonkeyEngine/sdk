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
 * Enum representing a recommended Additional Library (either Core or 3rd party)
 * that can be added to a jMonkeyEngine project.
 *
 * <p>To add a new additional library:</p>
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
 *
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
public enum AdditionalLibrary implements TemplateLibrary {

    JME3_EFFECTS("jMonkeyEngine Effects (jme3-effects)",
            NbBundle.getMessage(AdditionalLibrary.class,
            "additionalLibrary.jme3-effects.description"),
            null, "jme3-effects",
            null, true),
    JME3_TERRAIN("jMonkeyEngine TerraMonkey (jme3-terrain)",
            NbBundle.getMessage(AdditionalLibrary.class,
            "additionalLibrary.jme3-terrain.description"),
            null, "jme3-terrain",
            null, true),
    JME3_TESTDATA("jMonkeyEngine Test Data (jme3-testdata)",
            NbBundle.getMessage(AdditionalLibrary.class,
            "additionalLibrary.jme3-testdata.description"),
            null, "jme3-testdata",
            null, true),
    JME3_VR("jMonkeyEngine Virtual Reality (jme3-vr)",
            NbBundle.getMessage(AdditionalLibrary.class,
            "additionalLibrary.jme3-vr.description"),
            null, "jme3-vr",
            null, true),
    HEART("Heart Library", NbBundle.getMessage(AdditionalLibrary.class,
            "additionalLibrary.heart.description"),
            "com.github.stephengold", "Heart",
            "9.0.0", false),
    PARTICLE_MONKEY("Particle Monkey",
            NbBundle.getMessage(AdditionalLibrary.class,
            "additionalLibrary.particlemonkey.description"),
            "com.epagagames", "particlemonkey",
            "1.1.0", false),
    SHADERBLOW_EX("ShaderBlowEx", NbBundle.getMessage(AdditionalLibrary.class,
            "additionalLibrary.shaderblowex.description"),
            "com.github.polincdev", "ShaderBlowEx",
            "-SNAPSHOT", false),
    SIO2("SiO2", NbBundle.getMessage(AdditionalLibrary.class,
            "additionalLibrary.sio2.description"),
            "com.simsilica", "sio2",
            "1.7.0", false),
    ZAY_ES("Zay-ES Entity Component System",
            NbBundle.getMessage(AdditionalLibrary.class,
            "additionalLibrary.zayes.description"),
            "com.simsilica", "zay-es", "1.4.0", false),
    ZAY_ES_NET("Zay-ES-Net Networking Extension",
            NbBundle.getMessage(AdditionalLibrary.class,
            "additionalLibrary.zayesnet.description"),
            "com.simsilica", "zay-es-net", "1.5.0", false),
    WES("Wes Library", NbBundle.getMessage(AdditionalLibrary.class,
            "additionalLibrary.wes.description"),
            "com.github.stephengold", "Wes",
            "0.8.1", false),;

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
    AdditionalLibrary(String label, String description, String groupId,
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
        return label;
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
