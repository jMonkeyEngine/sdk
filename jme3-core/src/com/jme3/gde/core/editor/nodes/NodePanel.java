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
package com.jme3.gde.core.editor.nodes;

import com.jme3.gde.core.editor.icons.Icons;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * The NodePanel is the actual implementation of a Node.
 * @author Nehon
 */
public abstract class NodePanel extends DraggablePanel implements Selectable, KeyListener {
    protected List<JLabel> inputLabels = new ArrayList<JLabel>();
    protected List<JLabel> outputLabels = new ArrayList<JLabel>();
    protected List<ConnectionEndpoint> inputDots = new ArrayList<ConnectionEndpoint>();
    protected List<ConnectionEndpoint> outputDots = new ArrayList<ConnectionEndpoint>();
    private JPanel content;
    protected JLabel header;
    protected Color color;
    protected String name;
    protected NodeToolBar toolBar = null;

    /**
     * Creates new form NodePanel
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public NodePanel() {
        super();
        addKeyListener(this);
    }
    
    /*
     * Sets the Toolbar associated with this node (as it was impossible to do
     * in the constructor). Don't construct a toolbar before this class' AWT
     * parts have been initialized.
    */
    public void setToolbar(NodeToolBar toolBar) {
        this.toolBar = toolBar;
    }

    /**
     * Set this node's name, title and tooltipText
     * Note: This name is different from AWTs setName()
     * @param s The Name
     */
    public void setNameAndTitle(String s) {
        name = s;
        setTitle(name);
    }

    public void setTitle(String s) {
        header.setText(s);
        header.setToolTipText(s);
    }

    public ConnectionEndpoint getInputConnectPoint(String name) {
        return getConnectPoint(inputLabels, name, inputDots);
    }

    public ConnectionEndpoint getOutputConnectPoint(String name) {
        return getConnectPoint(outputLabels, name, outputDots);
    }

