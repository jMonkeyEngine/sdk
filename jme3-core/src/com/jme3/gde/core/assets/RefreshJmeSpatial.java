
package com.jme3.gde.core.assets;

import com.jme3.gde.core.sceneexplorer.nodes.JmeNode;
import org.openide.nodes.Node;

import com.jme3.gde.core.sceneexplorer.nodes.JmeSpatial;

/**
 * Work around for refresh not working recursively on JmeSpatial
 * @author rickard
 */
public class RefreshJmeSpatial implements Runnable {
    
    private final JmeNode rootNode;
    private final String spatialName;
    
    public RefreshJmeSpatial(JmeNode rootNode, String spatialName) {
        this.rootNode = rootNode;
        this.spatialName = spatialName;
    }
    
    @Override
    public void run() {
        refreshNamedSpatial(rootNode, spatialName);
    }
    /**
     * Look for the spatial to update using the name of the asset
     * @param spatial
     * @param name 
     */
    private void refreshNamedSpatial(JmeSpatial spatial, String name){
        if(spatial.getName().equals(name)){
            recurseRefresh(spatial);
        } else {
            for(Node s: spatial.getChildren().getNodes()){
                if(s instanceof JmeSpatial jmeSpatial){
                    refreshNamedSpatial(jmeSpatial, name);
                }
                
            }
        }
    }
    
    /**
     * Refreshes the spatial and all children
     * @param spatial 
     */
    private void recurseRefresh(JmeSpatial spatial){
        spatial.refresh(false);
        for(Node s: spatial.getChildren().getNodes()){
            if(s instanceof JmeSpatial jmeSpatial){
                recurseRefresh(jmeSpatial);
            }
        }
    }

}
