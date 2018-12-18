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

import java.awt.Point;

/**
 * The Top Component (the one containing the diagram) has to implement this
 * interface.
 * @author MeFisto94
 */
public interface NodeEditor {
    
    /**
     * Load the position of the given node (by key) from the meta data. That
     * enables saving the node layout inbetween uses.
     * @param key The key/nodeId
     * @param defaultx The default x position (if key is not found)
     * @param defaulty The default y position (if key is not found)
     * @return The position on the screen
     * @throws NumberFormatException if the metadata contained invalid integers
     */
    Point getPositionFromMetaData(String key, int defaultx, int defaulty) throws NumberFormatException;
    
    /**
     * Save the position of the given node (by key) from the meta data. That
     * enables saving the node layout inbetween uses.
     * @param key The key/nodeId
     * @param x The X Position
     * @param y The Y Position
     */
    void savePositionToMetaData(String key, int x, int y);
    
    /**
     * Transfer the UI Mapping to the Model part of your editor, that means:
     * apply the new connection to the underlying structures
     * @param conn The connection which has been formed
     */
    void makeMapping(Connection conn);
    
    /**
     * The connection has been removed, unlink it again
     * @param conn The connection which used to be
     */
    void notifyRemoveConnection(Connection conn);
    
    /**
     * Called when a node has been removed from the diagram
     * @param node the node
     */
    void notifyRemoveNode(NodePanel node);
    
    /**
     * Called when a node/connection has been selected. Use this to change the
     * Properties Dialog and others.
     * @param selectable The selected item
     */
    void selectionChanged(Selectable selectable);
}
