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

import com.jme3.light.SpotLight;
import com.jme3.math.FastMath;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.control.AbstractControl;

/**
 * A utility control to update the radius of the lightcone gizmo as the radius
 * of a tracked cone radius changes. This is used to adjust gizmos as the
 * user is changing the light parameters using the Properties Editor.
 * 
 * @author dokthar
 */
public class LightConeRadiusUpdate extends AbstractControl {

    private final SpotLight light;
    private float lastInnerAngle = -1;
    private float lastOuterAngle = -1;
    private float lastRange = -1;
    private final Geometry inner, outer;

    /**
     * 
     * @param l The Spotlight to track
     * @param inner The Geometry representing the inner angle
     * @param outer The Geometry representing the outer angle
     */
    public LightConeRadiusUpdate(SpotLight l, Geometry inner, Geometry outer) {
        this.light = l;
        this.inner = inner;
        this.outer = outer;

        light.getSpotInnerAngle();
        light.getSpotOuterAngle();
    }

    private static float oppositeSide(float angle, float adjacent) {
        // tan(angle) = opposite / adjacent
        return FastMath.tan(angle) * adjacent;
    }

    @Override
    protected void controlUpdate(float f) {
        float a = light.getSpotInnerAngle();
        if (a != lastInnerAngle) {
            lastInnerAngle = a;
            float r = oppositeSide(lastInnerAngle, light.getSpotRange());
            inner.setLocalScale(r, r, r);
        }

        a = light.getSpotOuterAngle();
        if (a != lastOuterAngle) {
            lastOuterAngle = a;
            float r = oppositeSide(lastOuterAngle, light.getSpotRange());
            outer.setLocalScale(r, r, r);
            if (getSpatial() != null) {
                spatial.setLocalScale(r, spatial.getLocalScale().y, 1);
            }
        }

        a = light.getSpotRange();
        if (a != lastRange) {
            lastRange = a;
        }
        if (getSpatial() != null) {
            spatial.setLocalScale(spatial.getLocalScale().x, light.getSpotRange(), 1);
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

}
