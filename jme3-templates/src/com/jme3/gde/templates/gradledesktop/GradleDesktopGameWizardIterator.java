/*
 * Copyright (c) 2009-2026 jMonkeyEngine
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
package com.jme3.gde.templates.gradledesktop;

import com.jme3.gde.templates.gradledesktop.options.CachedOptionsContainer;
import com.jme3.gde.templates.gradledesktop.options.LibraryVersion;
import com.jme3.gde.templates.gradledesktop.options.SemanticPlusTagVersionInfo;
import com.jme3.gde.templates.shared.AbstractProjectWizardIterator;
import com.jme3.gde.templates.shared.ProjectWizardPanel;
import com.jme3.gde.templates.shared.TemplateVersions;
import com.jme3.gde.templates.shared.ZipUtils;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

@SuppressWarnings({"unchecked", "rawtypes"})
public class GradleDesktopGameWizardIterator extends AbstractProjectWizardIterator {

    private static final String TEMPLATE_SETTINGS = "com/jme3/gde/templates/files/freemarker/settings.gradle.ftl";
    private static final String TEMPLATE_BUILDFILE = "com/jme3/gde/templates/files/freemarker/build.gradle.ftl";

    public GradleDesktopGameWizardIterator() {

        // Initiate the options getting...
        CachedOptionsContainer.getInstance();
    }

    public static GradleDesktopGameWizardIterator createIterator() {
        return new GradleDesktopGameWizardIterator();
    }

    @Override
    protected WizardDescriptor.Panel[] createPanels() {
        return new WizardDescriptor.Panel[]{
            new ProjectWizardPanel("GradleGame"),
            new GradleDesktopGameJMEVersionPanel(),
            new GradleDesktopGameGuiPanel(),
            new GradleDesktopGameAdditionalLibrariesPanel()
        };
    }

    @Override
    protected String[] createSteps() {
        return new String[]{
            NbBundle.getMessage(com.jme3.gde.templates.shared.ProjectWizardPanel.class, "LBL_CreateProjectStep"),
            NbBundle.getMessage(GradleDesktopGameWizardIterator.class, "LBL_ChooseEngineStep"),
            NbBundle.getMessage(GradleDesktopGameWizardIterator.class, "LBL_ChooseGuiStep"),
            NbBundle.getMessage(GradleDesktopGameWizardIterator.class, "LBL_ChooseAdditionalLibrariesStep")
        };
    }

    @Override
    protected Class<?> getResourceAnchor() {
        return GradleDesktopGameWizardIterator.class;
    }

    @Override
    protected void createProject(File dirF, FileObject dir) throws IOException {
        FileObject template = Templates.getTemplate(getWizard());
        try (var in = template.getInputStream()) {
            ZipUtils.unZipFile(in, dir);
        }

        // Create settings.gradle from template
        File gradleSettingsFile = new File(dirF, "settings.gradle");
        createFileFromTemplate(gradleSettingsFile, TEMPLATE_SETTINGS,
                Collections.singletonMap("name", getWizard().getProperty("name")));

        // Create build.gradle from template
        File gradleBuildFile = new File(dirF, "build.gradle");
        Map<String, Object> buildFileBindings = new HashMap<>();
        buildFileBindings.put("name", getWizard().getProperty("name"));
        buildFileBindings.put("jmeVersion", getJmeVersion());
        buildFileBindings.put("lwjglLibrary", getWizard().getProperty("lwjglLibrary"));
        buildFileBindings.put("guiLibrary", getWizard().getProperty("guiLibrary"));
        buildFileBindings.put("physicsLibrary", getWizard().getProperty("physicsLibrary"));
        buildFileBindings.put("networkingLibrary", getWizard().getProperty("networkingLibrary"));
        buildFileBindings.put("additionalLibraries", getWizard().getProperty("additionalLibraries"));
        buildFileBindings.put("javaVersion", TemplateVersions.getJavaVersion());
        buildFileBindings.put("gradleVersion", TemplateVersions.getGradleVersion());

        createFileFromTemplate(gradleBuildFile, TEMPLATE_BUILDFILE, buildFileBindings);
    }

    /**
     * The jME version selected in the wizard, defaulting to the version the
     * SDK itself is built against (from version.properties).
     */
    private LibraryVersion getJmeVersion() {
        Object selected = getWizard().getProperty("jmeVersion");
        if (selected instanceof LibraryVersion libraryVersion) {
            return libraryVersion;
        }
        String versionString = TemplateVersions.getJmeVersion();
        return new LibraryVersion() {
            @Override
            public String getGroupId() {
                return "org.jmonkeyengine";
            }

            @Override
            public String getArtifactId() {
                return "jme3-core";
            }

            @Override
            public String getPatchNotesPath() {
                return "/com/jme3/gde/templates/files/patchnotes/default.html";
            }

            @Override
            public com.jme3.gde.templates.gradledesktop.options.VersionInfo getVersionInfo() {
                return SemanticPlusTagVersionInfo.of(versionString);
            }

            @Override
            public String toString() {
                return versionString;
            }
        };
    }
}
