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

import java.util.ArrayList;
import java.util.List;

/**
 * Brace, yourselves, this file contains every word that means something in
 * GLSL. Expect about 400 lines of code that just adds strings.
 *
 * @author grizeldi
 */
class GlslKeywordLibrary {

    public enum KeywordType {
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
        builtinVariables.add("gl_Position");
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
        builtinVariables.add("gl_FragColor");
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
        builtinVariables.add("g_WorldMatrix");
        builtinVariables.add("g_ViewMatrix");
        builtinVariables.add("g_ProjectionMatrix");
        builtinVariables.add("g_WorldViewMatrix");
        builtinVariables.add("g_WorldViewProjectionMatrix");
        builtinVariables.add("g_WorldNormalMatrix");
        builtinVariables.add("g_NormalMatrix");
        builtinVariables.add("g_ViewProjectionMatrix");
        builtinVariables.add("g_WorldMatrixInverseTranspose");
        builtinVariables.add("g_WorldMatrixInverse");
        builtinVariables.add("g_ViewMatrixInverse");
        builtinVariables.add("g_ProjectionMatrixInverse");
        builtinVariables.add("g_ViewProjectionMatrixInverse");
        builtinVariables.add("g_WorldViewMatrixInverse");
        builtinVariables.add("g_NormalMatrixInverse");
        builtinVariables.add("g_WorldViewProjectionMatrixInverse");
        builtinVariables.add("g_ViewPort");
        builtinVariables.add("g_FrustumNearFar");
        builtinVariables.add("g_Resolution");
        builtinVariables.add("g_ResolutionInverse");
        builtinVariables.add("g_Aspect");
        builtinVariables.add("g_CameraPosition");
        builtinVariables.add("g_CameraDirection");
        builtinVariables.add("g_CameraLeft");
        builtinVariables.add("g_CameraUp");
        builtinVariables.add("g_Time");
        builtinVariables.add("g_Tpf");
        builtinVariables.add("g_FrameRate");
        builtinVariables.add("g_LightDirection");
        builtinVariables.add("g_LightPosition");
        builtinVariables.add("g_LightColor");
        builtinVariables.add("g_AmbientLightColor");
        //builtin functions
        builtinFunctions.add("radians");
        builtinFunctions.add("degrees");
        builtinFunctions.add("sin");
        builtinFunctions.add("cos");
        builtinFunctions.add("tan");
        builtinFunctions.add("asin");
        builtinFunctions.add("acos");
        builtinFunctions.add("atan");
        builtinFunctions.add("sinh");
        builtinFunctions.add("cosh");
        builtinFunctions.add("tanh");
        builtinFunctions.add("asinh");
        builtinFunctions.add("acosh");
        builtinFunctions.add("atanh");
        builtinFunctions.add("pow");
        builtinFunctions.add("exp");
        builtinFunctions.add("log");
        builtinFunctions.add("exp2");
        builtinFunctions.add("log2");
        builtinFunctions.add("sqrt");
        builtinFunctions.add("inversesqrt");
        builtinFunctions.add("abs");
        builtinFunctions.add("sign");
        builtinFunctions.add("floor");
        builtinFunctions.add("trunc");
        builtinFunctions.add("round");
        builtinFunctions.add("roundEven");
        builtinFunctions.add("ceil");
        builtinFunctions.add("fract");
        builtinFunctions.add("mod");
        builtinFunctions.add("modf");
        builtinFunctions.add("min");
        builtinFunctions.add("max");
        builtinFunctions.add("clamp");
        builtinFunctions.add("mix");
        builtinFunctions.add("step");
        builtinFunctions.add("smoothstep");
        builtinFunctions.add("isnan");
        builtinFunctions.add("isinf");
        builtinFunctions.add("floatBitsToInt");
        builtinFunctions.add("floatBitsToUInt");
        builtinFunctions.add("intBitsToFloat");
        builtinFunctions.add("uintBitsToFloat");
        builtinFunctions.add("fma");
        builtinFunctions.add("frexp");
        builtinFunctions.add("packUnorm2x16");
        builtinFunctions.add("packSnorm2x16");
        builtinFunctions.add("packUnorm4x8");
        builtinFunctions.add("packSnorm4x8");
        builtinFunctions.add("unpackUnorm2x16");
        builtinFunctions.add("unpackSnorm2x16");
        builtinFunctions.add("unpackUnorm4x8");
        builtinFunctions.add("unpackSnorm4x8");
        builtinFunctions.add("packDouble2x32");
        builtinFunctions.add("unpackDouble2x32");
        builtinFunctions.add("packHalf2x16");
        builtinFunctions.add("unpackHalf2x16");
        builtinFunctions.add("length");
        builtinFunctions.add("distance");
        builtinFunctions.add("dot");
        builtinFunctions.add("cross");
        builtinFunctions.add("normalize");
        builtinFunctions.add("ftransform");
        builtinFunctions.add("faceforward");
        builtinFunctions.add("reflect");
        builtinFunctions.add("refract");
        builtinFunctions.add("matrixCompMult");
        builtinFunctions.add("outerProduct");
        builtinFunctions.add("transpose");
        builtinFunctions.add("determinant");
        builtinFunctions.add("inverse");
        builtinFunctions.add("lessThan");
        builtinFunctions.add("lessThanEqual");
        builtinFunctions.add("greaterThan");
        builtinFunctions.add("greaterThanEqual");
        builtinFunctions.add("equal");
        builtinFunctions.add("notEqual");
        builtinFunctions.add("any");
        builtinFunctions.add("all");
        builtinFunctions.add("not");
        builtinFunctions.add("uaddCarry");
        builtinFunctions.add("usubBorrow");
        builtinFunctions.add("umulExtended");
        builtinFunctions.add("imulExtended");
        builtinFunctions.add("bitfieldExtract");
        builtinFunctions.add("bitfieldInsert");
        builtinFunctions.add("bitfieldReverse");
        builtinFunctions.add("bitCount");
        builtinFunctions.add("findLSB");
        builtinFunctions.add("findMSB");
        builtinFunctions.add("textureSize");
        builtinFunctions.add("textureQueryLod");
        builtinFunctions.add("textureQueryLevels");
        builtinFunctions.add("texture");
        builtinFunctions.add("textureProj");
        builtinFunctions.add("textureLod");
        builtinFunctions.add("textureOffset");
        builtinFunctions.add("texelFetch");
        builtinFunctions.add("texelFetchOffset");
        builtinFunctions.add("textureProjOffset");
        builtinFunctions.add("textureLodOffset");
        builtinFunctions.add("textureProjLod");
        builtinFunctions.add("textureProjLodOffset");
        builtinFunctions.add("textureGrad");
        builtinFunctions.add("textureGradOffset");
        builtinFunctions.add("textureProjGrad");
        builtinFunctions.add("textureProjGradOffset");
        builtinFunctions.add("textureGather");
        builtinFunctions.add("textureGatherOffset");
        builtinFunctions.add("textureGatherOffsets");
        builtinFunctions.add("texture1D");
        builtinFunctions.add("texture1DProj");
        builtinFunctions.add("texture1DLod");
        builtinFunctions.add("texture1DProjLod");
        builtinFunctions.add("texture2D");
        builtinFunctions.add("texture2DProj");
        builtinFunctions.add("texture2DLod");
        builtinFunctions.add("texture2DProjLod");
        builtinFunctions.add("texture3D");
        builtinFunctions.add("texture3DProj");
        builtinFunctions.add("texture3DLod");
        builtinFunctions.add("texture3DProjLod");
        builtinFunctions.add("textureCube");
        builtinFunctions.add("textureCubeLod");
        builtinFunctions.add("shadow1D");
        builtinFunctions.add("shadow2D");
        builtinFunctions.add("shadow1DProj");
        builtinFunctions.add("shadow2DProj");
        builtinFunctions.add("shadow1DLod");
        builtinFunctions.add("shadow2DLod");
        builtinFunctions.add("shadow1DProjLod");
        builtinFunctions.add("shadow2DProjLod");
        builtinFunctions.add("atomicCounterIncrement");
        builtinFunctions.add("atomicCounterDecrement");
        builtinFunctions.add("atomicCounter");
        builtinFunctions.add("atomicAdd");
        builtinFunctions.add("atomicMin");
        builtinFunctions.add("atomicMax");
        builtinFunctions.add("atomicAnd");
        builtinFunctions.add("atomicOr");
        builtinFunctions.add("atomicXor");
        builtinFunctions.add("atomicExchange");
        builtinFunctions.add("atomicCompSwap");
        builtinFunctions.add("imageSize");
        builtinFunctions.add("imageLoad");
        builtinFunctions.add("imageStore");
        builtinFunctions.add("imageAtomicAdd");
        builtinFunctions.add("imageAtomicMin");
        builtinFunctions.add("imageAtomicMax");
        builtinFunctions.add("imageAtomicAnd");
        builtinFunctions.add("imageAtomicOr");
        builtinFunctions.add("imageAtomicXor");
        builtinFunctions.add("imageAtomicExchange");
        builtinFunctions.add("imageAtomicCompSwap");
        builtinFunctions.add("dFdx");
        builtinFunctions.add("dFdy");
        builtinFunctions.add("fwidth");
        builtinFunctions.add("interpolateAtCentroid");
        builtinFunctions.add("interpolateAtSample");
        builtinFunctions.add("interpolateAtOffset");
        builtinFunctions.add("noise1");
        builtinFunctions.add("noise2");
        builtinFunctions.add("noise3");
        builtinFunctions.add("noise4");
        builtinFunctions.add("EmitStreamVertex");
        builtinFunctions.add("EndStreamPrimitive");
        builtinFunctions.add("EmitVertex");
        builtinFunctions.add("EndPrimitive");
        builtinFunctions.add("barrier");
        builtinFunctions.add("memoryBarrier");
        builtinFunctions.add("memoryBarrierAtomicCounter");
        builtinFunctions.add("memoryBarrierBuffer");
        builtinFunctions.add("memoryBarrierShared");
        builtinFunctions.add("memoryBarrierImage");
        builtinFunctions.add("groupMemoryBarrier");
    }

