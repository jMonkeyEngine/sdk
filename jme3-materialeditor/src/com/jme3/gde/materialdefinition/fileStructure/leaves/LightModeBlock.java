/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.gde.materialdefinition.fileStructure.leaves;

import com.jme3.gde.materialdefinition.fileStructure.MatDefParseException;
import com.jme3.material.TechniqueDef;
import com.jme3.util.blockparser.Statement;

/**
 *
 * @author Nehon
 */
public class LightModeBlock extends LeafStatement {

    protected String lightMode;

    protected LightModeBlock(int lineNumber, String line) {
        super(lineNumber, line);
    }

    public LightModeBlock(Statement sta) {
        this(sta.getLineNumber(), sta.getLine());
        parse(sta);
         updateLine();
    }

    public LightModeBlock(String lightMode) {
        super(0, "");
        this.lightMode = lightMode;
        updateLine();
    }

    private void updateLine() {
        this.line = "LightMode " + lightMode;
    }

    public String getLightMode() {
        return lightMode;
    }

    public void setLightMode(String lightMode) {
        this.lightMode = lightMode;
        updateLine();
    }

    private void parse(Statement sta) {
        String[] split = sta.getLine().trim().split("\\s+");
        if (split.length < 2) {
            throw new MatDefParseException("LightMode statement is missing its value, expected \"LightMode <Mode>\"", sta.getLineNumber(), sta.getLine());
        }
        lightMode = split[1].trim();
        try {
            TechniqueDef.LightMode.valueOf(lightMode);
        } catch (IllegalArgumentException ex) {
            throw new MatDefParseException("Invalid light mode \"" + lightMode + "\", expected one of " + java.util.Arrays.toString(TechniqueDef.LightMode.values()), sta.getLineNumber(), sta.getLine(), ex);
        }
    }
}
