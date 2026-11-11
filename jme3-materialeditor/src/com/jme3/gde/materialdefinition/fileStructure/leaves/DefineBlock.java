package com.jme3.gde.materialdefinition.fileStructure.leaves;

import com.jme3.gde.materialdefinition.fileStructure.MatDefParseException;
import com.jme3.util.blockparser.Statement;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles one row of Defines in a MaterialDef, ie ALBEDOMAP : AlbedoMap
 * @author rickard
 */
public final class DefineBlock extends LeafStatement {

    private String name;
    private String define;

    protected DefineBlock(int lineNumber, String line) {
        super(lineNumber, line);
    }

    public DefineBlock(Statement sta) {
        this(sta.getLineNumber(), sta.getLine());
        parse(sta);
        updateLine();
    }

    public DefineBlock(String name, String define) {
        super(0, "");
        this.name = name;
        this.define = define;
        updateLine();
    }

    private void updateLine() {
        this.line = String.format("%s : %s", name, define == null ? "" : define);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        updateLine();
    }

    public String getDefine() {
        return define;
    }

    public void setDefine(String define) {
        this.define = define;
        updateLine();
    }

    private void parse(Statement sta) {
        String content = sta.getLine();
        int commentIndex = content.indexOf("//");
        String comment = null;
        if (commentIndex != -1) {
            comment = content.substring(commentIndex + 2).trim();
            content = content.substring(0, commentIndex);
        }
        String[] values = content.split(":", 2);
        name = values[0].trim();
        if (name.isEmpty()) {
            throw new MatDefParseException("Define has no name, expected \"DEFINE_NAME : MaterialParam\"", sta.getLineNumber(), sta.getLine());
        }
        if (values.length < 2 || values[1].trim().isEmpty()) {
            // Older j3md files (and some hand written ones) contain defines
            // without a mapped material parameter, e.g. "HAS_COLORMAP // comment".
            // The engine treats these as defines that are always set, so we
            // keep the line but leave the define unmapped.
            define = null;
            if (comment != null && !comment.isEmpty()) {
                Logger.getLogger(DefineBlock.class.getName()).log(Level.WARNING,
                        "Define \"{0}\" at line {1} has no material parameter mapping (missing \": ParamName\"), it will always be set. Comment: {2}",
                        new Object[]{name, sta.getLineNumber(), comment});
            } else {
                Logger.getLogger(DefineBlock.class.getName()).log(Level.WARNING,
                        "Define \"{0}\" at line {1} has no material parameter mapping (missing \": ParamName\"), it will always be set.",
                        new Object[]{name, sta.getLineNumber()});
            }
            return;
        }
        define = values[1].trim();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.name);
        hash = 67 * hash + Objects.hashCode(this.define);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DefineBlock)) {
            return false;
        }
        return ((DefineBlock) obj).getName().equals(name) && Objects.equals(((DefineBlock) obj).getDefine(), define);
    }

}
