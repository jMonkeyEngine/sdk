/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.gde.glsl.highlighter.recognizer;

import java.awt.Image;
import java.io.IOException;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

@Messages({
    "LBL_Glsl_LOADER=OpenGL Shader Language files"
})
@MIMEResolver.ExtensionRegistration(
        displayName = "#LBL_Glsl_LOADER",
        mimeType = "text/x-glsl",
        extension = {"frag", "FRAG", "vert", "VERT", "glsllib", "GLSLLIB"}
)
@DataObject.Registration(
        mimeType = "text/x-glsl",
        displayName = "#LBL_Glsl_LOADER",
        position = 300
)
@ActionReferences({
    @ActionReference(
            path = "Loaders/text/x-glsl/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.OpenAction"),
            position = 100,
            separatorAfter = 200
    )
    ,
    @ActionReference(
            path = "Loaders/text/x-glsl/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.CutAction"),
            position = 300
    )
    ,
    @ActionReference(
            path = "Loaders/text/x-glsl/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.CopyAction"),
            position = 400,
            separatorAfter = 500
    )
    ,
    @ActionReference(
            path = "Loaders/text/x-glsl/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.DeleteAction"),
            position = 600
    )
    ,
    @ActionReference(
            path = "Loaders/text/x-glsl/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.RenameAction"),
            position = 700,
            separatorAfter = 800
    )
    ,
    @ActionReference(
            path = "Loaders/text/x-glsl/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.SaveAsTemplateAction"),
            position = 900,
            separatorAfter = 1000
    )
    ,
    @ActionReference(
            path = "Loaders/text/x-glsl/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.FileSystemAction"),
            position = 1100,
            separatorAfter = 1200
    )
    ,
    @ActionReference(
            path = "Loaders/text/x-glsl/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.ToolsAction"),
            position = 1300
    )
    ,
    @ActionReference(
            path = "Loaders/text/x-glsl/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.PropertiesAction"),
            position = 1400
    )
})
public class GlslDataObject extends MultiDataObject {

    public GlslDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        registerEditor("text/x-glsl", false);
    }

    @Override
    protected int associateLookup() {
        return 1;
    }

    @Override
    protected Node createNodeDelegate() {
        return new GlslNode(getLookup());
    }
    
    //We need a custom node in order to get a dynamic icon for some reason
    private class GlslNode extends DataNode {
        private final Image fragIcon = ImageUtilities.loadImage("com/jme3/gde/glsl/highlighter/recognizer/fragIcon.png"),
                vertIcon = ImageUtilities.loadImage("com/jme3/gde/glsl/highlighter/recognizer/vertIcon.png"),
                defaultIcon = ImageUtilities.loadImage("org/netbeans/modules/java/resources/annotation_file.png");
        
        public GlslNode(Lookup lookup) {
            super(GlslDataObject.this, Children.LEAF, lookup);
        }        

        @Override
        public Image getIcon(int type) {
            if (getPrimaryFile().getExt().toLowerCase().equals("frag"))
                return fragIcon;
            else if (getPrimaryFile().getExt().toLowerCase().equals("vert"))
                return vertIcon;
            return defaultIcon;
        }
    }
}
