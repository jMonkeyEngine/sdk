/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.gde.glsl.highlighter.lexer;

import org.netbeans.api.lexer.TokenId;

/**
 *
 * @author grizeldi
 */
public enum GlslTokenID implements TokenId{
    KEYWORD("keywords"),
    INLINE_COMMENT("comments"),
    STRING("string"),
    SPACE("whitespace"),
    NEW_LINE("whitespace"),
    TEXT("generic"),
    NUMBER("primitives");

    private final String category;

    private GlslTokenID(String category) {
        this.category = category;
    }

    @Override
    public String primaryCategory() {
        return category;
    }
}
