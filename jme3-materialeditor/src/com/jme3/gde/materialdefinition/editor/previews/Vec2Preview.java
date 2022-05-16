/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jme3.gde.materialdefinition.editor.previews;

import com.jme3.shader.ShaderNodeVariable;
import javax.swing.JLabel;

/**
 * Component for previewing and changing a default Vector2 value for a MatParam.
 * @author rickard
 */
public class Vec2Preview extends BasePreview{
    
    public Vec2Preview(ShaderNodeVariable output){
        super(output);
        add(new JLabel(output.getDefaultValue()));
        setSize(45, 20);
    }
}
