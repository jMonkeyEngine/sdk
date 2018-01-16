/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.gde.glsl.highlighter.lexer;

import java.util.Collection;
import java.util.EnumSet;
import org.netbeans.spi.lexer.LanguageHierarchy;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

/**
 *
 * @author grizeldi
 */
public class GlslLanguageHierarchy extends LanguageHierarchy<GlslTokenID>{

    @Override
    protected Collection<GlslTokenID> createTokenIds() {
        return EnumSet.allOf(GlslTokenID.class);
    }

    @Override
    protected Lexer<GlslTokenID> createLexer(LexerRestartInfo<GlslTokenID> info) {
        return new GlslLexer(info);
    }

    @Override
    protected String mimeType() {
        return "text/x-glsl";
    }
    
}
