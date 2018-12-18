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

import com.jme3.gde.core.editor.nodes.Connection;
import com.jme3.gde.core.editor.nodes.Diagram;
import com.jme3.gde.core.editor.nodes.NodePanel;
import com.jme3.gde.core.editor.nodes.Selectable;
import com.jme3.gde.core.assets.ProjectAssetManager;
import com.jme3.gde.materialdefinition.dialog.AddAttributeDialog;
import com.jme3.gde.materialdefinition.dialog.AddMaterialParameterDialog;
import com.jme3.gde.materialdefinition.dialog.AddNodeDialog;
import com.jme3.gde.materialdefinition.dialog.AddWorldParameterDialog;
import com.jme3.gde.materialdefinition.editor.ShaderNodePanel.NodeType;
import com.jme3.gde.materialdefinition.fileStructure.ShaderNodeBlock;
import com.jme3.gde.materialdefinition.fileStructure.leaves.MappingBlock;
import com.jme3.gde.core.editor.icons.Icons;
import com.jme3.gde.materialdefinition.utils.MaterialUtils;
import com.jme3.material.Material;
import com.jme3.shader.Shader;
//import static com.jme3.gde.materialdefinition.editor.ShaderNodePanel.NodeType;
import com.jme3.shader.ShaderNodeDefinition;
import com.jme3.shader.ShaderNodeVariable;
import com.jme3.shader.UniformBinding;
import com.jme3.shader.VarType;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

/**
 * The Diagram is the main canvas where all nodes {@link DraggablePanel} and
 * their connections {@link ConnectionEndpoint} {@link Connection} are added onto.
 * @author Nehon
 */
public class ShaderNodeDiagram extends Diagram implements ComponentListener {

    protected List<ShaderOutBusPanel> outBuses = new ArrayList<ShaderOutBusPanel>();
    private String currentTechniqueName;
    private final BackdropPanel backDrop = new BackdropPanel();
    private final Point pp = new Point();

    @SuppressWarnings("LeakingThisInConstructor")
    public ShaderNodeDiagram() {
        super();
    }
    
    @Override
    protected boolean mouseLMBPrePressedEvent(MouseEvent e) {
        for (ShaderOutBusPanel outBusPanel : outBuses) {
            Point p = SwingUtilities.convertPoint(this, e.getX(), e.getY(), outBusPanel);
            if (outBusPanel.contains(p)) {
                MouseEvent me = SwingUtilities.convertMouseEvent(this, e, outBusPanel);
                outBusPanel.dispatchEvent(me);
                if (me.isConsumed()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (draggedFrom != null && draggedFrom.getNode() instanceof ShaderOutBusPanel) {
                MouseEvent me = SwingUtilities.convertMouseEvent(this, e, draggedFrom.getNode());
                draggedFrom.getNode().dispatchEvent(me);
                if (me.isConsumed()) {
                    return;
                }
            }

            dispatchToOutBuses(e);
        } else {
            super.mouseReleased(e); // Handle all the UI Stuff
        }

    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);
        dispatchToOutBuses(e);
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (draggedFrom == null) {
                for (Selectable selectedItem : selectedItems) {
                    if (selectedItem instanceof ShaderOutBusPanel) {
                        ShaderOutBusPanel bus = (ShaderOutBusPanel) selectedItem;
                        MouseEvent me = SwingUtilities.convertMouseEvent(this, e, bus);
                        bus.dispatchEvent(me);
                    }
                }
            }
        } else {
            super.mouseDragged(e); // Handle all the UI Stuff
        }
    }

    /**
     * Called by {@link ConnectionEndpoint} when a Curve has been dragged
     */
    @Override
    protected void draggingDot(MouseEvent e) {
        for (ShaderOutBusPanel outBusPanel: outBuses) {
            Point p = SwingUtilities.convertPoint(this, e.getX(), e.getY(), outBusPanel);
            if (outBusPanel.contains(p)) {
                MouseEvent me = SwingUtilities.convertMouseEvent(this, e, outBusPanel);
                outBusPanel.draggingDot(me);
                if (me.isConsumed()) {
                    return;
                }
            }
        }
    }
    
    
    public void refreshPreviews(Material mat, String technique) {
        for (ShaderOutBusPanel outBusPanel : outBuses) {
            outBusPanel.updatePreview(mat, technique);
        }
        if (backDrop.isVisible()) {
            backDrop.showMaterial(mat, technique);
        }
    }

    public void displayBackdrop() {
        if (backDrop.getParent() == null) {
            add(backDrop);
            ((JViewport)getParent()).addChangeListener(backDrop);
        }

        backDrop.setVisible(true);
        backDrop.update(((JViewport)getParent()));
    }
    
    public void setCurrentTechniqueName(String currentTechniqueName) {
        this.currentTechniqueName = currentTechniqueName;
    }

    public String getCurrentTechniqueName() {
        return currentTechniqueName;
    }

    @Override
    public void addConnection(Connection conn) {
        super.addConnection(conn);
        
        // Adjust outBuses and repaint again
        for (ShaderOutBusPanel bus : outBuses) {
            setComponentZOrder(bus, getComponentCount() - 1);
        }
        repaint();
    }
    
