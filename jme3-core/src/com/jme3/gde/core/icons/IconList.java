/*
 * Copyright (c) 2003-2022 jMonkeyEngine
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
package com.jme3.gde.core.icons;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.openide.util.ImageUtilities;

/**
 * Svg icons from game-icons.net. Icon names are fairly (but not completely) 
 * consistent with names on the web.
 * Authors: https://game-icons.net/about.html#authors
 * 
 * 
 * @author normenhansen
 */
public class IconList {
    
    public static final String ICONS_PATH = "com/jme3/gde/core/icons/";
    public static final String JAIME = ICONS_PATH + "jaime_silhouette.svg";
    public static final String CARDBOARD_BOX = ICONS_PATH + "cardboard-box.svg";
    public static final String CARDBOARD_BOX_CLOSED = ICONS_PATH + "cardboard-box-closed.svg";
    public static final String SOUND = ICONS_PATH + "speaker.svg";
    public static final String MATERIAL = ICONS_PATH + "palette.svg";
    public static final String FILTER = ICONS_PATH + "ice-iris.svg";
    public static final String FONT = ICONS_PATH + "font.svg";
    public static final String LIGHT_BULB = ICONS_PATH + "light-bulb.svg";
    public static final String LIGHT_BULB_YELLOW = ICONS_PATH + "light-bulb-yellow.svg";
    public static final String LIGHT_BULB_OFF = ICONS_PATH + "light-bulb-off.svg";
    public static final String CONFIRMED = ICONS_PATH + "confirmed.svg";
    public static final String WARNING = ICONS_PATH + "interdiction.svg";
    public static final String ERROR = ICONS_PATH + "hazard-sign.svg";
    public static final String PHYSICS_WHEEL = ICONS_PATH + "cog.svg";
    public static final String ANIM_CONTROL = ICONS_PATH + "film-spool.svg";
    public static final String ANIM = ICONS_PATH + "film-strip.svg";
    public static final String SKELETON = ICONS_PATH + "skeleton.svg";
    public static final String PLAY = ICONS_PATH + "play-button.svg";
    public static final String PICTURE = ICONS_PATH + "mona-lisa.svg";
    public static final String INFO = ICONS_PATH + "info.svg";
    public static final String INFO_GREEN = ICONS_PATH + "info_green.svg";
    public static final String EMITTER = ICONS_PATH + "bright-fireworks.svg";
    public static final String PLAYER = ICONS_PATH + "character.svg";
    public static final String GHOST_CONTROL = ICONS_PATH + "invisible.svg";
    public static final String QUESTION = ICONS_PATH + "uncertainty.svg";
    public static final String NODE = ICONS_PATH + "family-tree.svg";
    public static final String IMPORTANT = ICONS_PATH + "files.svg";
    public static final String BONE = ICONS_PATH + "bone.svg";
    public static final String TRACK = ICONS_PATH + "railway.svg";
    public static final String MESH = ICONS_PATH + "wireframe-globe.svg";
    public static final String GEOMETRY = ICONS_PATH + "cube.svg";
    public static final String VEHICLE = ICONS_PATH + "race-car.svg";
    public static final String BOX_COLOR = ICONS_PATH + "cube-green.svg";
    public static final String WIRE_MESH = ICONS_PATH + "wire-mesh.svg";
    public static final String BONE_TRACK = ICONS_PATH + "bone-track.svg";
    public static final String AUDIO_TRACK = ICONS_PATH + "audio-track.svg";
    public static final String EFFECT_TRACK = ICONS_PATH + "effect-track.svg";
    public static final String LINK = ICONS_PATH + "sideswipe.svg";
    public static final String EYE = ICONS_PATH + "eye.svg";
    public static final String EYE_GREEN = ICONS_PATH + "eye-green.svg";
    public static final String EYE_OFF = ICONS_PATH + "sight-disabled.svg";
    public static final String EYE_OFF_GREEN = ICONS_PATH + "sight-disabled-green.svg";
    public static final String MOTION_EVENT = ICONS_PATH + "run.svg";
    public static final String TERRAIN = ICONS_PATH + "terrain.svg";
    public static final String PBR_SKY = ICONS_PATH + "sun-cloud.svg";
    public static final String PBR_ENV = ICONS_PATH + "confirmed_large.svg";
    public static final String NORMAL_VIEW = ICONS_PATH + "normal_view.svg";
    public static final String JME_LOGO = ICONS_PATH + "jme_logo.svg";
    
    // 16x16
    
