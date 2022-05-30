package com.jme3.gde.core.util.datatransfer;

import com.jme3.scene.Spatial;

/**
 * Generic interface for data transfer from files updated externally
 *
 * @author rickard
 */
public interface SpatialDataTransferInterface {

    /**
     * Performs the data transfer.
     *
     * @param root     the node containing the spatial to update
     * @param original spatial to update data from
     */
    abstract void update(Spatial root, Spatial original);
}
