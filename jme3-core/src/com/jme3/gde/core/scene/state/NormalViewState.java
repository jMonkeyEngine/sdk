/*
 *  Copyright (c) 2009-2022 jMonkeyEngine
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
package com.jme3.gde.core.scene.state;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.gde.core.scene.SceneApplication;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.TangentBinormalGenerator;
import java.util.ArrayList;
import java.util.List;
import jme3tools.optimize.GeometryBatchFactory;

/**
 * Generates a set of geometries for debugging normals. Does not support moving
 * or animated meshes. Implementation ref:
 * https://hub.jmonkeyengine.org/t/debug-graphics-show-normals-as-lines/35058/3
 *
 * @author rickard
 */
public final class NormalViewState extends BaseAppState {

    private final Node debugNode = new Node("Normals debug");
    private final float normalLength = 0.2f;

    @Override
    protected void initialize(final Application app) {
        // not used
    }

    @Override
    protected void cleanup(final Application app) {
        // not used
    }

    @Override
    protected void onEnable() {
        final SceneApplication app = (SceneApplication) getApplication();
        final List<Geometry> geometries = new ArrayList<>();
        GeometryBatchFactory.gatherGeoms(app.getRootNode(), geometries);
        final Material debugMat = app.getAssetManager().loadMaterial(
                "Common/Materials/VertexColor.j3m");

        for (final Geometry geometry : geometries) {
            if(geometry.getMesh().getBuffer(Type.Normal) == null) {
                continue;
            }
            
            final Geometry debug = new Geometry(geometry.getName(),
                    TangentBinormalGenerator.genNormalLines(geometry.getMesh(),
                            normalLength));
            debug.setMaterial(debugMat);
            debug.setCullHint(Spatial.CullHint.Never);
            debug.setLocalTransform(geometry.getWorldTransform());
            debugNode.attachChild(debug);
        }
        app.getRootNode().attachChild(debugNode);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        final Node rootNode = ((SceneApplication) getApplication()).getRootNode();
        for (final Spatial debug : debugNode.getChildren()) {
            final Spatial original = rootNode.getChild(debug.getName());
            if (original == null) {
                debug.removeFromParent();
                continue;
            }
            debug.setLocalTransform(original.getWorldTransform());
        }
    }

    @Override
    protected void onDisable() {
        debugNode.removeFromParent();
        debugNode.detachAllChildren();
    }

}
