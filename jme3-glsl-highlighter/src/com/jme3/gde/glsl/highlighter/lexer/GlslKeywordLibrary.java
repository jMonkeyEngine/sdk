package com.jme3.gde.glsl.highlighter.lexer;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author grizeldi
 */
class GlslKeywordLibrary {
    public enum KeywordType{
        KEYWORD, BUILTIN_FUNCTION, BUILTIN_VARIABLE, BASIC_TYPE, UNFINISHED;
    }
    private static final List<String> keywords = new ArrayList<>(),
            builtinFunctions = new ArrayList<>(),
            builtinVariables = new ArrayList<>(),
            basicTypes = new ArrayList<>();
    
    static {
        //keywords
        keywords.add("attribute");
        keywords.add("const");
        keywords.add("uniform");
        keywords.add("varying");
        keywords.add("buffer");
        keywords.add("shared");
        keywords.add("coherent");
        keywords.add("volatile");
        keywords.add("restrict");
        keywords.add("readonly");
        keywords.add("writeonly");
        keywords.add("atomic_uint");
        keywords.add("layout");
        keywords.add("centroid");
        keywords.add("flat");
        keywords.add("smooth");
        keywords.add("noperspective");
        keywords.add("patch");
        keywords.add("sample");
        keywords.add("break");
        keywords.add("continue");
        keywords.add("do");
        keywords.add("for");
        keywords.add("while");
        keywords.add("switch");
        keywords.add("case");
        keywords.add("default");
        keywords.add("if");
        keywords.add("else");
        keywords.add("subroutine");
        keywords.add("in");
        keywords.add("out");
        keywords.add("inout");
        keywords.add("void");
        keywords.add("true");
        keywords.add("false");
        keywords.add("invariant");
        keywords.add("precise");
        keywords.add("discard");
        keywords.add("return");
        //primitives and other types
        basicTypes.add("float");
        basicTypes.add("double");
        basicTypes.add("int");
        basicTypes.add("bool");
        basicTypes.add("mat2");
        basicTypes.add("mat3");
        basicTypes.add("mat4");
        basicTypes.add("dmat2");
        basicTypes.add("dmat3");
        basicTypes.add("dmat4");
        basicTypes.add("mat2x2");
        basicTypes.add("mat2x3");
        basicTypes.add("mat2x4");
        basicTypes.add("dmat2x2");
        basicTypes.add("dmat2x3");
        basicTypes.add("dmat2x4");
        basicTypes.add("mat3x2");
        basicTypes.add("mat3x3");
        basicTypes.add("mat3x4");
        basicTypes.add("dmat3x2");
        basicTypes.add("dmat3x3");
        basicTypes.add("dmat3x4");
        basicTypes.add("mat4x2");
        basicTypes.add("mat4x3");
        basicTypes.add("mat4x4");
        basicTypes.add("dmat4x2");
        basicTypes.add("dmat4x3");
        basicTypes.add("dmat4x4");
        basicTypes.add("vec2");
        basicTypes.add("vec3");
        basicTypes.add("vec4");
        basicTypes.add("ivec2");
        basicTypes.add("ivec3");
        basicTypes.add("ivec4");
        basicTypes.add("bvec2");
        basicTypes.add("bvec3");
        basicTypes.add("bvec4");
        basicTypes.add("dvec2");
        basicTypes.add("dvec3");
        basicTypes.add("dvec4");
        basicTypes.add("uint");
        basicTypes.add("uvec2");
        basicTypes.add("uvec3");
        basicTypes.add("uvec4");
        basicTypes.add("lowp");
        basicTypes.add("mediump");
        basicTypes.add("highp");
        basicTypes.add("precision");
        basicTypes.add("sampler1D");
        basicTypes.add("sampler2D");
        basicTypes.add("sampler3D");
        basicTypes.add("samplerCube");
        basicTypes.add("sampler1DShadow");
        basicTypes.add("sampler2DShadow");
        basicTypes.add("samplerCubeShadow");
        basicTypes.add("sampler1DArray");
        basicTypes.add("sampler2DArray");
        basicTypes.add("sampler1DArrayShadow");
        basicTypes.add("sampler2DArrayShadow");
        basicTypes.add("isampler1D");
        basicTypes.add("isampler2D");
        basicTypes.add("isampler3D");
        basicTypes.add("isamplerCube");
        basicTypes.add("isampler1DArray");
        basicTypes.add("isampler2DArray");
        basicTypes.add("usampler1D");
        basicTypes.add("usampler2D");
        basicTypes.add("usampler3D");
        basicTypes.add("usamplerCube");
        basicTypes.add("usampler1DArray");
        basicTypes.add("usampler2DArray");
        basicTypes.add("sampler2DRect");
        basicTypes.add("sampler2DRectShadow");
        basicTypes.add("isampler2DRect");
        basicTypes.add("usampler2DRect");
        basicTypes.add("samplerBuffer");
        basicTypes.add("isamplerBuffer");
        basicTypes.add("usamplerBuffer");
        basicTypes.add("sampler2DMS");
        basicTypes.add("isampler2DMS");
        basicTypes.add("usampler2DMS");
        basicTypes.add("sampler2DMSArray");
        basicTypes.add("isampler2DMSArray");
        basicTypes.add("usampler2DMSArray");
        basicTypes.add("samplerCubeArray");
        basicTypes.add("samplerCubeArrayShadow");
        basicTypes.add("isamplerCubeArray");
        basicTypes.add("usamplerCubeArray");
        basicTypes.add("image1D");
        basicTypes.add("iimage1D");
        basicTypes.add("uimage1D");
        basicTypes.add("image2D");
        basicTypes.add("iimage2D");
        basicTypes.add("uimage2D");
        basicTypes.add("image3D");
        basicTypes.add("iimage3D");
        basicTypes.add("uimage3D");
        basicTypes.add("image2DRect");
        basicTypes.add("iimage2DRect");
        basicTypes.add("uimage2DRect");
        basicTypes.add("imageCube");
        basicTypes.add("iimageCube");
        basicTypes.add("uimageCube");
        basicTypes.add("imageBuffer");
        basicTypes.add("iimageBuffer");
        basicTypes.add("uimageBuffer");
        basicTypes.add("image1DArray");
        basicTypes.add("iimage1DArray");
        basicTypes.add("uimage1DArray");
        basicTypes.add("image2DArray");
        basicTypes.add("iimage2DArray");
        basicTypes.add("uimage2DArray");
        basicTypes.add("imageCubeArray");
        basicTypes.add("iimageCubeArray");
        basicTypes.add("uimageCubeArray");
        basicTypes.add("image2DMS");
        basicTypes.add("iimage2DMS");
        basicTypes.add("uimage2DMS");
        basicTypes.add("image2DMSArray");
        basicTypes.add("iimage2DMSArray");
        basicTypes.add("uimage2DMSArray");
        basicTypes.add("struct");
        //builtin variables
        //compute shaders
        builtinVariables.add("gl_NumWorkGroups");
        builtinVariables.add("gl_WorkGroupSize");
        builtinVariables.add("gl_WorkGroupID");
        builtinVariables.add("gl_LocalInvocationID");
        builtinVariables.add("gl_GlobalInvocationID");
        builtinVariables.add("gl_LocalInvocationIndex");
        //vertex shaders
        builtinVariables.add("gl_VertexID");
        builtinVariables.add("gl_InstanceID");
        //geometry shaders
        builtinVariables.add("gl_PrimitiveIDIn");
        builtinVariables.add("gl_Layer");
        builtinVariables.add("gl_ViewportIndex");
        //tesselation shaders
        builtinVariables.add("gl_MaxPatchVertices");
        builtinVariables.add("gl_PatchVerticesIn");
        builtinVariables.add("gl_TessLevelOuter");
        builtinVariables.add("gl_TessLevelInner");
        builtinVariables.add("gl_TessCoord");
        //fragment shaders
        builtinVariables.add("gl_FragCoord");
        builtinVariables.add("gl_FrontFacing");
        builtinVariables.add("gl_PointCoord");
        builtinVariables.add("gl_SampleID");
        builtinVariables.add("gl_SamplePosition");
        builtinVariables.add("gl_SampleMaskIn");
        builtinVariables.add("gl_Layer");
        builtinVariables.add("gl_ViewportIndex");
        //general
        builtinVariables.add("gl_Position");
        builtinVariables.add("gl_PointSize");
        builtinVariables.add("gl_ClipDistance");
        builtinVariables.add("gl_InvocationID");
        builtinVariables.add("gl_PrimitiveID");
        //jme variables - this is why we build custom plugins :) (apart from existing being under GPL)
        builtinVariables.add("inPosition");
        builtinVariables.add("inNormal");
        builtinVariables.add("inColor");
        builtinVariables.add("inTextCoord");
        builtinVariables.add("g_WorldViewMatrix");
        builtinVariables.add("g_ProjectionMatrix");
        builtinVariables.add("g_WorldViewProjectionMatrix");
        builtinVariables.add("g_NormalMatrix");
    }
    
    public static KeywordType lookup(String s){
        KeywordType returnType = null;
        for (String primitive : basicTypes){
            if (primitive.startsWith(s)){
                if (primitive.equals(s)){
                    returnType = KeywordType.BASIC_TYPE;
                    break;
                }else
                    returnType = KeywordType.UNFINISHED;
            }
        }
        for (String var : builtinVariables){
            if (var.startsWith(s)){
                if (var.equals(s)){
                    returnType = KeywordType.BUILTIN_VARIABLE;
                    break;
                }else
                    returnType = KeywordType.UNFINISHED;
            }
        }
        for (String keyword : keywords){
            if (keyword.startsWith(s) && (returnType == KeywordType.UNFINISHED || returnType == null)){
                if (keyword.equals(s)){
                    returnType = KeywordType.KEYWORD;
                    break;
                }
                else
                    returnType = KeywordType.UNFINISHED;
            }
        }
        
        return returnType;
    }
}
