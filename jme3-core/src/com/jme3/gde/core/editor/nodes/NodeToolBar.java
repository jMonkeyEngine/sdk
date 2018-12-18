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

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JPanel;

/**
 * This class displays a small bar where an edit and remove icon is typically
 * placed
 * @author MeFisto94
 */
public abstract class NodeToolBar extends JPanel implements ComponentListener, 
        MouseListener {

    // The node to which we're added to
    private final NodePanel node;

    /**
     * Creates a new toolbar for the desired node
     * @param node The node
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public NodeToolBar(NodePanel node) {
        initComponents();
        this.node = node;
        node.addComponentListener(this);
    }

    /**
     * Implement this method (preferably using a form editor) to
     * generate the AWT Code for your toolbar
     */
    protected abstract void initComponents();

    /**
     * Called by the NodePanel when the user hovers over this node or selects
     * it or something<br>.
     * Hint: There is no "remove" call, that happens automatically. Display
     * is only required to set the Bounds and Visibility
     */
    public void display() {
        if (getParent() == null) {
            node.getParent().add(this);
        }
        setBounds(node.getLocation().x + 5, node.getLocation().y - 18, node.getWidth() - 10, 16);
        node.getParent().setComponentZOrder(this, 0);
        setVisible(true);
    }

    @Override
    public void componentResized(ComponentEvent e) {}

    /**
     * When parent node moves, also move (this should be done by a layout manager
     * though....
     */
    @Override
    public void componentMoved(ComponentEvent e) {
        setLocation(node.getLocation().x + 5, node.getLocation().y - 18);
    }

    @Override
    public void componentShown(ComponentEvent e) {}

    @Override
    public void componentHidden(ComponentEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {
        e.consume();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        e.consume();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        e.consume();
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

}
