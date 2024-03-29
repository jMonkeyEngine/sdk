/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.gde.gui.palette;

import jada.ngeditor.guiviews.DND.WidgetData;
import jada.ngeditor.model.GUIFactory;
import jada.ngeditor.model.elements.GElement;
import jada.ngeditor.model.exception.NoProductException;
import java.awt.Image;
import java.awt.datatransfer.Transferable;
import java.io.IOException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/**
 *
 * @author cris
 */
public class WidgetNode extends AbstractNode{
    private final Class<?extends GElement> clazz;
    private static final String basePath="com/jme3/gde/gui/multiview/icons";

    public WidgetNode(Class<?extends GElement> wrappedClass) {
        super(Children.LEAF);
        this.clazz = wrappedClass;
        String name = wrappedClass.getSimpleName();
        this.setName(name);
        this.setIconBaseWithExtension(basePath+"/"+name+".png");
    }

    @Override
    public Image getIcon(int type) {
        return super.getIcon(type); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public Transferable drag() throws IOException {
        try {
            final GElement gElement = GUIFactory.getInstance().newGElement(clazz);
           return new WidgetData(gElement);
        } catch (NoProductException ex) {
            throw new IOException(ex);
        }
    }
    
    
    
    
}
