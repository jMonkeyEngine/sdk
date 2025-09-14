/*
 *  Copyright (c) 2009-2025 jMonkeyEngine
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
package com.jme3.gde.core.dnd;

import com.jme3.gde.core.assets.AssetDataNode;
import com.jme3.gde.core.sceneviewer.SceneViewerTopComponent;
import com.jme3.math.Vector2f;
import java.awt.Cursor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles dropping Materials or Spatial from the AssetBrowser and Projects tab
 * to the SceneViewer
 *
 * @author rickard
 */
public class SceneViewerDropTargetListener implements DropTargetListener {

    private static final Cursor droppableCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    private static final Cursor notDroppableCursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);

    private final SceneViewerTopComponent rootPanel;

    public SceneViewerDropTargetListener(final SceneViewerTopComponent rootPanel) {
        this.rootPanel = rootPanel;
    }

    @Override
    public void dragEnter(final DropTargetDragEvent dtde) {
    }

    @Override
    public void dragOver(final DropTargetDragEvent dtde) {
        if (!this.rootPanel.getCursor().equals(droppableCursor)) {
            this.rootPanel.setCursor(droppableCursor);
        }
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
    }

    @Override
    public void dragExit(final DropTargetEvent dte) {
        this.rootPanel.setCursor(notDroppableCursor);
    }

    @Override
    public void drop(final DropTargetDropEvent dtde) {
        this.rootPanel.setCursor(Cursor.getDefaultCursor());
        String assetKey = null;
        Transferable transferable = null;
        DataFlavor flavor = null;

        try {
            transferable = dtde.getTransferable();
            final DataFlavor[] flavors = transferable.getTransferDataFlavors();

            flavor = flavors[0];
            // What does the Transferable support
            if (transferable.isDataFlavorSupported(flavor)) {
                Object o = dtde.getTransferable().getTransferData(flavor);
                if (o instanceof AssetNameHolder assetNameHolder) {
                    assetKey = assetNameHolder.getAssetName();
                } else if (o instanceof AssetDataNode assetDataNode) {
                    assetKey = assetDataNode.getAssetName();
                }
            }

        } catch (UnsupportedFlavorException | IOException ex) {
            Logger.getLogger(SceneViewerDropTargetListener.class.getName()).log(Level.WARNING, "Non-supported flavor {0}", transferable);
        }

        if (transferable == null || assetKey == null) {
            return;
        }

        final int dropYLoc = dtde.getLocation().y;
        final int dropXLoc = dtde.getLocation().x;

        if (flavor instanceof SpatialDataFlavor || isModelExtension(assetKey)) {
            rootPanel.addModel(assetKey, new Vector2f(dropXLoc, dropYLoc));
        } else if (flavor instanceof MaterialDataFlavor || isMaterialExtension(assetKey)) {
            rootPanel.applyMaterial(assetKey, new Vector2f(dropXLoc, dropYLoc));
        }

    }

    /**
     * Determines if the file extension represents a model/spatial asset.
     *
     * @param extension The file extension
     * @return true if the extension is for model files
     */
    private boolean isModelExtension(String assetKey) {
        return assetKey.endsWith("j3o");
    }

    /**
     * Determines if the file extension represents a material asset.
     *
     * @param extension The file extension
     * @return true if the extension is for material files
     */
    private boolean isMaterialExtension(String assetKey) {
        return assetKey.endsWith("j3m");
    }

}
