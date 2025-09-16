/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.gde.materials.multiview;

import com.jme3.asset.MaterialKey;
import com.jme3.gde.core.assets.AssetDataObject;
import com.jme3.gde.core.assets.ProjectAssetManager;
import com.jme3.gde.core.sceneexplorer.SceneExplorerTopComponent;
import com.jme3.gde.materials.EditableMaterialFile;
import com.jme3.gde.materials.MaterialProperty;
import com.jme3.gde.core.sceneexplorer.MaterialChangeListener;
import com.jme3.gde.core.sceneexplorer.MaterialChangeProvider;
import com.jme3.gde.materials.multiview.widgets.MaterialPropertyWidget;
import com.jme3.gde.materials.multiview.widgets.MaterialWidgetListener;
import com.jme3.gde.materials.multiview.widgets.WidgetFactory;
import com.jme3.material.Material;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.windows.CloneableTopComponent;

/**
 * Top component for Material Editor
 */
@ConvertAsProperties(dtd = "-//com.jme3.gde.materials.multiview//MaterialEditor//EN",
autostore = false)
@SuppressWarnings({"unchecked", "rawtypes"})
public final class MaterialEditorTopComponent extends CloneableTopComponent implements MaterialWidgetListener, MaterialChangeProvider {

    private static MaterialEditorTopComponent instance;
    private static final String PREFERRED_ID = "MaterialEditorTopComponent";
    private DataObject dataObject;
    private EditableMaterialFile materialFile;
    private String materialFileName;
    private String relativeMaterialFileName;
    private ProjectAssetManager manager;
    private final SaveCookie saveCookie = new SaveCookieImpl();
    private boolean saveImmediate = true;
    private boolean updateProperties = false;
    private final List<MaterialChangeListener> materialListeners = new ArrayList<>();

    public MaterialEditorTopComponent() {
    }

    public MaterialEditorTopComponent(DataObject dataObject) {
        this.dataObject = dataObject;
        materialFileName = dataObject.getPrimaryFile().getPath();
        initWindow();

    }

    private void initWindow() {
        initComponents();
        optionsPane.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                if(optionsPane.getWidth() > 400) {
                    ((GridLayout)optionsPanel.getLayout()).setColumns(2);
                } else {
                    ((GridLayout)optionsPanel.getLayout()).setColumns(1);
                }
            }

            @Override
            public void componentMoved(ComponentEvent e) {
            }

            @Override
            public void componentShown(ComponentEvent e) {
            }

