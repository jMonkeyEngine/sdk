/*
 * Copyright (c) 2009-2026 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
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
package com.jme3.gde.materialdefinition.fileStructure;

import com.jme3.gde.materialdefinition.fileStructure.leaves.DefineBlock;
import com.jme3.gde.materialdefinition.fileStructure.leaves.FragmentShaderFileBlock;
import com.jme3.gde.materialdefinition.fileStructure.leaves.VertexShaderFileBlock;
import com.jme3.util.blockparser.BlockLanguageParser;
import com.jme3.util.blockparser.Statement;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Tests parsing of Defines rows in j3md files, including the older format
 * where a define has no mapped material parameter (see jMonkeyEngine/sdk#650).
 *
 * @author Nehon
 */
public class DefineBlockTest {

    private static Statement statement(int lineNumber, String line) {
        return new Statement(lineNumber, line);
    }

    @Test
    public void testStandardDefine() {
        DefineBlock block = new DefineBlock(statement(3, "HAS_COLORMAP : ColorMap"));
        assertEquals("HAS_COLORMAP", block.getName());
        assertEquals("ColorMap", block.getDefine());
    }

    @Test
    public void testDefineWithComment() {
        DefineBlock block = new DefineBlock(statement(4, "HAS_GLOWMAP : GlowMap // Texture of the glowing parts"));
        assertEquals("HAS_GLOWMAP", block.getName());
        assertEquals("GlowMap", block.getDefine());
    }

    /**
     * Old j3md files (and hand written ones) may contain defines without a
     * mapped material parameter. These must not throw, the define is kept
     * unmapped and treated as always set by the engine.
     */
    @Test
    public void testDefineWithoutMappingDoesNotThrow() {
        DefineBlock block = new DefineBlock(statement(5, "HAS_COLORMAP // Must be passed so that Unshaded.vert exports texCoord."));
        assertEquals("HAS_COLORMAP", block.getName());
        assertNull(block.getDefine());
    }

    @Test
    public void testDefineWithoutMappingOrCommentDoesNotThrow() {
        DefineBlock block = new DefineBlock(statement(6, "HAS_COLORMAP"));
        assertEquals("HAS_COLORMAP", block.getName());
        assertNull(block.getDefine());
    }

    @Test
    public void testEmptyDefineNameThrows() {
        MatDefParseException ex = assertThrows(MatDefParseException.class,
                () -> new DefineBlock(statement(7, " : ColorMap")));
        assertTrue(ex.getMessage().contains("no name"));
        assertEquals(7, ex.getLineNumber());
    }

    @Test
    public void testParseExceptionMessageContainsLineInfo() {
        MatDefParseException ex = assertThrows(MatDefParseException.class,
                () -> new DefineBlock(statement(42, " : ColorMap")));
        assertTrue(ex.getMessage().contains("line 42"));
        assertTrue(ex.getMessage().contains(": ColorMap"));
    }

    @Test
    public void testVertexShaderWithMultipleLanguages() {
        VertexShaderFileBlock block = new VertexShaderFileBlock(statement(10, "VertexShader GLSL300 GLSL100:   assets/matdefs/JaReTerrainArray.vert"));
        assertEquals("GLSL100", block.getLanguage());
        assertEquals("assets/matdefs/JaReTerrainArray.vert", block.getPath());
    }

    @Test
    public void testFragmentShaderWithMultipleLanguages() {
        FragmentShaderFileBlock block = new FragmentShaderFileBlock(statement(11, "FragmentShader GLSL300 GLSL100: Common/MatDefs/Light/Glow.frag"));
        assertEquals("GLSL100", block.getLanguage());
        assertEquals("Common/MatDefs/Light/Glow.frag", block.getPath());
    }

    @Test
    public void testVertexShaderSingleLanguage() {
        VertexShaderFileBlock block = new VertexShaderFileBlock(statement(12, "VertexShader GLSL150: Common/MatDefs/SSAO/normal.vert"));
        assertEquals("GLSL150", block.getLanguage());
        assertEquals("Common/MatDefs/SSAO/normal.vert", block.getPath());
    }

    @Test
    public void testShaderStatementMissingPathThrows() {
        MatDefParseException ex = assertThrows(MatDefParseException.class,
                () -> new VertexShaderFileBlock(statement(13, "VertexShader GLSL100:")));
        assertTrue(ex.getMessage().contains("missing its language or path"));
        assertEquals(13, ex.getLineNumber());
    }

    /**
     * The j3md from jMonkeyEngine/sdk#650 contains a define without a mapped
     * material parameter. Parsing the whole file must not throw.
     */
    @Test
    public void testIssue650FileParses() throws Exception {
        String j3md = "MaterialDef JaReTerrainArray {\n"
                + "    MaterialParameters {\n"
                + "        TextureArray ColorMap\n"
                + "        Color Color\n"
                + "    }\n"
                + "    Technique {\n"
                + "        VertexShader GLSL300 GLSL100:   assets/matdefs/JaReTerrainArray.vert\n"
                + "        FragmentShader GLSL300 GLSL100: assets/matdefs/JaReTerrainArray.frag\n"
                + "        WorldParameters {\n"
                + "            WorldViewProjectionMatrix\n"
                + "        }\n"
                + "        Defines {\n"
                + "            HAS_COLORMAP : ColorMap\n"
                + "            HAS_COLOR : Color\n"
                + "        }\n"
                + "    }\n"
                + "    Technique Glow {\n"
                + "        VertexShader GLSL300 GLSL100:   assets/matdefs/JaReTerrainArray.vert\n"
                + "        FragmentShader GLSL300 GLSL100: Common/MatDefs/Light/Glow.frag\n"
                + "        WorldParameters {\n"
                + "            WorldViewProjectionMatrix\n"
                + "        }\n"
                + "        Defines {\n"
                + "            HAS_GLOWMAP : GlowMap\n"
                + "            HAS_COLORMAP // Must be passed so that Unshaded.vert exports texCoord.\n"
                + "        }\n"
                + "    }\n"
                + "}\n";
        List<Statement> statements = BlockLanguageParser.parse(new java.io.ByteArrayInputStream(j3md.getBytes("UTF-8")));
        MatDefBlock matDef = new MatDefBlock(statements.get(0));
        assertEquals("JaReTerrainArray", matDef.getName());
        assertEquals(2, matDef.getTechniques().size());

        TechniqueBlock glow = matDef.getTechniques().get(1);
        assertEquals("Glow", glow.getName());
        DefinesBlock defines = glow.getDefines();
        assertEquals(2, defines.getDefineBlocks().size());

        DefineBlock unmapped = defines.getDefineBlocks().get(1);
        assertEquals("HAS_COLORMAP", unmapped.getName());
        assertNull(unmapped.getDefine());
    }
}
