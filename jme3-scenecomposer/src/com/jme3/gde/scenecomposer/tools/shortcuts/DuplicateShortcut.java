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
package com.jme3.gde.scenecomposer.tools.shortcuts;

import com.jme3.asset.AssetManager;
import com.jme3.gde.core.sceneexplorer.SceneExplorerTopComponent;
import com.jme3.gde.core.sceneexplorer.nodes.AbstractSceneExplorerNode;
import com.jme3.gde.core.sceneexplorer.nodes.JmeNode;
import com.jme3.gde.core.sceneviewer.SceneViewerTopComponent;
import com.jme3.gde.core.undoredo.AbstractUndoableSceneEdit;
import com.jme3.gde.scenecomposer.SceneComposerToolController;
import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;

/**
 *
 * @author dokthar
 */
public class DuplicateShortcut extends ShortcutTool {

    @Override
    public boolean isActivableBy(KeyInputEvent kie) {
        if (kie.getKeyCode() == KeyInput.KEY_D && kie.isPressed()) {
            if (Lookup.getDefault().lookup(ShortcutManager.class).isShiftDown()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void cancel() {
        terminate();
    }

    @Override
    public void activate(AssetManager manager, Node toolNode, Node onTopToolNode, Spatial selectedSpatial, SceneComposerToolController toolController) {
        super.activate(manager, toolNode, onTopToolNode, selectedSpatial, toolController); //To change body of generated methods, choose Tools | Templates.
        hideMarker();
        if (selectedSpatial != null) {
            duplicate();
            terminate();
            
            //then enable move shortcut
            toolController.doKeyPressed(new KeyInputEvent(KeyInput.KEY_G, 'g', true, false));
        } else {
            terminate();
        }
    }

    private void duplicate() {
        Spatial selected = toolController.getSelectedSpatial();
        
        if(selected == null) {
            return;
        }
        
        Spatial clone = selected.clone();
        clone.move(1, 0, 1);

        selected.getParent().attachChild(clone);
        actionPerformed(new DuplicateUndo(clone, selected.getParent()));
        selected = clone;
        final Spatial cloned = clone;
        final JmeNode rootNode = toolController.getRootNode();
        refreshSelected(rootNode, selected.getParent());
        
        java.awt.EventQueue.invokeLater(() -> {
                SceneViewerTopComponent.findInstance().setActivatedNodes(new org.openide.nodes.Node[]{rootNode.getChild(cloned)});
                SceneExplorerTopComponent.findInstance().setSelectedNode(new AbstractSceneExplorerNode[]{rootNode.getChild(cloned)});
        });
        toolController.updateSelection(selected);
    }

    private void refreshSelected(final JmeNode jmeRootNode, final Node parent) {
        java.awt.EventQueue.invokeLater(() -> {
            jmeRootNode.getChild(parent).refresh(false);
        });
    }

    @Override
    public void keyPressed(KeyInputEvent kie) {

    }

    @Override
    public void actionPrimary(Vector2f screenCoord, boolean pressed, JmeNode rootNode, DataObject dataObject) {
    }

    @Override
    public void actionSecondary(Vector2f screenCoord, boolean pressed, JmeNode rootNode, DataObject dataObject) {
    }

    @Override
    public void mouseMoved(Vector2f screenCoord, JmeNode rootNode, DataObject dataObject) {
    }

    @Override
    public void draggedPrimary(Vector2f screenCoord, boolean pressed, JmeNode rootNode, DataObject currentDataObject) {
    }

    @Override
    public void draggedSecondary(Vector2f screenCoord, boolean pressed, JmeNode rootNode, DataObject currentDataObject) {
    }

    private class DuplicateUndo extends AbstractUndoableSceneEdit {

        private final Spatial spatial;
        private final Node parent;

        DuplicateUndo(Spatial spatial, Node parent) {
            this.spatial = spatial;
            this.parent = parent;
        }

        @Override
        public void sceneUndo() {
            spatial.removeFromParent();
        }

        @Override
        public void sceneRedo() {
            parent.attachChild(spatial);
        }
    }
}
