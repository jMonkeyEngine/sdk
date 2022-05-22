package com.jme3.gde.materialdefinition.editor.previews;

import com.jme3.shader.ShaderNodeVariable;

/**
 *
 * @author rickard
 */
public class PreviewFactory {
    
    public static BasePreview createPreviewComponent(ShaderNodeVariable output){
        switch (output.getType()) {
            case "bool":
                return new BoolPreview(output);
            case "vec4":
                if(output.getName().toLowerCase().contains("color")){
                    return new ColorPreview(output);
                } else {
                    return new VecPreview(output, 4);
                }
            case "vec3":
                return new VecPreview(output, 3);
            case "vec2":
                return new VecPreview(output, 2);
            case "float":
                return new FloatPreview(output);
            case "sampler2D|sampler2DShadow":
                return new TexturePreview(output);
            default:
                return null;
        }
    }
}