    private ConnectionEndpoint getConnectPoint(List<JLabel> list, String varName, List<ConnectionEndpoint> listDot) {
        //This has been commented out because it was causing issues when a variable name was explicitely starting with m_ or g_ in the j3md.
        //I can't remember why it was done in the first place, but I can't see any case where the m_ should be stripped out.
        //I'm letting this commented in case this comes to light some day, and something more clever will have to be done.
        //if (varName.startsWith("m_") || varName.startsWith("g_")) {
        //   varName = varName.substring(2);
        //}
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getText().equals(varName)) {
                return listDot.get(i);
            }
        }
        return null;
    }

    /**
     * Create a Label used for TODO.
     * @param txt The text on the label
     * @param type The ParameterType (Input, Output, Both)
     * @return 
     */
    protected JLabel createLabel(String txt, ConnectionEndpoint.ParamType type) {
        JLabel label = new JLabel(txt);
        label.setForeground(Color.BLACK);
        label.setOpaque(false);
        //label.setPreferredSize(new Dimension(50, 15));        
        label.setHorizontalAlignment(type == ConnectionEndpoint.ParamType.Output ? SwingConstants.RIGHT : SwingConstants.LEFT);
        label.setFont(new Font("Tahoma", 0, 10));
        label.addMouseListener(labelMouseMotionListener);
        label.addMouseMotionListener(labelMouseMotionListener);
        // label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        return label;
    }
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);        
        diagram.select(this, e.isShiftDown() || e.isControlDown());
        showToolBar();
    }
    
    private void showToolBar(){
        if (toolBar != null) {
            toolBar.display();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        diagram.fixSize();
        if (svdx != getLocation().x) {
            getDiagram().getEditorParent().savePositionToMetaData(getKey(), getLocation().x, getLocation().y);
        }
    }

    /**
     * Try to open an edit dialog for this node (if supported)
     */
    public void edit() {
        if (canEdit()) {
            diagram.showEdit(this);
        }
    }
    
    /**
     * Whether this node shall trigger an edit dialog in the diagram
     * @see Diagram#showEdit(com.jme3.gde.materialdefinition.editor.NodePanel) 
     * @return Whether this Node can be edited
     */
    protected abstract boolean canEdit();
    
    @Override
    public abstract String getKey(); // satisfy Selectable interface
    
    public void cleanup(){
        if (toolBar != null) {
            toolBar.getParent().remove(toolBar);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    protected void initComponents() {
        header = new JLabel(Icons.vert);
        header.setForeground(Color.BLACK);
        header.addMouseListener(labelMouseMotionListener);
        header.addMouseMotionListener(labelMouseMotionListener);
        header.setHorizontalAlignment(SwingConstants.LEFT);
        header.setFont(new Font("Tahoma", Font.BOLD, 11));

        content = new JPanel();
        content.setOpaque(false);
        GroupLayout contentLayout = new GroupLayout(content);
        content.setLayout(contentLayout);

        int txtLength = 100;

        GroupLayout.ParallelGroup grpHoriz = contentLayout.createParallelGroup(GroupLayout.Alignment.LEADING);

        for (int i = 0; i < outputDots.size(); i++) {
            grpHoriz.addGroup(GroupLayout.Alignment.TRAILING, contentLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(outputLabels.get(i), GroupLayout.PREFERRED_SIZE, txtLength, GroupLayout.PREFERRED_SIZE)
                    .addGap(2, 2, 2)
                    .addComponent(outputDots.get(i), GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE));
        }
        for (int i = 0; i < inputDots.size(); i++) {
            grpHoriz.addGroup(GroupLayout.Alignment.LEADING, contentLayout.createSequentialGroup()
                    .addComponent(inputDots.get(i), GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
                    .addGap(2, 2, 2)
                    .addComponent(inputLabels.get(i), GroupLayout.PREFERRED_SIZE, txtLength, GroupLayout.PREFERRED_SIZE));
        }

        contentLayout.setHorizontalGroup(grpHoriz);

        GroupLayout.ParallelGroup grpVert = contentLayout.createParallelGroup(GroupLayout.Alignment.LEADING);

        GroupLayout.SequentialGroup grp = contentLayout.createSequentialGroup();
        for (int i = 0; i < inputDots.size(); i++) {
            grp.addGroup(contentLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(inputDots.get(i), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(inputLabels.get(i))).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
        }
        for (int i = 0; i < outputDots.size(); i++) {
            grp.addGroup(contentLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(outputDots.get(i), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(outputLabels.get(i))).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
        }

        grpVert.addGroup(GroupLayout.Alignment.TRAILING, grp);

        contentLayout.setVerticalGroup(grpVert);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(header, 100, 100, 100))
                .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(6, 6, 6))
                .addComponent(content, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(header, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(content, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10));
    }
    
    @Override
    protected void paintComponent(Graphics g1) {
        Graphics2D g = (Graphics2D) g1;
        Color borderColor = Color.BLACK;
        if (getDiagram().getSelectedItems().contains(this)) {
            borderColor = Color.WHITE;
        }
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // Anti-alias!
                RenderingHints.VALUE_ANTIALIAS_ON);
        // Color[] colors = {new Color(0, 0, 0, 0.7f), new Color(0, 0, 0, 0.15f)};
        if (getDiagram().getSelectedItems().contains(this)) {
            Color[] colors = new Color[]{ new Color(0.6f, 0.6f, 1.0f, 0.8f),
                new Color(0.6f, 0.6f, 1.0f, 0.5f) };
            
            float[] factors = {0f, 1f};
            g.setPaint(new RadialGradientPaint(getWidth() / 2,
                    getHeight() / 2, getWidth() / 2, factors, colors));
            
            g.fillRoundRect(8, 3, getWidth() - 10, getHeight() - 6, 15, 15);
        } else if (toolBar != null) {
            // Hide the toolBar when we've been unselected
            if(toolBar.isVisible()){
                toolBar.setVisible(false);
            }
        }

        g.setColor(new Color(170, 170, 170, 120));
        g.fillRoundRect(5, 1, getWidth() - 9, getHeight() - 6, 15, 15);
        g.setColor(borderColor);

        g.drawRoundRect(4, 0, getWidth() - 9, getHeight() - 6, 15, 15);
        g.setColor(new Color(170, 170, 170, 120));
        g.fillRect(4, 1, 10, 10);
        g.setColor(borderColor);
        g.drawLine(4, 0, 14, 0);
        g.drawLine(4, 0, 4, 10);
        g.setColor(Color.BLACK);
        g.drawLine(5, 15, getWidth() - 6, 15);
        g.setColor(new Color(190, 190, 190));
        g.drawLine(5, 16, getWidth() - 6, 16);

        Color c1 = new Color(color.getRed(), color.getGreen(), color.getBlue(), 150);
        Color c2 = new Color(color.getRed(), color.getGreen(), color.getBlue(), 0);
        g.setPaint(new GradientPaint(0, 15, c1, getWidth(), 15, c2));
        g.fillRect(5, 1, getWidth() - 10, 14);

    }

    public abstract ConnectionEndpoint createConnectionEndpoint(String type, 
            ConnectionEndpoint.ParamType paramType, String paramName);

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DELETE) {
            delete();
        }
    }

    public void delete() {
        getDiagram().removeSelected();
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
    
// used to pass press and drag events to the NodePanel when they occur on the label
    private LabelMouseMotionListener labelMouseMotionListener = new LabelMouseMotionListener();

    private class LabelMouseMotionListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            MouseEvent me = SwingUtilities.convertMouseEvent(e.getComponent(), e, NodePanel.this);
            NodePanel.this.dispatchEvent(me);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            MouseEvent me = SwingUtilities.convertMouseEvent(e.getComponent(), e, NodePanel.this);
            NodePanel.this.dispatchEvent(me);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            MouseEvent me = SwingUtilities.convertMouseEvent(e.getComponent(), e, NodePanel.this);
            NodePanel.this.dispatchEvent(me);
        }
    }
}
