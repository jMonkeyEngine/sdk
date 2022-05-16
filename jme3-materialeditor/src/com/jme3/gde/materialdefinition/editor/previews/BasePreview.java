/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jme3.gde.materialdefinition.editor.previews;

import com.jme3.shader.ShaderNodeVariable;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * Base component for all previews.
 * @author rickard
 */
public class BasePreview extends JPanel implements ActionListener{
    
    private ShaderNodeVariable output;
    private OnDefaultValueChangedListener listener;
    
    protected Logger logger = Logger.getLogger(BasePreview.class.getName());
    
    public interface OnDefaultValueChangedListener{
        
        void onDefaultValueChanged(String value);
    }
    
    public BasePreview(ShaderNodeVariable output){
        this.output = output;
        setSize(20, 20);
        setBorder(new EmptyBorder(0,0,0,0));
        ((FlowLayout)getLayout()).setVgap(0);
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
    }
    
    protected void onDefaultValueChanged(String value){
        output.setDefaultValue(value);
        if(listener != null){
            listener.onDefaultValueChanged(value);
        }
    }
    
    public void setOnDefaultValueChangeListener(OnDefaultValueChangedListener listener){
        this.listener = listener;
    }
    
}
