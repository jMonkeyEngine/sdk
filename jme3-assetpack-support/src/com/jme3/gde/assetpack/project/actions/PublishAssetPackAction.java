/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.gde.assetpack.project.actions;

import com.jme3.gde.assetpack.Installer;
import com.jme3.gde.assetpack.online.OnlinePacksConnector;
import com.jme3.gde.assetpack.project.AssetPackProject;
import com.jme3.gde.assetpack.project.wizards.PublishAssetPackWizardPanel1;
import com.jme3.gde.assetpack.project.wizards.PublishAssetPackWizardPanel2;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.swing.Action;
import javax.swing.JComponent;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.project.Project;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbPreferences;

@SuppressWarnings({"unchecked", "rawtypes"})
public final class PublishAssetPackAction implements Action {

    private final Project context;
    private WizardDescriptor.Panel[] panels;

    public PublishAssetPackAction(Project context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        final WizardDescriptor wizardDescriptor = new WizardDescriptor(getPanels());
        // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
        wizardDescriptor.setTitleFormat(new MessageFormat("{0}"));
        wizardDescriptor.setTitle("Publish AssetPack");
        wizardDescriptor.putProperty("project", context);
        String projectName = ((AssetPackProject) context).getProjectName().replaceAll(" ", "_") + ".zip";
        wizardDescriptor.putProperty("filename", projectName);
        Dialog dialog = DialogDisplayer.getDefault().createDialog(wizardDescriptor);
        dialog.setVisible(true);
        dialog.toFront();
        boolean cancelled = wizardDescriptor.getValue() != WizardDescriptor.FINISH_OPTION;
        if (!cancelled) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    ProgressHandle handle = ProgressHandle.createHandle("Publishing AssetPack..");
                    handle.start();
                    packZip(wizardDescriptor);
                    copyData(wizardDescriptor);
                    handle.progress("Uploading AssetPack..");
                    uploadData(wizardDescriptor);
                    cleanup(wizardDescriptor);
                    handle.finish();
                }
            }, "AssetPackPublisher").start();
        }
    }

    private void packZip(WizardDescriptor wiz) {
        try {
            String outFilename = context.getProjectDirectory().getPath() + "/" + wiz.getProperty("filename");
            try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(new File(outFilename)))) {
                out.setLevel(9);
                zipDir(((AssetPackProject) context).getProjectDirectory(), out, (String) wiz.getProperty("filename"));
            }
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error creating ZIP file!");
        }
    }

    public void zipDir(FileObject dir2zip, ZipOutputStream zos, String fileName) {
        try {
            FileObject[] dirList = dir2zip.getChildren();
            byte[] readBuffer = new byte[2156];
            int bytesIn;
            for (FileObject f : dirList) {
                if (f.isFolder()) {
                    zipDir(f, zos, fileName);
                    //loop again
                    continue;
                }
                if (!f.getNameExt().equals(fileName) && !f.getNameExt().startsWith(".")) {
                    try(InputStream fis = f.getInputStream()) {
                        String filePathName = f.getPath().replaceAll(context.getProjectDirectory().getPath(), "");
                        ZipEntry anEntry = new ZipEntry(filePathName);
                        zos.putNextEntry(anEntry);
                        while ((bytesIn = fis.read(readBuffer)) != -1) {
                            zos.write(readBuffer, 0, bytesIn);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error creating ZIP file!");
        }
    }

    private void copyData(WizardDescriptor wiz) {
        String folder = (String) wiz.getProperty("publish_folder");
        if (folder == null) {
            return;
        }
        String zipFilename = context.getProjectDirectory().getPath() + "/" + wiz.getProperty("filename");
        try {
            FileObject source = FileUtil.toFileObject(new File(zipFilename));
            FileObject destination = FileUtil.toFileObject(new File(folder));
            source.copy(destination, source.getName(), source.getExt());
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error copying ZIP file!");
        }
    }

    private void uploadData(WizardDescriptor wiz) {
        if (wiz.getProperty("publish_jmeorg") == null) {
            return;
        }
        String file = context.getProjectDirectory().getPath() + "/" + wiz.getProperty("filename");
        String user = NbPreferences.forModule(Installer.class).get("assetpack_user", null);
        String pass = NbPreferences.forModule(Installer.class).get("assetpack_pass", null);
        OnlinePacksConnector.upload(file, user, pass);
    }

    private void cleanup(WizardDescriptor wiz) {
        String file = context.getProjectDirectory().getPath() + "/" + wiz.getProperty("filename");
        new File(file).delete();
    }

    /**
     * Initialize panels representing individual wizard's steps and sets
     * various properties for them influencing wizard appearance.
     */
    private WizardDescriptor.Panel[] getPanels() {
        if (panels == null) {
            panels = new WizardDescriptor.Panel[]{
                        new PublishAssetPackWizardPanel1(),
                        new PublishAssetPackWizardPanel2()
                    };
            String[] steps = new String[panels.length];
            for (int i = 0; i < panels.length; i++) {
                Component c = panels[i].getComponent();
                // Default step name to component name of panel. Mainly useful
                // for getting the name of the target chooser to appear in the
                // list of steps.
                steps[i] = c.getName();
                if (c instanceof JComponent) { // assume Swing components
                    JComponent jc = (JComponent) c;
                    // Sets step number of a component
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                    // Sets steps names for a panel
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                    // Turn on subtitle creation on each step
                    jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, Boolean.TRUE);
                    // Show steps on the left side with the image on the background
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, Boolean.TRUE);
                    // Turn on numbering of all steps
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, Boolean.TRUE);
                }
            }
        }
        return panels;
    }

    @Override
    public Object getValue(String key) {
        if (key.equals(NAME)) {
            return "Publish AssetPack..";
        }
        return null;
    }

    @Override
    public void putValue(String key, Object value) {
    }

    @Override
    public void setEnabled(boolean b) {
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
    }
}
