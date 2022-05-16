/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jme3.gde.materialdefinition.editor.previews;

import com.jme3.shader.ShaderNodeVariable;
import javax.swing.JLabel;

/**
 * Component for previewing and changing a default Vector4 value for a MatParam.
 * @author rickard
 */
public class Vec4Preview extends BasePreview{
    
    public Vec4Preview(ShaderNodeVariable output){
        super(output);
        add(new JLabel(output.getDefaultValue()));
        setSize(90, 20);
    }
}
