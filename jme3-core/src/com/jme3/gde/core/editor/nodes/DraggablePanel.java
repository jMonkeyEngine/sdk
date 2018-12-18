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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * The DraggablePanel Class represents a Panel in a canvas called {@link Diagram}.
 * This is used to represent the Nodes which contain of multiple input/outputs
 * represented by {@link ConnectionEndpoint}s and the {@link Connection}s 
 * connecting them.
 * 
 * @author Nehon
 */
public class DraggablePanel extends JPanel implements MouseListener, MouseMotionListener {
    protected int svdx, svdy, svdex, svdey;
    private boolean vertical = false;
    protected Diagram diagram;

    public DraggablePanel(boolean vertical) {
        this();
        this.vertical = vertical;
    }

    public DraggablePanel() {
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON2) {
            if (!vertical) {
                svdex = e.getXOnScreen();
            }
            svdey = e.getYOnScreen();
            saveLocation();
            diagram.multiStartDrag(this);
            e.consume();
        }
    }

    protected void saveLocation() {
        svdy = getLocation().y;
        svdx = getLocation().x;
    }

    // empty dummy overrides
    @Override
    public void mouseReleased(MouseEvent e) {
    }
    @Override
    public void mouseEntered(MouseEvent e) {
    }
    @Override
    public void mouseExited(MouseEvent e) {
    }
    @Override
    public void mouseMoved(MouseEvent e) {
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        if (!SwingUtilities.isMiddleMouseButton(e)) {
            int xoffset = 0;
            if (!vertical) {
                xoffset = e.getLocationOnScreen().x - svdex;
            }
            int yoffset = e.getLocationOnScreen().y - svdey;
            movePanel(xoffset, yoffset);
            diagram.multiMove(this, xoffset, yoffset);
            e.consume();
        }
    }

    protected void movePanel(int xoffset, int yoffset) {
        if (vertical) {
            xoffset = 0;
        }
        setLocation(Math.max(0, svdx + xoffset), Math.max(0, svdy + yoffset));
    }

    /** 
     * Returns the Diagram this Panel belongs to
     * @return Diagram
     */
    public Diagram getDiagram() {
        return diagram;
    }

    /**
     * Sets the Diagram this Panel belongs to
     * @param diagram Diagram
     */
    public void setDiagram(Diagram diagram) {
        this.diagram = diagram;
    }

}
