/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jme3.gde.materialdefinition.editor.previews;

import com.jme3.shader.ShaderNodeVariable;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;

/**
 * Component for previewing and changing a default Float value for a MatParam.
 * @author rickard
 */
public class FloatPreview extends BasePreview{
    
    public FloatPreview(ShaderNodeVariable output){
        super(output);
        JTextField textField = new JTextField(output.getDefaultValue());
        textField.addActionListener((ActionEvent e) -> {
            String value = ((JTextField)e.getSource()).getText();
            if(verifyFloatString(value)){
                onDefaultValueChanged(value);
            }
        });
        add(textField);
        setSize(30, 20);
    }
    
    private boolean verifyFloatString(String value){
        try {
            Float.parseFloat(value);
        } catch(NumberFormatException ex){
            logger.warning("Value is not valid float");
            return false;
        } catch(NullPointerException ex){
            logger.warning("Value is null");
            return false;
        }
        return true;
    }
}
