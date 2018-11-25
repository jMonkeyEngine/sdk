/*
 *  Copyright (c) 2009-2018 jMonkeyEngine
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
package com.jme3.gde.scenecomposer.gizmo.light;

import com.jme3.bounding.BoundingSphere;
import com.jme3.bounding.BoundingVolume;
import com.jme3.gde.core.sceneexplorer.nodes.JmeLightProbe;
import com.jme3.gde.scenecomposer.gizmo.NodeCallback;
import com.jme3.light.LightProbe;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

/**
 * Updates the marker's position whenever the light probe has moved. 
 * Also update the gizmo radius according to the probe radius.
 * Each Gizmo extends NodeCallback, so it is notified, when it's moved
 * by the Scene Composer Tools.
 */
public class LightProbeGizmo extends NodeCallback {

    private final Vector3f lastPos = new Vector3f();
    private final LightProbe lightProbe;
    private final JmeLightProbe jmeProbe;    

    LightProbeGizmo(JmeLightProbe jmeProbe) {
        super("point light callback", true, false, false);
        this.jmeProbe = jmeProbe;
        lightProbe = jmeProbe.getLightProbe();
    }
    
    protected float getRadius() {
        if (lightProbe.isReady()) {
            return ((BoundingSphere)lightProbe.getBounds()).getRadius();
        } else {
            return 0f;
        }
    }

    /**
     * Handles the SceneComposers Translation "Requests".
     * Essentially this is called when setLocalTranslation is invoked on the Gizmo.
     * We then need to ensure that the Properties update and that the light
     * itself updates (since the SceneComposer actually works with our Gizmos).
     * 
     * @param oldTranslation The Translation as it was before
     * @param newTranslation The Translation as it is now
     */
    @Override
    public void onTranslation(Vector3f oldTranslation, Vector3f newTranslation) {
        // For lights, we're always interested in the WorldTranslation
        lightProbe.setPosition(getWorldTranslation());
        jmeProbe.setValue("position", lightProbe.getPosition());
    }

    /**
     * Handles the SceneComposers Scale "Requests".
     * Essentially this is called when setLocalScale is invoked on the Gizmo.
     * We then need to ensure that the Propertries update and that the light
     * itself updates (since the SceneComposer actually works with our Gizmos).
     * 
     * @param oldScale The Scale as it was before
     * @param newScale The Scale as it is now
     */
    @Override
    public void onResize(Vector3f oldScale, Vector3f newScale) {
        jmeProbe.getLightProbe().getArea().setRadius(LightGizmoFactory.scaleToRadius(newScale));
    }

    /**
     * Handles the SceneComposers Rotate "Requests".
     * Essentially this is called when setLocalRotation is invoked on the Gizmo.
     * We then need to ensure that the Propertries update and that the light
     * itself updates (since the SceneComposer actually works with our Gizmos).
     * 
     * @param oldRotation The Rotation as it was before
     * @param newRotation The Rotation as it is now
     */
    @Override
    public void onRotation(Quaternion oldRotation, Quaternion newRotation) {
        // Does not make sense.
    }
    
    
    // Convert Radius to a fake "scale"
    @Override
    public Vector3f getLocalScale() {
        float r = getRadius();
        return new Vector3f(r, r, r);
    }

    @Override
    public Vector3f getWorldScale() {
        float r = getRadius();
        return new Vector3f(r, r, r);
    }

    @Override
    public BoundingVolume getWorldBound() {
        return new BoundingSphere(getRadius(), lightProbe.getPosition());
    }

}
