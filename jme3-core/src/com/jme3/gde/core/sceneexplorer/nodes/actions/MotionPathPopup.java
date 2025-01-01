/*
 *  Copyright (c) 2009-2024 jMonkeyEngine
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
package com.jme3.gde.core.sceneexplorer.nodes.actions;

import com.jme3.gde.core.scene.SceneApplication;
import com.jme3.gde.core.scene.controller.SceneToolController;
import com.jme3.gde.core.sceneexplorer.SceneExplorerTopComponent;
import com.jme3.gde.core.sceneexplorer.nodes.AbstractSceneExplorerNode;
import com.jme3.gde.core.sceneexplorer.nodes.JmeMotionPath;
import com.jme3.gde.core.sceneexplorer.nodes.JmeVector3f;
import com.jme3.gde.core.sceneexplorer.nodes.JmeVector3fChildren;
import com.jme3.math.Vector3f;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.actions.Presenter;

/**
 * This is the Popup which enables or disables Debug Shapes of the Motion Path.
 * Also it allows you to add new Waypoints
 * @author MeFisto94
 */
public class MotionPathPopup extends AbstractAction implements Presenter.Popup {

    protected JmeMotionPath jmeMotionPath;
    protected Node node;
    protected DataObject dataObject;

    public MotionPathPopup(JmeMotionPath path) {
        this.jmeMotionPath = path;
        this.node = path.getLookup().lookup(Node.class);
        this.dataObject = path.getLookup().lookup(DataObject.class);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public JMenuItem getPopupPresenter() {
        JMenu result = new JMenu("Debug Shapes");
        result.add(new JMenuItem(getShowAction()));
        result.add(new JMenuItem(getHideAction()));
        
        return result;
    }
    
    public AbstractAction getShowAction() {
        return new AbstractAction("Show") {
            @Override
            public void actionPerformed(ActionEvent e) {
                jmeMotionPath.enableDebugShapes();
            }
        };
    }

    public AbstractAction getHideAction() {
        return new AbstractAction("Hide") {
            @Override
            public void actionPerformed(ActionEvent e) {
                jmeMotionPath.disableDebugShapes();
            }
        };
    }
    
    /**
     * This is the Add Waypoint Action. It resides in this Popup Class, however
     * it's not added to the Debug Shapes Popup. Instead it is added as a seperate action
     * @return 
     */
    public AbstractAction getAddAction() {
        return new AbstractAction("Add Waypoint") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Vector3f pos;
                
                SceneToolController controller = SceneApplication.getApplication().getStateManager().getState(SceneToolController.class);
                 // Vector3f.ZERO means not yet clicked
                if (controller != null && (!controller.getCursorLocation().equals(Vector3f.ZERO))) {
                    pos = controller.getCursorLocation().clone().addLocal(0, jmeMotionPath.getDebugBoxExtents() * 3f, 0); // Shifting up so a) Netbeans isn't merging Waypoints and b) it's visible
                } else {
                    AbstractSceneExplorerNode[] nodes = SceneExplorerTopComponent.findInstance().getLastSelected();
                    if(nodes == null || nodes.length == 0) {
                        return;
                    }
                    final AbstractSceneExplorerNode node = nodes[0];
                    if (node instanceof JmeVector3f jmeVector3f) {
                        pos = jmeVector3f.getVector3f().clone().addLocal(0, jmeMotionPath.getDebugBoxExtents() * 3f, 0);
                    } else {
                        // Default is a bit over the Center
                        pos = new Vector3f(0f, 1.0f, 0f);
                    }
                }
                
                JmeVector3fChildren children = (JmeVector3fChildren)jmeMotionPath.getChildren();
                JmeVector3f jme = new JmeVector3f(children, jmeMotionPath, pos);
                children.add(new Node[] {jme});
                jmeMotionPath.refreshChildren();
            }
        };
    }
}
