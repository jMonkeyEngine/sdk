package com.jme3.gde.glsl.highlighter;

import org.netbeans.editor.Syntax;
import org.netbeans.editor.TokenID;

/**
 * Defines what is what.
 * @author grizeldi
 */
public class GlslSyntax extends Syntax{

    @Override
    protected TokenID parseToken() {
        char c;
        while (offset < stopOffset){
            c = buffer[offset];
            if (isNumber(c))
                return GlslTokenContext.NUMBER;
            switch (c){
                case '\"':
                case '\'':
                    //String starts here
                    char starter = c;
                    while (true){
                        char previous = c;
                        offset ++;
                        c = buffer[offset];
                        
                        if (c == starter && previous != '\\')
                            break;
                    }
                    return GlslTokenContext.STRING;
                    
                case '/':
                    if (buffer[offset + 1] == '/'){
                        while (!(buffer[offset] == '\n' || buffer[offset] == '\r') && offset < stopOffset)
                            offset ++;
                        return GlslTokenContext.COMMENT;
                    }
            }
            offset ++;
        }
        return GlslTokenContext.TEXT;
    }
    
    private boolean isNumber(char c){
        switch (c){
            case '0':case '1':case '2':case '3':case '4':
            case '5':case '6':case '7':case '8':case '9':
                return true;
            default:
                return false;
        }
    }
}