            @Override
            public void componentHidden(ComponentEvent e) {
            }
        });
        setName(NbBundle.getMessage(MaterialEditorTopComponent.class, "CTL_MaterialEditorTopComponent"));
        setToolTipText(NbBundle.getMessage(MaterialEditorTopComponent.class, "HINT_MaterialEditorTopComponent"));
        setActivatedNodes(new Node[]{dataObject.getNodeDelegate()});
        ((AssetDataObject) dataObject).setSaveCookie(saveCookie);
        manager = dataObject.getLookup().lookup(ProjectAssetManager.class);
        materialFile = new EditableMaterialFile(dataObject.getPrimaryFile(), dataObject.getLookup().lookup(ProjectAssetManager.class));
        materialFile.read();
        setMatDefList(manager.getMatDefs(), materialFile.getMatDefName());
        try {
            jTextArea1.setText(dataObject.getPrimaryFile().asText());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        jTextArea1.getDocument().addDocumentListener(new DocumentChangeListener());

        updateProperties();
        materialPreviewWidget1.showMaterial(manager, materialFileName);

        relativeMaterialFileName = manager.getRelativeAssetPath(materialFileName);
        
        jTextField1.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateName();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateName();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateName();
            }

            private void updateName() {
                if (materialFile != null) {
                    materialFile.setName(jTextField1.getText());
                    String string = materialFile.getUpdatedContent();
                    jTextArea1.setText(string);
                }
            }

        });
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane4 = new javax.swing.JScrollPane();
        editorPanel = new javax.swing.JPanel();
        texturesAndColorsPane = new javax.swing.JTabbedPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        texturePanel = new javax.swing.JPanel();
        optionsPane = new javax.swing.JTabbedPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        optionsPanel = new javax.swing.JPanel();
        jToolBar2 = new javax.swing.JToolBar();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jToolBar3 = new javax.swing.JToolBar();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jCheckBox1 = new javax.swing.JCheckBox();
        animationCheckBox = new javax.swing.JCheckBox();
        materialPreviewWidget1 = new com.jme3.gde.materials.multiview.widgets.MaterialPreviewWidget();
        additionalRenderStatePane = new javax.swing.JTabbedPane();
        jScrollPane10 = new javax.swing.JScrollPane();
        statesPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jTabbedPane1.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPane1.setMinimumSize(new java.awt.Dimension(0, 0));
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(0, 0));

        jScrollPane4.setPreferredSize(new java.awt.Dimension(0, 0));

        texturesAndColorsPane.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        texturesAndColorsPane.setMinimumSize(new java.awt.Dimension(150, 31));
        texturesAndColorsPane.setPreferredSize(new java.awt.Dimension(480, 355));

        jScrollPane3.setBorder(null);
        jScrollPane3.setMinimumSize(new java.awt.Dimension(0, 0));

        texturePanel.setLayout(new javax.swing.BoxLayout(texturePanel, javax.swing.BoxLayout.PAGE_AXIS));
        jScrollPane3.setViewportView(texturePanel);

        texturesAndColorsPane.addTab(org.openide.util.NbBundle.getMessage(MaterialEditorTopComponent.class, "MaterialEditorTopComponent.jScrollPane3.TabConstraints.tabTitle"), jScrollPane3); // NOI18N

        optionsPane.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        optionsPane.setMinimumSize(new java.awt.Dimension(200, 355));
        optionsPane.setPreferredSize(new java.awt.Dimension(500, 355));

        jScrollPane2.setBorder(null);
        jScrollPane2.setMinimumSize(new java.awt.Dimension(220, 0));

        optionsPanel.setLayout(new java.awt.GridLayout(0, 2));
        jScrollPane2.setViewportView(optionsPanel);

        optionsPane.addTab(org.openide.util.NbBundle.getMessage(MaterialEditorTopComponent.class, "MaterialEditorTopComponent.jScrollPane2.TabConstraints.tabTitle_1"), jScrollPane2); // NOI18N

        jToolBar2.setRollover(true);

        jPanel3.setPreferredSize(new java.awt.Dimension(0, 21));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(MaterialEditorTopComponent.class, "MaterialEditorTopComponent.jLabel1.text")); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(0, 4, Short.MAX_VALUE))
        );

        jToolBar2.add(jPanel3);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Common/MatDefs/Misc/SolidColor.j3md", "Common/MatDefs/Misc/VertexColor.j3md", "Common/MatDefs/Misc/SimpleTextured.j3md", "Common/MatDefs/Misc/ColoredTextured.j3md", "Common/MatDefs/Misc/Particle.j3md", "Common/MatDefs/Misc/Sky.j3md", "Common/MatDefs/Gui/Gui.j3md", "Common/MatDefs/Light/Lighting.j3md", "Common/MatDefs/Light/Reflection.j3md", "Common/MatDefs/Misc/ShowNormals.j3md", "Common/MatDefs/Hdr/LogLum.j3md", "Common/MatDefs/Hdr/ToneMap.j3md", "Common/MatDefs/Shadow/PreShadow.j3md", "Common/MatDefs/Shadow/PostShadow.j3md" }));
        jComboBox1.setMaximumSize(new java.awt.Dimension(32767, 27));
        jComboBox1.setMinimumSize(new java.awt.Dimension(256, 27));
        jComboBox1.setPreferredSize(new java.awt.Dimension(390, 23));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });
        jToolBar2.add(jComboBox1);

        jToolBar3.setRollover(true);

        jPanel1.setPreferredSize(new java.awt.Dimension(140, 21));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(MaterialEditorTopComponent.class, "MaterialEditorTopComponent.jLabel3.text")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addContainerGap(100, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 4, Short.MAX_VALUE)
                .addComponent(jLabel3))
        );

        jToolBar3.add(jPanel1);

        jTextField1.setText(org.openide.util.NbBundle.getMessage(MaterialEditorTopComponent.class, "MaterialEditorTopComponent.jTextField1.text")); // NOI18N
        jToolBar3.add(jTextField1);

        jCheckBox1.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(jCheckBox1, org.openide.util.NbBundle.getMessage(MaterialEditorTopComponent.class, "MaterialEditorTopComponent.jCheckBox1.text")); // NOI18N
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        animationCheckBox.setSelected(false);
        org.openide.awt.Mnemonics.setLocalizedText(animationCheckBox, "Enable Animation");
        animationCheckBox.setToolTipText("Enable continuous rendering for animated materials");
        animationCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                animationCheckBoxActionPerformed(evt);
            }
        });

        additionalRenderStatePane.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        additionalRenderStatePane.setMinimumSize(new java.awt.Dimension(100, 100));
        additionalRenderStatePane.setPreferredSize(new java.awt.Dimension(380, 355));

        jScrollPane10.setBorder(null);

        statesPanel.setLayout(new javax.swing.BoxLayout(statesPanel, javax.swing.BoxLayout.PAGE_AXIS));
        jScrollPane10.setViewportView(statesPanel);

        additionalRenderStatePane.addTab(org.openide.util.NbBundle.getMessage(MaterialEditorTopComponent.class, "MaterialEditorTopComponent.jScrollPane10.TabConstraints.tabTitle"), jScrollPane10); // NOI18N

        javax.swing.GroupLayout editorPanelLayout = new javax.swing.GroupLayout(editorPanel);
        editorPanel.setLayout(editorPanelLayout);
        editorPanelLayout.setHorizontalGroup(
            editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editorPanelLayout.createSequentialGroup()
                .addGroup(editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(materialPreviewWidget1, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(optionsPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(editorPanelLayout.createSequentialGroup()
                        .addComponent(texturesAndColorsPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(additionalRenderStatePane, javax.swing.GroupLayout.DEFAULT_SIZE, 331, Short.MAX_VALUE)
                        .addContainerGap(16, Short.MAX_VALUE))
                    .addGroup(editorPanelLayout.createSequentialGroup()
                        .addGroup(editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jToolBar3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(animationCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(39, 39, 39))))
        );
        editorPanelLayout.setVerticalGroup(
            editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editorPanelLayout.createSequentialGroup()
                .addGroup(editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(editorPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jCheckBox1)
                            .addComponent(animationCheckBox))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(materialPreviewWidget1, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(optionsPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(texturesAndColorsPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(additionalRenderStatePane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jScrollPane4.setViewportView(editorPanel);

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(MaterialEditorTopComponent.class, "MaterialEditorTopComponent.jScrollPane4.TabConstraints.tabTitle"), jScrollPane4); // NOI18N

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(MaterialEditorTopComponent.class, "MaterialEditorTopComponent.jScrollPane1.TabConstraints.tabTitle"), jScrollPane1); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1351, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 619, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        saveImmediate = jCheckBox1.isSelected();
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        if (materialFile != null) {
            updateProperties = true;
            materialFile.setMatDefName((String) jComboBox1.getSelectedItem());
            String string = materialFile.getUpdatedContent();
            jTextArea1.setText(string);
        }
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void animationCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {
        boolean enabled = animationCheckBox.isSelected();
        materialPreviewWidget1.setAnimationEnabled(enabled);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane additionalRenderStatePane;
    private javax.swing.JCheckBox animationCheckBox;
    private javax.swing.JPanel editorPanel;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    private com.jme3.gde.materials.multiview.widgets.MaterialPreviewWidget materialPreviewWidget1;
    private javax.swing.JTabbedPane optionsPane;
    private javax.swing.JPanel optionsPanel;
    private javax.swing.JPanel statesPanel;
    private javax.swing.JPanel texturePanel;
    private javax.swing.JTabbedPane texturesAndColorsPane;
    // End of variables declaration//GEN-END:variables

    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link #findInstance}.
     */
    public static synchronized MaterialEditorTopComponent getDefault() {
        if (instance == null) {
            instance = new MaterialEditorTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the MaterialEditorTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized MaterialEditorTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(MaterialEditorTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof MaterialEditorTopComponent) {
            return (MaterialEditorTopComponent) win;
        }
        Logger.getLogger(MaterialEditorTopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID
                + "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;//ALWAYS;
    }

    @Override
    public HelpCtx getHelpCtx() {
        HelpCtx ctx = new HelpCtx("sdk.material_editing");
        return ctx;
    }

    @Override
    public void componentOpened() {
        SceneExplorerTopComponent.getDefault().addMaterialChangeProvider(this);
    }

    @Override
    public void componentClosed() {
        materialPreviewWidget1.cleanUp();
        for (int i = 0; i < texturePanel.getComponentCount(); i++) {
            Component c = texturePanel.getComponent(i);
            if (c instanceof MaterialPropertyWidget) {
                ((MaterialPropertyWidget) c).cleanUp();
            }
        }

        clearMaterialChangeListeners();
        SceneExplorerTopComponent.getDefault().removeMaterialChangeProvider(this);

    }

    void writeProperties(java.util.Properties p) {
        p.setProperty("version", "1.0");
        p.setProperty("MaterialFileName", materialFileName);
    }

    Object readProperties(java.util.Properties p) {
        if (instance == null) {
            instance = this;
        }
        instance.readPropertiesImpl(p);
        return instance;
    }

    private void readPropertiesImpl(java.util.Properties p) {
        try {
            String version = p.getProperty("version");
            materialFileName = p.getProperty("MaterialFileName");
            // TODO read your settings according to their version
            dataObject = DataObject.find(FileUtil.toFileObject(new File(materialFileName)));
            initWindow();
        } catch (DataObjectNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    @Override
    public String getKey() {
        return relativeMaterialFileName;
    }

    @Override
    public void addMaterialChangeListener(MaterialChangeListener listener) {
        materialListeners.add(listener);
    }

    @Override
    public void removeMaterialChangeListener(MaterialChangeListener listener) {
        materialListeners.remove(listener);
    }

    @Override
    public void clearMaterialChangeListeners() {
        materialListeners.clear();

    }

    @Override
    public void addAllMaterialChangeListener(List<MaterialChangeListener> listeners) {
        materialListeners.addAll(listeners);
    }

    private class DocumentChangeListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            checkSave();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            checkSave();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            checkSave();
        }

        public void updateLog(DocumentEvent e, String action) {
        }

        private void checkSave() {
            if (saveImmediate) {
                try {
                    saveCookie.save();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            } else {
                dataObject.setModified(true);
            }
            if (updateProperties) {
                updateProperties();
                updateProperties = false;
            }
        }
    }

    private class SaveCookieImpl implements SaveCookie {

        @Override
        public void save() throws IOException {
            String text = jTextArea1.getText();
            materialFile.setAsText(text);
            dataObject.setModified(false);
            if( ! text.trim().equals("")){
                materialPreviewWidget1.showMaterial(manager, materialFileName);
            }
        }
    }

    public void setMatDefList(final String[] matDefs, String selected) {
        EditableMaterialFile prop = materialFile;
        materialFile = null;
        jComboBox1.removeAllItems();
        jComboBox1.addItem("");
        Set<String> matDefList = new TreeSet<>(Arrays.asList(matDefs));
        for (String string : matDefList) {
            jComboBox1.addItem(string);
        }
        jComboBox1.setSelectedItem(selected);
        materialFile = prop;
    }

    private void updateProperties() {
        for (Component component : optionsPanel.getComponents()) {
            if (component instanceof MaterialPropertyWidget materialPropertyWidget) {
                materialPropertyWidget.registerChangeListener(null);
            }
        }
        for (Component component : texturePanel.getComponents()) {
            if (component instanceof MaterialPropertyWidget materialPropertyWidget) {
                materialPropertyWidget.registerChangeListener(null);
            }
        }
        optionsPanel.removeAll();
        texturePanel.removeAll();
        List<Component> optionList = new LinkedList<>();
        List<Component> colorList = new LinkedList<>();
        List<Component> valueList = new LinkedList<>();
        List<Component> textureList = new LinkedList<>();
        List<Component> otherList = new LinkedList<>();
        Map<String, MaterialProperty> sorted = materialFile.getParameterMap().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        for (Entry<String, MaterialProperty> entry : sorted.entrySet()) {
            MaterialPropertyWidget widget = WidgetFactory.getWidget(entry.getValue(), manager);
            widget.registerChangeListener(this);
            if ("Boolean".equals(entry.getValue().getType())) {
                optionList.add(widget);
            } else if (entry.getValue().getType().contains("Texture")) {
                textureList.add(widget);
            } else if ("Color".equals(entry.getValue().getType())) {
                colorList.add(widget);
            } else if ("Float".equals(entry.getValue().getType())) {
                valueList.add(widget);
            } else if ("Int".equals(entry.getValue().getType())) {
                valueList.add(widget);
            } else {
                otherList.add(widget);
            }
        }
        for (Component component : textureList) {
            texturePanel.add(component);
        }
        for (Component component : optionList) {
            optionsPanel.add(component);
        }
        for (Component component : colorList) {
            texturePanel.add(component);
        }
        for (Component component : valueList) {
            optionsPanel.add(component);
        }
        for (Component component : otherList) {
            optionsPanel.add(component);
        }
        jScrollPane2.repaint();
        jScrollPane3.repaint();
        setDisplayName(materialFile.getName() + " - " + materialFile.getMaterialPath());
        EditableMaterialFile prop = materialFile;
        materialFile = null;
        jTextField1.setText(prop.getName());
        materialFile = prop;
        updateStates();
    }

    private void updateStates() {
        for (Component component : statesPanel.getComponents()) {
            if (component instanceof MaterialPropertyWidget materialPropertyWidget) {
                materialPropertyWidget.registerChangeListener(null);
            }
        }
        statesPanel.removeAll();
        for (Entry<String, MaterialProperty> entry : materialFile.getStateMap().entrySet()) {
            MaterialPropertyWidget widget = WidgetFactory.getWidget(entry.getValue(), manager);
            widget.registerChangeListener(this);
            statesPanel.add(widget);
        }
    }

    @Override
    public void propertyChanged(MaterialProperty property) {
        String string = materialFile.getUpdatedContent();
        jTextArea1.setText(string);
        try {
            MaterialKey key = new MaterialKey(manager.getRelativeAssetPath(materialFileName));
            manager.deleteFromCache(key);
            Material material = manager.loadAsset(key);
            if (material != null) {
                for (MaterialChangeListener listener : materialListeners) {
                    listener.setMaterial(material);
                }
            }
        } catch (Exception e) {
        }

    }
}
