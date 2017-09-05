/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.gde.scenecomposer.gizmo.light;

import com.jme3.asset.AssetManager;
import com.jme3.environment.util.BoundingSphereDebug;
import com.jme3.gde.scenecomposer.gizmo.light.shape.ProbeRadiusShape;
import com.jme3.gde.core.sceneexplorer.nodes.JmeDirectionalLight;
import com.jme3.gde.core.sceneexplorer.nodes.JmeLight;
import com.jme3.gde.core.sceneexplorer.nodes.JmePointLight;
import com.jme3.gde.core.sceneexplorer.nodes.JmeSpotLight;
import com.jme3.gde.scenecomposer.gizmo.shape.RadiusShape;
import com.jme3.gde.scenecomposer.gizmo.shape.Triangle;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.light.LightProbe;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.BillboardControl;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import com.jme3.scene.debug.Arrow;

/**
 * Handles the creation of the appropriate light gizmo according to the light type.
 * @author Nehon, dokthar
 */
public class LightGizmoFactory {
    private static Quaternion pitch90 = new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_X);  
    
    public static Spatial createGizmo(AssetManager assetManager, JmeLight lightNode) {
        Light light = lightNode.getLookup().lookup(Light.class);
        if (light == null) {
            return null;
        }
        switch (light.getType()) {
            case Point:
                return createPointGizmo(assetManager, (JmePointLight) lightNode, light);
            case Spot:
                return createSpotGizmo(assetManager, (JmeSpotLight) lightNode, light);
            case Directional:
                return createDirectionalGizmo(assetManager, (JmeDirectionalLight) lightNode, light);
                
            case Probe:
                return createLightProbeGizmo(assetManager, light);

            //  default:
            //      return createDefaultGizmo(assetManager, lightNode);
        }
        return null;
    }
    
    private static Node createPointGizmo(AssetManager assetManager, JmePointLight jmeLight, Light light) {
        PointLightGizmo gizmo = new PointLightGizmo(jmeLight);
        gizmo.addControl(new LightPositionUpdate(light, gizmo));
        
        Node billboardNode = new Node("billboard lightGizmo");
        billboardNode.addControl(new BillboardControl());
        gizmo.attachChild(billboardNode);
        billboardNode.attachChild(createLightBulbe(assetManager));
        
        Geometry radius = RadiusShape.createShape(assetManager, "radius shape");
        radius.addControl(new LightRadiusUpdate((PointLight) light));
        radius.addControl(new LightColorUpdate(light, radius.getMaterial(), "Color"));
        billboardNode.attachChild(radius);
        
        return gizmo;
    }
    
    private static Node createDirectionalGizmo(AssetManager assetManager, JmeDirectionalLight jmeLight, Light light) {
        DirectionalLightGizmo gizmo = new DirectionalLightGizmo(jmeLight);
        gizmo.move(0, 5, 0);
        
        Node billboardNode = new Node("billboard lightGizmo");
        billboardNode.addControl(new BillboardControl());
        gizmo.attachChild(billboardNode);
        billboardNode.attachChild(createLightBulbe(assetManager));
        
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.White);
        mat.getAdditionalRenderState().setLineWidth(2f);
        
        Geometry arrow = new Geometry("direction arrow", new Arrow(((DirectionalLight) light).getDirection().mult(5f)));
        arrow.setMaterial(mat);
        arrow.addControl(new LightColorUpdate(light, arrow.getMaterial(), "Color"));
        
        gizmo.attachChild(arrow);
        
        return gizmo;
    }
    
    private static Node createSpotGizmo(AssetManager assetManager, JmeSpotLight jmeLight, Light light) {
        SpotLightGizmo gizmo = new SpotLightGizmo(jmeLight);
        gizmo.addControl(new LightDirectionUpdate(light, gizmo));
        gizmo.addControl(new LightPositionUpdate(light, gizmo));
        
        Node billboardNode = new Node("billboard lightGizmo");
        gizmo.attachChild(billboardNode);
        billboardNode.addControl(new BillboardControl());
        billboardNode.attachChild(createLightBulbe(assetManager));
        
        Node radiusNode = new Node("radius Node");
        gizmo.attachChild(radiusNode);
        
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.White);
        mat.getAdditionalRenderState().setLineWidth(1f);
        
        Geometry arrow = new Geometry("direction arrow", new Arrow(Vector3f.UNIT_Y.mult(1f)));
        arrow.setMaterial(mat);
        arrow.addControl(new LightColorUpdate(light, arrow.getMaterial(), "Color"));
        gizmo.attachChild(arrow);
        
        Geometry inRadius = RadiusShape.createShape(assetManager, "inner radius shape");
        inRadius.rotate(pitch90);
        inRadius.addControl(new LightColorUpdate(light, inRadius.getMaterial(), "Color"));
        inRadius.getMaterial().setFloat("DashSize", 0.875f);
        radiusNode.attachChild(inRadius);
        
        Geometry outRadius = RadiusShape.createShape(assetManager, "outer radius shape");
        outRadius.addControl(new LightColorUpdate(light, outRadius.getMaterial(), "Color"));
        outRadius.getMaterial().setFloat("DashSize", 0.125f);
        radiusNode.attachChild(outRadius);
        outRadius.rotate(pitch90);
        
        Geometry cone = new Geometry("cone shape", new Triangle(1f, -1f));
        cone.setMaterial(mat);
        BillboardControl bc = new BillboardControl();
        bc.setAlignment(BillboardControl.Alignment.AxialY);
        cone.addControl(bc);
        cone.addControl(new LightColorUpdate(light, outRadius.getMaterial(), "Color"));
        cone.addControl(new LightConeRadiusUpdate((SpotLight) light, inRadius, outRadius));
        radiusNode.attachChild(cone);
        
        radiusNode.addControl(new LightRangeUpdate((SpotLight) light, arrow));
        
        return gizmo;
    }
    
    protected static Geometry createLightBulbe(AssetManager assetManager) {
        Quad q = new Quad(0.5f, 0.5f);
        Geometry lightBulb = new Geometry("light bulb", q);
        lightBulb.move(-q.getHeight() / 2f, -q.getWidth() / 2f, 0);
        
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture tex = assetManager.loadTexture("com/jme3/gde/scenecomposer/lightbulb32.png");
        mat.setTexture("ColorMap", tex);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        lightBulb.setMaterial(mat);
        lightBulb.setQueueBucket(RenderQueue.Bucket.Transparent);
        
        return lightBulb;
    }
    
    private static Spatial createLightProbeGizmo(AssetManager assetManager, Light light){
        Node debugNode = new Node("Environment debug Node");
        Sphere s = new Sphere(16, 16, 0.5f);
        Geometry debugGeom = new Geometry(light.getName(), s);
        Material debugMaterial = new Material(assetManager, "Common/MatDefs/Misc/reflect.j3md");
        debugGeom.setMaterial(debugMaterial);
        Spatial debugBounds = ProbeRadiusShape.createShape(assetManager);
        
        debugNode.attachChild(debugGeom);
        debugNode.attachChild(debugBounds);
        debugNode.addControl(new LightProbeGizmoControl((LightProbe)light));
        
        return debugNode;        
    }
}
