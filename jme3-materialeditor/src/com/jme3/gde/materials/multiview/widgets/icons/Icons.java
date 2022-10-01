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
    public static final String TEXTURE_ADD = ICONS_PATH + "add_texture.svg";
    public static final String TEXTURE_REMOVE = ICONS_PATH + "remove_texture.svg";
    public static final String CUBE = ICONS_PATH + "cube.svg";
    public static final String SPHERE = ICONS_PATH + "sphere.svg";
    public static final String PLANE = ICONS_PATH + "plane.svg";
    public static final String SELECT_COLOR = ICONS_PATH + "large-paint-brush.svg";
    
    public static final ImageIcon textureAdd =
            ImageUtilities.loadImageIcon(TEXTURE_ADD, false);
    public static final ImageIcon textureRemove =
            ImageUtilities.loadImageIcon(TEXTURE_REMOVE, false);
    public static final ImageIcon cube =
            ImageUtilities.loadImageIcon(CUBE, false);
    public static final ImageIcon sphere =
            ImageUtilities.loadImageIcon(SPHERE, false);
    public static final ImageIcon plane =
            ImageUtilities.loadImageIcon(PLANE, false);
    public static final ImageIcon selectColor =
            ImageUtilities.loadImageIcon(SELECT_COLOR, false);
}
