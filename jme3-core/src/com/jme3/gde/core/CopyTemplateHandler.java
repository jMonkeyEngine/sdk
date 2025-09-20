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
package com.jme3.gde.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.templates.CreateDescriptor;
import org.netbeans.api.templates.CreateFromTemplateHandler;
import org.openide.filesystems.FileObject;

/**
 * This class will handle all .j3o templates to be simply copied.
 * Starting with NetBeans 8.1 they changed the default action for templates to be using their template engine to parse them.
 * This lead to Encoding Errors since our binary files are, well, binary.
 * Unfortunately the only way to change this default behavior were switches inside the template or the Path (i.e. being placed under java)
 * Also their COPY Action even relies on the StringBuffer, so same issue here.
 * 
 * See https://github.com/jMonkeyEngine/sdk/issues/33
 * @author MeFisto94
 */
@org.openide.util.lookup.ServiceProvider(service=CreateFromTemplateHandler.class)
public class CopyTemplateHandler extends CreateFromTemplateHandler {

    @Override
    protected boolean accept(CreateDescriptor cd) {
        String ext = cd.getTemplate().getExt();
        return ext.startsWith("j3") || ext.equals("blend"); /* Add your own binary extensions here !! */
    }

    @Override
    protected List<FileObject> createFromTemplate(CreateDescriptor cd) throws IOException {
        ArrayList<FileObject> list = new ArrayList<FileObject>();
        FileObject newFile = cd.getTemplate().copy(cd.getTarget(), cd.getProposedName(), cd.getTemplate().getExt()); /* Proposed Name is getName() + default (when name is null) */
        list.add(newFile);
        return list;
    }
}
