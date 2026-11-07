/*
 *  Copyright (c) 2009-2023 jMonkeyEngine
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
package com.jme3.gde.assetbrowser;

import com.jme3.gde.assetbrowser.icons.Icons;
import com.jme3.gde.assetbrowser.widgets.AssetPreviewWidget;
import com.jme3.gde.assetbrowser.widgets.MatDefPreview;
import com.jme3.gde.assetbrowser.widgets.MaterialPreview;
import com.jme3.gde.assetbrowser.widgets.ModelPreview;
import com.jme3.gde.assetbrowser.widgets.PreviewInteractionListener;
import com.jme3.gde.assetbrowser.widgets.SoundPreview;
import com.jme3.gde.assetbrowser.widgets.TexturePreview;
import com.jme3.gde.core.assets.BinaryModelDataObject;
import com.jme3.gde.core.assets.ProjectAssetManager;
import com.jme3.gde.core.util.ProjectSelection;
import com.jme3.gde.materials.JMEMaterialDataObject;
import com.jme3.gde.materials.multiview.MaterialOpenSupport;
import com.jme3.gde.scenecomposer.SceneComposerTopComponent;
import com.jme3.gde.textureeditor.ImageEditorTopComponent;
import com.jme3.gde.textureeditor.JmeTextureDataObject;
import com.jme3.gde.textureeditor.OpenTexture;
import com.jme3.scene.Spatial;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 * Top component for AssetBrowser
 *
 * @author rickard
 */
public class AssetBrowser extends javax.swing.JPanel implements PreviewInteractionListener {

    private static final String MATERIALS = "Materials";
    private static final String MAT_DEFS = "MatDefs";
    private static final String MODELS = "Models";
    private static final String TEXTURES = "Textures";
    private static final String SOUNDS = "Sounds";
    private ProjectAssetManager assetManager;
    private PreviewHelper previewUtil;
    private String projectName;

    private int lastGridColumns = 0;
    private int lastGridRows = 0;
    private String lastFilter = "";

    private int sizeX = Constants.sizeX;
    private int sizeY = Constants.sizeY;
    private int imageSize = Constants.imageSize;
    private int oldSliderValue = 2;

