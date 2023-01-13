/*
 *  Copyright (c) 2009-2016 jMonkeyEngine
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
package com.jme3.gde.core.sceneexplorer.nodes;

import com.jme3.gde.core.icons.IconList;
import com.jme3.gde.core.scene.SceneApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import java.awt.Image;
import java.io.IOException;
import java.util.concurrent.Callable;
import javax.swing.Action;
import org.openide.actions.DeleteAction;
import org.openide.actions.MoveDownAction;
import org.openide.actions.MoveUpAction;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.actions.SystemAction;

/**
 * This Class actually represents the MotionPaths Waypoints in the
 * SceneComposer.<br>
 * It is added and managed by {@link JmeVector3fChildren } but it could also be
 * used for any other Waypointish Thing
 *
 * @author MeFisto94
 */
@SuppressWarnings({"unchecked", "rawtypes", "OverridableMethodCallInConstructor", "LeakingThisInConstructor"})
public class JmeVector3f extends JmeSpatial {

    private static Image smallImage = IconList.track.getImage();
    private JmeVector3fChildren parent;
    private JmeMotionPath jmeMotionPath;
    private Vector3f v;
    private float extents;

    public JmeVector3f() {
        super();
    }

    public JmeVector3f(JmeVector3fChildren parent, JmeMotionPath jmeMotionPath, Vector3f vec) {
        super();

        v = vec;
        this.parent = parent;
        this.jmeMotionPath = jmeMotionPath;
        spatial = generateBox();

        getLookupContents().add(spatial);
        getLookupContents().add(vec);
        getLookupContents().add(this);

        super.setDisplayName("Waypoint");
        super.setName(spatial.getName());

        attachBox(spatial, jmeMotionPath);
    }

    /**
     * GenerateBox will simply generate our {@link DebugBoxGeometry }
     *
     * @return the generated DebugBoxGeometry
     */
    private Geometry generateBox() {
        extents = jmeMotionPath.getDebugBoxExtents();
        DebugBoxGeometry geom = new DebugBoxGeometry("Waypoint", extents, this);

        Material mat = new Material(SceneApplication.getApplication().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Cyan);
        geom.setMaterial(mat);
        geom.setInternalLocalTranslation(v);

        return geom;
    }

