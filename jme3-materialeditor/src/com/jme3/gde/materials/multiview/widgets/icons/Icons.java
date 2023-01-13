/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jme3.gde.materials.multiview.widgets.icons;

import javax.swing.ImageIcon;
import org.openide.util.ImageUtilities;

/**
 *
 * @author rickard
 */
public class Icons {
    
    public static final String ICONS_PATH = "com/jme3/gde/materials/multiview/widgets/icons/";
    public static final String TEXTURE_REMOVE = ICONS_PATH + "remove_texture.svg";
    public static final String CUBE_OFF = ICONS_PATH + "cube.svg";
    public static final String SPHERE_OFF = ICONS_PATH + "sphere.svg";
    public static final String PLANE_OFF = ICONS_PATH + "plane.svg";
    public static final String CUBE_ON = ICONS_PATH + "cube-on.svg";
    public static final String SPHERE_ON = ICONS_PATH + "sphere-on.svg";
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
    public static final ImageIcon cubeOn =
            ImageUtilities.loadImageIcon(CUBE_OFF, false);
    public static final ImageIcon sphereOn =
            ImageUtilities.loadImageIcon(SPHERE_OFF, false);
    public static final ImageIcon planeOn =
            ImageUtilities.loadImageIcon(PLANE_OFF, false);
    public static final ImageIcon lightOn =
            ImageUtilities.loadImageIcon(LIGHT_ON, false);
    public static final ImageIcon lightOff =
            ImageUtilities.loadImageIcon(LIGHT_OFF, false);
}
