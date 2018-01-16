/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.gde.glsl.highlighter;

import com.jme3.gde.glsl.highlighter.lexer.GlslLanguageHierarchy;
import com.jme3.gde.glsl.highlighter.lexer.GlslTokenID;
import org.netbeans.api.lexer.Language;
import org.netbeans.modules.csl.spi.DefaultLanguageConfig;
import org.netbeans.modules.csl.spi.LanguageRegistration;

/**
 *
 * @author grizeldi
 */
@LanguageRegistration(mimeType = "text/x-glsl")
public class GlslLanguage extends DefaultLanguageConfig{
    @Override
    public Language getLexerLanguage() {
        return new GlslLanguageHierarchy().language();
    }

    @Override
    public String getDisplayName() {
        return "GLSL - OpenGL Shading Language";
    }
    
    public static Language<GlslTokenID> getLanguage(){
        return new GlslLanguageHierarchy().language();
    }
}