    /**
     * AttachBox is the internal method simply used to attach the DebugBox to
     * the Scene Graph.
     *
     * @param s The Spatial to attach
     * @param jmeMotionPath The Parental Node to refresh.
     */
    protected void attachBox(final Spatial s, final JmeMotionPath jmeMotionPath) {
        SceneApplication.getApplication().enqueue(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                SceneApplication.getApplication().getRootNode().attachChild(s);
                jmeMotionPath.refresh(true);
                return null;
            }
        });
    }

    /**
     * This detaches the DebugBox from the SceneGraph but also waits for this to
     * happen.<br>This is because we want to reattach a new box and it's just
     * better this way :P
     *
     * @param s The Spatial to detach
     */
    protected void detachBox(final Spatial s) {
        SceneApplication.getApplication().enqueue(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                s.removeFromParent();
                return null;
            }
        });
    }

    /**
     * This will trigger a rebuild of the whole Box.<br>It is used internally if
     * the box dimensions have changed ({@link JmeMotionPath#setDebugBoxExtents(float)
     * }).<br>Moving the Boxes around does NOT need a rebuild.
     */
    public void updateBox() {
        getLookupContents().remove(spatial);
        detachBox(spatial);
        spatial = generateBox();
        getLookupContents().add(spatial);
        attachBox(spatial, jmeMotionPath);
    }

    /**
     * This is called when the Vector v has been moved (without the setVector3f
     * which is our callback).<br>This means it was moved by the Properties
     * Dialog (see setXYZ())
     */
    public void moveBox() {
        SceneApplication.getApplication().enqueue(new Runnable() {
            @Override
            public void run() {
                spatial.setLocalTranslation(v); // It's a bit redundant since it will call #setVector3f() but there's no other way.
                // Plus it will invoke updateSpline() for us.
            }
        });
    }

    // <editor-fold desc="Just some Overrides for Node">
    @Override
    public Image getIcon(int type) {
        return smallImage;
    }

    @Override
    public Image getOpenedIcon(int type) {
        return smallImage;
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{
            SystemAction.get(DeleteAction.class),
            SystemAction.get(MoveUpAction.class),
            SystemAction.get(MoveDownAction.class)
        };
    }

    @Override
    public boolean canRename() {
        return true;
    }

    @Override
    public boolean canDestroy() {
        return true;
    }

    @Override
    public boolean canCut() {
        return false;
    }

    @Override
    public boolean canCopy() {
        return false;
    }

    @Override
    public void destroy() throws IOException {
        detachBox(spatial);

        /* These are mandatory:
         * Without them the Node looks like it's undeletable
         * (since it stays in the Motion Path and gets readded everytime)
         */
        parent.remove(new Node[]{this});
        parent.refreshChildren(true);
        jmeMotionPath.updateSpline(true);
        super.destroy();
    }
    // </editor-fold>

    /* For Properties */
    public int getChildIndex() {
        int idx = parent.indexOf(this);
        setDisplayName("Waypoint " + idx);
        return idx;
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        set.setDisplayName("Vector3f");
        set.setName(Vector3f.class.getName());
        set.setShortDescription("These are the Properties of the Motion Paths's Waypoint (Vector3f). Feel free to either edit the floats seperately or use the [x, y, z] way. Make sure that you defocus and focus this Node again in order to have the Properties be reloaded");

        if (v == null) {
            return sheet;
        }

        Property p = makeEmbedProperty(this, getExplorerNodeClass(), float.class, "getX", "setX", "X");
        p.setShortDescription("The Vector3f's X-Value");
        set.put(p);

        p = makeEmbedProperty(this, getExplorerNodeClass(), float.class, "getY", "setY", "Y");
        p.setShortDescription("The Vector3f's Y-Value");
        set.put(p);

        p = makeEmbedProperty(this, getExplorerNodeClass(), float.class, "getZ", "setZ", "Z");
        p.setShortDescription("The Vector3f's Z-Value");
        set.put(p);

        p = makeEmbedProperty(this, getExplorerNodeClass(), int.class, "getChildIndex", null, "Child Index");
        p.setShortDescription("The Index of this Node inside of the MotionPath's Children");
        set.put(p);

        createFields(Vector3f.class, set, v);

        sheet.put(set);
        return sheet;
    }

    /**
     * This is a conveniance method to access the internal Vector3f.
     *
     * @return The Vector3f representated by this Node
     */
    public Vector3f getVector3f() {
        return v;
    }

    /**
     * This is the callback which will be called by the Geometry. It is used to
     * update the DataStructure with the Debug Box Position
     *
     * @param to
     */
    public void setVector3f(Vector3f to) {
        v.set(to);
    }

    /* The following 6 methods are just so we know when the user typed in some properties.
     * Note: A PropertyChangeListener would also be appropriate and even less code.
     */
    //<editor-fold desc="The Setters for the Properties Panel">
    public void setX(float x) {
        //v.x = x;
        v.setX(x);
        moveBox();
    }

    public void setY(float y) {
        v.y = y;
        moveBox();
    }

    public void setZ(float z) {
        v.z = z;
        moveBox();
    }

    public float getX() {
        return v.x;
    }

    public float getY() {
        return v.y;
    }

    public float getZ() {
        return v.z;
    }
//</editor-fold>

    @Override
    public Class getExplorerObjectClass() {
        return Vector3f.class;
    }

    @Override
    public Class getExplorerNodeClass() {
        return JmeVector3f.class;
    }

    @Override
    public org.openide.nodes.Node[] createNodes(Object key, DataObject key2, boolean cookie) {
        return null;
    }

    private class DebugBoxGeometry extends Geometry {

        JmeVector3f self;

        public DebugBoxGeometry(String s, float extents, JmeVector3f jme) {
            super(s, new Box(extents, extents, extents));
            self = jme;
        }

        @Override
        public void setLocalTranslation(float x, float y, float z) {
            this.setLocalTranslation(new Vector3f(x, y, z));
        }

        @Override
        public void setLocalTranslation(Vector3f localTranslation) {
            self.setVector3f(localTranslation);
            self.jmeMotionPath.updateSpline(true); // This also triggers setModified() to have it saved.
            super.setLocalTranslation(localTranslation);
        }

        /**
         * Since the usual translation updates the spline and hence triggers
         * setModified, we need an internal method (e.g. for the Constructor)
         *
         * @param localTranslation the translation to set.
         */
        public void setInternalLocalTranslation(Vector3f localTranslation) {
            super.setLocalTranslation(localTranslation);
        }
    }
}
