/*
 * Copyright (c) 2009-2022 jMonkeyEngine
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
package com.jme3.gde.scenecomposer;

import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.gde.core.scene.SceneApplication;
import com.jme3.gde.core.scene.controller.SceneToolController;
import com.jme3.gde.core.sceneexplorer.nodes.AbstractSceneExplorerNode;
import com.jme3.gde.core.sceneexplorer.nodes.JmeNode;
import com.jme3.gde.scenecomposer.gizmo.GizmoFactory;
import com.jme3.gde.scenecomposer.tools.shortcuts.ShortcutManager;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import org.openide.util.Lookup;

/**
 *
 * @author Brent Owens
 */
public class SceneComposerToolController extends SceneToolController {

    private JmeNode rootNode;
    private SceneEditTool editTool;
    private SceneEditorController editorController;
    private ViewPort overlayView;
    private Node onTopToolsNode;
    private Node nonSpatialMarkersNode;
    private HashMap<AbstractSceneExplorerNode, Spatial> nonSpatialMarkers;

    private boolean snapToGrid = false;
    private boolean snapToScene = false;
    private boolean selectTerrain = false;
    private boolean selectGeometries = false;
    private TransformationType transformationType = TransformationType.local;
    
    private final float fifteenDegs = FastMath.HALF_PI / 6f;

    public enum TransformationType {
        local, global, camera
    }

    public SceneComposerToolController(final Node toolsNode, AssetManager manager, JmeNode rootNode) {
        super(toolsNode, manager);
        this.rootNode = rootNode;
        nonSpatialMarkersNode = new Node("lightMarkersNode");
        nonSpatialMarkers = new HashMap<AbstractSceneExplorerNode, Spatial>();
        SceneApplication.getApplication().enqueue(new Callable<Object>() {

            public Object call() throws Exception {
                toolsNode.attachChild(nonSpatialMarkersNode);
                return null;
            }
        });
        setShowGrid(showGrid);
    }

    public SceneComposerToolController(AssetManager manager) {
        super(manager);
    }

    public void setEditorController(SceneEditorController editorController) {
        this.editorController = editorController;
    }

    public void createOnTopToolNode() {
        // a node in a viewport that will always render on top
        onTopToolsNode = new Node("OverlayNode");
        overlayView = SceneApplication.getApplication().getOverlayView();
        SceneApplication.getApplication().enqueue(new Callable<Void>() {

            public Void call() throws Exception {
                overlayView.attachScene(onTopToolsNode);
                return null;
            }
        });
    }

    @Override
    public void cleanup() {
        super.cleanup();
        editorController = null;
        SceneApplication.getApplication().enqueue(new Callable<Void>() {

            public Void call() throws Exception {
                overlayView.detachScene(onTopToolsNode);
                onTopToolsNode.detachAllChildren();
                return null;
            }
        });
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        if (editTool != null) {
            editTool.doUpdateToolsTransformation();
        }
        if (onTopToolsNode != null) {
            onTopToolsNode.updateLogicalState(tpf);
            onTopToolsNode.updateGeometricState();
        }
    }

    @Override
    public void render(RenderManager rm) {
        super.render(rm);
    }

    public boolean isEditToolEnabled() {
        return editTool != null;
    }

    /**
     * If the current tool overrides camera zoom/pan controls
     *
     * @return
     */
    public boolean isOverrideCameraControl() {
        if (editTool != null) {
            return editTool.isOverrideCameraControl();
        } else {
            return false;
        }
    }

    /**
     * Scene composer edit tool activated. Pass in null to remove tools.
     *
     * @param sceneEditTool pass in null to hide any existing tool markers
     */
    public void showEditTool(final SceneEditTool sceneEditTool) {
        SceneApplication.getApplication().enqueue(new Callable<Object>() {

            public Object call() throws Exception {
                doEnableEditTool(sceneEditTool);
                return null;
            }
        });
    }

    private void doEnableEditTool(SceneEditTool sceneEditTool) {
        if (editTool != null) {
            editTool.hideMarker();
        }
        editTool = sceneEditTool;
        editTool.activate(manager, toolsNode, onTopToolsNode, selected, this);
    }

    public void selectedSpatialTransformed() {
        if (editTool != null) {
            editTool.updateToolsTransformation();
        }
    }

    public void setSelected(Spatial selected) {
        this.selected = selected;
    }

    public void setNeedsSave(boolean needsSave) {
        editorController.setNeedsSave(needsSave);
    }

