/*
 *  Copyright (c) 2009-2016 jMonkeyEngine
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
package com.jme3.gde.core.sceneexplorer.nodes;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.objects.PhysicsCharacter;
import com.jme3.gde.core.icons.IconList;
import com.jme3.math.Vector3f;
import java.awt.Image;
import org.openide.loaders.DataObject;
import org.openide.nodes.Sheet;

/**
 *
 * @author normenhansen
 */
@org.openide.util.lookup.ServiceProvider(service=SceneExplorerNode.class)
@SuppressWarnings({"unchecked", "rawtypes"})
public class JmeCharacterControl extends JmeControl {

    private static Image smallImage = IconList.player.getImage();
    private CharacterControl geom;

    public JmeCharacterControl() {
    }

    public JmeCharacterControl(CharacterControl spatial, DataObject dataObject) {
        super(dataObject);
        getLookupContents().add(this);
        getLookupContents().add(spatial);
        this.geom = spatial;
        control = spatial;
        setName("CharacterControl");
    }

    @Override
    public Image getIcon(int type) {
        return smallImage;
    }

    @Override
    public Image getOpenedIcon(int type) {
        return smallImage;
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = super.createSheet();
        Sheet.Set set = Sheet.createPropertiesSet();
        set.setDisplayName("CharacterControl");
        set.setName(CharacterControl.class.getName());
        CharacterControl obj = geom;//getLookup().lookup(Spatial.class);
        if (obj == null) {
            return sheet;
        }

        set.put(makeProperty(obj, Vector3f.class, "getPhysicsLocation", "setPhysicsLocation", "Physics Location"));

        PhysicsCharacter character = obj.getCharacter();
        
        set.put(makeEmbedProperty(character, PhysicsCharacter.class, CollisionShape.class, "getCollisionShape", "setCollisionShape", "Collision Shape"));
        set.put(makeEmbedProperty(character, PhysicsCharacter.class, int.class, "getCollisionGroup", "setCollisionGroup", "Collision Group"));
        set.put(makeEmbedProperty(character, PhysicsCharacter.class, int.class, "getCollideWithGroups", "setCollideWithGroups", "Collide With Groups"));
        
        set.put(makeEmbedProperty(character, PhysicsCharacter.class, float.class, "getFallSpeed", "setFallSpeed", "Fall Speed"));
        set.put(makeEmbedProperty(character, PhysicsCharacter.class, float.class, "getJumpSpeed", "setJumpSpeed", "Jump Speed"));
        set.put(makeEmbedProperty(character, PhysicsCharacter.class, float.class, "getMaxSlope", "setMaxSlope", "Max Slope"));

        sheet.put(set);
        return sheet;

    }

    @Override
    public Class getExplorerObjectClass() {
        return CharacterControl.class;
    }

    @Override
    public Class getExplorerNodeClass() {
        return JmeCharacterControl.class;
    }

    @Override
    public org.openide.nodes.Node[] createNodes(Object key, DataObject key2, boolean cookie) {
        return new org.openide.nodes.Node[]{new JmeCharacterControl((CharacterControl) key, key2).setReadOnly(cookie)};
    }
}
