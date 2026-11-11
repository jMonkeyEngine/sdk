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

import com.jme3.gde.templates.shared.AbstractProjectWizardIterator;
import com.jme3.gde.templates.shared.ProjectWizardPanel;
import com.jme3.gde.templates.shared.ZipUtils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/**
 * Wizard iterator for sample project templates that download a project from
 * GitHub. The downloaded project keeps its own build files; only the project
 * name in settings.gradle is rewritten to the name chosen in the wizard.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class SampleProjectWizardIterator extends AbstractProjectWizardIterator {

    private final SampleProjectConfig config;
    private SampleProjectDownloadPanel downloadPanel;

    public SampleProjectWizardIterator(SampleProjectConfig config) {
        this.config = config;
    }

    @Override
    protected WizardDescriptor.Panel[] createPanels() {
        downloadPanel = new SampleProjectDownloadPanel(config);
        return new WizardDescriptor.Panel[]{
            downloadPanel,
            new ProjectWizardPanel(config.getDefaultProjectName(), true)
        };
    }

    @Override
    protected String[] createSteps() {
        return new String[]{
            NbBundle.getMessage(SampleProjectDownloadPanel.class, "LBL_DownloadProjectStep"),
            NbBundle.getMessage(ProjectWizardPanel.class, "LBL_CreateProjectStep"),
        };
    }

    @Override
    protected Class<?> getResourceAnchor() {
        return SampleProjectWizardIterator.class;
    }

    @Override
    protected void createProject(File dirF, FileObject dir) throws IOException {
        File zipFile = downloadPanel.getCachedFile();
        if (!zipFile.isFile() || zipFile.length() == 0) {
            throw new IOException("Project archive was not downloaded: " + zipFile.getAbsolutePath());
        }
        try (var in = Files.newInputStream(zipFile.toPath())) {
            ZipUtils.unZipFile(in, dir);
        }

        // Set the project name chosen in the wizard
        String name = String.valueOf(getWizard().getProperty("name"));
        File settingsFile = new File(dirF, "settings.gradle");
        if (settingsFile.isFile()) {
            String content = Files.readString(settingsFile.toPath(), StandardCharsets.UTF_8);
            content = content.replaceFirst("(?m)^rootProject\\.name\\s*=.*$",
                    "rootProject.name = '" + name + "'");
            Files.writeString(settingsFile.toPath(), content, StandardCharsets.UTF_8);
        } else {
            try (var out = Files.newBufferedWriter(settingsFile.toPath(), StandardCharsets.UTF_8)) {
                out.write("rootProject.name = '" + name + "'\n");
            }
        }
    }

}