    /**
     * Primary button activated, send command to the tool for appropriate
     * action.
     *
     * @param mouseLoc
     * @param pressed
     * @param camera
     */
    public void doEditToolActivatedPrimary(Vector2f mouseLoc, boolean pressed, Camera camera) {
        ShortcutManager scm = Lookup.getDefault().lookup(ShortcutManager.class);

        if (scm.isActive()) {
            scm.getActiveShortcut().setCamera(camera);
            scm.getActiveShortcut().actionPrimary(mouseLoc, pressed, rootNode, editorController.getCurrentDataObject());
        } else if (editTool != null) {
            editTool.setCamera(camera);
            editTool.actionPrimary(mouseLoc, pressed, rootNode, editorController.getCurrentDataObject());
        }
    }

    /**
     * Secondary button activated, send command to the tool for appropriate
     * action.
     *
     * @param mouseLoc
     * @param pressed
     * @param camera
     */
    public void doEditToolActivatedSecondary(Vector2f mouseLoc, boolean pressed, Camera camera) {
        ShortcutManager scm = Lookup.getDefault().lookup(ShortcutManager.class);

        if (scm.isActive()) {
            scm.getActiveShortcut().setCamera(camera);
            scm.getActiveShortcut().actionSecondary(mouseLoc, pressed, rootNode, editorController.getCurrentDataObject());
        } else if (editTool != null) {
            editTool.setCamera(camera);
            editTool.actionSecondary(mouseLoc, pressed, rootNode, editorController.getCurrentDataObject());
        }
    }

    public void doEditToolMoved(Vector2f mouseLoc, Camera camera) {
        ShortcutManager scm = Lookup.getDefault().lookup(ShortcutManager.class);

        if (scm.isActive()) {
            scm.getActiveShortcut().setCamera(camera);
            scm.getActiveShortcut().mouseMoved(mouseLoc, rootNode, editorController.getCurrentDataObject());
        } else if (editTool != null && editorController != null) {
            editTool.setCamera(camera);
            editTool.mouseMoved(mouseLoc, rootNode, editorController.getCurrentDataObject());
        }
    }

    public void doEditToolDraggedPrimary(Vector2f mouseLoc, boolean pressed, Camera camera) {
        ShortcutManager scm = Lookup.getDefault().lookup(ShortcutManager.class);

        if (scm.isActive()) {
            scm.getActiveShortcut().setCamera(camera);
            scm.getActiveShortcut().draggedPrimary(mouseLoc, pressed, rootNode, editorController.getCurrentDataObject());
        } else if (editTool != null) {
            editTool.setCamera(camera);
            editTool.draggedPrimary(mouseLoc, pressed, rootNode, editorController.getCurrentDataObject());
        }
    }

    public void doEditToolDraggedSecondary(Vector2f mouseLoc, boolean pressed, Camera camera) {
        ShortcutManager scm = Lookup.getDefault().lookup(ShortcutManager.class);

        if (scm.isActive()) {
            scm.getActiveShortcut().setCamera(null);
            scm.getActiveShortcut().draggedSecondary(mouseLoc, pressed, rootNode, editorController.getCurrentDataObject());
        } else if (editTool != null) {
            editTool.setCamera(camera);
            editTool.draggedSecondary(mouseLoc, pressed, rootNode, editorController.getCurrentDataObject());
        }
    }

    public void doKeyPressed(KeyInputEvent kie) {
        ShortcutManager scm = Lookup.getDefault().lookup(ShortcutManager.class);

        if (scm.isActive()) {
            scm.doKeyPressed(kie);
        } else if (scm.activateShortcut(kie)) {
            scm.getActiveShortcut().activate(manager, toolsNode, onTopToolsNode, selected, this);
        } else if (editTool != null) {
            editTool.keyPressed(kie);
        }
    }

    protected void refreshNonSpatialMarkers() {
        addMarkers();
    }

    private void getNodes(org.openide.nodes.Node node, List<AbstractSceneExplorerNode> list) {
        if (node instanceof AbstractSceneExplorerNode) {
            list.add((AbstractSceneExplorerNode) node);
        }
        if (!node.isLeaf()) {
            for (org.openide.nodes.Node n : node.getChildren().getNodes(true)) {
                getNodes(n, list);
            }
        }
    }

