/*
 *  Copyright (c) 2009-2024 jMonkeyEngine
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
package com.jme3.gde.scenecomposer;

import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.gde.core.assets.AssetDataObject;
import com.jme3.gde.core.assets.ProjectAssetManager;
import com.jme3.gde.core.assets.SpatialAssetDataObject;
import com.jme3.gde.core.scene.PreviewRequest;
import com.jme3.gde.core.scene.SceneApplication;
import com.jme3.gde.core.scene.SceneListener;
import com.jme3.gde.core.scene.SceneRequest;
import com.jme3.gde.core.scene.controller.SceneToolController;
import com.jme3.gde.core.sceneexplorer.SceneExplorerTopComponent;
import com.jme3.gde.core.sceneexplorer.nodes.AbstractSceneExplorerNode;
import com.jme3.gde.core.sceneexplorer.nodes.JmeNode;
import com.jme3.gde.core.sceneexplorer.nodes.JmeSpatial;
import com.jme3.gde.core.sceneexplorer.nodes.NodeUtility;
import com.jme3.gde.core.sceneviewer.SceneViewerTopComponent;
import com.jme3.gde.scenecomposer.icons.Icons;
import com.jme3.gde.scenecomposer.tools.MoveTool;
import com.jme3.gde.scenecomposer.tools.RotateTool;
import com.jme3.gde.scenecomposer.tools.ScaleTool;
import com.jme3.gde.scenecomposer.tools.SelectTool;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.NotifyDescriptor.Confirmation;
import org.openide.NotifyDescriptor.Message;
import org.openide.awt.Toolbar;
import org.openide.awt.ToolbarPool;
import org.openide.awt.UndoRedo;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.Lookup.Result;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * TODO: some threading stuff
 */
@ConvertAsProperties(dtd = "-//com.jme3.gde.scenecomposer//SceneComposer//EN",
        autostore = false)
