/*
 *  Copyright (c) 2009-2010 jMonkeyEngine
 *  All rights reserved.
 * 
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are
 *  met:
 * 
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 
 *  * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 *  TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *  PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme3.gde.core;

import com.jme3.gde.core.scene.SceneApplication;
import java.awt.Component;
import java.awt.Frame;
import java.io.File;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileSystemView;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.modules.ModuleInstall;
import org.openide.modules.Places;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.netbeans.core.startup.*;

/**
 * Manages a module's lifecycle. Remember that an installer is optional and
 * often not needed at all.
 */
public class Installer extends ModuleInstall {

    private static final Logger logger = Logger.getLogger(Installer.class.getName());

    @Override
    public boolean closing() {
        SceneApplication.getApplication().stop();
        return true;
    }

    @Override
    public void restored() {
        //start scene app
        SceneApplication.getApplication();
        Logger.getLogger("org.openide.loaders").setLevel(Level.SEVERE);
    }

    /**
     * GLFW (used by the jME lwjgl3 backend) dlopen()s libEGL.so.1 while probing
     * for GL context creation and dlclose()s it again when the context is
     * destroyed at SDK shutdown. glvnd registers a pthread_key destructor that
     * points into libEGL and never removes it. After the dlclose that destructor
     * is dangling, and when the thread that touched EGL exits, glibc's
     * __nptl_deallocate_tsd calls it and the JVM dies with SIGSEGV.
     * Holding our own reference on the library keeps it mapped until the JVM
     * itself terminates, so the destructor stays valid. No-op away from Linux.
     */
    private static void holdEglLibraryReference() {
        if (!System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("linux")) {
            return;
        }
        String[] candidates = {
            "/usr/lib/x86_64-linux-gnu/libEGL.so.1",
            "/usr/lib64/libEGL.so.1",
            "/usr/lib/aarch64-linux-gnu/libEGL.so.1",
            "/usr/lib/libEGL.so.1",
        };
        for (String lib : candidates) {
            if (new File(lib).canRead()) {
                try {
                    System.load(lib);
                    Logger.getLogger(Installer.class.getName())
                          .log(Level.INFO, "Holding {0} to prevent GLFW/glvnd unloading it at exit", lib);
                    return;
                } catch (UnsatisfiedLinkError | RuntimeException ex) {
                    Logger.getLogger(Installer.class.getName())
                          .log(Level.INFO, "Could not hold a reference on " + lib, ex);
                }
            }
        }
    }

    static {
        //set exception report levels
        System.setProperty("netbeans.exception.report.min.level", "99999");
        System.setProperty("netbeans.exception.alert.min.level", "99999");
        //keep glvnd's EGL library mapped for the lifetime of the JVM (see holdEglLibraryReference)
        holdEglLibraryReference(); 
        //set http agent
        System.setProperty("http.agent", NbBundle.getBundle("org.netbeans.core.windows.view.ui.Bundle").getString("CTL_MainWindow_Title")
                + " (" + System.getProperty("os.name") + "/" + System.getProperty("os.version") + ")");

        //select project folder
        String projectDir = NbPreferences.forModule(Installer.class).get("projects_path", null);
        if (projectDir == null) {
            JFileChooser fr = new JFileChooser();
            FileSystemView fw = fr.getFileSystemView();
            projectDir = fw.getDefaultDirectory().getAbsolutePath();
            FileChooserBuilder builder = new FileChooserBuilder(projectDir);
            builder.setApproveText("Set Project Folder");
            builder.setTitle("Please select folder for storing projects");
            builder.setDirectoriesOnly(true);
            
            Splash s = Splash.getInstance();
            
            Component comp = s.getComponent();
            while (!(comp instanceof Frame)) { // Loop through the Hierarchy until you have the parental Frame
                if (comp.getParent() != null)
                    comp = comp.getParent();
                else { // No Frame found in Hiarchy
                    comp = null;
                    break;
                }
            }
            
            if (comp != null)
                comp.setVisible(false);
            else
                s.setRunning(false); // Workaround from the Workaround.
           
            File file = builder.showOpenDialog(); //*/  chooser.getSelectedFile();
            if (file != null) {
                projectDir = file.getAbsolutePath();
                NbPreferences.forModule(Installer.class).put("projects_path", projectDir);
            }
                
            if (comp != null)
                comp.setVisible(true);
            else
                s.setRunning(true); // Unfortunately this has no effect
        }
        
        //netbeans.default_userdir_root
        logger.log(Level.INFO, "Set project dir {0}", projectDir);
        System.setProperty("netbeans.projects.dir", projectDir);

        //set extraction dir for platform natives
        String jmpDir = Places.getUserDirectory().getAbsolutePath();
        File file = new File(jmpDir);
        if (!file.exists()) {
            logger.log(Level.INFO, "Create settings dir {0}", projectDir);
            file.mkdirs();
        }
        
        com.jme3.system.NativeLibraryLoader.setCustomExtractionFolder(jmpDir);
        //avoid problems with lightweight popups
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
    }
}
