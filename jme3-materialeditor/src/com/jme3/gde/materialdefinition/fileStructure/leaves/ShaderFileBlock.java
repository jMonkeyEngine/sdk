/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.gde.materialdefinition.fileStructure.leaves;

import com.jme3.gde.materialdefinition.fileStructure.MatDefParseException;
import com.jme3.util.blockparser.Statement;

/**
 *
 * @author Nehon
 */
public class ShaderFileBlock extends LeafStatement {

    protected String language;
    protected String path;

    protected ShaderFileBlock(int lineNumber, String line) {
        super(lineNumber, line);
    }

    public ShaderFileBlock(Statement sta) {
        this(sta.getLineNumber(), sta.getLine());
        parse(sta);
        updateLine();
    }

    public ShaderFileBlock(String language, String path, int lineNumber, String line) {
        super(lineNumber, line);
        this.language = language;
        this.path = path;
        updateLine();
    }

    public String getLanguage() {
        return language;
    }

    public String getPath() {
        return path;
    }

    protected void updateLine() {
        this.line = "Shader " + language + ": " + path;
    }

    private void parse(Statement sta) {
        String[] split = sta.getLine().trim().split("\\s+");
        if (split.length < 3) {
            throw new MatDefParseException("Shader statement is missing its language or path, expected \"Shader GLSL100: path/to/shader.vert\"", sta.getLineNumber(), sta.getLine());
        }
        String lastLanguage = split[split.length - 2];
        if (lastLanguage.endsWith(":")) {
            lastLanguage = lastLanguage.substring(0, lastLanguage.length() - 1);
        }
        language = lastLanguage;
        path = split[split.length - 1];
    }
}
