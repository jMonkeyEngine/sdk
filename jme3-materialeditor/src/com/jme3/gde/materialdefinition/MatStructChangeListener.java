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

package com.jme3.gde.materialdefinition;

import com.jme3.gde.materialdefinition.fileStructure.MatDefBlock;
import com.jme3.gde.materialdefinition.fileStructure.ShaderNodeBlock;
import com.jme3.gde.materialdefinition.fileStructure.TechniqueBlock;
import com.jme3.gde.materialdefinition.fileStructure.leaves.InputMappingBlock;
import com.jme3.gde.materialdefinition.fileStructure.leaves.MatParamBlock;
import com.jme3.gde.materialdefinition.fileStructure.leaves.OutputMappingBlock;
import com.jme3.util.blockparser.Statement;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * Listens for structural changes to the MatDef (Broken out from EditableMatDefFile.
 * 
 * @author rickard
 */
class MatStructChangeListener implements PropertyChangeListener {
    
    private final EditableMatDefFile matDefFile;

    MatStructChangeListener(final EditableMatDefFile matDefFile) {
        this.matDefFile = matDefFile;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final TechniqueBlock currentTechnique = matDefFile.getCurrentTechnique();
        if (evt.getSource() instanceof ShaderNodeBlock && evt.getPropertyName().equals("name")) {
            String oldValue = (String) evt.getOldValue();
            String newValue = (String) evt.getNewValue();
            for (ShaderNodeBlock shaderNodeBlock : currentTechnique.getShaderNodes()) {
                List<InputMappingBlock> lin = shaderNodeBlock.getInputs();
                if (lin != null) {
                    for (InputMappingBlock inputMappingBlock : shaderNodeBlock.getInputs()) {
                        if (inputMappingBlock.getLeftNameSpace().equals(oldValue)) {
                            inputMappingBlock.setLeftNameSpace(newValue);
                        }
                        if (inputMappingBlock.getRightNameSpace().equals(oldValue)) {
                            inputMappingBlock.setRightNameSpace(newValue);
                        }
                    }
                }
                List<OutputMappingBlock> l = shaderNodeBlock.getOutputs();
                if (l != null) {
                    for (OutputMappingBlock outputMappingBlock : l) {
                        if (outputMappingBlock.getRightNameSpace().equals(oldValue)) {
                            outputMappingBlock.setRightNameSpace(newValue);
                        }
                    }
                }
            }
        }
        if (evt.getPropertyName().equals(MatDefBlock.REMOVE_MAT_PARAM)) {
            MatParamBlock oldValue = (MatParamBlock) evt.getOldValue();
            for (ShaderNodeBlock shaderNodeBlock : currentTechnique.getShaderNodes()) {
                if (shaderNodeBlock.getCondition() != null && shaderNodeBlock.getCondition().contains(oldValue.getName())) {
                    shaderNodeBlock.setCondition(shaderNodeBlock.getCondition().replaceAll(oldValue.getName(), "").trim());
                }
                List<InputMappingBlock> lin = shaderNodeBlock.getInputs();
                if (lin != null) {
                    for (InputMappingBlock inputMappingBlock : shaderNodeBlock.getInputs()) {
                        if (inputMappingBlock.getCondition() != null && inputMappingBlock.getCondition().contains(oldValue.getName())) {
                            inputMappingBlock.setCondition(inputMappingBlock.getCondition().replaceAll(oldValue.getName(), "").trim());
                        }
                    }
                }
                List<OutputMappingBlock> l = shaderNodeBlock.getOutputs();
                if (l != null) {
                    for (OutputMappingBlock outputMappingBlock : l) {
                        if (outputMappingBlock.getCondition() != null && outputMappingBlock.getCondition().contains(oldValue.getName())) {
                            outputMappingBlock.setCondition(outputMappingBlock.getCondition().replaceAll(oldValue.getName(), "").trim());
                        }
                    }
                }
            }
        }
        if (evt.getPropertyName().equals(MatDefBlock.ADD_MAT_PARAM) || evt.getPropertyName().equals(TechniqueBlock.ADD_SHADER_NODE) || evt.getPropertyName().equals(ShaderNodeBlock.ADD_MAPPING)) {
            matDefFile.registerListener((Statement) evt.getNewValue());
        }
        matDefFile.applyChange();
    }
    
}
