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
package com.jme3.gde.materialdefinition.editor;

import com.jme3.gde.core.editor.nodes.ConnectionEndpoint;
import com.jme3.gde.core.editor.icons.Icons;
import com.jme3.shader.Shader;
import com.jme3.shader.ShaderUtils;

/**
 * This is the input/ouput connector of a ShaderNode.
 * @author MeFisto94
 */
public class ShaderNodeDot extends ConnectionEndpoint {
    protected Shader.ShaderType shaderType;
    
        
    public void setShaderType(Shader.ShaderType shaderType){
         this.shaderType = shaderType;
    }
    
    @Override
    public boolean canConnect(ConnectionEndpoint pair) {
        // One is at least outBus but the shaderType doesn't match
        if (pair != null && pair instanceof ShaderNodeDot &&
                (pair.getNode() instanceof ShaderOutBusPanel || 
                    node instanceof ShaderOutBusPanel) && 
                    shaderType != ((ShaderNodeDot)pair).shaderType) {
            setIcon(Icons.imgOrange);
            return false;
        }
        
        // cannot connect to: nothing || input panels ||
        if (pair == null || paramType == ParamType.Input) {
            setIcon(Icons.imgOrange);
            return false;
        } else if (allowConnection(pair)) {
            setIcon(Icons.imgGreen);
            return true;
        } else {
            setIcon(Icons.imgRed);
            return false;
        }
    }
    
    @Override
    protected boolean allowConnection(ConnectionEndpoint pair) {
        return matches(pair.getType(), getType()) && (pair.getParamType() != getParamType()
            || pair.getParamType() == ParamType.Both
            || getParamType() == ParamType.Both)
            || ShaderUtils.isSwizzlable(pair.getType())
            && ShaderUtils.isSwizzlable(getType());
    }
    
    /**
     * This method checks if any of the both strings contain the same substring
     * seperated by \|
     * @param type1 The Typelist 1
     * @param type2 The Typelist 2
     * @return whether they contain the same substring
     */
    private boolean matches(String type1, String type2) {
        String[] s1 = type1.split("\\|");
        String[] s2 = type2.split("\\|");
        for (String string : s1) {
            for (String string1 : s2) {
                if (string.equals(string1)) {
                    return true;
                }
            }
        }
        return false;
    }
}
