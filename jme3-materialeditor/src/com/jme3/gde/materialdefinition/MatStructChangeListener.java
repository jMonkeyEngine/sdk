/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jme3.gde.materialdefinition;

import com.jme3.gde.materialdefinition.fileStructure.MatDefBlock;
import com.jme3.gde.materialdefinition.fileStructure.ShaderNodeBlock;
import com.jme3.gde.materialdefinition.fileStructure.TechniqueBlock;
import com.jme3.gde.materialdefinition.fileStructure.leaves.InputMappingBlock;
import com.jme3.gde.materialdefinition.fileStructure.leaves.MatParamBlock;
import com.jme3.gde.materialdefinition.fileStructure.leaves.OutputMappingBlock;
import com.jme3.material.Technique;
import com.jme3.util.blockparser.Statement;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
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