    public static KeywordType lookup(String s) {
        KeywordType returnType = null;
        for (String primitive : basicTypes) {
            if (primitive.startsWith(s)) {
                if (primitive.equals(s)) {
                    returnType = KeywordType.BASIC_TYPE;
                    break;
                } else {
                    returnType = KeywordType.UNFINISHED;
                }
            }
        }
        for (String var : builtinVariables) {
            if (var.startsWith(s)) {
                if (var.equals(s)) {
                    returnType = KeywordType.BUILTIN_VARIABLE;
                    break;
                } else {
                    returnType = KeywordType.UNFINISHED;
                }
            }
        }
        for (String func : builtinFunctions) {
            if (func.startsWith(s) && (returnType == KeywordType.UNFINISHED || returnType == null)) {
                if (func.equals(s)) {
                    returnType = KeywordType.BUILTIN_FUNCTION;
                    break;
                } else {
                    returnType = KeywordType.UNFINISHED;
                }
            }
        }
        for (String keyword : keywords) {
            if (keyword.startsWith(s) && (returnType == KeywordType.UNFINISHED || returnType == null)) {
                if (keyword.equals(s)) {
                    returnType = KeywordType.KEYWORD;
                    break;
                } else {
                    returnType = KeywordType.UNFINISHED;
                }
            }
        }

        return returnType;
    }
}