    private boolean componentListenerAdded = false;
    private ComponentListener resizeListener = new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                setSize(getParent().getSize());
                setPreferredSize(getParent().getSize());
                getLayout().layoutContainer(AssetBrowser.this);
                java.awt.EventQueue.invokeLater(() -> {

                    loadAssets(lastFilter);

                });
            }

            @Override
            public void componentMoved(ComponentEvent e) {
//                setSize(new Dimension(0,0));
            }

            @Override
            public void componentShown(ComponentEvent e) {
            }

            @Override
            public void componentHidden(ComponentEvent e) {
            }
        };

    /**
     * Creates new form AssetBrowser
     */
    public AssetBrowser() {

        initComponents();
        
        addComponentListener(resizeListener);
    }

    /**
     * Will recalculate grid, and remove all previews and regenerate if rows or
     * columns or filter has changed
     *
     * @param filter only show previews containing filter
     */
    private void loadAssets(String filter) {
        if (assetManager == null) {
            return;
        }

        // this is required to make the panel resize
        if (!componentListenerAdded && getParent() != null) {
            getParent().addComponentListener(resizeListener);
            componentListenerAdded = true;
            removeComponentListener(resizeListener);
        }
        Dimension size = previewsPanel.getSize();

        int rows = Math.min(size.height, getHeight() - 30) / sizeY;
        
        final var textures = Arrays.stream(assetManager.getTextures()).filter(s -> filter.isEmpty() || s.toLowerCase().contains(filter)).collect(Collectors.toList());
        final var materials = Arrays.stream(assetManager.getMaterials()).filter(s -> filter.isEmpty() || s.toLowerCase().contains(filter)).collect(Collectors.toList());
        final var models = Arrays.stream(assetManager.getModels()).filter(s -> filter.isEmpty() || s.toLowerCase().contains(filter)).collect(Collectors.toList());
        final var sounds = Arrays.stream(assetManager.getSounds()).filter(s -> filter.isEmpty() || s.toLowerCase().contains(filter)).collect(Collectors.toList());
        final var matdefs = Arrays.stream(assetManager.getMatDefs()).filter(s -> filter.isEmpty() || s.toLowerCase().contains(filter)).collect(Collectors.toList());
        int numAssets = textures.size() + materials.size() + models.size() + sounds.size() + matdefs.size();
        int columns = Math.max(numAssets / rows, 1);

        Dimension newSize = new Dimension(columns * sizeX, rows * sizeY);
        if (columns != lastGridColumns || rows != lastGridRows || !lastFilter.equals(filter)) {
            GridBagConstraints constraints = new GridBagConstraints();
            previewsPanel.setLayout(new GridBagLayout());
            constraints.fill = GridBagConstraints.BOTH;
            constraints.gridx = sizeX;
            constraints.gridy = sizeY;
            previewsPanel.removeAll();
            previewsPanel.setSize(newSize);
            previewsPanel.setPreferredSize(newSize);

            previewsPanel.setLayout(new GridBagLayout());

            int index = addAssets(textures, TEXTURES, constraints, columns, rows, 0);
            index = addAssets(materials, MATERIALS, constraints, columns, rows, index);
            index = addAssets(models, MODELS, constraints, columns, rows, index);
            index = addAssets(sounds, SOUNDS, constraints, columns, rows, index);
            index = addAssets(matdefs, MAT_DEFS, constraints, columns, rows, index);
            lastGridColumns = columns;
            lastGridRows = rows;
            lastFilter = filter;
        }
    }

    /**
     * Add assets of a specific type to the grid
     *
     * @param items the assets to preview
     * @param type type of asset
     * @param constraints
     * @param columns columns in the grid
     * @param rows rows in the grid
     * @param startIndex last used index when adding previews
     * @return
     */
    private int addAssets(List<String> items, String type, GridBagConstraints constraints, int columns, int rows, int startIndex) {
        Collections.sort(items);
        int index = startIndex;
        for (String item : items) {
            AssetPreviewWidget preview = null;

            constraints.gridx = index % columns;
            constraints.gridy = (int) (((float) index-1) / (columns));
            if (type.startsWith(TEXTURES)) {
                preview = new TexturePreview(this, previewUtil.getOrCreateTexturePreview(item, imageSize));
            } else if (type.startsWith(MATERIALS)) {
                preview = new MaterialPreview(this);
                preview.setPreviewImage(previewUtil.getOrCreateMaterialPreview(item, preview, imageSize));
            } else if (type.startsWith(MODELS)) {
                preview = new ModelPreview(this);
                preview.setPreviewImage(previewUtil.getOrCreateModelPreview(item, preview, imageSize));
            } else if (type.startsWith(SOUNDS)) {
                preview = new SoundPreview(this, previewUtil.getSoundPreview(item, imageSize));
            } else if (type.startsWith(MAT_DEFS)) {
                preview = new MatDefPreview(this, previewUtil.getDefaultIcon(item, imageSize));
            }
            if (preview == null) {
                continue;
            }
            preview.setMinimumSize(new Dimension(sizeX, sizeY));
            preview.setPreferredSize(new Dimension(sizeX, sizeY));
            if (assetManager.getAbsoluteAssetPath(item) != null) {
                preview.setEditable(true);
            }
            preview.setPreviewName(item);
            previewsPanel.add(preview, constraints);
            index++;
        }
        return index;
    }

    /**
     * Creates the base folder for previews in the project directory
     *
     * @param assetManager
     */
    private void createAssetBrowserFolder(ProjectAssetManager assetManager) {
        final FileObject fileObject = assetManager.getProject().getProjectDirectory();
        
        final String path = assetManager.isGradleProject() ? 
            fileObject.getParent().getPath() : fileObject.getPath();
     
        final File file = new File(path, ".assetBrowser/");
        if (!file.exists()) {
            file.mkdirs();
        }
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        projectLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        previewsPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        filterField = new javax.swing.JTextField();
        clearFilterButton = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        sizeSlider = new javax.swing.JSlider();

        setAlignmentX(0.0F);
        setAlignmentY(0.0F);
        setPreferredSize(new java.awt.Dimension(2000, 2000));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.PAGE_AXIS));

        projectLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        org.openide.awt.Mnemonics.setLocalizedText(projectLabel, org.openide.util.NbBundle.getMessage(AssetBrowser.class, "AssetBrowser.projectLabel.text")); // NOI18N
        projectLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        projectLabel.setAlignmentX(0.5F);
        projectLabel.setAlignmentY(0.0F);
        projectLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        projectLabel.setMaximumSize(new java.awt.Dimension(32000, 32000));
        projectLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                projectLabelMouseClicked(evt);
            }
        });
        add(projectLabel);

        jScrollPane1.setMinimumSize(new java.awt.Dimension(150, 150));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(2000, 3000));

        previewsPanel.setPreferredSize(new java.awt.Dimension(200, 300));
        previewsPanel.setLayout(new java.awt.GridBagLayout());
        jScrollPane1.setViewportView(previewsPanel);

        add(jScrollPane1);

        jPanel2.setMaximumSize(new java.awt.Dimension(2147483647, 23));
        jPanel2.setMinimumSize(new java.awt.Dimension(104, 33));
        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEADING));

        filterField.setText(org.openide.util.NbBundle.getMessage(AssetBrowser.class, "AssetBrowser.filterField.text")); // NOI18N
        filterField.setToolTipText(org.openide.util.NbBundle.getMessage(AssetBrowser.class, "AssetBrowser.filterField.toolTipText")); // NOI18N
        filterField.setMinimumSize(new java.awt.Dimension(40, 23));
        filterField.setPreferredSize(new java.awt.Dimension(250, 23));
        filterField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                filterFieldFocusLost(evt);
            }
        });
        filterField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                filterFieldMouseClicked(evt);
            }
        });
        filterField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterFieldActionPerformed(evt);
            }
        });
        filterField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                filterFieldKeyPressed(evt);
            }
        });
        jPanel2.add(filterField);

        clearFilterButton.setIcon(Icons.clearFilter);
        org.openide.awt.Mnemonics.setLocalizedText(clearFilterButton, org.openide.util.NbBundle.getMessage(AssetBrowser.class, "AssetBrowser.clearFilterButton.text")); // NOI18N
        clearFilterButton.setMaximumSize(new java.awt.Dimension(23, 23));
        clearFilterButton.setMinimumSize(new java.awt.Dimension(23, 23));
        clearFilterButton.setPreferredSize(new java.awt.Dimension(23, 23));
        clearFilterButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clearFilterButtonMouseClicked(evt);
            }
        });
        jPanel2.add(clearFilterButton);
        jPanel2.add(filler1);

        sizeSlider.setMaximum(2);
        sizeSlider.setToolTipText(org.openide.util.NbBundle.getMessage(AssetBrowser.class, "AssetBrowser.sizeSlider.toolTipText")); // NOI18N
        sizeSlider.setName("sizeSlider"); // NOI18N
        sizeSlider.setPreferredSize(new java.awt.Dimension(100, 20));
        sizeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sizeSliderStateChanged(evt);
            }
        });
        jPanel2.add(sizeSlider);

        add(jPanel2);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Select project to view
     *
     * @param evt
     */
    private void projectLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_projectLabelMouseClicked
        assetManager = ProjectSelection.getProjectAssetManager("Select project");
        projectName = assetManager.getProject().getProjectDirectory().getName();
        projectLabel.setText(projectName);
        previewUtil = new PreviewHelper(assetManager);
        createAssetBrowserFolder(assetManager);
        // Check which assets was added/deleted/renamed/changed? Nah, just load
        // everything!
        assetManager.getAssetFolder().addRecursiveListener(new FileChangeListener() {
            @Override
            public void fileFolderCreated(FileEvent fe) {
                loadAssets(lastFilter);
            }

            @Override
            public void fileDataCreated(FileEvent fe) {
                loadAssets(lastFilter);
            }

            @Override
            public void fileChanged(FileEvent fe) {
                loadAssets(lastFilter);
            }

            @Override
            public void fileDeleted(FileEvent fe) {
                loadAssets(lastFilter);
            }

            @Override
            public void fileRenamed(FileRenameEvent fre) {
                loadAssets(lastFilter);
            }

            @Override
            public void fileAttributeChanged(FileAttributeEvent fae) {
                loadAssets(lastFilter);
            }
        });
        loadAssets("");
    }//GEN-LAST:event_projectLabelMouseClicked

    private void filterFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_filterFieldFocusLost

    }//GEN-LAST:event_filterFieldFocusLost

    private void filterFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_filterFieldKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_TAB || evt.getKeyCode() == KeyEvent.VK_ENTER) {
            previewsPanel.requestFocusInWindow();
            loadAssets(filterField.getText().toLowerCase());
        }
    }//GEN-LAST:event_filterFieldKeyPressed

    private void filterFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_filterFieldMouseClicked
        //filterField.setSelectionStart(0);
        //filterField.setSelectionEnd(filterField.getSelectedText().length());
    }//GEN-LAST:event_filterFieldMouseClicked

    private void filterFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filterFieldActionPerformed

    /**
     * Change size of previews
     *
     * @param evt
     */
    private void sizeSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sizeSliderStateChanged
        final var value = sizeSlider.getValue();
        switch (value) {
            case 0:
                sizeX = (int) (Constants.sizeX * 0.5f);
                sizeY = (int) (Constants.sizeY * 0.5f);
                imageSize = (int) (Constants.imageSize * 0.5f);
                break;
            case 1:
                sizeX = (int) (Constants.sizeY * 0.75f);
                sizeY = (int) (Constants.sizeX * 0.75f);
                imageSize = (int) (Constants.imageSize * 0.75f);
                break;
            case 2:
                sizeX = Constants.sizeY;
                sizeY = Constants.sizeX;
                imageSize = Constants.imageSize;
                break;
        }
        if (value != oldSliderValue) {
            loadAssets(lastFilter);
            oldSliderValue = value;
        }
    }//GEN-LAST:event_sizeSliderStateChanged

    private void clearFilterButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clearFilterButtonMouseClicked
        filterField.setText("");
        lastFilter = "";
        loadAssets("");
    }//GEN-LAST:event_clearFilterButtonMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton clearFilterButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JTextField filterField;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel previewsPanel;
    private javax.swing.JLabel projectLabel;
    private javax.swing.JSlider sizeSlider;
    // End of variables declaration//GEN-END:variables

    /**
     * Double click an asset to open it (if supported)
     */
    @Override
    public void openAsset(AssetPreviewWidget widget) {
        FileObject pf = assetManager.getAssetFileObject(widget.getPreviewName());
        if (widget instanceof MaterialPreview) {
            try {
                JMEMaterialDataObject matObject = (JMEMaterialDataObject) DataObject.find(pf);
                new MaterialOpenSupport(matObject.getPrimaryEntry()).open();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        } else if (widget instanceof TexturePreview) {
            try {
                JmeTextureDataObject textureObject = (JmeTextureDataObject) DataObject.find(pf);
                DataObject dobj = textureObject.getPrimaryEntry().getDataObject();
                FileObject file = dobj.getPrimaryFile();
                ImageEditorTopComponent display = new ImageEditorTopComponent(file);
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }

        } else if (widget instanceof ModelPreview) {
            try {
                BinaryModelDataObject model = (BinaryModelDataObject) DataObject.find(pf);
                Runnable call = () -> {
                    assetManager.clearCache();
                    final Spatial asset = model.loadAsset();
                    if (asset != null) {
                        java.awt.EventQueue.invokeLater(() -> {
                            SceneComposerTopComponent composer = SceneComposerTopComponent.findInstance();
                            composer.openScene(asset, model, assetManager);
                        });
                    } else {
                        NotifyDescriptor.Confirmation msg = new NotifyDescriptor.Confirmation(
                                "Error opening " + model.getPrimaryFile().getNameExt(),
                                NotifyDescriptor.OK_CANCEL_OPTION,
                                NotifyDescriptor.ERROR_MESSAGE);
                        DialogDisplayer.getDefault().notify(msg);
                    }

                };
                new Thread(call).start();
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Not yet supported");
        }
    }

    @Override
    public void refreshPreview(AssetPreviewWidget widget) {
        // not yet implemented
    }

    /**
     * Delete the asset
     */
    @Override
    public void deleteAsset(AssetPreviewWidget widget) {
        int result = JOptionPane.showConfirmDialog(null, "Delete asset? " + widget.getAssetName());
        if (result == JOptionPane.OK_OPTION) {
            FileObject pf = assetManager.getAssetFileObject(widget.getPreviewName());
            try {
                pf.delete();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

}
