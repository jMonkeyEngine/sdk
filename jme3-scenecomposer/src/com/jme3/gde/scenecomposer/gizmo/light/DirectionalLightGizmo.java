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
import com.jme3.gde.core.sceneexplorer.nodes.JmeDirectionalLight;
import com.jme3.gde.core.sceneexplorer.nodes.gizmo.DirectionalLightGizmoInterface;
import com.jme3.gde.scenecomposer.SceneComposerToolController;
import com.jme3.gde.scenecomposer.SceneComposerTopComponent;
import com.jme3.gde.scenecomposer.gizmo.NodeCallback;
import com.jme3.light.DirectionalLight;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

/**
 * Handles the DirectionalLight's Gizmo.
 * 
 * Note: There are actually three classes involved:
 * The DirectionalLight (jme class) is not added to the gizmo.
 * The Gizmo is a seperate node, which just passes it's transforms to
 * the lights transform (powered by NodeCallback) (but the light obviously has
 * another parent, because that affects lighting).
 * 
 * The JmeDirectionalLight / JmeLight is responsible for making the Light appear
 * in SceneExplorer as node and it handles the properties.
 * 
 * @author dokthar
 */
public class DirectionalLightGizmo extends NodeCallback implements DirectionalLightGizmoInterface {
    private final Vector3f initialDirection;
    private final JmeDirectionalLight jmeLight;
    private final DirectionalLight light;

    public DirectionalLightGizmo(JmeDirectionalLight jmelight) {
        super("directional light gizmo", true, true, false);
        jmeLight = jmelight;
        light = jmeLight.getLookup().lookup(DirectionalLight.class);
        initialDirection = light.getDirection().clone();
    }

    @Override
    public void onTranslation(Vector3f oldTranslation, Vector3f newTranslation) {
    }

    @Override
    public void onResize(Vector3f oldScale, Vector3f newScale) {
    }

    @Override
    public void onRotation(Quaternion oldRotation, Quaternion newRotation) {
        light.setDirection(newRotation.mult(initialDirection));
        jmeLight.setValue("direction", light.getDirection());
    }
    
    @Override
    public void onSetDirection(Vector3f direction) {
        /*
          Commented out on 17/01/2018 by MeFisto94
          Reason: This method and interface have been introduced by me in order
          to fix a bug. Unfortunately I did not see the real root cause back then.
          I've now fixed the actual cause, but this block of code will stay, because
          it's what we'll ultimatively refactor to: Event-Driven Property -> Gizmos.
          Currently we have stupid Controls checking the Direction every frame and
          adjusting the gizmos if they changed...
        
        // We cannot set the light direction directly, we have to adjust the
        // rotation as well (which in turn calls onRotation to set the direction)
         
        // Black Quaternion Magic taken from https://stackoverflow.com/a/11741520
        float k_cos_theta = initialDirection.dot(direction);
        float k = FastMath.sqrt(initialDirection.lengthSquared() * direction.lengthSquared());

        if (FastMath.approximateEquals(k_cos_theta / k, 1)) {
            setLocalRotation(Quaternion.IDENTITY);
        } else if (FastMath.approximateEquals(k_cos_theta / k, -1f)) {
            // 180 degree rotation around any orthogonal vector
            Vector3f anyOrthogonal = initialDirection.cross(Vector3f.UNIT_Y).normalize();
            setLocalRotation(new Quaternion(anyOrthogonal.x, anyOrthogonal.y, anyOrthogonal.z, 0));
        } else {
            Vector3f xyz = initialDirection.cross(direction);
            setLocalRotation(new Quaternion(xyz.x, xyz.y, xyz.z, k_cos_theta + k));
        }
        
        SceneComposerToolController sctc = SceneComposerTopComponent.findInstance().getToolController();
        if (sctc.getSelectedSpatial() != null) {
           sctc.selectedSpatialTransformed();
        }*/
    }

    private final BoundingSphere bv = new BoundingSphere(10f, getWorldTranslation());

    @Override
    public BoundingVolume getWorldBound() {
        return bv;
    }

}