@SuppressWarnings("unchecked")
public final class SceneComposerTopComponent extends TopComponent implements
        SceneListener, LookupListener, 
        SceneToolController.SceneToolControllerListener {

    private static SceneComposerTopComponent instance;
    /**
     * path to the icon used by the component and its open action
     */
    static final String ICON_PATH = "com/jme3/gde/scenecomposer/jme-logo24.png";
    private static final String PREFERRED_ID = "SceneComposerTopComponent";
    private final Result<AbstractSceneExplorerNode> result;
    ComposerCameraController camController;
    private SceneComposerToolController toolController;
    SceneEditorController editorController;
    CameraPositionTrackerAppState cameraPositionTrackerAppState;
    private SceneRequest sentRequest;
    private SceneRequest currentRequest;
    private final HelpCtx ctx = new HelpCtx("sdk.scene_composer");
    private ProjectAssetManager.ClassPathChangeListener listener;

    public SceneComposerTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(SceneComposerTopComponent.class, "CTL_SceneComposerTopComponent"));
        setToolTipText(NbBundle.getMessage(SceneComposerTopComponent.class, "HINT_SceneComposerTopComponent"));
        setIcon(ImageUtilities.loadImage(ICON_PATH, true));
        result = Utilities.actionsGlobalContext().lookupResult(AbstractSceneExplorerNode.class);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("UnnecessaryBoxing")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        spatialModButtonGroup = new ButtonGroup();
        cameraPanel = new javax.swing.JPanel();
        jSlider1 = new javax.swing.JSlider();
        jSlider2 = new javax.swing.JSlider();
        jSpinner1 = new javax.swing.JSpinner();
        jSpinner2 = new javax.swing.JSpinner();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        cameraPositionLabel = new javax.swing.JLabel();
        cameraDirectionLabel = new javax.swing.JLabel();
        cursorPositionHeader = new javax.swing.JLabel();
        cursorPositionLabel = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        fovSlider = new javax.swing.JSlider();
        fovSpinner = new javax.swing.JSpinner();
        jToolBar1 = new javax.swing.JToolBar();
        transformationTypeComboBox = new javax.swing.JComboBox<>();
        jSeparator9 = new javax.swing.JToolBar.Separator();
        selectButton = new javax.swing.JToggleButton();
        moveButton = new javax.swing.JToggleButton();
        rotateButton = new javax.swing.JToggleButton();
        scaleButton = new javax.swing.JToggleButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        jToggleScene = new javax.swing.JToggleButton();
        jToggleGrid = new javax.swing.JToggleButton();
        jSeparator8 = new javax.swing.JToolBar.Separator();
        jToggleSelectGeom = new javax.swing.JToggleButton();
        jToggleSelectTerrain = new javax.swing.JToggleButton();
        jSeparator7 = new javax.swing.JToolBar.Separator();
        moveToCursorButton = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        jLabel1 = new javax.swing.JLabel();
        resetCursorButton = new javax.swing.JButton();
        cursorToSelectionButton = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jLabel2 = new javax.swing.JLabel();
        camToCursorSelectionButton = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        jLabel3 = new javax.swing.JLabel();
        showSelectionToggleButton = new javax.swing.JToggleButton();
        showGridToggleButton = new javax.swing.JToggleButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jToolBar2 = new javax.swing.JToolBar();
        createPhysicsMeshButton = new javax.swing.JButton();
        jCheckBox1 = new javax.swing.JCheckBox();
        jTextField1 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jToolBar3 = new javax.swing.JToolBar();
        jButton1 = new javax.swing.JButton();
        fixedCheckBox = new javax.swing.JCheckBox();
        radiusSpinner = new javax.swing.JSpinner();
        heightSpinner = new javax.swing.JSpinner();
        jSeparator6 = new javax.swing.JSeparator();
        jLabel5 = new javax.swing.JLabel();
        emitButton = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        sceneInfoPanel = new javax.swing.JPanel();
        sceneInfoLabel = new javax.swing.JLabel();
        sceneInfoLabel1 = new javax.swing.JLabel();

        cameraPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.cameraPanel.border.title"))); // NOI18N

        jSlider1.setMaximum(2000);
        jSlider1.setMinimum(100);
        jSlider1.setToolTipText(org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.jSlider1.toolTipText")); // NOI18N
        jSlider1.setValue(1000);
        jSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider1StateChanged(evt);
            }
        });

        jSlider2.setMaximum(3000);
        jSlider2.setMinimum(5);
        jSlider2.setValue(1000);
        jSlider2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider2StateChanged(evt);
            }
        });

        jSpinner1.setModel(new javax.swing.SpinnerNumberModel(0.1f, null, null, 0.01f));
        jSpinner1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinner1StateChanged(evt);
            }
        });

        jSpinner2.setModel(new javax.swing.SpinnerNumberModel(500.0f, null, null, 1.0f));
        jSpinner2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinner2StateChanged(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel7, org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.jLabel7.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel8, org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.jLabel8.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel9, org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.jLabel9.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel10, org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.jLabel10.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(cameraPositionLabel, org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.cameraPositionLabel.text")); // NOI18N
        cameraPositionLabel.setMaximumSize(new java.awt.Dimension(170, 17));
        cameraPositionLabel.setMinimumSize(new java.awt.Dimension(170, 17));
        cameraPositionLabel.setPreferredSize(new java.awt.Dimension(170, 17));

        org.openide.awt.Mnemonics.setLocalizedText(cameraDirectionLabel, org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.cameraDirectionLabel.text")); // NOI18N
        cameraDirectionLabel.setMaximumSize(new java.awt.Dimension(170, 17));
        cameraDirectionLabel.setMinimumSize(new java.awt.Dimension(170, 17));
        cameraDirectionLabel.setPreferredSize(new java.awt.Dimension(170, 17));

        org.openide.awt.Mnemonics.setLocalizedText(cursorPositionHeader, org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.cursorPositionHeader.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(cursorPositionLabel, org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.cursorPositionLabel.text")); // NOI18N
        cursorPositionLabel.setMaximumSize(new java.awt.Dimension(170, 17));
        cursorPositionLabel.setMinimumSize(new java.awt.Dimension(170, 17));
        cursorPositionLabel.setPreferredSize(new java.awt.Dimension(170, 17));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel13, org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.jLabel13.text")); // NOI18N

        fovSlider.setMaximum(360);
        fovSlider.setMinimum(1);
        fovSlider.setValue(45);
        fovSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                fovSliderStateChanged(evt);
            }
        });

        fovSpinner.setModel(new javax.swing.SpinnerNumberModel(45, null, null, 1));
        fovSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                fovSpinnerStateChanged(evt);
            }
        });

        javax.swing.GroupLayout cameraPanelLayout = new javax.swing.GroupLayout(cameraPanel);
        cameraPanel.setLayout(cameraPanelLayout);
        cameraPanelLayout.setHorizontalGroup(
            cameraPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cameraPanelLayout.createSequentialGroup()
                .addGroup(cameraPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10)
                    .addComponent(cursorPositionHeader))
                .addGap(18, 18, 18)
                .addGroup(cameraPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cursorPositionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cameraDirectionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cameraPositionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(cameraPanelLayout.createSequentialGroup()
                .addGroup(cameraPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(cameraPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(cameraPanelLayout.createSequentialGroup()
                        .addComponent(fovSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fovSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(cameraPanelLayout.createSequentialGroup()
                        .addGroup(cameraPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSlider1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jSlider2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(cameraPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jSpinner2)
                            .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))))
        );
        cameraPanelLayout.setVerticalGroup(
            cameraPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cameraPanelLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(cameraPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(fovSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(fovSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(cameraPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(cameraPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSpinner2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(cameraPanelLayout.createSequentialGroup()
                        .addGroup(cameraPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jSlider2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8))
                        .addGap(1, 1, 1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(cameraPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(cameraPositionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(cameraPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(cameraDirectionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(cameraPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cursorPositionHeader)
                    .addComponent(cursorPositionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jToolBar1.setRollover(true);
        jToolBar1.setMinimumSize(new java.awt.Dimension(684, 40));
        jToolBar1.setPreferredSize(new java.awt.Dimension(824, 40));

        transformationTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Local", "Global", "Camera" }));
        transformationTypeComboBox.setToolTipText(org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.transformationTypeComboBox.toolTipText")); // NOI18N
        transformationTypeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transformationTypeComboBoxActionPerformed(evt);
            }
        });
        jToolBar1.add(transformationTypeComboBox);
        jToolBar1.add(jSeparator9);

        spatialModButtonGroup.add(selectButton);
        selectButton.setIcon(Icons.select);
        selectButton.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(selectButton, org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.selectButton.text")); // NOI18N
        selectButton.setToolTipText(org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.selectButton.toolTipText")); // NOI18N
        selectButton.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        selectButton.setFocusable(false);
        selectButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        selectButton.setMargin(new java.awt.Insets(8, 14, 8, 14));
        selectButton.setMaximumSize(new java.awt.Dimension(32, 32));
        selectButton.setMinimumSize(new java.awt.Dimension(32, 32));
        selectButton.setPreferredSize(new java.awt.Dimension(32, 32));
        selectButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        selectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(selectButton);

        spatialModButtonGroup.add(moveButton);
        moveButton.setIcon(Icons.move);
        org.openide.awt.Mnemonics.setLocalizedText(moveButton, org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.moveButton.text")); // NOI18N
        moveButton.setToolTipText(org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.moveButton.toolTipText")); // NOI18N
        moveButton.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        moveButton.setFocusable(false);
        moveButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        moveButton.setMargin(new java.awt.Insets(8, 14, 8, 14));
        moveButton.setMaximumSize(new java.awt.Dimension(32, 32));
        moveButton.setMinimumSize(new java.awt.Dimension(32, 32));
        moveButton.setPreferredSize(new java.awt.Dimension(32, 32));
        moveButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        moveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(moveButton);

        spatialModButtonGroup.add(rotateButton);
        rotateButton.setIcon(Icons.rotate);
        org.openide.awt.Mnemonics.setLocalizedText(rotateButton, org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.rotateButton.text")); // NOI18N
        rotateButton.setToolTipText(org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.rotateButton.toolTipText")); // NOI18N
        rotateButton.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        rotateButton.setFocusable(false);
        rotateButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        rotateButton.setMaximumSize(new java.awt.Dimension(32, 32));
        rotateButton.setMinimumSize(new java.awt.Dimension(32, 32));
        rotateButton.setPreferredSize(new java.awt.Dimension(32, 32));
        rotateButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        rotateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rotateButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(rotateButton);

        spatialModButtonGroup.add(scaleButton);
        scaleButton.setIcon(Icons.scale);
        org.openide.awt.Mnemonics.setLocalizedText(scaleButton, org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.scaleButton.text")); // NOI18N
        scaleButton.setToolTipText(org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.scaleButton.toolTipText")); // NOI18N
        scaleButton.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        scaleButton.setFocusable(false);
        scaleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        scaleButton.setMaximumSize(new java.awt.Dimension(32, 32));
        scaleButton.setMinimumSize(new java.awt.Dimension(32, 32));
        scaleButton.setPreferredSize(new java.awt.Dimension(32, 32));
        scaleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        scaleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scaleButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(scaleButton);
        scaleButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.scaleButton.AccessibleContext.accessibleDescription")); // NOI18N

        jSeparator5.setPreferredSize(new java.awt.Dimension(10, 32));
        jSeparator5.setSeparatorSize(new java.awt.Dimension(10, 32));
        jToolBar1.add(jSeparator5);

        jToggleScene.setIcon(Icons.snapToScene);
        org.openide.awt.Mnemonics.setLocalizedText(jToggleScene, org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.jToggleScene.text")); // NOI18N
        jToggleScene.setToolTipText(org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.jToggleScene.toolTipText")); // NOI18N
        jToggleScene.setFocusable(false);
        jToggleScene.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleScene.setMaximumSize(new java.awt.Dimension(32, 32));
        jToggleScene.setMinimumSize(new java.awt.Dimension(32, 32));
        jToggleScene.setPreferredSize(new java.awt.Dimension(32, 32));
        jToggleScene.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleScene.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleSceneActionPerformed(evt);
            }
        });
        jToolBar1.add(jToggleScene);

        jToggleGrid.setIcon(Icons.snapToGrid);
        org.openide.awt.Mnemonics.setLocalizedText(jToggleGrid, org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.jToggleGrid.text")); // NOI18N
        jToggleGrid.setToolTipText(org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.jToggleGrid.toolTipText")); // NOI18N
        jToggleGrid.setFocusable(false);
        jToggleGrid.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleGrid.setMaximumSize(new java.awt.Dimension(32, 32));
        jToggleGrid.setMinimumSize(new java.awt.Dimension(32, 32));
        jToggleGrid.setPreferredSize(new java.awt.Dimension(32, 32));
        jToggleGrid.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleGrid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleGridActionPerformed(evt);
            }
        });
        jToolBar1.add(jToggleGrid);
        jToolBar1.add(jSeparator8);

        jToggleSelectGeom.setIcon(Icons.selectGeometry);
        org.openide.awt.Mnemonics.setLocalizedText(jToggleSelectGeom, org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.jToggleSelectGeom.text")); // NOI18N
        jToggleSelectGeom.setToolTipText(org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.jToggleSelectGeom.toolTipText")); // NOI18N
        jToggleSelectGeom.setFocusable(false);
        jToggleSelectGeom.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleSelectGeom.setMaximumSize(new java.awt.Dimension(32, 32));
        jToggleSelectGeom.setMinimumSize(new java.awt.Dimension(32, 32));
        jToggleSelectGeom.setPreferredSize(new java.awt.Dimension(32, 32));
        jToggleSelectGeom.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleSelectGeom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleSelectGeomActionPerformed(evt);
            }
        });
        jToolBar1.add(jToggleSelectGeom);

        jToggleSelectTerrain.setIcon(Icons.selectTerrain);
        org.openide.awt.Mnemonics.setLocalizedText(jToggleSelectTerrain, org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.jToggleSelectTerrain.text")); // NOI18N
        jToggleSelectTerrain.setToolTipText(org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.jToggleSelectTerrain.toolTipText")); // NOI18N
        jToggleSelectTerrain.setFocusable(false);
        jToggleSelectTerrain.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleSelectTerrain.setMaximumSize(new java.awt.Dimension(32, 32));
        jToggleSelectTerrain.setMinimumSize(new java.awt.Dimension(32, 32));
        jToggleSelectTerrain.setPreferredSize(new java.awt.Dimension(32, 32));
        jToggleSelectTerrain.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleSelectTerrain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleSelectTerrainActionPerformed(evt);
            }
        });
        jToolBar1.add(jToggleSelectTerrain);

        jSeparator7.setPreferredSize(new java.awt.Dimension(10, 40));
        jSeparator7.setRequestFocusEnabled(false);
        jSeparator7.setSeparatorSize(new java.awt.Dimension(10, 40));
        jToolBar1.add(jSeparator7);

        moveToCursorButton.setIcon(Icons.moveToCursor);
        org.openide.awt.Mnemonics.setLocalizedText(moveToCursorButton, org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.moveToCursorButton.text")); // NOI18N
        moveToCursorButton.setToolTipText(org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.moveToCursorButton.toolTipText")); // NOI18N
        moveToCursorButton.setFocusable(false);
        moveToCursorButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        moveToCursorButton.setMaximumSize(new java.awt.Dimension(32, 32));
        moveToCursorButton.setMinimumSize(new java.awt.Dimension(32, 32));
        moveToCursorButton.setPreferredSize(new java.awt.Dimension(32, 32));
        moveToCursorButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        moveToCursorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveToCursorButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(moveToCursorButton);

        jSeparator3.setSeparatorSize(new java.awt.Dimension(24, 24));
        jToolBar1.add(jSeparator3);

        jLabel1.setIcon(Icons.cursorPin);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.jLabel1.text")); // NOI18N
        jToolBar1.add(jLabel1);

        org.openide.awt.Mnemonics.setLocalizedText(resetCursorButton, org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.resetCursorButton.text")); // NOI18N
        resetCursorButton.setToolTipText(org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.resetCursorButton.toolTipText")); // NOI18N
        resetCursorButton.setFocusable(false);
        resetCursorButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        resetCursorButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        resetCursorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetCursorButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(resetCursorButton);

        org.openide.awt.Mnemonics.setLocalizedText(cursorToSelectionButton, org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.cursorToSelectionButton.text")); // NOI18N
        cursorToSelectionButton.setToolTipText(org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.cursorToSelectionButton.toolTipText")); // NOI18N
        cursorToSelectionButton.setFocusable(false);
        cursorToSelectionButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cursorToSelectionButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cursorToSelectionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cursorToSelectionButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(cursorToSelectionButton);
        jToolBar1.add(jSeparator2);

        jLabel2.setIcon(Icons.camera);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.jLabel2.text")); // NOI18N
        jLabel2.setMaximumSize(new java.awt.Dimension(24, 24));
        jLabel2.setMinimumSize(new java.awt.Dimension(24, 24));
        jLabel2.setPreferredSize(new java.awt.Dimension(24, 24));
        jToolBar1.add(jLabel2);

        org.openide.awt.Mnemonics.setLocalizedText(camToCursorSelectionButton, org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.camToCursorSelectionButton.text")); // NOI18N
        camToCursorSelectionButton.setToolTipText(org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.camToCursorSelectionButton.toolTipText")); // NOI18N
        camToCursorSelectionButton.setFocusable(false);
        camToCursorSelectionButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        camToCursorSelectionButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        camToCursorSelectionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                camToCursorSelectionButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(camToCursorSelectionButton);

        jSeparator4.setSeparatorSize(new java.awt.Dimension(24, 24));
        jToolBar1.add(jSeparator4);

        jLabel3.setIcon(Icons.display);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.jLabel3.text")); // NOI18N
        jToolBar1.add(jLabel3);

        showSelectionToggleButton.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(showSelectionToggleButton, org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.Selection.text")); // NOI18N
        showSelectionToggleButton.setToolTipText(org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.Selection.toolTipText")); // NOI18N
        showSelectionToggleButton.setFocusable(false);
        showSelectionToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        showSelectionToggleButton.setMaximumSize(new java.awt.Dimension(72, 26));
        showSelectionToggleButton.setMinimumSize(new java.awt.Dimension(72, 26));
        showSelectionToggleButton.setName("Selection"); // NOI18N
        showSelectionToggleButton.setPreferredSize(new java.awt.Dimension(72, 26));
        showSelectionToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        showSelectionToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showSelectionToggleButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(showSelectionToggleButton);

        showGridToggleButton.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(showGridToggleButton, org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.Grid.text")); // NOI18N
        showGridToggleButton.setToolTipText(org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.Grid.toolTipText")); // NOI18N
        showGridToggleButton.setFocusable(false);
        showGridToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        showGridToggleButton.setMaximumSize(new java.awt.Dimension(58, 26));
        showGridToggleButton.setMinimumSize(new java.awt.Dimension(58, 26));
        showGridToggleButton.setName("Grid"); // NOI18N
        showGridToggleButton.setPreferredSize(new java.awt.Dimension(58, 26));
        showGridToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        showGridToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showGridToggleButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(showGridToggleButton);
        jToolBar1.add(jSeparator1);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 45, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 36, Short.MAX_VALUE)
        );

        jToolBar1.add(jPanel3);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.jPanel4.border.title"))); // NOI18N

        jToolBar2.setRollover(true);

        org.openide.awt.Mnemonics.setLocalizedText(createPhysicsMeshButton, org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.createPhysicsMeshButton.text")); // NOI18N
        createPhysicsMeshButton.setToolTipText(org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.createPhysicsMeshButton.toolTipText")); // NOI18N
        createPhysicsMeshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createPhysicsMeshButtonActionPerformed(evt);
            }
        });
        jToolBar2.add(createPhysicsMeshButton);

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBox1, org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.jCheckBox1.text")); // NOI18N
        jToolBar2.add(jCheckBox1);

        jTextField1.setText(org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.jTextField1.text")); // NOI18N
        jToolBar2.add(jTextField1);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.jLabel4.text")); // NOI18N
        jToolBar2.add(jLabel4);

        jToolBar3.setRollover(true);

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jToolBar3.add(jButton1);

        fixedCheckBox.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(fixedCheckBox, org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.fixedCheckBox.text")); // NOI18N
        fixedCheckBox.setEnabled(false);
        jToolBar3.add(fixedCheckBox);

        radiusSpinner.setModel(new javax.swing.SpinnerNumberModel(0.5f, null, null, 0.1f));
        jToolBar3.add(radiusSpinner);

        heightSpinner.setModel(new javax.swing.SpinnerNumberModel(1.8f, null, null, 0.1f));
        jToolBar3.add(heightSpinner);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.jLabel5.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(emitButton, org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.emitButton.text")); // NOI18N
        emitButton.setToolTipText(org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.emitButton.toolTipText")); // NOI18N
        emitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                emitButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.jLabel6.text")); // NOI18N

        jButton2.setIcon(Icons.play);
        org.openide.awt.Mnemonics.setLocalizedText(jButton2, org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.jButton2.text")); // NOI18N
        jButton2.setToolTipText(org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.jButton2.toolTipText")); // NOI18N
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton2.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton2.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton2.setName(""); // NOI18N
        jButton2.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setIcon(Icons.pause);
        org.openide.awt.Mnemonics.setLocalizedText(jButton3, org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.jButton3.text")); // NOI18N
        jButton3.setToolTipText(org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.jButton3.toolTipText")); // NOI18N
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton3.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton3.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, 389, Short.MAX_VALUE)
            .addComponent(jToolBar3, javax.swing.GroupLayout.DEFAULT_SIZE, 389, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator6, javax.swing.GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emitButton)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(emitButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jLabel6))
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        sceneInfoPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.sceneInfoPanel.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(sceneInfoLabel, org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.sceneInfoLabel.text_1")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(sceneInfoLabel1, org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.sceneInfoLabel1.text")); // NOI18N

        javax.swing.GroupLayout sceneInfoPanelLayout = new javax.swing.GroupLayout(sceneInfoPanel);
        sceneInfoPanel.setLayout(sceneInfoPanelLayout);
        sceneInfoPanelLayout.setHorizontalGroup(
            sceneInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sceneInfoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 391, Short.MAX_VALUE)
            .addComponent(sceneInfoLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        sceneInfoPanelLayout.setVerticalGroup(
            sceneInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sceneInfoPanelLayout.createSequentialGroup()
                .addComponent(sceneInfoLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sceneInfoLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sceneInfoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cameraPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 1231, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cameraPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(sceneInfoPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        cameraPanel.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.cameraPanel.AccessibleContext.accessibleName")); // NOI18N
        sceneInfoPanel.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SceneComposerTopComponent.class, "SceneComposerTopComponent.sceneInfoPanel.AccessibleContext.accessibleName")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void showSelectionToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showSelectionToggleButtonActionPerformed
        if (toolController != null) {
            toolController.setShowSelection(showSelectionToggleButton.isSelected());
        }
    }//GEN-LAST:event_showSelectionToggleButtonActionPerformed

    private void showGridToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showGridToggleButtonActionPerformed
        if (toolController != null) {
            toolController.setShowGrid(showGridToggleButton.isSelected());
        }
    }//GEN-LAST:event_showGridToggleButtonActionPerformed

    private void moveToCursorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveToCursorButtonActionPerformed
        if (editorController != null) {
            editorController.moveSelectedSpatial(toolController.getCursorLocation());
            toolController.selectedSpatialTransformed();
        }
    }//GEN-LAST:event_moveToCursorButtonActionPerformed

    private void resetCursorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetCursorButtonActionPerformed
        if (toolController != null) {
            toolController.setCursorLocation(Vector3f.ZERO);
        }
    }//GEN-LAST:event_resetCursorButtonActionPerformed

    private void camToCursorSelectionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_camToCursorSelectionButtonActionPerformed
        camController.setCamFocus(toolController.getCursorLocation(), true);
    }//GEN-LAST:event_camToCursorSelectionButtonActionPerformed

    private void cursorToSelectionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cursorToSelectionButtonActionPerformed
        if (toolController != null) {
            toolController.snapCursorToSelection();
        }
    }//GEN-LAST:event_cursorToSelectionButtonActionPerformed

    private void createPhysicsMeshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createPhysicsMeshButtonActionPerformed
        if (editorController != null) {
            if (jCheckBox1.isSelected()) {
                try {
                    editorController.createDynamicPhysicsMeshForSelectedSpatial(Float.parseFloat(jTextField1.getText()));
                } catch (Exception e) {
                }
            } else {
                editorController.createPhysicsMeshForSelectedSpatial();
            }
        }
    }//GEN-LAST:event_createPhysicsMeshButtonActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        if (editorController != null) {
            boolean auto = !fixedCheckBox.isSelected();
            float radius = (Float) radiusSpinner.getValue();
            float height = (Float) heightSpinner.getValue();
            editorController.createCharacterControlForSelectedSpatial(false, radius, height);
        }

    }//GEN-LAST:event_jButton1ActionPerformed

    private void selectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectButtonActionPerformed
        SelectTool tool = new SelectTool();
        toolController.showEditTool(tool);
    }//GEN-LAST:event_selectButtonActionPerformed

    private void moveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveButtonActionPerformed
        MoveTool tool = new MoveTool();
        toolController.showEditTool(tool);
    }//GEN-LAST:event_moveButtonActionPerformed

private void emitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_emitButtonActionPerformed
    SceneApplication.getApplication().enqueue(new Callable<Object>() {

        @Override
        public Object call() throws Exception {
            emit(editorController.getSelectedSpat());
            return null;
        }
    });

}//GEN-LAST:event_emitButtonActionPerformed

private void scaleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scaleButtonActionPerformed
    ScaleTool tool = new ScaleTool();
    toolController.showEditTool(tool);
}//GEN-LAST:event_scaleButtonActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        SceneApplication.getApplication().setPhysicsEnabled(true);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        SceneApplication.getApplication().setPhysicsEnabled(false);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void rotateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rotateButtonActionPerformed
        RotateTool tool = new RotateTool();
        toolController.showEditTool(tool);
    }//GEN-LAST:event_rotateButtonActionPerformed

private void jToggleSceneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleSceneActionPerformed
    toolController.setSnapToScene(jToggleScene.isSelected());
}//GEN-LAST:event_jToggleSceneActionPerformed

private void jToggleGridActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleGridActionPerformed
    toolController.setSnapToGrid(jToggleGrid.isSelected());
}//GEN-LAST:event_jToggleGridActionPerformed

private void jToggleSelectTerrainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleSelectTerrainActionPerformed
    toolController.setSelectTerrain(jToggleSelectTerrain.isSelected());
}//GEN-LAST:event_jToggleSelectTerrainActionPerformed

private void jToggleSelectGeomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleSelectGeomActionPerformed
    toolController.setSelectGeometries(jToggleSelectGeom.isSelected());
}//GEN-LAST:event_jToggleSelectGeomActionPerformed

    private void transformationTypeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transformationTypeComboBoxActionPerformed
        toolController.setTransformationType(transformationTypeComboBox.getItemAt(transformationTypeComboBox.getSelectedIndex()));
    }//GEN-LAST:event_transformationTypeComboBoxActionPerformed

    private void fovSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_fovSpinnerStateChanged
        // This is called, when the spinner of the near plane has been changed.
        int fov = (int) fovSpinner.getValue();

        // Prevent an endless loop of state changes and don't change the slider when the spinner
        // has gone out of range, since this would lead to the slider's StateChanged overwriting the spinner again.
        // but we want the spinner to be a free-form field

        if (fov <= 360 && fov >= 1 && fovSlider.getValue() != fov) {
            fovSlider.setValue((int) fovSpinner.getValue());
        }

        final Camera cam = SceneApplication.getApplication().getCamera();
        cam.setFrustumPerspective(fov, (float)cam.getWidth() / cam.getHeight(), cam.getFrustumNear(), cam.getFrustumFar());
    }//GEN-LAST:event_fovSpinnerStateChanged

    private void fovSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_fovSliderStateChanged
        int fov = (int) fovSlider.getValue();

        // Prevent an endless loop of state changes
        if ((int) fovSpinner.getValue() != fov) {
            fovSpinner.setValue((int) fov);
        }
    }//GEN-LAST:event_fovSliderStateChanged

    private void jSpinner2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinner2StateChanged
        // Prevent an endless loop of state changes and don't change the slider when the spinner
        // has gone out of range, since this would lead to the slider's StateChanged overwriting the spinner again.
        // but we want the spinner to be a free-form field
        float fov = (int) fovSpinner.getValue();
        float spin = (Float)jSpinner2.getValue();
        if (spin <= 3000f && spin >= 5f && !FastMath.approximateEquals(spin, (float)jSlider2.getValue())) {
            jSlider2.setValue((int)spin);
        }

        final Camera cam = SceneApplication.getApplication().getCamera();
        cam.setFrustumPerspective(fov, (float)cam.getWidth() / cam.getHeight(), cam.getFrustumNear(), spin);
    }//GEN-LAST:event_jSpinner2StateChanged

    private void jSpinner1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinner1StateChanged
        // This is called, when the spinner of the near plane has been changed.
        float near = ((float)jSlider1.getValue() / 1000f);
        float spin = (Float)jSpinner1.getValue();
        float fov = (int) fovSpinner.getValue();
        // Prevent an endless loop of state changes and don't change the slider when the spinner
        // has gone out of range, since this would lead to the slider's StateChanged overwriting the spinner again.
        // but we want the spinner to be a free-form field

        if (spin <= 2000f && spin >= 100f && !FastMath.approximateEquals((Float)(jSpinner1.getValue()), near)) {
            jSlider1.setValue((int)((Float)(jSpinner1.getValue()) * 1000f));
        }

        final Camera cam = SceneApplication.getApplication().getCamera();
        cam.setFrustumPerspective(fov, (float)cam.getWidth() / cam.getHeight(), spin, cam.getFrustumFar());
    }//GEN-LAST:event_jSpinner1StateChanged

    private void jSlider2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider2StateChanged
        // This is called, when the slider of the far plane has been dragged.
        float far = jSlider2.getValue();

        // Prevent an endless loop of state changes
        if (!FastMath.approximateEquals((Float)(jSpinner2.getValue()), far)) {
            jSpinner2.setValue(far);
        }
    }//GEN-LAST:event_jSlider2StateChanged

    private void jSlider1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider1StateChanged
        // This is called, when the slider of the near plane has been dragged.
        float near = ((float)jSlider1.getValue() / 1000f);

        // Prevent an endless loop of state changes
        if (!FastMath.approximateEquals((Float)(jSpinner1.getValue()), near)) {
            jSpinner1.setValue(near);
        }
    }//GEN-LAST:event_jSlider1StateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton camToCursorSelectionButton;
    private javax.swing.JLabel cameraDirectionLabel;
    private javax.swing.JPanel cameraPanel;
    private javax.swing.JLabel cameraPositionLabel;
    private javax.swing.JButton createPhysicsMeshButton;
    private javax.swing.JLabel cursorPositionHeader;
    private javax.swing.JLabel cursorPositionLabel;
    private javax.swing.JButton cursorToSelectionButton;
    private javax.swing.JButton emitButton;
    private javax.swing.JCheckBox fixedCheckBox;
    private javax.swing.JSlider fovSlider;
    private javax.swing.JSpinner fovSpinner;
    private javax.swing.JSpinner heightSpinner;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JToolBar.Separator jSeparator7;
    private javax.swing.JToolBar.Separator jSeparator8;
    private javax.swing.JToolBar.Separator jSeparator9;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JSlider jSlider2;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JSpinner jSpinner2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JToggleButton jToggleGrid;
    private javax.swing.JToggleButton jToggleScene;
    private javax.swing.JToggleButton jToggleSelectGeom;
    private javax.swing.JToggleButton jToggleSelectTerrain;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JToggleButton moveButton;
    private javax.swing.JButton moveToCursorButton;
    private javax.swing.JSpinner radiusSpinner;
    private javax.swing.JButton resetCursorButton;
    private javax.swing.JToggleButton rotateButton;
    private javax.swing.JToggleButton scaleButton;
    private javax.swing.JLabel sceneInfoLabel;
    private javax.swing.JLabel sceneInfoLabel1;
    private javax.swing.JPanel sceneInfoPanel;
    private javax.swing.JToggleButton selectButton;
    private javax.swing.JToggleButton showGridToggleButton;
    private javax.swing.JToggleButton showSelectionToggleButton;
    private javax.swing.ButtonGroup spatialModButtonGroup;
    private javax.swing.JComboBox<String> transformationTypeComboBox;
    // End of variables declaration//GEN-END:variables

    private void emit(Spatial root) {
        if (root instanceof ParticleEmitter particleEmitter) {
            particleEmitter.killAllParticles();
            particleEmitter.emitAllParticles();
        } else if (root instanceof Node n) {
            for (Spatial child : n.getChildren()) {
                emit(child);
            }
        }

    }

    /**
     * Gets default instance. Do not use directly: reserved for *.settings files
     * only, i.e. deserialization routines; otherwise you could get a
     * non-deserialized instance. To obtain the singleton instance, use
     * {@link #findInstance}.
     *
     * @return
     */
    public static synchronized SceneComposerTopComponent getDefault() {
        if (instance == null) {
            instance = new SceneComposerTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the SceneComposerTopComponent instance. Never call
     * {@link #getDefault} directly!
     *
     * @return
     */
    public static synchronized SceneComposerTopComponent findInstance() {
        TopComponent window = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (window == null) {
            Logger.getLogger(SceneComposerTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (window instanceof SceneComposerTopComponent sceneComposerTopComponent) {
            return sceneComposerTopComponent;
        }
        Logger.getLogger(SceneComposerTopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID
                + "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    @Override
    public HelpCtx getHelpCtx() {
        //this call is for single components:
        //HelpCtx.setHelpIDString(this, "com.jme3.gde.core.sceneviewer");
        return ctx;
    }

    @Override
    public UndoRedo getUndoRedo() {
        return Lookup.getDefault().lookup(UndoRedo.class);
    }

    @Override
    public void componentOpened() {
        super.componentOpened();
        Toolbar tb = ToolbarPool.getDefault().findToolbar("SceneComposer-Tools");
        if (tb != null) {
            if (!tb.isVisible()) {
                tb.setVisible(true);
            }
        }
        if (currentRequest == null) {
            close();
        }
    }

    @Override
    public void componentClosed() {
        super.componentClosed();
        if (currentRequest != null) {
            SceneApplication.getApplication().closeScene(currentRequest);
        }
        Toolbar tb = ToolbarPool.getDefault().findToolbar("SceneComposer-Tools");
        if (tb != null) {
            if (tb.isVisible()) {
                tb.setVisible(false);
            }
        }
    }

    @Override
    protected void componentActivated() {
        SceneViewerTopComponent.findInstance().requestVisible();
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    Object readProperties(java.util.Properties p) {
        if (instance == null) {
            instance = this;
        }
        instance.readPropertiesImpl(p);
        return instance;
    }

    private void readPropertiesImpl(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    private void setSelectedObjectText(final String text) {
        java.awt.EventQueue.invokeLater(() -> {
            if (text != null) {
                ((TitledBorder) jPanel4.getBorder()).setTitle("Utilities - " + text);
            } else {
                ((TitledBorder) jPanel4.getBorder()).setTitle("Utilities - no spatial selected");
            }
        });
    }

    /**
     * method to set the state of the ui items
     */
    private void setSceneInfo(final JmeNode jmeNode, final FileObject file, final boolean active) {
        final SceneComposerTopComponent inst = this;
        if (jmeNode != null) {
            ((TitledBorder) sceneInfoPanel.getBorder()).setTitle(jmeNode.getName());
            selectSpatial(jmeNode.getLookup().lookup(Node.class));
        } else {
            ((TitledBorder) sceneInfoPanel.getBorder()).setTitle("");
        }
        //XXX: wtf? why do i have to repaint?
        sceneInfoPanel.repaint();

        if (!active) {
            result.removeLookupListener(inst);
            showSelectionToggleButton.setSelected(true);
            showGridToggleButton.setSelected(true);
            sceneInfoLabel.setText("");
            sceneInfoLabel.setText("");
            sceneInfoLabel.setToolTipText("");
            sceneInfoLabel.setToolTipText("");
            close();
        } else {
            result.addLookupListener(this);
            showSelectionToggleButton.setSelected(true);
            showGridToggleButton.setSelected(true);
            //TODO: threading
            if (file != null) {
                sceneInfoLabel.setText("Name: " + file.getNameExt());
                sceneInfoLabel.setText("Size: " + file.getSize() / 1024 + " kB");
                sceneInfoLabel.setToolTipText("Name: " + file.getNameExt());
                sceneInfoLabel.setToolTipText("Size: " + file.getSize() / 1024 + " kB");
            }
            open();
            requestActive();
        }
    }

    public void openScene(Spatial spatial, AssetDataObject file, ProjectAssetManager manager) {
        cleanupControllers();
        SceneApplication.getApplication().addSceneListener(this);
        Node node;
        if (spatial instanceof Node node1) {
            node = node1;
        } else {
            node = new Node();
            node.attachChild(spatial);
        }
        JmeNode jmeNode = NodeUtility.createNode(node, file, false);
        SceneRequest request = new SceneRequest(this, jmeNode, manager);
        request.setDataObject(file);
        request.setHelpCtx(ctx);
        this.sentRequest = request;
        request.setWindowTitle("SceneComposer - " + manager.getRelativeAssetPath(file.getPrimaryFile().getPath()));
        request.setToolNode(new Node("SceneComposerToolNode"));
        SceneApplication.getApplication().openScene(request);
    }

    public void addModel(Spatial model) {
        if (editorController != null) {
            editorController.addModel(model, toolController.getCursorLocation());
        } else {
            displayInfo("No scene opened!");
        }
    }

    public void addModel(SpatialAssetDataObject model) {
        if (editorController != null) {
            editorController.addModel(model, toolController.getCursorLocation());
        } else {
            displayInfo("No scene opened!");
        }
    }

    public void linkModel(AssetManager manager, String assetName) {
        if (editorController != null) {
            editorController.linkModel(manager, assetName, toolController.getCursorLocation());
        } else {
            displayInfo("No scene opened!");
        }
    }

    public void doMoveCursor(Vector3f vector) {
        if (toolController != null) {
            toolController.doSetCursorLocation(vector);
        } else {
            displayInfo("No scene opened!");
        }
    }

    /**
     * listener for node selection changes
     *
     * @param ev
     */
    @Override
    public void resultChanged(LookupEvent ev) {
        if (currentRequest == null || !currentRequest.isDisplayed()) {
            return;
        }
        Collection<AbstractSceneExplorerNode> items = (Collection<AbstractSceneExplorerNode>) result.allInstances();

        AbstractSceneExplorerNode[] abstractNodes = new AbstractSceneExplorerNode[items.size()];
        SceneViewerTopComponent.findInstance().setActivatedNodes(items.toArray(org.openide.nodes.Node[]::new));
        items.toArray(abstractNodes);
        select(abstractNodes);
    }

    private boolean select(AbstractSceneExplorerNode[] nodes) {
        if( nodes.length == 0) {
            return false;
        }
        AbstractSceneExplorerNode first = nodes[0];
        if (editorController != null) {
            editorController.setSelectedExplorerNode(first);
        }
        if (first instanceof JmeSpatial jmeSpatial) {
            selectSpatial(jmeSpatial.getLookup().lookup(Spatial.class));
            SceneExplorerTopComponent.findInstance().setSelectedNode(nodes);
            return true;
        } else if (toolController != null) {
            Spatial selectedGizmo = toolController.getMarker(first);
            if (selectedGizmo != null) {
                selectSpatial(selectedGizmo);
                SceneExplorerTopComponent.findInstance().setSelectedNode(nodes);
                return true;
            }
        }
        return false;
    }

    private void selectSpatial(Spatial selection) {
        if (editorController != null) {
            editorController.setSelectedSpat(selection);
        }
        if (selection == null) {
            setSelectedObjectText(null);
            return;
        } else if (toolController != null) {
            toolController.updateSelection(selection);
        }
        if (selection instanceof Node node) {
            setSelectedObjectText(node.getName());
        } else if (selection instanceof Spatial) {
            setSelectedObjectText(selection.getName());
        } else {
            setSelectedObjectText(null);
        }

    }

    private void cleanupControllers() {
        if (camController != null) {
            camController.disable();
            camController = null;
        }
        if (toolController != null) {
            toolController.cleanup();
            toolController = null;
        }
        if (editorController != null) {
            editorController.cleanup();
            editorController = null;
        }
    }

    /*
     * SceneListener
     */
    @Override
    public void sceneOpened(SceneRequest request) {
        if (request.equals(sentRequest)) {
            currentRequest = request;
            if (editorController != null) {
                editorController.cleanup();
            }
            editorController = new SceneEditorController(request.getJmeNode(), request.getDataObject());
            setActivatedNodes(new org.openide.nodes.Node[]{request.getDataObject().getNodeDelegate()});
            setSceneInfo(request.getJmeNode(), editorController.getCurrentFileObject(), true);
            if (camController != null) {
                camController.disable();
            }
            if (toolController != null) {
                toolController.cleanup();
            }
            toolController = new SceneComposerToolController(request.getToolNode(), request.getManager(), request.getJmeNode());
            toolController.setToolListener(this);
            camController = new ComposerCameraController(SceneApplication.getApplication().getCamera(), request.getJmeNode());
            toolController.setEditorController(editorController);
            camController.setToolController(toolController);
            camController.setMaster(this);
            camController.enable();
            
            cameraPositionTrackerAppState = new CameraPositionTrackerAppState(cameraPositionLabel, cameraDirectionLabel);
            SceneApplication.getApplication().getStateManager().attach(cameraPositionTrackerAppState);

            toolController.createOnTopToolNode();
            SelectTool tool = new SelectTool();
            toolController.showEditTool(tool);
            toolController.setShowSelection(true);
            toolController.setShowGrid(true);

            editorController.setToolController(toolController);
            toolController.refreshNonSpatialMarkers();
            toolController.setCamController(camController);

            editorController.setTerrainLodCamera();
            final SpatialAssetDataObject dobj = ((SpatialAssetDataObject) currentRequest.getDataObject());
            listener = (final ProjectAssetManager manager) -> {
                if (dobj.isModified()) {
                    Confirmation msg = new NotifyDescriptor.Confirmation(
                            "Classes have been changed, save and reload scene?",
                            NotifyDescriptor.OK_CANCEL_OPTION,
                            NotifyDescriptor.INFORMATION_MESSAGE);
                    Object result1 = DialogDisplayer.getDefault().notify(msg);
                    if (!NotifyDescriptor.OK_OPTION.equals(result1)) {
                        return;
                    }
                    try {
                        dobj.saveAsset();
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
                Runnable call = new Runnable() {
                    
                    @Override
                    public void run() {
                        ProgressHandle progressHandle = ProgressHandle.createHandle("Reloading Scene..");
                        progressHandle.start();
                        try {
                            manager.clearCache();
                            final Spatial asset = dobj.loadAsset();
                            if (asset != null) {
                                java.awt.EventQueue.invokeLater(new Runnable() {
                                    
                                    @Override
                                    public void run() {
                                        SceneComposerTopComponent composer = SceneComposerTopComponent.findInstance();
                                        composer.openScene(asset, dobj, manager);
                                    }
                                });
                            } else {
                                Confirmation msg = new NotifyDescriptor.Confirmation(
                                        "Error opening " + dobj.getPrimaryFile().getNameExt(),
                                        NotifyDescriptor.OK_CANCEL_OPTION,
                                        NotifyDescriptor.ERROR_MESSAGE);
                                DialogDisplayer.getDefault().notify(msg);
                            }
                        } finally {
                            progressHandle.finish();
                        }
                    }
                };
                new Thread(call).start();
            };
//            currentRequest.getManager().addClassPathEventListener(listener);
        }
    }

    @Override
    public void sceneClosed(SceneRequest request) {
        if (request.equals(currentRequest)) {
            setActivatedNodes(new org.openide.nodes.Node[]{});
            if(request.getManager() != null && listener != null) {
                listener = null;
            }

            SceneApplication.getApplication().removeSceneListener(this);
            SceneApplication.getApplication().getStateManager().detach(cameraPositionTrackerAppState);
            currentRequest = null;
            setSceneInfo(null, null, false);
            cleanupControllers();
        }
    }

    @Override
    public void previewCreated(PreviewRequest request) {
    }

    public void displayInfo(String info) {
        Message msg = new NotifyDescriptor.Message(info);
        DialogDisplayer.getDefault().notifyLater(msg);
    }

    public SceneComposerToolController getToolController() {
        return toolController;
    }

    @Override
    public void onSetCursorLocation(final Vector3f location) {

        SwingUtilities.invokeLater(() -> {
            cursorPositionLabel.setText(
                    SceneComposerUtil.trimDecimals(location));
        });
    }
}