    private void addMarkers() {
        final List<AbstractSceneExplorerNode> nodes = new ArrayList<AbstractSceneExplorerNode>();
        // gather nodes, have to be in an other thread than the sceneApplication
        getNodes(rootNode, nodes);
        
        // then update markers
        SceneApplication.getApplication().enqueue(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                
                Iterator<AbstractSceneExplorerNode> it = nonSpatialMarkers.keySet().iterator();
                while (it.hasNext()) {
                    AbstractSceneExplorerNode n = it.next();
                    if (nodes.contains(n)) {
                        // a node is already added
                        nodes.remove(n);
                    } else {
                        // a node is no more needed
                        nonSpatialMarkers.get(n).removeFromParent();
                        it.remove();
                        //nonSpatialMarkers.remove(n);
                    }
                }

                it = nodes.iterator();
                while (it.hasNext()) {
                    AbstractSceneExplorerNode n = it.next();
                    if (!nonSpatialMarkers.containsKey(n)) {
                        Spatial s = GizmoFactory.createGizmo(manager, n);
                        if (s != null) {
                            nonSpatialMarkers.put(n, s);
                            nonSpatialMarkersNode.attachChild(s);
                        }
                    }
                }
                return null;
            }
        });
    }

    public Spatial getMarker(AbstractSceneExplorerNode node) {
        return nonSpatialMarkers.get(node);
    }

    public boolean isSnapToGrid() {
        return snapToGrid;
    }

    public void setSnapToGrid(boolean snapToGrid) {
        this.snapToGrid = snapToGrid;
    }

    public void setSnapToScene(boolean snapToScene) {
        this.snapToScene = snapToScene;
    }

    public boolean isSnapToScene() {
        return snapToScene;
    }

    public boolean isSelectTerrain() {
        return selectTerrain;
    }

    public void setSelectTerrain(boolean selectTerrain) {
        this.selectTerrain = selectTerrain;
    }

    public boolean isSelectGeometries() {
        return selectGeometries;
    }

    public void setSelectGeometries(boolean selectGeometries) {
        this.selectGeometries = selectGeometries;
    }

    public void setTransformationType(String type) {
        if (type != null) {
            if (type.equals("Local")) {
                setTransformationType(TransformationType.local);
            } else if (type.equals("Global")) {
                setTransformationType(TransformationType.global);
            } else if (type.equals("Camera")) {
                setTransformationType(TransformationType.camera);
            }
        }
    }

    /**
     * @param type the transformationType to set
     */
    public void setTransformationType(TransformationType type) {
        if (type != this.transformationType) {
            this.transformationType = type;
            if (editTool != null) {
                //update the transform type of the tool
                editTool.setTransformType(transformationType);
            }
        }
    }

    /**
     * @return the transformationType
     */
    public TransformationType getTransformationType() {
        return transformationType;
    }
     
    public JmeNode getRootNode() {
        return rootNode;
    }
    
    /**
     * Update the selected spatial with translation from user input
     * 
     * @param translation absolute translation
     * @param constraints axes affected
     */
    public void updateSelectedTranslation(final Vector3f translation, 
            final Vector3f constraints) {
        if (isSnapToScene()) {
            translation.set(snapToScene(translation));
        }
        if (isSnapToGrid()) {
            if (constraints.x != 0f) {
                translation.setX((int) translation.x);
            }
            if (constraints.y != 0f) {
                translation.setY((int) translation.y);
            }
            if (constraints.z != 0f) {
                translation.setZ((int) translation.z);
            }
        }
        selected.setLocalTranslation(translation);
    }
    
    /**
     * Update the selected spatial with rotation from user input
     * 
     * @param rotation absolute rotation
     * @param constraints axes affected
     */
    public void updateSelectedRotation(final Quaternion rotation, 
            final Vector3f constraints) {
        if (isSnapToGrid()) {
            final float[] angles = new float[3];
            rotation.toAngles(angles);
            
            if (constraints.y != 0f) {
                angles[1] = Math.round(angles[1] / FastMath.HALF_PI) 
                        * fifteenDegs;
            }
            if (constraints.x != 0f) {
                angles[0] = Math.round(angles[0] / FastMath.HALF_PI) 
                        * fifteenDegs;
            }
            if (constraints.z != 0f) {
                angles[2] = Math.round(angles[2] / FastMath.HALF_PI) 
                        * fifteenDegs;
            }
            rotation.fromAngles(angles);
        }
        selected.setLocalRotation(rotation);
    }
    
    /**
     * Update the selected spatial with scale from user input
     * 
     * @param scale absolute scale
     * @param constraints axes affected 
     */
    public void updateSelectedScale(final Vector3f scale, 
            final Vector3f constraints) {
        if (isSnapToGrid()) {
            if (constraints.x != 0f) {
                scale.setX((int) Math.max(scale.x, 1));
            }
            if (constraints.y != 0f) {
                scale.setY((int) Math.max(scale.y, 1));
            }
            if (constraints.z != 0f) {
                scale.setZ((int) Math.max(scale.z, 1));
            }
        }
        selected.setLocalScale(scale);
    }

    private Vector3f snapToScene(final Vector3f position) {
        final Ray ray = new Ray(position, Vector3f.UNIT_Y.negate());
        final CollisionResults collisionResults = new CollisionResults();
        final Node root = getRootNode().getLookup().lookup(Node.class);
        root.collideWith(ray, collisionResults);
        for (CollisionResult r : collisionResults) {
            if (r.getGeometry() != selected) {
                position.y = r.getContactPoint().y;
                break;
            }
        }
        return position;
    }

}
