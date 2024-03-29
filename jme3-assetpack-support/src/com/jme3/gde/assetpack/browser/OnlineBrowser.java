/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * OnlineBrowser.java
 *
 * Created on 25.10.2010, 23:18:26
 */
package com.jme3.gde.assetpack.browser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.NotifyDescriptor.Confirmation;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author normenhansen
 */
public class OnlineBrowser extends javax.swing.JPanel implements HyperlinkListener {

    private AssetPackLibrary lib;

    /** Creates new form OnlineBrowser */
    public OnlineBrowser(AssetPackLibrary lib) {
        initComponents();
        this.lib = lib;
        jEditorPane1.addHyperlinkListener(this);
        try {
            URL url = URI.create("http://jmonkeyengine.org/assetpacks/list.php").toURL();
            jEditorPane1.setPage(url);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void hyperlinkUpdate(final HyperlinkEvent event) {
        if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            new Thread(new Runnable() {

                public void run() {
                    ProgressHandle handle = ProgressHandle.createHandle("Downloading AssetPack..");
                    handle.start();
                    String name = event.getURL().toString();
                    name = name.substring(name.lastIndexOf("/") + 1, name.length());
                    name = name.substring(0, name.lastIndexOf("."));
                    name = lib.getProjectDirectory().getPath() + "/" + name;
                    File folder = new File(name);
                    folder.mkdirs();
                    try {
                        unZipFile(event.getURL().openStream(), FileUtil.toFileObject(folder));
                    } catch (IOException ex) {
                        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error creating URL: {0}", ex.toString());
                    }
                    handle.finish();
                    Confirmation msg = new NotifyDescriptor.Confirmation(
                            "Successfully downloaded asset pack!\n" + name,
                            NotifyDescriptor.OK_CANCEL_OPTION,
                            NotifyDescriptor.INFORMATION_MESSAGE);
                    DialogDisplayer.getDefault().notifyLater(msg);
                }
            }).start();
        }
    }

    private void unZipFile(InputStream source, FileObject projectRoot) throws IOException {
        try {
            ZipInputStream str = new ZipInputStream(source);
            ZipEntry entry;
            while ((entry = str.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    FileUtil.createFolder(projectRoot, entry.getName());
                } else {
                    FileObject fo = FileUtil.createData(projectRoot, entry.getName());
                    writeFile(str, fo);
                }
            }
        } finally {
            source.close();
        }
    }

    private void writeFile(ZipInputStream str, FileObject fo) throws IOException {
        OutputStream out = fo.getOutputStream();
        try {
            FileUtil.copy(str, out);
        } finally {
            out.close();
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();

        jEditorPane1.setEditable(false);
        jEditorPane1.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        jScrollPane1.setViewportView(jEditorPane1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
