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

package com.jme3.gde.materialdefinition.editor.util;

import com.jme3.asset.AssetManager;
import com.jme3.asset.ShaderNodeDefinitionKey;
import com.jme3.gde.core.editor.nodes.Connection;
import com.jme3.gde.materialdefinition.editor.InOut;
import com.jme3.gde.materialdefinition.editor.ShaderOutBusPanel;
import com.jme3.gde.materialdefinition.fileStructure.ShaderNodeBlock;
import com.jme3.gde.materialdefinition.fileStructure.TechniqueBlock;
import com.jme3.gde.materialdefinition.fileStructure.leaves.InputMappingBlock;
import com.jme3.gde.materialdefinition.fileStructure.leaves.OutputMappingBlock;
import com.jme3.gde.materialdefinition.fileStructure.leaves.WorldParamBlock;
import com.jme3.shader.ShaderNodeDefinition;
import com.jme3.shader.ShaderUtils;
import java.util.List;

/**
 *
 * @author rickard
 */
public class MatDefEditorUtil {
    
    public static void notifyAddTechnique(AssetManager assetManager, TechniqueBlock tech) {
        String path = "Common/MatDefs/ShaderNodes/Common/Unshaded.j3sn";
        ShaderNodeDefinitionKey key = new ShaderNodeDefinitionKey(path);
        List<ShaderNodeDefinition> defs = assetManager.loadAsset(key);
        ShaderNodeBlock node = new ShaderNodeBlock(defs.get(0), path);
        tech.addFragmentShaderNode(node);
        node.addOutputMapping(new OutputMappingBlock("color", "color", "", "", "Global", "Unshaded", null));

        path = "Common/MatDefs/ShaderNodes/Common/CommonVert.j3sn";
        key = new ShaderNodeDefinitionKey(path);
        defs = assetManager.loadAsset(key);
        node = new ShaderNodeBlock(defs.get(0), path);
        tech.addVertexShaderNode(node);

        node.addInputMapping(new InputMappingBlock("worldViewProjectionMatrix", "WorldViewProjectionMatrix", "", "", "CommonVert", "WorldParam", null));
        node.addInputMapping(new InputMappingBlock("modelPosition", "position", "", "xyz", "CommonVert", "Global", null));

        node.addOutputMapping(new OutputMappingBlock("position", "projPosition", "", "", "Global", "CommonVert", null));

        WorldParamBlock param = new WorldParamBlock("WorldViewProjectionMatrix");
        tech.addWorldParam(param);
    }

    public static void makeMapping(Connection conn, String currentTechniqueName) {
        InOut startNode = (InOut) conn.getStart().getNode();
        InOut endNode = (InOut) conn.getEnd().getNode();
        String leftVarName = conn.getEnd().getText();
        String rightVarName = conn.getStart().getText();
        String leftVarSwizzle = null;
        String rightVarSwizzle = null;

        int endCard = ShaderUtils.getCardinality(conn.getEnd().getType(), "");
        int startCard = ShaderUtils.getCardinality(conn.getStart().getType(), "");
        String swizzle = "xyzw";
        if (startCard > endCard) {
            rightVarSwizzle = swizzle.substring(0, endCard);
        } else if (endCard > startCard) {
            leftVarSwizzle = swizzle.substring(0, startCard);
        }

        if (endNode instanceof ShaderOutBusPanel) {
            OutputMappingBlock mapping = new OutputMappingBlock(leftVarName, rightVarName, leftVarSwizzle, rightVarSwizzle, endNode.getName(), startNode.getName(), null);
            startNode.addOutputMapping(mapping);
            conn.makeKey(mapping, currentTechniqueName);
        } else {
            InputMappingBlock mapping = new InputMappingBlock(leftVarName, rightVarName, leftVarSwizzle, rightVarSwizzle, endNode.getName(), startNode.getName(), null);
            endNode.addInputMapping(mapping);
            conn.makeKey(mapping, currentTechniqueName);
        }
    }
}
