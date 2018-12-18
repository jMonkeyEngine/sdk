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
import com.jme3.gde.core.editor.nodes.NodePanel;
import com.jme3.gde.materialdefinition.fileStructure.ShaderNodeBlock;
import com.jme3.gde.materialdefinition.fileStructure.leaves.DefinitionBlock;
import com.jme3.gde.materialdefinition.fileStructure.leaves.InputMappingBlock;
import com.jme3.gde.materialdefinition.fileStructure.leaves.OutputMappingBlock;
import com.jme3.gde.core.editor.icons.Icons;
import com.jme3.shader.Shader;
import com.jme3.shader.ShaderNodeDefinition;
import com.jme3.shader.ShaderNodeVariable;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import org.openide.util.WeakListeners;

/**
 * The ShaderNodePanel is the ShaderNode specific implementation of 
 * {@link NodePanel}. 
 * @author MeFisto94
 */
public class ShaderNodePanel extends NodePanel implements InOut, 
        PropertyChangeListener {
    private NodeType type = NodeType.Vertex;
    protected Shader.ShaderType shaderType;
    // The Name of the currently active Technique
    private String techName;
    // Required for the Editor
    protected List<String> filePaths = new ArrayList<String>();
    
    public enum NodeType {
        Vertex(new Color(220, 220, 70)),//yellow
        Fragment(new Color(114, 200, 255)),//bleue
        Attribute(Color.WHITE),
        MatParam(new Color(70, 220, 70)),//green
        WorldParam(new Color(220, 70, 70)); //red
        private Color color;

        private NodeType() {
        }

        private NodeType(Color color) {
            this.color = color;
        }

        public Color getColor() {
            return color;
        }
    }

    public ShaderNodePanel(ShaderNodeBlock node, ShaderNodeDefinition def) {
        super();
        
        shaderType = def.getType();
        if (shaderType == Shader.ShaderType.Vertex) {
            type = NodeType.Vertex;
        } else {
            type = NodeType.Fragment;
        }
        
        node.addPropertyChangeListener(WeakListeners.propertyChange(this, node));
        this.addPropertyChangeListener(WeakListeners.propertyChange(node, this));
        
        init(def.getInputs(), def.getOutputs());
        setToolbar(new ShaderNodeToolBar(this));
        refresh(node);
        
        filePaths.addAll(def.getShadersPath());
        String defPath = ((DefinitionBlock)node.getContents().get(0)).getPath();
        filePaths.add(defPath);    
    }

    public ShaderNodePanel(ShaderNodeVariable singleOut, NodeType type) {
        super();
        this.type = type;
        List<ShaderNodeVariable> outputs = new ArrayList<ShaderNodeVariable>();
        outputs.add(singleOut);
        init(new ArrayList<ShaderNodeVariable>(), outputs);
        setToolbar(new ShaderNodeToolBar(this));
    }
    
    private void init(List<ShaderNodeVariable> inputs, List<ShaderNodeVariable> outputs) {
        setBounds(0, 0, 120, 30 + inputs.size() * 20 + outputs.size() * 20);

        for (ShaderNodeVariable input : inputs) {
            JLabel label = createLabel(input.getType(), input.getName(), ConnectionEndpoint.ParamType.Input);
            ConnectionEndpoint dot = createConnectionEndpoint(input.getType(), ConnectionEndpoint.ParamType.Input, input.getName());
            inputLabels.add(label);
            inputDots.add(dot);
        }
        int index = 0;
        for (ShaderNodeVariable output : outputs) {
            String outName = output.getName();
            JLabel label = createLabel(output.getType(), outName, ConnectionEndpoint.ParamType.Output);
            ConnectionEndpoint dot = createConnectionEndpoint(output.getType(), ConnectionEndpoint.ParamType.Output, outName);
            dot.setIndex(index++);
            outputLabels.add(label);
            outputDots.add(dot);
        }

        initComponents();
        updateType();
        setOpaque(false);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        if (svdx != getLocation().x) {
            firePropertyChange(ShaderNodeBlock.POSITION, svdx, getLocation().x);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("name")) {
            refresh((ShaderNodeBlock) evt.getSource());
        }
    }
    
    @Override
    /**
     * Keys are important for Selectable
     */
    public String getKey() {
        switch (type) {
            case Attribute:
                return "Attr." + outputLabels.get(0).getText();
            case WorldParam:
                return "WorldParam." + outputLabels.get(0).getText();
            case MatParam:
                return "MatParam." + outputLabels.get(0).getText();
            default:
                return techName + "/" + name;
        }
    }
    
    public NodeType getType() {
        return type;
    }
    
    public void setTechName(String techName) {
        this.techName = techName;
    }
    
    /**
     * This methods is responsible for setting the correct icon and text
     * in the header-bar of the node. Call this from your constructor,
     * _after_ components have been inited.
     */
    protected void updateType() {
        switch (type) {
            case Vertex:
                header.setIcon(Icons.vert);
                break;
            case Fragment:
                header.setIcon(Icons.frag);
                break;
            case Attribute:
                header.setIcon(Icons.attrib);
                setNameAndTitle("Attribute"); // sets text _and_ tooltip the same
                break;
            case WorldParam:
                header.setIcon(Icons.world);
                setNameAndTitle("WorldParam");
                break;
            case MatParam:
                header.setIcon(Icons.mat);
                setNameAndTitle("MatParam");
                break;
        }
        color = type.getColor();
    }
    
    /**
     * Utility method to update the node text when the underlying ShaderNode
     * has been changed (called by PropertyChangeListeners)
     * @param node The source shadernode
     */
    protected final void refresh(ShaderNodeBlock node) {
        setNameAndTitle(node.getName());
    }
    
    @Override
    protected boolean canEdit() {
        return (type == NodeType.Fragment || type == NodeType.Vertex);
    }
    
    /**
     * Create a Label for a given ShaderNodes' Input/Output
     * @param glslType The Type (class) of this variable
     * @param txt The Name of this variable
     * @param type Whether this is the input or output
     * @return The IO Label
     */
    protected JLabel createLabel(String glslType, String txt, ConnectionEndpoint.ParamType type) {
        JLabel label = super.createLabel(txt, type);
        label.setToolTipText(glslType + " " + txt);
        return label;
    }
    
    @Override
    public ConnectionEndpoint createConnectionEndpoint(String type, 
            ConnectionEndpoint.ParamType paramType, String paramName) {
        ShaderNodeDot dot1 = new ShaderNodeDot();
        dot1.setShaderType(shaderType);
        dot1.setNode(this);
        dot1.setText(paramName);
        dot1.setParamType(paramType);
        dot1.setType(type);
        return dot1;
    }
    
    // Callbacks when Connections are formed and released
    @Override
    public void addInputMapping(InputMappingBlock block) {
        firePropertyChange(ShaderNodeBlock.INPUT, null, block);
    }

    @Override
    public void removeInputMapping(InputMappingBlock block) {
        firePropertyChange(ShaderNodeBlock.INPUT, block, null);
    }

    @Override
    public void addOutputMapping(OutputMappingBlock block) {
        firePropertyChange(ShaderNodeBlock.OUTPUT, null, block);
    }

    @Override
    public void removeOutputMapping(OutputMappingBlock block) {
        firePropertyChange(ShaderNodeBlock.OUTPUT, block, null);
    }
}
