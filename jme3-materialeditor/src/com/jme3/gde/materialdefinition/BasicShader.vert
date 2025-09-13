#import "Common/ShaderLib/GLSLCompat.glsllib"

attribute vec3 inPosition;

uniform mat4 g_WorldViewProjectionMatrix;

void main(){
    gl_Position = g_WorldViewProjectionMatrix * vec4(inPosition, 1.0);
}