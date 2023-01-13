/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.gde.vehiclecreator;

import com.jme3.asset.DesktopAssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.objects.VehicleWheel;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.gde.core.assets.BinaryModelDataObject;
import com.jme3.gde.core.assets.ProjectAssetManager;
import com.jme3.gde.core.scene.SceneApplication;
import com.jme3.gde.core.scene.controller.SceneToolController;
import com.jme3.gde.core.sceneexplorer.nodes.JmeSpatial;
import com.jme3.gde.core.undoredo.AbstractUndoableSceneEdit;
import com.jme3.gde.core.undoredo.SceneUndoRedoManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.Lookup.Result;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;

/**
 *
 * @author normenhansen
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class VehicleEditorController implements LookupListener, ActionListener {

    private final JmeSpatial jmeRootNode;
    private JmeSpatial selectedSpat;
    private final BinaryModelDataObject currentFileObject;
    private VehicleControl vehicleControl;
    private final Result<JmeSpatial> result;
    private Result<VehicleWheel> result2;
    private final List<Geometry> list = new LinkedList<>();
    private SceneToolController toolController;
    private final Node toolsNode;
    private final BulletAppState bulletState;
    private boolean testing = false;
    private float motorForce = 800;
    private float brakeForce = 40;

    public VehicleEditorController(JmeSpatial jmeRootNode, BinaryModelDataObject currentFileObject) {
        this.jmeRootNode = jmeRootNode;
        this.currentFileObject = currentFileObject;
        toolsNode = new Node("ToolsNode");
        toolController = new SceneToolController(toolsNode, currentFileObject.getLookup().lookup(ProjectAssetManager.class));
        toolController.setShowSelection(true);
        result = Utilities.actionsGlobalContext().lookupResult(JmeSpatial.class);
        result.addLookupListener(this);
        toolsNode.addLight(new DirectionalLight());
        Node track = (Node) new DesktopAssetManager(true).loadModel("Models/Racetrack/Raceway.j3o");
        if (track.getChild("Grass").getControl(RigidBodyControl.class) == null) {
            track.getChild("Grass").addControl(new RigidBodyControl());
        }
        if (track.getChild("Road").getControl(RigidBodyControl.class) == null) {
            track.getChild("Road").addControl(new RigidBodyControl());
        }
        if (track.getChild("Wall").getControl(RigidBodyControl.class) == null) {
            track.getChild("Wall").addControl(new RigidBodyControl());
        }
        if (track.getChild("Rumble-Strip").getControl(RigidBodyControl.class) == null) {
            track.getChild("Rumble-Strip").addControl(new RigidBodyControl());
        }

        toolsNode.attachChild(track);
        bulletState = new BulletAppState();

        result2 = Utilities.actionsGlobalContext().lookupResult(VehicleWheel.class);
        LookupListener listener = (LookupEvent ev) -> {
            for (VehicleWheel wheel : result2.allInstances()) {
                toolController.updateSelection(wheel.getWheelSpatial());
            }
        };
        result2.addLookupListener(listener);
    }

    public void prepareApplication() {
        SceneApplication.getApplication().getStateManager().attach(getBulletState());
        SceneApplication.getApplication().getInputManager().addMapping("VehicleEditor_Left", new KeyTrigger(KeyInput.KEY_A));
        SceneApplication.getApplication().getInputManager().addMapping("VehicleEditor_Right", new KeyTrigger(KeyInput.KEY_D));
        SceneApplication.getApplication().getInputManager().addMapping("VehicleEditor_Up", new KeyTrigger(KeyInput.KEY_W));
        SceneApplication.getApplication().getInputManager().addMapping("VehicleEditor_Down", new KeyTrigger(KeyInput.KEY_S));
        SceneApplication.getApplication().getInputManager().addMapping("VehicleEditor_Space", new KeyTrigger(KeyInput.KEY_SPACE));
        SceneApplication.getApplication().getInputManager().addMapping("VehicleEditor_Reset", new KeyTrigger(KeyInput.KEY_RETURN));
        SceneApplication.getApplication().getInputManager().addListener(this, "VehicleEditor_Left", "VehicleEditor_Right", "VehicleEditor_Up", "VehicleEditor_Down", "VehicleEditor_Space", "VehicleEditor_Reset");
    }

    public void cleanupApplication() {
        SceneApplication.getApplication().getInputManager().removeListener(this);
        SceneApplication.getApplication().getStateManager().detach(getBulletState());
    }

    public JmeSpatial getJmeRootNode() {
        return jmeRootNode;
    }

    public JmeSpatial getSelectedSpat() {
        return selectedSpat;
    }

    public void setSelectedSpat(JmeSpatial selectedSpat) {
        this.selectedSpat = selectedSpat;
    }

    public BinaryModelDataObject getCurrentFileObject() {
        return currentFileObject;
    }

    public void awtCall() {
        if (selectedSpat == null) {
            return;
        }
        try {
            final Spatial node = selectedSpat.getLookup().lookup(Spatial.class);
            if (node != null) {
                SceneApplication.getApplication().enqueue(() -> {
                    doAwtCall(node);
                    return null;
                }).get();
            }
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void doAwtCall(Spatial selected) {
    }

    public void testVehicle() {
        if (jmeRootNode == null) {
            return;
        }
        final Node node = jmeRootNode.getLookup().lookup(Node.class);
        if (node != null) {
            SceneApplication.getApplication().enqueue(() -> {
                doTestVehicle(node);
                return null;
            });
        }
    }
//    private ChaseCamera chaseCam;

    public void doTestVehicle(Node vehicleNode) {
        testing = true;
        bulletState.getPhysicsSpace().addAll(toolsNode);
        bulletState.getPhysicsSpace().add(vehicleControl);
//        vehicleControl.detachDebugShape();
//        cameraController.disable();
//        if (chaseCam == null) {
//            chaseCam = new ChaseCamera(SceneApplication.getApplication().getCamera(), vehicleNode);
//            chaseCam.registerWithInput(SceneApplication.getApplication().getInputManager());
//        }
//        chaseCam.setEnabled(true);
    }

    public void stopVehicle() {
        try {
            SceneApplication.getApplication().enqueue(() -> {
                doStopVehicle();
                return null;
            }).get();
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void doStopVehicle() {
        testing = false;
        vehicleControl.setPhysicsLocation(Vector3f.ZERO);
        vehicleControl.setPhysicsRotation(new Matrix3f());
        vehicleControl.setLinearVelocity(Vector3f.ZERO);
        vehicleControl.setAngularVelocity(Vector3f.ZERO);
        vehicleControl.resetSuspension();
//        vehicleControl.createDebugShape(SceneApplication.getApplication().getAssetManager());
        bulletState.getPhysicsSpace().removeAll(toolsNode);
        bulletState.getPhysicsSpace().remove(vehicleControl);
//        chaseCam.setEnabled(false);
//        cameraController.enable();
    }

    public void centerSelected() {
        if (selectedSpat == null) {
            return;
        }
        try {
            final Spatial node = selectedSpat.getLookup().lookup(Spatial.class);
            if (node != null) {
                SceneApplication.getApplication().enqueue(() -> {
                    doCenterSelected(node);
                    return null;
                }).get();
                currentFileObject.setModified(true);
            }
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void doCenterSelected(final Spatial selected) {
        final Vector3f location = new Vector3f(selected.getLocalTranslation());
        selected.center();
        Lookup.getDefault().lookup(SceneUndoRedoManager.class).addEdit(this, new AbstractUndoableSceneEdit() {

            @Override
            public void sceneUndo() {
                //undo stuff here
                selected.setLocalTranslation(location);
            }

            @Override
            public void sceneRedo() {
                //redo stuff here
                selected.center();
            }

            @Override
            public void awtRedo() {
            }

            @Override
            public void awtUndo() {
            }
        });
    }

    public void addWheel(final SuspensionSettings settings) {
        if (selectedSpat == null || selectedSpat == jmeRootNode) {
            return;
        }
        try {
            final Spatial node = selectedSpat.getLookup().lookup(Spatial.class);
            final Node rootNode = jmeRootNode.getLookup().lookup(Node.class);
            if (node != null) {
                SceneApplication.getApplication().enqueue(() -> {
                    doAddWheel(node, rootNode, settings);
                    return null;
                }).get();
                currentFileObject.setModified(true);
                refreshSelectedParent();
            }
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void doAddWheel(Spatial selected, Spatial vehicle, SuspensionSettings settings) {
        Spatial node;
        Vector3f wheelLocation = vehicle.worldToLocal(selected.getWorldBound().getCenter(), new Vector3f());
        wheelLocation.add(0, settings.getRestLength(), 0);

        //compute radius from bounding volue with scale if set
        if (settings.getBoundingScale() > 0) {
            BoundingBox worldBound;
            if (selected.getWorldBound() instanceof BoundingBox) {
                worldBound = (BoundingBox) selected.getWorldBound();
                settings.setRadius(worldBound.getYExtent() * settings.getBoundingScale());
            } else {
                Logger.getLogger(VehicleEditorController.class.getName()).log(Level.WARNING, "Cannot get bounding box!");
            }
        }

        if (settings.isCreateNode()) {
            node = new Node(selected.getName() + "-WheelNode");
        } else {
            node = selected;
        }

        node.setLocalTranslation(selected.worldToLocal(selected.getWorldBound().getCenter(), new Vector3f()));

        VehicleWheel wheel = vehicleControl.addWheel(node, wheelLocation, settings.getDirection(), settings.getAxle(), settings.getRestLength(), settings.getRadius(), settings.isFrontWheel());
        settings.applyData(wheel);
        if (settings.isCreateNode()) {
            selected.center();
            Node parent = selected.getParent();
            ((Node) node).attachChild(selected);
            parent.attachChild(node);
        }
    }

    public void checkVehicle() {
        if (jmeRootNode == null) {
            return;
        }
        try {
            final Node node = jmeRootNode.getLookup().lookup(Node.class);
            if (node != null) {
                if (SceneApplication.getApplication().enqueue(() -> doCheckVehicle(node)).get()) {
                    currentFileObject.setModified(true);
                    refreshRoot();
                }
            }
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public boolean doCheckVehicle(Node rootNode) {
        VehicleControl control = rootNode.getControl(VehicleControl.class);
        if (control == null) {
            vehicleControl = new VehicleControl(new BoxCollisionShape(Vector3f.UNIT_XYZ), 200);
//            vehicleControl.createDebugShape(SceneApplication.getApplication().getAssetManager());
            rootNode.addControl(vehicleControl);
            return true;
        } else {
            vehicleControl = control;
//            vehicleControl.createDebugShape(SceneApplication.getApplication().getAssetManager());
            return false;
        }
    }

    public void createHullShapeFromSelected() {
        if (selectedSpat == null) {
            return;
        }
        try {
            final Spatial node = selectedSpat.getLookup().lookup(Spatial.class);
            Logger.getLogger(VehicleEditorController.class.getName()).log(Level.INFO, "Creating hull shape");
//            if (list.isEmpty()) {
//                return;
//            }
            final VehicleControl control = vehicleControl;
            SceneApplication.getApplication().enqueue(() -> {
                doCreateHullShapeFromSelected(control, node);// new LinkedList<Geometry>(list));
                return null;
            }).get();
            currentFileObject.setModified(true);
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void doCreateHullShapeFromSelected(VehicleControl control, Spatial spat) {
//        Logger.getLogger(VehicleEditorController.class.getName()).log(Level.INFO, "Merging Geometries");
//        Mesh mesh = new Mesh();
//        GeometryBatchFactory.mergeGeometries(list, mesh);
//        control.setCollisionShape(new HullCollisionShape(list.get(0).getMesh()));
        control.setCollisionShape(CollisionShapeFactory.createDynamicMeshShape(spat));
        refreshSelected();
    }

    public void applyWheelData(final int wheels, final SuspensionSettings settings) {
        try {
            final VehicleControl vehicleControl = this.vehicleControl;
            SceneApplication.getApplication().enqueue(() -> {
                doApplyWheelData(vehicleControl, wheels, settings);
                return null;
            }).get();
            currentFileObject.setModified(true);
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void doApplyWheelData(VehicleControl control, int wheels, SuspensionSettings settings) {
        for (int i = 0; i < control.getNumWheels(); i++) {
            VehicleWheel wheel = control.getWheel(i);
            switch (wheels) {
                case 0:
                    break;
                case 1:
                    if (!wheel.isFrontWheel()) {
                        continue;
                    }
                    break;
                case 2:
                    if (wheel.isFrontWheel()) {
                        continue;
                    }
                    break;
            }
            wheel.setRestLength(settings.getRestLength());
            wheel.setMaxSuspensionForce(settings.getMaxForce());
            wheel.setSuspensionStiffness(settings.getStiffness());
            wheel.setRollInfluence(settings.getRollInfluence());
            wheel.setWheelsDampingCompression(settings.getCompression());
            wheel.setWheelsDampingRelaxation(settings.getRelease());
//            wheel.setRadius(settings.getRadius());
            wheel.setFrictionSlip(settings.getFriction());
        }
    }

    private void refreshSelected() {
        java.awt.EventQueue.invokeLater(() -> {
            if (getSelectedSpat() != null) {
                getSelectedSpat().refresh(false);
            }
        });

    }

    private void refreshSelectedParent() {
        java.awt.EventQueue.invokeLater(() -> {
            if (getSelectedSpat() != null) {
                ((JmeSpatial) getSelectedSpat().getParentNode()).refresh(false);
            }
        });

    }

    private void refreshRoot() {
        java.awt.EventQueue.invokeLater(() -> {
            if (getJmeRootNode() != null) {
                getJmeRootNode().refresh(true);
            }
        });

    }

    public void cleanup() {
        result.removeLookupListener(this);
        result2.removeLookupListener(this);
        toolController.cleanup();
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        boolean cleared = false;
        for (JmeSpatial jmeSpatial : result.allInstances()) {
            selectedSpat = jmeSpatial;
            Spatial spat = jmeSpatial.getLookup().lookup(Spatial.class);
            toolController.updateSelection(spat);
            Geometry geom = jmeSpatial.getLookup().lookup(Geometry.class);
            if (geom != null) {
                if (!cleared) {
                    list.clear();
                    cleared = true;
                }
                Logger.getLogger(VehicleEditorController.class.getName()).log(Level.INFO, "adding:{0}", jmeSpatial.getName());
                list.add(geom);
            }
        }
    }

    /**
     * @return the toolsNode
     */
    public Node getToolsNode() {
        return toolsNode;
    }

    /**
     * @return the bulletState
     */
    public BulletAppState getBulletState() {
        return bulletState;
    }
    float steeringValue = 0;
    float accelerationValue = 0;

    @Override
    public void onAction(String binding, boolean value, float f) {
        if (!testing) {
            return;
        }
        switch (binding) {
            case "VehicleEditor_Left":
                if (value) {
                    steeringValue += .5f;
                } else {
                    steeringValue += -.5f;
                }
                vehicleControl.steer(steeringValue);
                break;
            case "VehicleEditor_Right":
                if (value) {
                    steeringValue += -.5f;
                } else {
                    steeringValue += .5f;
                }
                vehicleControl.steer(steeringValue);
                break;
            case "VehicleEditor_Up":
                if (value) {
                    accelerationValue += motorForce;
                } else {
                    accelerationValue -= motorForce;
                }
                vehicleControl.accelerate(accelerationValue);
                break;
            case "VehicleEditor_Down":
                if (value) {
                    vehicleControl.brake(brakeForce);
                } else {
                    vehicleControl.brake(0);
                }
                break;
            case "VehicleEditor_Reset":
                if (value) {
                    System.out.println("Reset");
                    vehicleControl.setPhysicsLocation(Vector3f.ZERO);
                    vehicleControl.setPhysicsRotation(new Matrix3f());
                    vehicleControl.setLinearVelocity(Vector3f.ZERO);
                    vehicleControl.setAngularVelocity(Vector3f.ZERO);
                    vehicleControl.resetSuspension();
                } else {
                }
                break;
            default:
                break;
        }
    }

    /**
     * @return the motorForce
     */
    public float getMotorForce() {
        return motorForce;
    }

    /**
     * @param motorForce the motorForce to set
     */
    public void setMotorForce(float motorForce) {
        this.motorForce = motorForce;
    }

    /**
     * @return the brakeForce
     */
    public float getBrakeForce() {
        return brakeForce;
    }

    /**
     * @param brakeForce the brakeForce to set
     */
    public void setBrakeForce(float brakeForce) {
        this.brakeForce = brakeForce;
    }
}
