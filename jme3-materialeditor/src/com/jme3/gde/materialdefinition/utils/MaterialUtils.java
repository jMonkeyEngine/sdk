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
package com.jme3.gde.materialdefinition.utils;

import com.jme3.asset.ShaderNodeDefinitionKey;
import com.jme3.gde.core.assets.ProjectAssetManager;
import com.jme3.gde.materialdefinition.fileStructure.ShaderNodeBlock;
import com.jme3.gde.materialdefinition.fileStructure.leaves.InputMappingBlock;
import com.jme3.gde.materialdefinition.fileStructure.leaves.MappingBlock;
import com.jme3.gde.materialdefinition.fileStructure.leaves.MatParamBlock;
import com.jme3.shader.ShaderNodeDefinition;
import com.jme3.shader.ShaderNodeVariable;
import com.jme3.shader.ShaderUtils;
import com.jme3.shader.UniformBinding;
import com.jme3.shader.VarType;
import java.util.List;

/**
 *
 * @author Nehon
 */
public class MaterialUtils {
    
    public static String makeKey(MappingBlock mapping, String techName) {
        String rightName = mapping.getRightVar();
        String leftName = mapping.getLeftVar();
        String leftSwizzle = mapping.getLeftVarSwizzle() != null ? "." + mapping.getLeftVarSwizzle() : "";
        String rightSwizzle = mapping.getRightVarSwizzle() != null ? "." + mapping.getRightVarSwizzle() : "";
        return techName + "/" + mapping.getLeftNameSpace() + "." + leftName + leftSwizzle + "=" + mapping.getRightNameSpace() + "." + rightName + rightSwizzle;
    }

    /**
     * trims a line and removes comments
     *
     * @param line
     * @return
     */
    public static String trimLine(String line) {
        int idx = line.indexOf("//");
        if (idx != -1) {
            line = line.substring(0, idx);
        }
        return line.trim();
    }

    /**
     * trims a line and removes everything behind colon
     *
     * @param line
     * @return
     */
    public static String trimName(String line) {        
        int idx = line.indexOf("-");
        if(idx!=-1){             
             line= line.substring(0, idx);
        }
        line = trimLine(line);
        idx = line.indexOf("(");
        if (idx == -1) {
            idx = line.indexOf(":");
        }
        if (idx != -1) {
            line = line.substring(0, idx);
        }
        return line.trim();
    }
    
    public static ShaderNodeDefinition loadShaderNodeDefinition(ShaderNodeBlock shaderNode, ProjectAssetManager manager) {
        return loadShaderNodeDefinition(shaderNode.getDefinition().getPath(), shaderNode.getDefinition().getName(), manager);
    }
    
    public static ShaderNodeDefinition loadShaderNodeDefinition(String path, String name, ProjectAssetManager manager) {
        ShaderNodeDefinitionKey k = new ShaderNodeDefinitionKey(path);
        k.setLoadDocumentation(true);
        List<ShaderNodeDefinition> defs = manager.loadAsset(k);
        for (ShaderNodeDefinition shaderNodeDefinition : defs) {
            if (shaderNodeDefinition.getName().equals(name)) {
                return shaderNodeDefinition;
            }
        }
        return null;
    }

    /**
     * updates the type of the right variable of a mapping from the type of the
     * left variable
     *
     * @param mapping the mapping to consider
     */
    public static String guessType(InputMappingBlock mapping, ShaderNodeVariable left) {
        String type = left.getType();
        int card = ShaderUtils.getCardinality(type, mapping.getRightVarSwizzle() == null ? "" : mapping.getRightVarSwizzle());
        if (card > 0) {
            if (card == 1) {
                type = "float";
            } else {
                type = "vec" + card;
            }
        }
        return type;
    }
    
    public static ShaderNodeVariable getVar(List<ShaderNodeVariable> ins, String name) {
        for (ShaderNodeVariable shaderNodeVariable : ins) {
            if (shaderNodeVariable.getName().equals(name)) {
                return shaderNodeVariable;
            }
        }
        return null;
    }
    
    public static String getMatParamType(MatParamBlock param) {
        String type = param.getType();        
        if (type.equals("Color")) {
            type = "Vector4";
        }
        return VarType.valueOf(type).getGlslType();
    }
    
    public static String getWorldParamType(String name) {
        return UniformBinding.valueOf(name).getGlslType();
    }
    
    public static boolean contains(List<ShaderNodeVariable> vars, ShaderNodeVariable var) {
        for (ShaderNodeVariable shaderNodeVariable : vars) {
            if (shaderNodeVariable.getName().equals(var.getName()) && shaderNodeVariable.getNameSpace().equals(var.getNameSpace())) {
                return true;
            }
        }
        return false;
    }
}
