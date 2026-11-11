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
package com.jme3.gde.templates.basic;

import com.jme3.gde.templates.shared.AbstractProjectWizardIterator;
import com.jme3.gde.templates.shared.ProjectWizardPanel;
import com.jme3.gde.templates.shared.ZipUtils;
import java.io.File;
import java.io.IOException;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

@SuppressWarnings({"unchecked", "rawtypes"})
public class BasicGameWizardIterator extends AbstractProjectWizardIterator {

    public BasicGameWizardIterator() {
    }

    public static BasicGameWizardIterator createIterator() {
        return new BasicGameWizardIterator();
    }

    @Override
    protected WizardDescriptor.Panel[] createPanels() {
        return new WizardDescriptor.Panel[]{
            new ProjectWizardPanel("BasicGame", true),
        };
    }

    @Override
    protected String[] createSteps() {
        return new String[]{
            NbBundle.getMessage(com.jme3.gde.templates.shared.ProjectWizardPanel.class, "LBL_CreateProjectStep")
        };
    }

    @Override
    protected Class<?> getResourceAnchor() {
        return BasicGameWizardIterator.class;
    }

    @Override
    protected void createProject(File dirF, FileObject dir) throws IOException {
        FileObject template = Templates.getTemplate(getWizard());
        try (var in = template.getInputStream()) {
            ZipUtils.unZipFile(in, dir);
        }
    }
}
