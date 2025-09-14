/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jme3.gde.core.dnd;

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
 * Handles dropping Materials or Spatial from the AssetBrowser to the
 * SceneViewer
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

        AssetNameHolder transferableObj = null;
        Transferable transferable = null;
        DataFlavor flavor = null;

        try {
            transferable = dtde.getTransferable();
            final DataFlavor[] flavors = transferable.getTransferDataFlavors();

            flavor = flavors[0];
            // What does the Transferable support
            if (transferable.isDataFlavorSupported(flavor)) {
                transferableObj = (AssetNameHolder) dtde.getTransferable().getTransferData(flavor);
            }

        } catch (UnsupportedFlavorException | IOException ex) {
            Logger.getLogger(SceneViewerDropTargetListener.class.getName()).log(Level.WARNING, "Non-supported flavor {0}", transferable);
        }

        if (transferable == null || transferableObj == null) {
            return;
        }

        final int dropYLoc = dtde.getLocation().y;
        final int dropXLoc = dtde.getLocation().x;

        if (flavor instanceof SpatialDataFlavor) {
            rootPanel.addModel(transferableObj.getAssetName(), new Vector2f(dropXLoc, dropYLoc));
        } else if (flavor instanceof MaterialDataFlavor) {
            rootPanel.applyMaterial(transferableObj.getAssetName(), new Vector2f(dropXLoc, dropYLoc));
        } else {
            // Handle generic flavors by determining asset type from file extension
            String assetName = transferableObj.getAssetName();
            if (assetName != null) {
                String extension = getFileExtension(assetName);
                if (isModelExtension(extension)) {
                    rootPanel.addModel(assetName, new Vector2f(dropXLoc, dropYLoc));
                } else if (isMaterialExtension(extension)) {
                    rootPanel.applyMaterial(assetName, new Vector2f(dropXLoc, dropYLoc));
                }
            }
        }

    }

    /**
     * Extracts the file extension from an asset name.
     * @param filename The asset filename
     * @return The file extension in lowercase, or empty string if no extension
     */
    private String getFileExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }

    /**
     * Determines if the file extension represents a model/spatial asset.
     * @param extension The file extension
     * @return true if the extension is for model files
     */
    private boolean isModelExtension(String extension) {
        return "j3o".equals(extension);
    }

    /**
     * Determines if the file extension represents a material asset.
     * @param extension The file extension  
     * @return true if the extension is for material files
     */
    private boolean isMaterialExtension(String extension) {
        return "j3m".equals(extension);
    }

}
