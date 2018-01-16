package com.jme3.gde.glsl.highlighter;

import org.netbeans.editor.BaseTokenID;
import org.netbeans.editor.TokenContext;
import org.netbeans.editor.TokenContextPath;
import org.netbeans.editor.Utilities;

/**
 * Defines differently highlighted groups.
 * @author grizeldi
 */
public class GlslTokenContext extends TokenContext{
    public static final int KEYWORD_ID = 1;
    public static final int VARIABLE_ID = 2;
    public static final int COMMENT_ID = 3;
    public static final int PREPROCESSOR_ID = 4;
    public static final int NUMBER_ID = 5;
    public static final int STRING_ID = 6;
    
    public static final int TEXT_ID = 1000;
    
    public static final BaseTokenID KEYWORD = new BaseTokenID("keyword", KEYWORD_ID);
    public static final BaseTokenID VARIABLE = new BaseTokenID("variable", VARIABLE_ID);
    public static final BaseTokenID COMMENT = new BaseTokenID("comment", COMMENT_ID);
    public static final BaseTokenID PREPROCESSOR = new BaseTokenID("preprocessor", PREPROCESSOR_ID);
    public static final BaseTokenID NUMBER = new BaseTokenID("number", NUMBER_ID);
    public static final BaseTokenID STRING = new BaseTokenID("string", STRING_ID);
    
    public static final BaseTokenID TEXT = new BaseTokenID("other", TEXT_ID);
    
    public static final GlslTokenContext context = new GlslTokenContext();
    public static final TokenContextPath contextPath = context.getContextPath();
    
    public GlslTokenContext() {
        super("glsl-");
        
        try {
            addDeclaredTokenIDs();
        } catch (Exception e){
            Utilities.annotateLoggable(e);
        }
    }
    
}
