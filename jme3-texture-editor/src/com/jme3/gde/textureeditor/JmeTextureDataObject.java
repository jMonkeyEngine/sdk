/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.gde.textureeditor;

import com.jme3.gde.core.assets.AssetDataObject;
import com.jme3.gde.core.assets.ProjectAssetManager;
import com.jme3.gde.textureeditor.icons.Icons;
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
import org.openide.nodes.Node;
import org.openide.nodes.Children;
import org.openide.nodes.CookieSet;
import org.openide.util.Lookup;

@MIMEResolver.ExtensionRegistration(
        displayName = "jME3 Texture",
        mimeType = "image/x-jmetexture",
        extension = {"png", "bmp", "jpg", "PNG", "BMP", "JPG", "jpeg", "JPEG", "tga", "TGA"}
)
@DataObject.Registration(displayName = "jME3 Texture", mimeType = "image/x-jmetexture", iconBase = Icons.TEXTURE_BLUE)
public class JmeTextureDataObject extends AssetDataObject {

    public JmeTextureDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        CookieSet cookies = getCookieSet();
        if (getLookup().lookup(ProjectAssetManager.class) != null) {
            cookies.add((Node.Cookie) new OpenTexture(getPrimaryEntry()));
        }

    }

}
