/*
 *  Copyright (c) 2009-2010 jMonkeyEngine
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

import com.jme3.gde.core.filters.AbstractFilterNode;
import com.jme3.gde.core.properties.SceneExplorerProperty;
import com.jme3.gde.core.properties.ScenePropertyChangeListener;
import com.jme3.gde.core.scene.SceneSyncListener;
import com.jme3.gde.core.util.DynamicLookup;
import com.jme3.gde.core.util.PropertyUtils;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.openide.loaders.DataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author normenhansen
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class AbstractSceneExplorerNode extends AbstractNode implements SceneExplorerNode, ScenePropertyChangeListener, SceneSyncListener {

    protected Children jmeChildren;
    protected final InstanceContent lookupContents;
    protected boolean readOnly = false;
    protected DataObject dataObject;
    private final List<Property<?>> sceneProperties = Collections.synchronizedList(new LinkedList<Property<?>>());

    public AbstractSceneExplorerNode() {
        this(Children.LEAF, null);
    }

    public AbstractSceneExplorerNode(Children children, DataObject dataObject) {
        super(children, new ProxyLookup(dataObject != null ? dataObject.getLookup() : Lookup.EMPTY, new DynamicLookup(new InstanceContent())));
        this.dataObject = dataObject;
        this.jmeChildren = children;
        lookupContents = getLookup().lookup(DynamicLookup.class).getInstanceContent();
    }

    public AbstractSceneExplorerNode(DataObject dataObject) {
        this(Children.LEAF, dataObject);
    }

    public AbstractSceneExplorerNode(Children children) {
        this(children, (children instanceof JmeSpatialChildren) ? ((JmeSpatialChildren) children).getDataObject() : null);
    }

    public InstanceContent getLookupContents() {
        return lookupContents;
    }

    public AbstractSceneExplorerNode addToLookup(Object obj) {
        lookupContents.add(obj);
        return this;
    }

    protected void fireSave(boolean modified) {
        if (dataObject != null) {
            dataObject.setModified(true);
        }
    }
    
    /**
     * returns the PropertySet with the given name
     * @param name
     * @return The PropertySet or null if no PropertySet by that name exists
     */
    public PropertySet getPropertySet(String name) {
        for (int i = 0; i < getPropertySets().length; i++) {
            PropertySet propertySet = getPropertySets()[i];
            if (propertySet.getName().equals(name)) {
                return propertySet;
            }
        }
        return null;
    }

    /**
     * Set this Node as read-only which means it cannot be deleted, cutted or copied.
     * These are the actions you will see in the Context Menu of this Node.
     * @param readOnly true/false
     * @return Returns this Node for conveniance
     */
    public AbstractSceneExplorerNode setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        return this;
    }

    //TODO: refresh does not work
    public void refresh(boolean immediate) {
        if (jmeChildren instanceof JmeSpatialChildren) {
            ((JmeSpatialChildren) jmeChildren).refreshChildren(immediate);
        }
    }

    public void refreshProperties() {
        setSheet(createSheet());
    }

    /**
     * Create a Property to be used in the Properties Sheet. This is actually a helper method for you
     * See {@link #makeProperty(java.lang.Object, java.lang.Class, java.lang.String, java.lang.String, java.lang.String) } to specify the getter/setter name
     * 
     * @param obj The object of which you want to edit the Properties
     * @param returntype The Type (Class) of the getter/setter
     * @param method The Name of the Getter/setter Methods (guesses for getValue() and setValue(value))
     * @param name The Name of this Property.
     * @return The created Property to use for set.put()
     */
    protected Property<?> makeProperty(Object obj, Class<?> returntype, String method, String name) {
        Property<?> prop = null;
        try {
            prop = new SceneExplorerProperty(getExplorerObjectClass().cast(obj), returntype, method, null);
            prop.setName(name);
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
        return prop;
    }

    /**
     * Create a Property to be used in the Properties Sheet. This is actually a helper method for you
     * See {@link #makeEmbedProperty(java.lang.Object, java.lang.Class, java.lang.Class, java.lang.String, java.lang.String, java.lang.String) } if you get a ClassCastException.
     * This happens when the getter/setter isn't part of obj but rather of a sub variable
     * 
     * @param obj The object of which you want to edit the Properties
     * @param returntype The Type (Class) of the getter/setter
     * @param method The name of the getter method
     * @param setter The name of the setter method
     * @param name The name for this Property. 
     * @return The created Property to use for set.put()
     */
    protected Property<?> makeProperty(Object obj, Class<?> returntype, String method, String setter, String name) {
        Property<?> prop = null;
        try {
            if (readOnly) {
                prop = new SceneExplorerProperty(getExplorerObjectClass().cast(obj), returntype, method, null);
            } else {
                prop = new SceneExplorerProperty(getExplorerObjectClass().cast(obj), returntype, method, setter, this);
            }
            prop.setName(name);

        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
        return prop;
    }

    /**
     * Create a Property to be used in the Properties Sheet. This is actually a helper method for you
     * See {@link #makeProperty(java.lang.Object, java.lang.Class, java.lang.String, java.lang.String, java.lang.String)  }
     * Note: Embed is, when the Property isn't directly derived from obj but a getter.
     * 
     * @param obj The Object which contains this Property
     * @param objectClass The Class of obj (when not "embed", we take the Nodes' Type here. So this is for "multi-level" properties)
     * @param returntype The Type (Class) of the Property
     * @param method The Getter Method
     * @param setter The Setter Method (or null, if read only)
     * @param name The Name to be displayed under Properties
     * @return The created Property
     * @see AbstractFilterNode#createFields(java.lang.Class, org.openide.nodes.Sheet.Set, java.lang.Object) 
     * @see AbstractFilterNode#createMethods(java.lang.Class, org.openide.nodes.Sheet.Set, java.lang.Object, java.lang.reflect.Method[]) 
     */
    protected Property<?> makeEmbedProperty(Object obj, Class objectClass, Class returntype, String method, String setter, String name) {
        Property<?> prop = null;
        try {
            if (readOnly || setter == null) {
                prop = new SceneExplorerProperty(objectClass.cast(obj), returntype, method, null);
            } else {
                prop = new SceneExplorerProperty(objectClass.cast(obj), returntype, method, setter, this);
            }
            prop.setName(name);

        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
        return prop;
    }

    /**
     * Scans the passed object for all it's fields and adds them to the Property
     * Sheet Set.
     * It will however fail when getters/setters are missing or not following the Convention.
     * It will just skip those fields then.
     * 
     * @param c The Class of obj
     * @param set The Properties Sheet-Set you want to add things to
     * @param obj The Object for the properties
     * @throws SecurityException 
     * @see AbstractFilterNode#createFields(java.lang.Class, org.openide.nodes.Sheet.Set, java.lang.Object) 
     * @see AbstractFilterNode#createMethods(java.lang.Class, org.openide.nodes.Sheet.Set, java.lang.Object, java.lang.reflect.Method[]) 
     */
    protected void createFields(Class<?> c, Sheet.Set set, Object obj) throws SecurityException {
        for (Field field : c.getDeclaredFields()) {
            PropertyDescriptor prop = PropertyUtils.getPropertyDescriptor(c, field);
            if (prop != null) {
                set.put(makeProperty(obj, prop.getPropertyType(), prop.getReadMethod().getName(), prop.getWriteMethod().getName(), prop.getDisplayName()));
            }
        }
    }
    
    /**
     * Scans the passed object for all it's fields and adds them to the Property
     * Sheet Set.
     * See {@link #createFields(java.lang.Class, org.openide.nodes.Sheet.Set, java.lang.Object) }.
     * Embed additionally takes the object's type over this Nodes. (Compare makeEmbedProperty and makeProperty)
     * 
     * @param c The Class to create Properties for
     * @param set The Properties Sheet-Set you want to add things to
     * @param obj The Object to take the Properties from
     * @throws SecurityException
     * @see AbstractFilterNode#createFields(java.lang.Class, org.openide.nodes.Sheet.Set, java.lang.Object) 
     * @see AbstractFilterNode#createMethods(java.lang.Class, org.openide.nodes.Sheet.Set, java.lang.Object, java.lang.reflect.Method[]) 
     */
    protected void createEmbedFields(Class<?> c, Sheet.Set set, Object obj) throws SecurityException {
        for (Field field : c.getDeclaredFields()) {
            PropertyDescriptor prop = PropertyUtils.getPropertyDescriptor(c, field);
            if (prop != null) {
                set.put(makeEmbedProperty(obj, c, prop.getPropertyType(), prop.getReadMethod().getName(), prop.getWriteMethod().getName(), prop.getDisplayName()));
            }
        }
    }

    @Override
    protected Sheet createSheet() {
        return Sheet.createDefault();
    }
    
    @Override
    public void syncSceneData(float tpf) {
        //TODO: precache structure to avoid locks? Do it backwards, sending the actual bean value?
        for (PropertySet propertySet : getPropertySets()) {
            for (Property<?> property : propertySet.getProperties()) {
                if(property instanceof SceneExplorerProperty){
                    SceneExplorerProperty<?> prop = (SceneExplorerProperty<?>)property;
                    prop.syncValue();
                }
            }
        }
    }

    @Override
    public void propertyChange(final String type, final String name, final Object before, final Object after) {
        if (SceneExplorerProperty.PROP_USER_CHANGE.equals(type)) {
            fireSave(true);
            firePropertyChange(name, before, after);
        } else if (SceneExplorerProperty.PROP_SCENE_CHANGE.equals(type)) {
            firePropertyChange(name, before, after);
        } else if (SceneExplorerProperty.PROP_INIT_CHANGE.equals(type)) {
            firePropertyChange(name, before, after);
        }
    }

    @Override
    public Class<?> getExplorerNodeClass() {
        return this.getClass();
    }

    @Override
    public abstract Class getExplorerObjectClass();

    @Override
    public Node[] createNodes(Object key, DataObject dataObject, boolean readOnly) {
        return new Node[]{Node.EMPTY};
    }
}
