package com.jme3.gde.core.util;

import com.jme3.scene.Spatial;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Finds a previously marked spatial in the supplied root Spatial, creates
 * the name and path to be looked for from the given needle Spatial.
 */
public class TaggedSpatialFinder {
    
    private static final Logger LOGGER =
            Logger.getLogger(TaggedSpatialFinder.class.getName());
    
    /**
     * Finds a previously marked spatial in the supplied root Spatial, creates
     * the name and path to be looked for from the given needle Spatial.
     *
     * @param root   supplied root Spatial
     * @param needle spatial to look for
     * @return found spatial
     */
    public Spatial find(final Spatial root, final Spatial needle) {
        if (needle == null) {
            LOGGER.log(Level.WARNING, "Trying to find null needle for {0}",
                    root);
            return null;
        }
        final String name = needle.getName();
        final String path = SpatialUtil.getSpatialPath(needle);
        if (name == null) {
            LOGGER.log(Level.WARNING, "Trying to find tagged Spatial with "
                    + "null name spatial for {0}.", root);
            return null;
        }
        final Class<? extends Spatial> clazz = needle.getClass();
        final String rootName = root.getUserData(SpatialUtil.ORIGINAL_NAME);
        final String rootPath = root.getUserData(SpatialUtil.ORIGINAL_PATH);
        if (name.equals(rootName) && path.equals(rootPath)) {
            return root;
        }
        final SpatialHolder holder = new SpatialHolder();
        root.depthFirstTraversal(spatial -> {
            final String spatialName =
                    spatial.getUserData(SpatialUtil.ORIGINAL_NAME);
            final String spPath =
                    spatial.getUserData(SpatialUtil.ORIGINAL_PATH);
            if (name.equals(spatialName) && path.equals(spPath) && clazz.isInstance(spatial)) {
                if (holder.spatial == null) {
                    holder.spatial = spatial;
                } else {
                    LOGGER.log(Level.WARNING, "Found Spatial {0} twice in"
                            + " {1}", new Object[]{path, root});
                }
            }
        });
        return holder.spatial;
    }

    private static class SpatialHolder {
        private Spatial spatial;
    }
}
