package com.jme3.gde.core.util.datatransfer;

import com.jme3.gde.core.util.SpatialUtil;
import com.jme3.gde.core.util.TaggedSpatialFinder;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitorAdapter;
import com.jme3.scene.Spatial;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Copies mesh data from an updated external file to the spatial.
 */
public class CopyMeshDataFromOriginal implements SpatialDataTransferInterface {

    private static final Logger LOGGER =
            Logger.getLogger(CopyMeshDataFromOriginal.class.getName());

    private final TaggedSpatialFinder finder;

    public CopyMeshDataFromOriginal(final TaggedSpatialFinder finder) {
        this.finder = finder;
    }

    @Override
    public void update(final Spatial root, final Spatial original) {
        //loop through original to also find new geometry
        original.depthFirstTraversal(new SceneGraphVisitorAdapter() {
            @Override
            public void visit(Geometry geom) {
                //will always return same class type as 2nd param, so casting
                // is safe
                final Geometry spat = (Geometry) finder.find(root, geom);
                if (spat != null) {
                    spat.setMesh(geom.getMesh());
                    LOGGER.log(Level.INFO, "Updated mesh for Geometry "
                            + "{0}", geom.getName());
                } else {
                    addLeafWithNonExistingParents(root, geom);
                }
            }
        });
    }

    /**
     * Adds a leaf to a spatial, including all non-existing parents.
     *
     * @param root
     * @param leaf
     */
    private void addLeafWithNonExistingParents(final Spatial root, 
            final Spatial leaf) {
        if (!(root instanceof Node)) {
            LOGGER.log(Level.WARNING, "Cannot add new Leaf {0} to {1}, is not"
                    + " a Node!", new Object[]{leaf.getName(), root.getName()});
            return;
        }
        for (Spatial s = leaf; s.getParent() != null; s = s.getParent()) {
            final Spatial parent = s.getParent();
            final Spatial other = finder.find(root, parent);
            if (other == null) {
                continue;
            }
            if (other instanceof Node) {
                LOGGER.log(Level.INFO, "Attaching {0} to {1} in root {2} to "
                        + "add leaf {3}", new Object[]{s, other, root, leaf});
                //set original path data to leaf and new parents
                for (Spatial spt = leaf; spt != parent; spt = spt.getParent()) {
                    // this is to avoid a crash when changing
                    // names of meshes externally
                    if (spt == null) {
                        return;
                    }
                    spt.setUserData(SpatialUtil.ORIGINAL_NAME, spt.getName());
                    spt.setUserData(SpatialUtil.ORIGINAL_PATH,
                            SpatialUtil.getSpatialPath(spt));
                    spt = spt.getParent();
                }
                //attach to new node in own root
                Node otherNode = (Node) other;
                otherNode.attachChild(s);
                LOGGER.log(Level.INFO, "Attached Node {0} with leaf "
                        + "{0}", new Object[]{other.getName(), leaf.getName()});
                return;
            } else {
                LOGGER.log(Level.WARNING, "Cannot attach leaf {0} to found "
                                + "spatial {1} in root {2}, not a node.",
                        new Object[]{leaf, other, root});
            }
        }
        LOGGER.log(Level.WARNING, "Could not attach new Leaf {0}, no root "
                + "node found.", leaf.getName());
    }
}
