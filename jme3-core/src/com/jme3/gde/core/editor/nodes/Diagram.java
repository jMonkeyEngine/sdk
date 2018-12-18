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

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

/**
 * The Diagram is the main canvas where all nodes {@link NodePanel} and
 * their connections {@link ConnectionEndpoint} {@link Connection} are added onto.
 * @author Nehon
 */
public abstract class Diagram extends JPanel implements MouseListener, 
        MouseMotionListener, ComponentListener {
    // Content
    protected List<Selectable> selectedItems = new ArrayList<Selectable>();
    protected List<Connection> connections = new ArrayList<Connection>();
    protected List<NodePanel> nodes = new ArrayList<NodePanel>();
    
    // UI
    protected final JPopupMenu contextMenu = new JPopupMenu("Add");
    protected NodeEditor parent;
    
    // drag stuff
    protected ConnectionEndpoint draggedFrom;
    protected ConnectionEndpoint draggedTo;  
    private final Point pp = new Point();
    protected Point clickLoc = new Point(0, 0);    
    
    // dynamic switching between the regular and the move cursor (MMB)
    private final Cursor defCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
    private final Cursor hndCursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
    
    // filled in from componentResize()
    protected int minWidth = 0;
    protected int minHeight = 0;

    @SuppressWarnings("LeakingThisInConstructor")
    public Diagram() {
        addMouseListener(this);
        addMouseMotionListener(this);
        createPopupMenu();
    }
    
    /**
     * This method is called from within the mousePressed event when the Left 
     * Mouse Button is pressed. Use this if you need to run before the regular
     * connection logic. The return value determines whether the event was consumed
     * 
     * @param e The Event
     * @return Whether to continue passing the event (true) or to quit (false) 
     */
    protected abstract boolean mouseLMBPrePressedEvent(MouseEvent e);

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (mouseLMBPrePressedEvent(e)) {
                return; // event already consumed
            }

            for (Connection connection : connections) {
                MouseEvent me = SwingUtilities.convertMouseEvent(this, e, connection);
                connection.select(me);
                if (me.isConsumed()) {
                    return;
                }
            }

            selectedItems.clear();
            repaint();
        } else if (e.getButton() == MouseEvent.BUTTON2) {
            // change to "move using mouse wheel button" and set the cursor
            setCursor(hndCursor);
            pp.setLocation(e.getPoint());
            ((JScrollPane)getParent().getParent()).setWheelScrollingEnabled(false);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        switch (e.getButton()) {
            case MouseEvent.BUTTON2:
                setCursor(defCursor);
                ((JScrollPane) getParent().getParent()).setWheelScrollingEnabled(true);
                break;
            case MouseEvent.BUTTON3:
                contextMenu.show(this, e.getX(), e.getY());
                clickLoc.setLocation(e.getX(), e.getY());
                break;
        }

    }
    
    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
    
    @Override
    public void mouseMoved(MouseEvent e) {}
    
    @Override
    public void mouseDragged(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
        } else if (SwingUtilities.isMiddleMouseButton(e)) {
            JViewport vport = (JViewport) getParent();
            Point cp = e.getPoint();
            Point vp = vport.getViewPosition();
            vp.translate(pp.x - cp.x, pp.y - cp.y);
            scrollRectToVisible(new Rectangle(vp, vport.getSize()));
            //pp.setLocation(cp);
        }
    }

    public NodeEditor getEditorParent() {
        return parent;
    }
    
    public void setEditorParent(NodeEditor parent) {
        this.parent = parent;
    }
    
    /**
     * Create a unique key for this connection based on implementation specifc
     * data (obj)
     * @param con the connection
     * @param obj implementation specific data
     * @return the key
     */
    public abstract String makeKeyForConnection(Connection con, Object obj);

    /**
     * Add a Connection to this Diagram.
     * Usecode: Call {@link #connect(com.jme3.gde.materialdefinition.editor.ConnectionEndpoint,
     * com.jme3.gde.materialdefinition.editor.ConnectionEndpoint) } where
     * possible instead.
     * @param conn The connection to add
     */
    public void addConnection(Connection conn) {
        connections.add(conn);
        add(conn);
        repaint();
    }

    /**
     * This is called when an Editor should be shown for that Node.
     * Called by {@link NodePanel#edit() }
     * @param node The node in question
     */
    protected abstract void showEdit(NodePanel node);

    public void notifyMappingCreation(Connection conn) {
        parent.makeMapping(conn);
    }

    public void addNode(NodePanel node) {
        add(node);
        node.setDiagram(this);
        nodes.add(node);
        setComponentZOrder(node, 0);
        node.addComponentListener(this);
    }

    protected void removeSelectedConnection(Selectable selectedItem) {        
        Connection selectedConnection = (Connection) selectedItem;
        removeConnection(selectedConnection);
        parent.notifyRemoveConnection(selectedConnection);
    }
    
    /**
     * Called when user pressed delete after having selected something
     */
    protected void removeSelected() {
        int result = JOptionPane.showConfirmDialog(null, "Delete all selected items, nodes and mappings?", "Delete Selected", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            for (Selectable selectedItem : selectedItems) {
                if (selectedItem instanceof NodePanel) {
                    removeSelectedNode(selectedItem);
                }
                if (selectedItem instanceof Connection) {
                    removeSelectedConnection(selectedItem);
                }
            }
            selectedItems.clear();
        }
    }

    /**
     * Called from {@link #removeSelected() } to also disconnect all the 
     * connections made to the node in question
     * 
     * @param selectedItem The item to remove
     */
    private void removeSelectedNode(Selectable selectedItem) {
        NodePanel selectedNode = (NodePanel) selectedItem;
        nodes.remove(selectedNode);
        for (Iterator<Connection> it = connections.iterator(); it.hasNext();) {
            Connection conn = it.next();
            if (conn.start.getNode() == selectedNode || conn.end.getNode() == selectedNode) {
                it.remove();
                conn.end.disconnect();
                conn.start.disconnect();
                remove(conn);
            }
        }

        selectedNode.cleanup();
        remove(selectedNode);
        repaint();
        parent.notifyRemoveNode(selectedNode);
    }

    public List<Selectable> getSelectedItems() {
        return selectedItems;
    }

    /**
     * Called by {@link ConnectionEndpoint} when a Curve has been dragged
     */
    protected void draggingDot(MouseEvent e) {}

    /**
     * Connect two Dots to form a Connection
     * @param start The Start
     * @param end The End
     * @return The Connection
     */
    public Connection connect(ConnectionEndpoint start, ConnectionEndpoint end) {
        Connection conn = new Connection(start, end);
        start.connect(conn);
        end.connect(conn);
        addConnection(conn);
        return conn;
    }

    /**
     * Find a panel which corresponds to the given key (unique id). Use this to
     * locate nodes on the diagram
     * 
     * @param key The key
     * @return hopefully the correct node
     */
    public NodePanel getNodePanel(String key) {
        for (NodePanel nodePanel: nodes) {
            if (nodePanel.getKey().equals(key)) {
                return nodePanel;
            }
        }
        return null;
    }

    /**
     * Selection from the editor. Select the item and notify the topComponent
     * @param selectable the item to select
     */
    public void select(Selectable selectable, boolean multi) {
        parent.selectionChanged(doSelect(selectable, multi));
    }
    
    /**
     * Move one of the selected panels by a given offset
     * @param movedPanel the panel in question
     * @param xOffset the movement in x direction
     * @param yOffset the movement in y direction
     */
    public void multiMove(DraggablePanel movedPanel, int xOffset, int yOffset) {
        for (Selectable selectedItem: selectedItems) {
            if (selectedItem != movedPanel) {
                if (selectedItem instanceof DraggablePanel) {
                    ((DraggablePanel)selectedItem).movePanel(xOffset, yOffset);
                }
            }
        }
    }

    /**
     * Prepare dragging multiple selected panels
     * @param movedPanel The Panel which has been moved.
     */
    public void multiStartDrag(DraggablePanel movedPanel){
        for (Selectable selectedItem: selectedItems) {
            if (selectedItem != movedPanel) {
                if (selectedItem instanceof DraggablePanel) {
                    ((DraggablePanel)selectedItem).saveLocation();
                }
            }
        }
    }
    
    /**
     * Select the specified item and repaint the window to reflect selection
     * outlines.
     *
     * @param selectable The item which shall be selected
     * @param multi Whether multiple selection is allowed or if this should
     * clear previous selections
     * @return The selected item
     */
    private Selectable doSelect(Selectable selectable, boolean multi) {
        if (!multi && !selectedItems.contains(selectable)) {
            selectedItems.clear();
        }
        if (selectable != null) {
            selectedItems.add(selectable);
        }
        if (selectable instanceof Component) {
            ((Component)selectable).requestFocusInWindow();
        }
        repaint();
        return selectable;
    }

    /**
     * Subclasses which add selectable items to the Diagram have to lookup 
     * items by their key and return them here for regular dragging/selecting
     * to work.<br>
     * If nothing was found (or you don't add custom elements to the diagram),
     * return null.
     * 
     * @param key the unique key
     * @return The Selectable item or null
     */
    protected abstract Selectable trySelect(String key);
    
    /**
     * find the item with the given key and select it without notifying the
     * topComponent. Since this iterates over all possible panels, subclasses
     * have to implement {@link #trySelect(java.lang.String) }
     *
     * @param key The unique key
     * @return The selected item
     */
    public Selectable select(String key) {
        for (NodePanel nodePanel: nodes) {
            if (nodePanel.getKey().equals(key)) {
                return doSelect(nodePanel, false);
            }
        }

        for (Connection connection: connections) {
            if (connection.getKey().equals(key)) {
                return doSelect(connection, false);
            }
        }
        
        Selectable s = trySelect(key);
        if (s != null) {
            return doSelect(s, false);
        }

        return null;
    }

    public void clear() {
        removeAll();
        connections.clear();
        nodes.clear();
    }

    /**
     * Creates a horizontal separator with a black background
     * @return the separator
     */
    protected JSeparator createSeparator() {
        JSeparator jsep = new JSeparator(JSeparator.HORIZONTAL);
        jsep.setBackground(Color.BLACK);
        return jsep;
    }
    
    /**
     * Creates a MenuItem with the given text and icon.<br>
     * In addition to calling the constructor this sets the font to Tahoma 10px.
     * 
     * @param text The text
     * @param icon The icon
     * @return The MenuItem with Tahoma as Font
     */
    protected JMenuItem createMenuItem(String text, Icon icon) {
        JMenuItem item = new JMenuItem(text, icon);
        item.setFont(new Font("Tahoma", 1, 10)); // NOI18N
        return item;
    }

    /**
     * Override this method to fill the popup/context menu available via 
     * right clicking the diagram.<br>
     * It's important to call this (super) first. It will setup fonts and borders.<br>
     * You can use {@link #createMenuItem(java.lang.String, javax.swing.Icon) }
     * and {@link #createSeparator() } as helper methods.
     */
    protected void createPopupMenu() {
        contextMenu.setFont(new Font("Tahoma", 1, 10)); // NOI18N
        contextMenu.setOpaque(true);
        Border titleUnderline = BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK);
        TitledBorder labelBorder = BorderFactory.createTitledBorder(
                titleUnderline, contextMenu.getLabel(),
                TitledBorder.LEADING, TitledBorder.ABOVE_TOP, contextMenu.getFont(), Color.BLACK);

        contextMenu.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        contextMenu.setBorder(BorderFactory.createCompoundBorder(contextMenu.getBorder(),
                labelBorder));
    }

    private void removeConnection(Connection selectedConnection) {
        connections.remove(selectedConnection);
        selectedConnection.end.disconnect();
        selectedConnection.start.disconnect();
        remove(selectedConnection);
    }

    /**
     * As part of the semi-automatical layout, the maximum height of the diagram
     * has to be calculated.<br>
     * That means for each custom panel (no nodes), calculate the location.y
     * and add the element's height.<br>
     * Then find the largest value over all the custom panels.
     * 
     * @return The maximum height for all custom elements (or 0 if none are
     * present).
     */
    protected abstract int calcMaxHeight();
    
    /**
     * As part of the semi-automatical layout, the maximum width of the diagram
     * has to be calculated.<br>
     * That means for each custom panel (no nodes), calculate the location.x
     * and add the element's width.<br>
     * Then find the largest value over all the custom panels.
     * 
     * @return The maximum width for all custom elements (or 0 if none are
     * present).
     */
    protected abstract int calcMaxWidth();
    
    /**
     * This is called on multiple occassions to ensure that the size of this 
     * diagram is just large enough.
     */
    public void fixSize() {
        int maxWidth = minWidth;
        int maxHeight = minHeight;

        for (NodePanel nodePanel : nodes) {
            int w = nodePanel.getLocation().x + nodePanel.getWidth() + 150;
            if (w > maxWidth) {
                maxWidth = w;
            }
            int h = nodePanel.getLocation().y + nodePanel.getHeight();
            if (h > maxHeight) {
                maxHeight = h;
            }
        }
        
        // Custom Nodes        
        int w = calcMaxWidth();
        int h = calcMaxHeight();
        
        if (w > maxWidth) {
            maxWidth = w;
        }
        
        if (h > maxHeight) {
            maxHeight = h;
        }
        
        setPreferredSize(new Dimension(maxWidth, maxHeight));
        revalidate();
    }

    /**
     * Use this method to layout your elements. Have a look at 
     * {@link ShaderNodeDiagram#autoLayout() } for an example. Maybe you can
     * come up with a better/easier solution
     */
    public abstract void autoLayout();
    
    @Override
    public void componentResized(ComponentEvent e) {
        minWidth = e.getComponent().getWidth() - 2;
        minHeight = e.getComponent().getHeight() - 2;
        fixSize();
    }

    @Override
    public void componentMoved(ComponentEvent e) {}

    @Override
    public void componentShown(ComponentEvent e) {}

    @Override
    public void componentHidden(ComponentEvent e) {}

}
