package com.jme3.gde.glsl.highlighter;

import javax.swing.text.Document;
import org.netbeans.editor.Syntax;
import org.netbeans.modules.editor.NbEditorKit;

/**
 *
 * @author grizeldi
 */
public class GlslFragEditorKit extends NbEditorKit{
    public static final String MIME_TYPE = "text/x-glsl-frag";

    public GlslFragEditorKit() {
    }

    @Override
    public String getContentType() {
        return MIME_TYPE;
    }

    @Override
    public Syntax createSyntax(Document doc) {
        return new GlslSyntax();
    }    
}
