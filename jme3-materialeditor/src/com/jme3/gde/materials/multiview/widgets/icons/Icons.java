/*
 *  Copyright (c) 2009-2023 jMonkeyEngine
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
package com.jme3.gde.materials.multiview.widgets.icons;

import javax.swing.ImageIcon;
import org.openide.util.ImageUtilities;

/**
 * Lists all icons used by the Material Editor
 * @author rickard
 */
public class Icons {
    
    public static final String ICONS_PATH = "com/jme3/gde/materials/multiview/widgets/icons/";
    public static final String TEXTURE_REMOVE = ICONS_PATH + "remove_texture.svg";
    public static final String CUBE_OFF = ICONS_PATH + "cube.svg";
    public static final String SPHERE_OFF = ICONS_PATH + "sphere.svg";
    public static final String PLANE_OFF = ICONS_PATH + "plane.svg";
    public static final String TEAPOT_OFF = ICONS_PATH + "teapot-off.svg";
    public static final String CUBE_ON = ICONS_PATH + "cube-on.svg";
    public static final String SPHERE_ON = ICONS_PATH + "sphere-on.svg";
    public static final String TEAPOT_ON = ICONS_PATH + "teapot-on.svg";
    public static final String PLANE_ON = ICONS_PATH + "plane-on.svg";
    public static final String LIGHT_ON = ICONS_PATH + "light-bulb-on.svg";
    public static final String LIGHT_OFF = ICONS_PATH + "light-bulb-off.svg";
    
    public static final ImageIcon textureRemove =
            ImageUtilities.loadImageIcon(TEXTURE_REMOVE, false);
    public static final ImageIcon cube =
            ImageUtilities.loadImageIcon(CUBE_OFF, false);
    public static final ImageIcon sphere =
            ImageUtilities.loadImageIcon(SPHERE_OFF, false);
    public static final ImageIcon plane =
            ImageUtilities.loadImageIcon(PLANE_OFF, false);
    public static final ImageIcon teapotOff =
            ImageUtilities.loadImageIcon(TEAPOT_OFF, false);
    public static final ImageIcon cubeOn =
            ImageUtilities.loadImageIcon(CUBE_ON, false);
    public static final ImageIcon sphereOn =
            ImageUtilities.loadImageIcon(SPHERE_ON, false);
    public static final ImageIcon planeOn =
            ImageUtilities.loadImageIcon(PLANE_ON, false);
    public static final ImageIcon teapotOn =
            ImageUtilities.loadImageIcon(TEAPOT_ON, false);
    public static final ImageIcon lightOn =
            ImageUtilities.loadImageIcon(LIGHT_ON, false);
    public static final ImageIcon lightOff =
            ImageUtilities.loadImageIcon(LIGHT_OFF, false);
}
