/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.gde.glsl.highlighter.recognizer.vert;

import java.io.IOException;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;

@Messages({
    "LBL_GLSLVert_LOADER=Files of GLSLVert"
})
@MIMEResolver.ExtensionRegistration(
        displayName = "#LBL_GLSLVert_LOADER",
        mimeType = "text/x-vert",
        extension = {"vert", "VERT"}
)
@DataObject.Registration(
        mimeType = "text/x-vert",
        iconBase = "com/jme3/gde/glsl/highlighter/recognizer/vert/vertIcon.gif",
        displayName = "#LBL_GLSLVert_LOADER",
        position = 300
)
@ActionReferences({
    @ActionReference(
            path = "Loaders/text/x-vert/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.OpenAction"),
            position = 100,
            separatorAfter = 200
    )
    ,
    @ActionReference(
            path = "Loaders/text/x-vert/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.CutAction"),
            position = 300
    )
    ,
    @ActionReference(
            path = "Loaders/text/x-vert/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.CopyAction"),
            position = 400,
            separatorAfter = 500
    )
    ,
    @ActionReference(
            path = "Loaders/text/x-vert/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.DeleteAction"),
            position = 600
    )
    ,
    @ActionReference(
            path = "Loaders/text/x-vert/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.RenameAction"),
            position = 700,
            separatorAfter = 800
    )
    ,
    @ActionReference(
            path = "Loaders/text/x-vert/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.SaveAsTemplateAction"),
            position = 900,
            separatorAfter = 1000
    )
    ,
    @ActionReference(
            path = "Loaders/text/x-vert/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.FileSystemAction"),
            position = 1100,
            separatorAfter = 1200
    )
    ,
    @ActionReference(
            path = "Loaders/text/x-vert/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.ToolsAction"),
            position = 1300
    )
    ,
    @ActionReference(
            path = "Loaders/text/x-vert/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.PropertiesAction"),
            position = 1400
    )
})
public class GLSLVertDataObject extends MultiDataObject {

    public GLSLVertDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        registerEditor("text/x-vert", false);
    }

    @Override
    protected int associateLookup() {
        return 1;
    }

}
