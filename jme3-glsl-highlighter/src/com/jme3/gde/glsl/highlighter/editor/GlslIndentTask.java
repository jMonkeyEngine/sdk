/*
 * Copyright (c) 2003-2018 jMonkeyEngine
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
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
package com.jme3.gde.glsl.highlighter.editor;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.modules.editor.indent.api.IndentUtils;
import org.netbeans.modules.editor.indent.spi.Context;
import org.netbeans.modules.editor.indent.spi.ExtraLock;
import org.netbeans.modules.editor.indent.spi.IndentTask;

/**
 *
 * @author grizeldi
 */
public class GlslIndentTask implements IndentTask {

    private Context context;

    public GlslIndentTask(Context context) {
        this.context = context;
    }

    @Override
    public void reindent() throws BadLocationException {
        context.setCaretOffset(1);
        final Document doc = context.document();
        int indentModifier = 0;

        //Check if previous line ends with a {
        int previousLineLength = context.startOffset() - 1 - context.lineStartOffset(context.startOffset() - 1);
        String previousLine = doc.getText(context.lineStartOffset(context.startOffset() - 1), previousLineLength);

        //Hook other reasons for changes in indentation into this for loop
        for (int i = previousLineLength - 1; i >= 0; i--) {
            if (previousLine.charAt(i) == '}') {
                break;
            } else if (previousLine.charAt(i) == '{') {
                indentModifier += IndentUtils.indentLevelSize(doc);
                break;
            }
        }
        int previousLineIndent = context.lineIndent(context.lineStartOffset(context.startOffset() - 1));
        context.modifyIndent(context.startOffset(), previousLineIndent + indentModifier);
    }

    @Override
    public ExtraLock indentLock() {
        return null;
    }
}