    @Override
    protected void showEdit(NodePanel node) {
        if (node instanceof ShaderNodePanel &&
                parent instanceof MatDefEditorlElement) {
            ((MatDefEditorlElement)parent).showShaderEditor(node.getName(), 
                    ((ShaderNodePanel)node).getType(), 
                    ((ShaderNodePanel)node).filePaths);
        }
    }

    @Override
    public void addNode(NodePanel node) {
        super.addNode(node);
        if (node instanceof ShaderNodePanel) {
            ((ShaderNodePanel)node).setTechName(currentTechniqueName);
        }
    }

    public void addOutBus(ShaderOutBusPanel bus) {
        outBuses.add(bus);
        bus.setDiagram(this);
        add(bus);
        setComponentZOrder(bus, getComponentCount() - 1);
        addComponentListener(bus);
        bus.componentResized(new ComponentEvent(this, ActionEvent.ACTION_PERFORMED));
        bus.revalidate();
    }

    private String fixNodeName(String name) {
        return fixNodeName(name, 0);
    }

    private String fixNodeName(String name, int count) {
        for (NodePanel nodePanel : nodes) {
            if ((name + (count == 0 ? "" : count)).equals(nodePanel.getName())) {
                return fixNodeName(name, count + 1);
            }
        }
        return name + (count == 0 ? "" : count);
    }
    
    public void addNodesFromDefs(List<ShaderNodeDefinition> defList, String path, Point clickPosition) {
        int i = 0;
        for (ShaderNodeDefinition def : defList) {
            ShaderNodeBlock sn = new ShaderNodeBlock(def, path);
            sn.setName(fixNodeName(sn.getName()));
            NodePanel np = new ShaderNodePanel(sn, def);
            addNode(np);
            np.setLocation(clickPosition.x + i * 150, clickPosition.y);
            sn.setSpatialOrder(np.getLocation().x);
            i++;
            np.revalidate();
            ((MatDefEditorlElement)getEditorParent()).notifyAddNode(sn, def);
        }
        repaint();
    }

    public void addMatParam(String type, String name, Point point) {
        String fixedType = type;
        if (type.equals("Color")) {
            fixedType = "Vector4";
        }
        ShaderNodeVariable param = new ShaderNodeVariable(VarType.valueOf(fixedType).getGlslType(), name);
        NodePanel np = new ShaderNodePanel(param, NodeType.MatParam);
        addNode(np);
        np.setLocation(point.x, point.y);
        np.revalidate();
        repaint();
        ((MatDefEditorlElement)getEditorParent()).notifyAddMapParam(type, name);
    }

    public void addWorldParam(UniformBinding binding, Point point) {
        ShaderNodeVariable param = new ShaderNodeVariable(binding.getGlslType(), binding.name());
        NodePanel np = new ShaderNodePanel(param, NodeType.WorldParam);
        addNode(np);
        np.setLocation(point.x, point.y);
        np.revalidate();
        repaint();
        ((MatDefEditorlElement)getEditorParent()).notifyAddWorldParam(binding.name());
    }

    public void addAttribute(String name, String type, Point point) {
        ShaderNodeVariable param = new ShaderNodeVariable(type, "Attr", name);
        NodePanel np = new ShaderNodePanel(param, NodeType.Attribute);
        addNode(np);
        np.setLocation(point.x, point.y);
        np.revalidate();
        repaint();
    }

    @Override
    public String makeKeyForConnection(Connection con, Object obj) {
        return MaterialUtils.makeKey((MappingBlock)obj, currentTechniqueName);
    }

    /**
     * Find a OutBusPanel which corresponds to the given key (unique id). 
     * Use this to locate busses on the diagram
     * 
     * @param key The key
     * @return hopefully the correct panel
     */
    public ShaderOutBusPanel getOutBusPanel(String key) {
        for (ShaderOutBusPanel out : outBuses) {
            if (out.getKey().equals(key)) {
                return out;
            }
        }
        return null;
    }

    @Override
    protected Selectable trySelect(String key) {
        for (ShaderOutBusPanel outBusPanel : outBuses) {
            if (outBusPanel.getKey().equals(key)) {
                return outBusPanel;
            }
        }
        
        return null;
    }
    
    @Override
    public void clear() {
        super.clear();
        outBuses.clear();
    }

