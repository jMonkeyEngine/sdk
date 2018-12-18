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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;

/**
 * The ConnectionEndpoint class represents an Endpoint of a {@link Connection}
 * @author Nehon
 */
public abstract class ConnectionEndpoint extends JPanel implements MouseInputListener {
    public static boolean pressed = false;
    protected ImageIcon img;
    protected ImageIcon prevImg;
    private String type;
    protected ParamType paramType;
    private String text = "";
    protected DraggablePanel node;
    private int index = 1;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public enum ParamType {
        Input,
        Output,
        Both
    }

    @SuppressWarnings("LeakingThisInConstructor")
    public ConnectionEndpoint() {
        super();
        setMaximumSize(new Dimension(10, 10));
        setMinimumSize(new Dimension(10, 10));
        setPreferredSize(new Dimension(10, 10));
        setSize(10, 10);
        addMouseMotionListener(this);
        addMouseListener(this);
       
    }


    @Override
    protected void paintComponent(Graphics g) {
        if (img == null) {
            img = Icons.imgGrey;
        }
        g.drawImage(img.getImage(), 0, 0, this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        Diagram diag = getDiagram();
        diag.draggedFrom = this;
        prevImg = img;
        setIcon(Icons.imgOrange);
        e.consume();
    }

    @Override
    public void repaint() {
        if (getNode() != null) {
            getDiagram().repaint();
        } else {
            super.repaint();
        }
    }

    /**
     * Returns the Diagram (the surface containing all the nodes and curves)
     * @return the diagram
     */
    public Diagram getDiagram() {
        return node.getDiagram();
    }

    /**
     * Returns the Node this ConnectionEndpoint belongs to
     * @return the node
     */
    public DraggablePanel getNode() {
        return node;
    }

    /**
     * Sets the Node this ConnectionEndpoint belongs to
     * @param node the node
     */
    public void setNode(DraggablePanel node) {
        this.node = node;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Diagram diag = getDiagram();
        if (diag.draggedFrom == this && diag.draggedTo != null) {
            if (this.canConnect(diag.draggedTo)) {
                diag.notifyMappingCreation(diag.connect(this, diag.draggedTo));

            } else {
                diag.draggedTo.reset();
                this.reset();
            }
            diag.draggedFrom = null;
            diag.draggedTo = null;
        } else {
            reset();
            diag.draggedFrom = null;
        }
        e.consume();
    }

    /**
     * Changes the look of this connector. Implies repainting
     * @param icon The Icon to use
     */
    public void setIcon(ImageIcon icon) {
        img = icon;
        repaint();
    }
    
    /**
     * Resets the Icon to be the previous image.
     */
    public void reset() {
        setIcon(prevImg);
    }

    /**
     * Changes the Icon to the disconnect state.
     */
    public void disconnect() {
        setIcon(Icons.imgGrey);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        Diagram diag = getDiagram();
        if (diag.draggedFrom != null && diag.draggedFrom != this) {
            prevImg = img;
            canConnect(diag.draggedFrom);
            diag.draggedTo = this;
            diag.draggedFrom.canConnect(this);
        }

    }

    /**
     * Determines whether this dot can form a {@link Connection} with the other
     * specified dot. Subclasses should only override this, when they want to
     * change the icon behavior because otherwise the logic like not connecting
     * input to input has to be replicated as well. It is preferred to 
     * implement {@link #allowConnection(com.jme3.gde.materialdefinition.editor.ConnectionEndpoint) }
     * instead.
     * 
     * @param pair The other dot to form a connection with
     * @return Whether the dots can be connected
     */
    public boolean canConnect(ConnectionEndpoint pair) {
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
    
    protected abstract boolean allowConnection(ConnectionEndpoint pair);

    protected void connect(Connection connection) {
        getNode().addComponentListener(connection);
        setIcon(Icons.imgGreen);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        Diagram diag = getDiagram();
        if (diag.draggedFrom != null) {
            diag.draggedFrom.canConnect(null);
            if (diag.draggedFrom != this) {
                reset();
            }
            if (diag.draggedTo == this) {
                diag.draggedTo = null;
            }
        }
    }

    public Point getStartLocation() {
        Point p = getLocation();
        Component parent = getParent();
        while (parent != getNode()) {
            p.x += parent.getLocation().x;
            p.y += parent.getLocation().y;
            parent = parent.getParent();
        }
        p.x += 10 + getNode().getLocation().x;
        p.y += 5 + getNode().getLocation().y;
        return p;
    }

    public Point getEndLocation() {
        Point p = getLocation();
        Component parent = getParent();
        while (parent != getNode()) {
            p.x += parent.getLocation().x;
            p.y += parent.getLocation().y;
            parent = parent.getParent();
        }
        p.x += getNode().getLocation().x + 2;
        p.y += 5 + getNode().getLocation().y;
        return p;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        MouseEvent me = SwingUtilities.convertMouseEvent(this, e, getDiagram());
        getDiagram().draggingDot(me);
    }

    @Override
    public void mouseMoved(MouseEvent e) {}

    // This is implementation specific...
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns the parameter type
     * @return the paramtype 
     */
    public ParamType getParamType() {
        return paramType;
    }

    /**
     * Sets the parameter type (if this dot is Input, Output or Both)
     * @param paramType The parameter type
     */
    public void setParamType(ParamType paramType) {
        this.paramType = paramType;
    }

    /**
     * The Index is the "y-position" of this dot in the parental node.
     * The index is multiplied by an offset to generate the position
     * @return The index of this dot on the node
     */
    public int getIndex() {
        return index;
    }

    /**
     * The Index is the "y-position" of this dot in the parental node.
     * The index is multiplied by an offset to generate the position
     * @param index The index of this dot on the node
     */
    public void setIndex(int index) {
        this.index = index;
    }

}
