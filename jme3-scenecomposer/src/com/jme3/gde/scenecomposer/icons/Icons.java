/*
 * Copyright (c) 2009-2022 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme3.gde.scenecomposer.icons;

import javax.swing.ImageIcon;
import org.openide.util.ImageUtilities;

/**
 * 32x32
 * @author rickard
 */
public class Icons {
    
    public static final String ICONS_PATH = "com/jme3/gde/scenecomposer/icons/";
    public static final String SELECT = ICONS_PATH + "arrow-cursor.svg";
    public static final String MOVE = ICONS_PATH + "move.svg";
    public static final String SCALE = ICONS_PATH + "resize.svg";
    public static final String ROTATE = ICONS_PATH + "clockwise-rotation.svg";
    public static final String SNAP_SCENE = ICONS_PATH + "snap_geom.svg";
    public static final String SNAP_GRID = ICONS_PATH + "snap_grid.svg";
    public static final String SELECT_GEOM = ICONS_PATH + "select_geom.svg";
    public static final String SELECT_TERRAIN = ICONS_PATH + "select_terrain.svg";
    public static final String SNAP_CURSOR = ICONS_PATH + "save-arrow.svg";
    public static final String CURSOR_PIN = ICONS_PATH + "position-marker.svg";
    public static final String CAMERA = ICONS_PATH + "film-projector.svg";
    public static final String EYE = ICONS_PATH + "eye-green.svg";
    public static final String PLAY = ICONS_PATH + "play-button.svg";
    public static final String PAUSE = ICONS_PATH + "pause-button.svg";
    
    public static final ImageIcon select = ImageUtilities.loadImageIcon(SELECT, false);
    public static final ImageIcon move = ImageUtilities.loadImageIcon(MOVE, false);
    public static final ImageIcon scale = ImageUtilities.loadImageIcon(SCALE, false);
    public static final ImageIcon rotate = ImageUtilities.loadImageIcon(ROTATE, false);
    public static final ImageIcon snapToScene = ImageUtilities.loadImageIcon(SNAP_SCENE, false);
    public static final ImageIcon snapToGrid = ImageUtilities.loadImageIcon(SNAP_GRID, false);
    public static final ImageIcon selectGeometry = ImageUtilities.loadImageIcon(SELECT_GEOM, false);
    public static final ImageIcon selectTerrain = ImageUtilities.loadImageIcon(SELECT_TERRAIN, false);
    public static final ImageIcon moveToCursor = ImageUtilities.loadImageIcon(SNAP_CURSOR, false);
    public static final ImageIcon cursorPin = ImageUtilities.loadImageIcon(CURSOR_PIN, false);
    public static final ImageIcon camera = ImageUtilities.loadImageIcon(CAMERA, false);
    public static final ImageIcon display = ImageUtilities.loadImageIcon(EYE, false);
    public static final ImageIcon play = ImageUtilities.loadImageIcon(PLAY, false);
    public static final ImageIcon pause = ImageUtilities.loadImageIcon(PAUSE, false);
}
