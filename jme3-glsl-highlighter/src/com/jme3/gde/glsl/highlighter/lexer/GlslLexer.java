/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.gde.glsl.highlighter.lexer;

import java.util.logging.Logger;
import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;
import org.netbeans.spi.lexer.TokenFactory;

/**
 *
 * @author grizeldi
 */
public class GlslLexer implements Lexer<GlslTokenID>{
    private final LexerInput lexerInput;
    private final TokenFactory tokenFactory;
    private final Logger log = Logger.getLogger(this.getClass().getCanonicalName());
    
    private String thisLineSoFar = "";

    public GlslLexer(LexerRestartInfo info) {
        lexerInput = info.input();
        tokenFactory = info.tokenFactory();
    }

    @Override
    public Token<GlslTokenID> nextToken() {
        int c;
        c = lexerInput.read();
        thisLineSoFar += (char)c;
        log.fine("This line so far: " + thisLineSoFar + " Current char: " + (char)c);
        if (isDigit(c)){
            while (true){
                int next = lexerInput.read();
                if (!isDigit(next)){
                    if (next == '.' || next == 'f' || next == 'F')
                        continue;
                    lexerInput.backup(1);
                    return token(GlslTokenID.NUMBER);
                }
            }
        }
        switch (c){
            case '/':
                int next = lexerInput.read();
                if (next == '/'){
                    //It's an inline comment
                    readTillNewLine();
                    return token(GlslTokenID.INLINE_COMMENT);
                }else if (next == '*'){
                    while (true){
                        int c1 = lexerInput.read();
                        if (c1 == '*'){
                            if (lexerInput.read() == '/')
                                return token(GlslTokenID.BLOCK_COMMENT);
                            else
                                lexerInput.backup(1);
                        }else if (c1 == LexerInput.EOF)
                            return token(GlslTokenID.BLOCK_COMMENT);
                    }
                }else
                    lexerInput.backup(1);
                return token(GlslTokenID.OPERATOR);
            case '\"':
            case '\'':
                //String starts here
                int previous = c, starter = c;
                while (true){
                    int now = lexerInput.read();

                    if (now == starter && previous != '\\')
                        break;
                    previous = now;
                }
                return token(GlslTokenID.STRING);
            case '#':
                if (thisLineSoFar.trim().equals("#")){
                    //Preprocessor code
                    readTillNewLine();
                    return token(GlslTokenID.PREPROCESSOR);
                }
                return token(GlslTokenID.OPERATOR);
            case '|':
            case '&':
            case '.':
            case '>':
            case '<':
            case ',':
            case ';':
            case ':':
            case '=':
            case '+':
            case '-':
            case '*':
            case '%':
            case '!':
            case '~':
            case '^':
            case '\\':
                return token(GlslTokenID.OPERATOR);
            //Those have to be recognized separately for closing bracket recognition
            case '(':return token(GlslTokenID.LPARENTHESIS);
            case ')':return token(GlslTokenID.RPARENTHESIS);
            case '{':return token(GlslTokenID.LBRACKET);
            case '}':return token(GlslTokenID.RBRACKET);
            case '[':return token(GlslTokenID.LSQUARE);
            case ']':return token(GlslTokenID.RSQUARE);
            case '\n':
            case '\r':
                thisLineSoFar = "";
                return token(GlslTokenID.NEW_LINE);
            case LexerInput.EOF:
                return null;
        }
        return token(GlslTokenID.TEXT);
    }

    @Override
    public Object state() {
        return null;
    }

    //Honestly, I have no idea what is this.
    @Override
    public void release() {}
    
    private Token<GlslTokenID> token(GlslTokenID id){
        return tokenFactory.createToken(id);
    }
    
    private boolean isDigit(int c){
        switch (c){
            case'0':case'1':case'2':case'3':case'4':
            case'5':case'6':case'7':case'8':case'9':
                return true;
            default:
                return false;
        }
    }
    
    private void readTillNewLine(){
        while (true){
            int in = lexerInput.read();
            if (in == '\n' || in == '\r' || in == LexerInput.EOF){
                lexerInput.backup(1);
                break;
            }
        }
    }
}
