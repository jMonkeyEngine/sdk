/*
 * Copyright (c) 2009-2026 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme3.gde.textureeditor;

import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.cookies.CloseCookie;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.OpenSupport;
import org.openide.util.NbBundle;
import org.openide.windows.CloneableTopComponent;

@NbBundle.Messages("CTL_OpenTexture=Edit Texture")
@ActionID(id = "com.jme3.gde.textureeditor.OpenTexture", category = "Window")
@ActionReferences({
    @ActionReference(path = "Loaders/image/png/Actions", position = 0),
    @ActionReference(path = "Loaders/image/jpeg/Actions", position = 0),
    @ActionReference(path = "Loaders/image/image/x-jmetexture/Actions",     position = 0),
    @ActionReference(path = "Loaders/text/plain/Actions",       position = 0),
})
public final class OpenTexture extends OpenSupport implements OpenCookie, CloseCookie {

    public OpenTexture(JmeTextureDataObject.Entry entry) {
        super(entry);
    }
    
    @Override
    protected CloneableTopComponent createCloneableTopComponent() {
        DataObject dobj = entry.getDataObject();
        FileObject file = dobj.getPrimaryFile();
        ImageEditorTopComponent display = new ImageEditorTopComponent(file);
        return display;
    }

}
