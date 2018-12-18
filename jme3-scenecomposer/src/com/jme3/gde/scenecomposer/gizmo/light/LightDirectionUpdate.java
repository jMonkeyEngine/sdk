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

import com.jme3.gde.core.errorreport.ExceptionUtils;
import com.jme3.gde.scenecomposer.gizmo.NodeCallback;
import com.jme3.light.Light;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import java.lang.reflect.Method;

/**
 * A utility control to update the direction of an arrow as the direction of
 * a tracked light changes. This is used to adjust gizmos as the
 * user is changing the light direction using the Properties Editor.
 * 
 * @author dokthar
 */
public class LightDirectionUpdate extends AbstractControl {

    private final Light light;
    private final NodeCallback gizmo;
    private Method getDirection = null;

    private final Vector3f lastDir = new Vector3f();
    private final Vector3f initialDir = new Vector3f();
    private Vector3f lightDir;

    public LightDirectionUpdate(Light light, NodeCallback gizmo) {
        this.gizmo = gizmo;
        this.light = light;
        
        try {
            getDirection = light.getClass().getMethod("getDirection");
        } catch (NoSuchMethodException ex) {
            ExceptionUtils.caughtException(ex, "The LightDirectionUpdate "
                + "Control has been added for a light which doesn't even "
                + "have a direction. This means someone has seriously "
                + "fucked up. I just hope it's not me ;)");
        }
        
        refreshLightDirection();
        initialDir.set(lightDir);
    }
    
    protected void refreshLightDirection() {
        try {
            if (getDirection != null) {
                lightDir = (Vector3f) getDirection.invoke(light);
            }
        } catch (Exception ex) {
            ExceptionUtils.caughtException(ex);
        }
    }

    @Override
    protected void controlUpdate(float f) {
        refreshLightDirection();
        if (!lightDir.equals(lastDir)) {
            lastDir.set(lightDir);

            Vector3f axis = Vector3f.UNIT_Y.cross(lastDir);
            float angle = Vector3f.UNIT_Y.angleBetween(lastDir);
            //Quaternion rotation = gizmo.getWorldRotation().inverse().mult(new Quaternion().fromAngleAxis(angle, axis));
            
            if (angle > 1f * FastMath.DEG_TO_RAD) { // Anti Flickering
                Quaternion rotation = new Quaternion().fromAngleAxis(angle, axis);
                gizmo.silentLocalRotation(rotation); /* silent, because otherwise
                the gizmo would call light.setDirection() and update the property */
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

}
