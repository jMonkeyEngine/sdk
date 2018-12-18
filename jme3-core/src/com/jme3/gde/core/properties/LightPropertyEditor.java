/*
 *  Copyright (c) 2018 jMonkeyEngine
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
package com.jme3.gde.core.properties;

import com.jme3.gde.core.scene.SceneApplication;
import com.jme3.gde.core.sceneexplorer.nodes.JmeLight;
import com.jme3.light.Light;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import org.netbeans.api.project.Project;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;

/**
 * Allows the Property Editor to display and select
 * {@link com.jme3.light.Light} class instances.
 * 
 * @author MeFisto94
 */


public class LightPropertyEditor implements PropertyEditor {
    private final LinkedList<PropertyChangeListener> listeners = new LinkedList<PropertyChangeListener>();
    private JmeLight jmeLight;
    private Light li;
    private Project proj;

    public LightPropertyEditor() {
    }

    public LightPropertyEditor(JmeLight jmeLight, Project project) {
        this.jmeLight = jmeLight;
        this.li = jmeLight.getLookup().lookup(Light.class);
        this.proj = project;
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof Light) {
            li = (Light) value;
        }
    }

    @Override
    public Object getValue() {
        return li;
    }

    @Override
    public boolean isPaintable() {
        return false;
    }

    @Override
    public void paintValue(Graphics gfx, Rectangle box) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public String getJavaInitializationString() {
        return null;
    }

    @Override
    public String getAsText() {
        if (li == null) {
            return "null";
        } else {
            return li.getName();
        }
    }

    @Override
    public void setAsText(final String text) throws IllegalArgumentException {
        if(li != null && li.getName().equals(text)){
            return;
        }
        
        Light old = li;
        final Light[] new_light = new Light[1];
        
        /* @TODO / Note: The Ideal way would be to lookup the SceneComposerTopComponent
        * and then query it's currently active request, but we can't do that because
        * otherwise the core would depend on the scenecomposer. Instead we'll
        * search the rootNode (which might be the root of all models. Setting
        * the name is discouraged anyway.
        */

        //ProjectAssetManager manager = (ProjectAssetManager) proj.getLookup().lookup(ProjectAssetManager.class);
        try {            
            SceneApplication.getApplication().enqueue(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    SceneApplication.getApplication().getRootNode().breadthFirstTraversal(
                            new SceneGraphVisitor() {
                                @Override
                                public void visit(Spatial sptl) {
                                    for (Light l : sptl.getLocalLightList()) {
                                        if (l.getName() != null && l.getName().equals(text)) {
                                            new_light[0] = l;
                                        }
                                    }
                                }
                            }
                    );
                    
                    return null;
                }
            }).get();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        // since I don't trust the array to be initialized with null, also check the text
        if (new_light[0] != null && new_light[0].getName().equals(text)) {
            li = new_light[0];
        } else {
            DialogDisplayer.getDefault().notifyLater(
                new NotifyDescriptor.Message("The entered Light Name \"" + text
                + "\" could not be found. Ensure that you have the correct "
                + "Scene opened and did not mistype the name."));
        }

        if (li != old) {
            notifyListeners(old, li);
        }
    }

    @Override
    public String[] getTags() {
        return null;
    }

    @Override
    public Component getCustomEditor() {
        return null; // new ParticleInfluencerPicker(null, true, this, jmeLight);
    }

    @Override
    public boolean supportsCustomEditor() {
        return false; // @TODO: Support
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(Light before, Light after) {
        for (PropertyChangeListener propertyChangeListener : listeners) {
            propertyChangeListener.propertyChange(new PropertyChangeEvent(this, null, before, after));
        }
    }
}
