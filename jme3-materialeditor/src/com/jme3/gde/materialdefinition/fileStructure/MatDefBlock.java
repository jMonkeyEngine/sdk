/*
 *  Copyright (c) 2009-2018 jMonkeyEngine
 *  All rights reserved.
 * 
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are
 *  met:
 * 
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 
 *  * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 *  TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *  PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme3.gde.materialdefinition.fileStructure;

import com.jme3.gde.materialdefinition.fileStructure.leaves.MatParamBlock;
import com.jme3.gde.materialdefinition.fileStructure.leaves.UnsupportedStatement;
import com.jme3.util.blockparser.Statement;
import java.util.List;

/**
 * A Statement in ShaderNodes which contains information about the material def
 * (e.g. MatParams or Techniques)
 * @author Nehon
 */
public class MatDefBlock extends UberStatement {

    public static final String ADD_MAT_PARAM = "addMatParam";
    public static final String REMOVE_MAT_PARAM = "removeMatParam";
    protected String name;

    protected MatDefBlock(int lineNumber, String line) {
        super(lineNumber, line);
    }

    public MatDefBlock(Statement sta) {
        this(sta.getLineNumber(), sta.getLine());
        for (Statement statement : sta.getContents()) {
            if (statement.getLine().trim().startsWith("MaterialParameters")) {
                addStatement(new MaterialParametersBlock(statement));
            } else if (statement.getLine().trim().startsWith("Technique")) {
                addStatement(new TechniqueBlock(statement));
            } else {
                addStatement(new UnsupportedStatement(statement));
            }
        }
        String[] s = line.split("\\s");
        name = s[1];
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String oldName = this.name;
        this.name = name;
        line = "MaterialDef " + name;
        fire("name", oldName, name);
    }

    protected MaterialParametersBlock getMaterialParameters() {
        return getBlock(MaterialParametersBlock.class);
    }

    public List<MatParamBlock> getMatParams() {
        return getMaterialParameters().getMatParams();
    }

    public void addMatParam(MatParamBlock matParam) {
        MaterialParametersBlock mpBlock = getMaterialParameters();
        if (mpBlock == null) {
            mpBlock = new MaterialParametersBlock(0, "MaterialParameters");
            addStatement(0, mpBlock);
        }
        mpBlock.addMatParam(matParam);
        fire(ADD_MAT_PARAM, null, matParam);
    }

    public void removeMatParam(MatParamBlock matParam) {
        MaterialParametersBlock mpBlock = getMaterialParameters();
        if (mpBlock == null) {
            return;
        }
        mpBlock.removeMatParam(matParam);

        for (TechniqueBlock techniqueBlock : getTechniques()) {
            VertexShaderNodesBlock vblock = techniqueBlock.getBlock(VertexShaderNodesBlock.class);
            FragmentShaderNodesBlock fblock = techniqueBlock.getBlock(FragmentShaderNodesBlock.class);
            techniqueBlock.cleanMappings(vblock, "MatParam", matParam.getName());
            techniqueBlock.cleanMappings(fblock, "MatParam", matParam.getName());
        }
        fire(REMOVE_MAT_PARAM, matParam, null);
    }

    public List<TechniqueBlock> getTechniques() {
        return getBlocks(TechniqueBlock.class);
    }

    public void addTechnique(TechniqueBlock techniqueBlock) {
        addStatement(techniqueBlock);
        fire("technique", null, techniqueBlock);
    }
}
