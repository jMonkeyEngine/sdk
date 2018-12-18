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
import com.jme3.math.Vector3f;
import com.jme3.scene.control.BillboardControl;
import java.lang.reflect.Method;

/**
 * Updates the marker's position whenever the light has moved. It is also a
 * BillboardControl, so this marker always faces the camera.
 * 
 * Basically this is the combination of {@link LightPositionUpdate} and {@link 
 * BillboardControl}, but without u
 */
public class LightGizmoControl extends BillboardControl {

    private final Vector3f lastPos = new Vector3f();
    private Vector3f lightPos;
    private Method getPosition = null;
    private Light light;

    LightGizmoControl(Light light) {
        super();
        this.light = light;
        
        try {
            getPosition = light.getClass().getMethod("getPosition");
        } catch (NoSuchMethodException ex) {
            ExceptionUtils.caughtException(ex, "The LightPositionUpdate "
                + "Control has been added for a light which doesn't even "
                + "have a position. This means someone has seriously "
                + "fucked up. I just hope it's not me ;)");
        }
    }
    
    protected void updateLightPosition() {
        try {
            lightPos = (Vector3f) getPosition.invoke(light);
        } catch (Exception ex) {
            ExceptionUtils.caughtException(ex);
        }
    }

    @Override
    protected void controlUpdate(float f) {
        super.controlUpdate(f);
        updateLightPosition();
        
        if (!lightPos.equals(lastPos)) {
            lastPos.set(lightPos);
            getSpatial().getParent().worldToLocal(lastPos, lastPos);
            ((NodeCallback)getSpatial()).silentLocalTranslation(lastPos);
        }
    }
}