    @Override
    protected void createPopupMenu() {
        super.createPopupMenu();
        JMenuItem nodeItem = createMenuItem("Node", Icons.node);
        nodeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddNodeDialog d = new AddNodeDialog(null, true, 
                        ((MatDefEditorlElement)parent).obj.getLookup()
                            .lookup(ProjectAssetManager.class), 
                    ShaderNodeDiagram.this, clickLoc);
                d.setLocationRelativeTo(null);
                d.setVisible(true);
            }
        });

        contextMenu.add(nodeItem);
        contextMenu.add(createSeparator());
        JMenuItem matParamItem = createMenuItem("Material Parameter", Icons.mat);
        matParamItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddMaterialParameterDialog d = new AddMaterialParameterDialog(null, true, ShaderNodeDiagram.this, clickLoc);
                d.setLocationRelativeTo(null);
                d.setVisible(true);
            }
        });
        contextMenu.add(matParamItem);
        JMenuItem worldParamItem = createMenuItem("World Parameter", Icons.world);
        worldParamItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddWorldParameterDialog d = new AddWorldParameterDialog(null, true, ShaderNodeDiagram.this, clickLoc);
                d.setLocationRelativeTo(null);
                d.setVisible(true);
            }
        });
        contextMenu.add(worldParamItem);
        JMenuItem attributeItem = createMenuItem("Attribute", Icons.attrib);
        attributeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddAttributeDialog d = new AddAttributeDialog(null, true, ShaderNodeDiagram.this, clickLoc);
                d.setLocationRelativeTo(null);
                d.setVisible(true);
            }
        });
        contextMenu.add(attributeItem);
        contextMenu.add(createSeparator());
        JMenuItem outputItem = createMenuItem("Output color", Icons.output);
        contextMenu.add(outputItem);
        outputItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ShaderOutBusPanel p2 = new ShaderOutBusPanel("color" + (outBuses.size() - 1), Shader.ShaderType.Fragment);
                p2.setBounds(0, 350 + 50 * (outBuses.size() - 1), p2.getWidth(), p2.getHeight());

                addOutBus(p2);

            }
        });
    }

    private void dispatchToOutBuses(MouseEvent e) {
        for (ShaderOutBusPanel outBusPanel : outBuses) {
            Point p = SwingUtilities.convertPoint(this, e.getX(), e.getY(), outBusPanel);
            if (outBusPanel.contains(p)) {
                MouseEvent me = SwingUtilities.convertMouseEvent(this, e, outBusPanel);
                outBusPanel.dispatchEvent(me);
                if (me.isConsumed()) {
                    return;
                }
            }
        }
    }

    @Override
    protected int calcMaxWidth() {
        int maxHeight = 0;
        for (ShaderOutBusPanel outBusPanel : outBuses) {
            int h = outBusPanel.getLocation().y + outBusPanel.getHeight();
            if (h > maxHeight) {
                maxHeight = h;
            }
        }
        return maxHeight;
    }
    
    @Override
    protected int calcMaxHeight() {
        return 0;
    }
    
    int minWidth = 0;
    int minHeight = 0;

    @Override
    public void autoLayout() {

        int offset = 550;
        for (ShaderOutBusPanel outBus : outBuses) {
            if (outBus.getKey().equalsIgnoreCase("position")) {
                outBus.setLocation(0, 100);
                
            } else {
                outBus.setLocation(0, offset);
                offset += 260;
            }
            getEditorParent().savePositionToMetaData(outBus.getKey(), outBus.getLocation().x, outBus.getLocation().y);
        }
        offset = 0;
        String keys = "";
        for (NodePanel nodeP: nodes) {
            ShaderNodePanel node;
            if (nodeP instanceof ShaderNodePanel) {
                node = (ShaderNodePanel)nodeP;
            } else {
                continue; // Don't layout foreign nodes, actually they shouldnt
                // even be there...
            }
            if (node.getType() == NodeType.Vertex || node.getType() == NodeType.Fragment) {
                node.setLocation(offset + 200, getNodeTop(node));
                getEditorParent().savePositionToMetaData(node.getKey(), node.getLocation().x, node.getLocation().y);
                int pad = getNodeTop(node);
                for (Connection connection: connections) {
                    if (connection.getEnd().getNode() == node) {
                        if (connection.getStart().getNode() instanceof ShaderNodePanel) {
                            ShaderNodePanel startP = (ShaderNodePanel)connection.getStart().getNode();
                            if (startP.getType() != NodeType.Vertex && startP.getType() != NodeType.Fragment) {
                                startP.setLocation(offset + 30, pad);
                                getEditorParent().savePositionToMetaData(startP.getKey(), startP.getLocation().x, startP.getLocation().y);
                                keys += startP.getKey() + "|";
                                pad += 50;
                            }
                        }
                    }
                }
            }
            offset += 320;
        }
        offset = 0;
        for (NodePanel nodeP: nodes) {
            ShaderNodePanel node;
            if (nodeP instanceof ShaderNodePanel) {
                node = (ShaderNodePanel)nodeP;
            } else {
                continue; // Don't layout foreign nodes, actually they shouldnt
                // even be there...
            }
            if (node.getType() != NodeType.Vertex && node.getType() != NodeType.Fragment && !(keys.contains(node.getKey()))) {
                node.setLocation(offset + 10, 0);
                getEditorParent().savePositionToMetaData(node.getKey(), node.getLocation().x, node.getLocation().y);
                offset += 130;
            }
        }
    }

    private int getNodeTop(ShaderNodePanel node) {
        if (node.getType() == NodeType.Vertex) {
            return 150;
        }
        if (node.getType() == NodeType.Fragment) {
            return 400;
        }
        return 0;

    }
}
