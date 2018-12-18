/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.gde.scenecomposer.gizmo.light;

import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingSphere;
import com.jme3.bounding.BoundingVolume;
import com.jme3.gde.core.sceneexplorer.nodes.JmePointLight;
import com.jme3.gde.scenecomposer.gizmo.NodeCallback;
import com.jme3.light.PointLight;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 * Updates the marker's position whenever the light has moved.
 */
public class PointLightGizmo extends NodeCallback {

    private PointLight light;
    private JmePointLight jmeLight;

    public PointLightGizmo(JmePointLight jmelight) {
        super("point light callback", true, false, false);
        jmeLight = jmelight;
        light = jmeLight.getLookup().lookup(PointLight.class);
    }

    @Override
    public void onTranslation(Vector3f oldTranslation, Vector3f newTranslation) {
        light.setPosition(getWorldTranslation());
        jmeLight.setValue("position", light.getPosition());
    }

    @Override
    public void onResize(Vector3f oldScale, Vector3f newScale) {
        light.setRadius(LightGizmoFactory.scaleToRadius(newScale));
        jmeLight.setValue("radius", light.getRadius());
    }

    @Override
    public void onRotation(Quaternion oldRotation, Quaternion newRotation) {
    }

    @Override
    public Vector3f getLocalScale() {
        float r = light.getRadius();
        return new Vector3f(r, r, r);
    }

    @Override
    public Vector3f getWorldScale() {
        float r = light.getRadius();
        return new Vector3f(r, r, r);
    }

    @Override
    public BoundingVolume getWorldBound() {
        //return new BoundingBox(light.getPosition(), light.getRadius(), light.getRadius(), light.getRadius());
        return new BoundingSphere(light.getRadius(), light.getPosition());
    }

}