    public static final ImageIcon jmeLogo =
            ImageUtilities.loadImageIcon(JME_LOGO, false);
    public static final ImageIcon asset =
            ImageUtilities.loadImageIcon(CARDBOARD_BOX, false);
    public static final ImageIcon model =
            ImageUtilities.loadImageIcon(CARDBOARD_BOX_CLOSED, false);
    public static final ImageIcon sound =
            ImageUtilities.loadImageIcon(SOUND, false);
    public static final ImageIcon material =
            ImageUtilities.loadImageIcon(MATERIAL, false);
    public static final ImageIcon matDef =
            ImageUtilities.loadImageIcon(JME_LOGO, false);
    public static final ImageIcon font =
            ImageUtilities.loadImageIcon(FONT, false);
    public static final ImageIcon filter =
            ImageUtilities.loadImageIcon(FILTER, false);
    public static final ImageIcon texture =
            ImageUtilities.loadImageIcon(PICTURE, false);
    public static final ImageIcon orthoMode =
            ImageUtilities.loadImageIcon("com/jme3/gde/core/icons/ortho.png", false);
    public static final ImageIcon perspMode =
            ImageUtilities.loadImageIcon("com/jme3/gde/core/icons/persp.png", false);
    public static final ImageIcon userMode =
            ImageUtilities.loadImageIcon("com/jme3/gde/core/icons/user.png", false);
    public static final ImageIcon bottomView =
            ImageUtilities.loadImageIcon("com/jme3/gde/core/icons/bottom.png", false);
    public static final ImageIcon backView =
            ImageUtilities.loadImageIcon("com/jme3/gde/core/icons/back.png", false);
    public static final ImageIcon topView =
            ImageUtilities.loadImageIcon("com/jme3/gde/core/icons/top.png", false);
    public static final ImageIcon leftView =
            ImageUtilities.loadImageIcon("com/jme3/gde/core/icons/left.png", false);
    public static final ImageIcon rightView =
            ImageUtilities.loadImageIcon("com/jme3/gde/core/icons/right.png", false);
    public static final ImageIcon frontView =
            ImageUtilities.loadImageIcon("com/jme3/gde/core/icons/front.png", false);
    public static final ImageIcon audioTrack =
            ImageUtilities.loadImageIcon(AUDIO_TRACK, false);
    public static final ImageIcon effectTrack =
            ImageUtilities.loadImageIcon(EFFECT_TRACK, false);
    public static final ImageIcon boneTrack =
            ImageUtilities.loadImageIcon(BONE_TRACK, false);
    public static final ImageIcon track =
            ImageUtilities.loadImageIcon(TRACK, false);
    public static final ImageIcon lightOff =
            ImageUtilities.loadImageIcon(LIGHT_BULB_OFF, false);
    public static final ImageIcon lightOn =
            ImageUtilities.loadImageIcon(LIGHT_BULB, false);
    public static final ImageIcon eyeOpen =
            ImageUtilities.loadImageIcon(EYE, false);
    public static final ImageIcon eyeCrossed =
            ImageUtilities.loadImageIcon(EYE_OFF, false);
    public static final ImageIcon info =
            ImageUtilities.loadImageIcon(INFO, false);
    public static final ImageIcon player =
            ImageUtilities.loadImageIcon(PLAYER, false);
    public static final ImageIcon important =
            ImageUtilities.loadImageIcon(IMPORTANT, false);
    public static final ImageIcon animControl =
            ImageUtilities.loadImageIcon(ANIM_CONTROL, false);
    public static final ImageIcon animation =
            ImageUtilities.loadImageIcon(ANIM, false);
    public static final ImageIcon animationPlay =
            ImageUtilities.loadImageIcon(PLAY, false);
    public static final ImageIcon link =
            ImageUtilities.loadImageIcon(LINK, false);
    public static final ImageIcon bone =
            ImageUtilities.loadImageIcon(BONE, false);
    public static final ImageIcon wheel =
            ImageUtilities.loadImageIcon(PHYSICS_WHEEL, false);
    public static final ImageIcon geometry =
            ImageUtilities.loadImageIcon(GEOMETRY, false);
    public static final ImageIcon ghostControl =
            ImageUtilities.loadImageIcon(GHOST_CONTROL, false);
    public static final ImageIcon mesh =
            ImageUtilities.loadImageIcon(MESH, false);
    public static final ImageIcon motionEvent =
            ImageUtilities.loadImageIcon(MOTION_EVENT, false);
    public static final ImageIcon node =
            ImageUtilities.loadImageIcon(NODE, false);
    public static final ImageIcon emitter =
            ImageUtilities.loadImageIcon(EMITTER, false);
    public static final ImageIcon physicsControl =
            ImageUtilities.loadImageIcon(PHYSICS_WHEEL, false);
    public static final ImageIcon skeletonControl =
            ImageUtilities.loadImageIcon(SKELETON, false);
    public static final ImageIcon terrain =
            ImageUtilities.loadImageIcon(TERRAIN, false);
    public static final ImageIcon vehicle =
            ImageUtilities.loadImageIcon(VEHICLE, false);
    public static final ImageIcon chimpConfused =
            ImageUtilities.loadImageIcon(QUESTION, false);
    public static final ImageIcon exception =
            ImageUtilities.loadImageIcon(ERROR, false);
    public static final ImageIcon chimpNogood =
            ImageUtilities.loadImageIcon(ERROR, false);
    public static final ImageIcon chimpSad =
            ImageUtilities.loadImageIcon(WARNING, false);
    public static final ImageIcon chimpSmile =
            ImageUtilities.loadImageIcon(CONFIRMED, false);
    public static final ImageIcon jaime =
            ImageUtilities.loadImageIcon(JAIME, false);
    public static final ImageIcon lightYellow =
            ImageUtilities.loadImageIcon(LIGHT_BULB_YELLOW, false);
    
    // SceneViewer 32x32
   
    public static final ImageIcon colorBox =
            ImageUtilities.loadImageIcon(BOX_COLOR, false);
    public static final ImageIcon wireBox =
            ImageUtilities.loadImageIcon(WIRE_MESH, false);
    public static final ImageIcon light =
            ImageUtilities.loadImageIcon(LIGHT_BULB, false);
    public static final ImageIcon eyeOff =
            ImageUtilities.loadImageIcon(EYE_OFF_GREEN, false);
    public static final ImageIcon eyeGreen =
            ImageUtilities.loadImageIcon(EYE_GREEN, false);
    public static final ImageIcon enablePbrEnvironment =
            ImageUtilities.loadImageIcon(PBR_ENV, false);
    public static final ImageIcon enablePbrSky =
            ImageUtilities.loadImageIcon(PBR_SKY, false);
    public static final ImageIcon sceneInfo =
            ImageUtilities.loadImageIcon(INFO_GREEN, false);
    public static final ImageIcon normalView =
            ImageUtilities.loadImageIcon(NORMAL_VIEW, false);
}
