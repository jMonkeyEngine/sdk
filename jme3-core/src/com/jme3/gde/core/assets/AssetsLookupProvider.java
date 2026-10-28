/*
 * Copyright (c) 2009-2010 jMonkeyEngine
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
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme3.gde.core.assets;

import com.jme3.gde.core.projects.SDKProjectUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.modules.gradle.api.GradleBaseProject;
import org.netbeans.spi.project.LookupProvider;
import org.openide.util.lookup.Lookups;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author normenhansen
 */
public class AssetsLookupProvider implements LookupProvider {
    private static final Logger logger = Logger.getLogger(AssetsLookupProvider.class.getName());

    @Override
    public Lookup createAdditionalLookup(Lookup lookup) {
        Project prj = lookup.lookup(Project.class);
        if (SDKProjectUtils.isGradleProject(prj)) {
            GradleBaseProject gradle = GradleBaseProject.get(prj);

            // In a new project, Assets project name is returned by NB as "assets"
            // but when project is reloaded, it is returned as ":assets"
            Optional<String> assetsProjectName =
                    (gradle.getSubProjects().containsKey("assets")) ? Optional.of("assets") :
                    (gradle.getSubProjects().containsKey(":assets")) ? Optional.of(":assets") :
                    Optional.empty();

            if (assetsProjectName.isPresent()) {
                logger.log(Level.FINE, "Found assets subproject, extending with ProjectAssetManager");
                /* Note: The ProjectAssetManager needs the Subproject's Project,
                 * because otherwise reading assets won't work (as assets is
                 * not in the scope of the parent project). But it still needs
                 * to be added to the Parent Project's Lookup
                */

                /* Complicated code to find the relative path, because the
                 * assets subproject could be stored in a folder called "foo"
                 * or "bar" and we still need to locate it correctly
                */
                Path assetsPath = gradle.getSubProjects().get(assetsProjectName.get()).toPath();
                Path parentPath = new File(prj.getProjectDirectory().getPath()).toPath();
                Path relativePath = parentPath.relativize(assetsPath);
                try {
                    Project assetsProj = ProjectManager.getDefault().findProject(
                        prj.getProjectDirectory().getFileObject(relativePath.toString()));
                    if (assetsProj != null) {
                        return Lookups.fixed(new ProjectAssetManager(assetsProj,
                        "")); // the String is seen relative to the project root
                    }
                } catch (IOException io) {
                    Exceptions.printStackTrace(io);
                }
            }
        }
        return Lookups.fixed();
    }
}
