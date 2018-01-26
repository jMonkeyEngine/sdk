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
package com.jme3.gde.glsl.highlighter.lexer;

import java.util.logging.Logger;
import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;
import org.netbeans.spi.lexer.TokenFactory;

/**
 * Tokenizes the text the user is typing, so we can highlight it as needed.
 *
 * @author grizeldi
 */
public class GlslLexer implements Lexer<GlslTokenID> {

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
        thisLineSoFar += (char) c;
        if (isDigit(c)) {
            while (true) {
                int next = lexerInput.read();
                if (!isDigit(next)) {
                    if (next == '.' || next == 'f' || next == 'F') {
                        continue;
                    }
                    lexerInput.backup(1);
                    return token(GlslTokenID.NUMBER);
                }
            }
        }
        switch (c) {
            case '/':
                int next = lexerInput.read();
                if (next == '/') {
                    //It's an inline comment
                    readTillNewLine();
                    return token(GlslTokenID.INLINE_COMMENT);
                } else if (next == '*') {
                    while (true) {
                        int c1 = lexerInput.read();
                        if (c1 == '*') {
                            if (lexerInput.read() == '/') {
                                return token(GlslTokenID.BLOCK_COMMENT);
                            } else {
                                lexerInput.backup(1);
                            }
                        } else if (c1 == LexerInput.EOF) {
                            return token(GlslTokenID.BLOCK_COMMENT);
                        }
                    }
                } else {
                    lexerInput.backup(1);
                }
                return token(GlslTokenID.OPERATOR);
            case '\"':
            case '\'':
                //String starts here
                int previous = c,
                 starter = c;
                while (true) {
                    int now = lexerInput.read();

                    if (now == starter && previous != '\\') {
                        break;
                    }
                    previous = now;
                }
                return token(GlslTokenID.STRING);
            case '#':
                if (thisLineSoFar.trim().equals("#")) {
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
            case '(':
                return token(GlslTokenID.LPARENTHESIS);
            case ')':
                return token(GlslTokenID.RPARENTHESIS);
            case '{':
                return token(GlslTokenID.LBRACKET);
            case '}':
                return token(GlslTokenID.RBRACKET);
            case '[':
                return token(GlslTokenID.LSQUARE);
            case ']':
                return token(GlslTokenID.RSQUARE);
            case '\n':
            case '\r':
                thisLineSoFar = "";
                return token(GlslTokenID.NEW_LINE);
            case LexerInput.EOF:
                return null;
            default:
                //Text, gotta look it up the library
                String word = "" + (char) c;
                if (GlslKeywordLibrary.lookup(word) != null) {
                    GlslKeywordLibrary.KeywordType current;
                    while (true) {
                        word += (char) lexerInput.read();
                        current = GlslKeywordLibrary.lookup(word);
                        if (current != GlslKeywordLibrary.KeywordType.UNFINISHED) {
                            if (current == null) {
                                break;
                            }
                            char nextChar = (char) lexerInput.read();
                            lexerInput.backup(1);
                            if (GlslKeywordLibrary.lookup(word + nextChar) == null /*&& (
                                    nextChar == ' ' || nextChar == '(' || nextChar == '\n' || nextChar == '\r'
                                    || nextChar == ';' || nextChar == ',' || nextChar == '.')*/) { //Define here what is allowed to be directly behind a keyword
                                if (current == GlslKeywordLibrary.KeywordType.BASIC_TYPE && (//What can be behind a primitve
                                        nextChar == ' ' || nextChar == '(' || nextChar == '\n'
                                        || nextChar == '\r')) {
                                    break;
                                }
                                if (current == GlslKeywordLibrary.KeywordType.KEYWORD && (//What can be behind a keyword
                                        nextChar == ' ' || nextChar == '{' || nextChar == ':'
                                        || nextChar == ';' || nextChar == '(' || nextChar == ')'
                                        || nextChar == '\n' || nextChar == '\r')) {
                                    break;
                                }
                                if (current == GlslKeywordLibrary.KeywordType.BUILTIN_VARIABLE && (//What can be behind a builtin variable
                                        nextChar == ';' || nextChar == '.' || nextChar == ' '
                                        || nextChar == ',' || nextChar == '\n' || nextChar == '\r'
                                        || nextChar == ')')) {
                                    break;
                                }
                                if (current == GlslKeywordLibrary.KeywordType.BUILTIN_FUNCTION && (//What can be behind a builtin functions
                                        nextChar == '(' || nextChar == '\n' || nextChar == '\r')) {
                                    break;
                                }
                            }
                        }
                    }
                    if (current == null) {
                        break;
                    }
                    switch (current) {
                        case BASIC_TYPE:
                            return token(GlslTokenID.PRIMITIVE);
                        case KEYWORD:
                            return token(GlslTokenID.KEYWORD);
                        case BUILTIN_VARIABLE:
                            return token(GlslTokenID.BUILTIN_VARIABLE);
                        case BUILTIN_FUNCTION:
                            return token(GlslTokenID.BUILTIN_FUNCTION);
                    }
                }
        }
        return token(GlslTokenID.TEXT);
    }

    @Override
    public Object state() {
        return null;
    }

    //Honestly, I have no idea what is this.
    @Override
    public void release() {
    }

    private Token<GlslTokenID> token(GlslTokenID id) {
        return tokenFactory.createToken(id);
    }

    private boolean isDigit(int c) {
        switch (c) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                return true;
            default:
                return false;
        }
    }

    private void readTillNewLine() {
        while (true) {
            int in = lexerInput.read();
            if (in == '\n' || in == '\r' || in == LexerInput.EOF) {
                lexerInput.backup(1);
                break;
            }
        }
    }
}
