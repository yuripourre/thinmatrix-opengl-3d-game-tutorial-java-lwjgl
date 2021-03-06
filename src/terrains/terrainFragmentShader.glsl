#version 140

// outputs from vertexShader
//in vec3 color;
in vec2 pass_textureCoordinates;
in vec3 surfaceNormal;
in vec3 toLightVector[4];
in vec3 toCameraVector;
in float visibility;

out vec4 out_Color;

uniform sampler2D backgroundTexture;
uniform sampler2D rTexture;
uniform sampler2D gTexture;
uniform sampler2D bTexture;
uniform sampler2D blendMap;

uniform sampler2D modelTexture;
uniform vec3 lightColor[4];
uniform vec3 attenuation[4];
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColor;

//const float levels = 4.0; // tutorial 30 cel shading

void main() {
    // unit surface normal - in world coordinates
    vec3 unitNormal = normalize(surfaceNormal);
    // unit vector pointing from this fragment to the camera - in world coordinates
    vec3 unitVectorToCamera = normalize(toCameraVector);

    vec3 totalDiffuse = vec3(0.0);
    vec3 totalSpecular = vec3(0.0);

    vec4 blendMapColor = texture(blendMap, pass_textureCoordinates);

    // blending stuff (tutorial 17: multitexturing)
    float backTextureAmount = 1 - (blendMapColor.r + blendMapColor.g + blendMapColor.b);
    vec2 tiledCoords = pass_textureCoordinates * 30.0;
    vec4 backgroundTextureColor = texture(backgroundTexture, tiledCoords) * backTextureAmount;
    vec4 rTextureColor = texture(rTexture, tiledCoords) * blendMapColor.r;
    vec4 gTextureColor = texture(gTexture, tiledCoords) * blendMapColor.g;
    vec4 bTextureColor = texture(bTexture, tiledCoords) * blendMapColor.b;
    vec4 totalColor = backgroundTextureColor + rTextureColor + gTextureColor + bTextureColor;

    for (int i = 0; i < 4; i++) {
        float distance = length(toLightVector[i]);
        float attFactor = attenuation[i].x + (attenuation[i].y * distance) + (attenuation[i].z * distance * distance);
        // unit vector to light - in world coordinates
        vec3 unitLightVector = normalize(toLightVector[i]);
        float nDot1 = dot(unitNormal, unitLightVector);
        float brightness = max(nDot1, 0.0);

        // cel shading
        //float level = floor(brightness * levels);
        //brightness = level / levels;

        // unit vector from light - in world coordinates
        vec3 lightDirection = -unitLightVector;
        // light is reflected from the surface in this direction
        vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);

        // how much of the reflected light is going to the camera (before damping)
        float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
        specularFactor = max(specularFactor, 0.0);
        float dampedFactor = pow(specularFactor, shineDamper);

        // cel shading
        //level = floor(dampedFactor * levels);
        //dampedFactor = level / levels;

        totalDiffuse = totalDiffuse + (brightness * lightColor[i]) / attFactor;
        totalSpecular = totalSpecular + (dampedFactor * reflectivity * lightColor[i]) / attFactor;
    }
    totalDiffuse = max(totalDiffuse, 0.2);

    // if not using blending:
    // totalColor = texture(modelTexture, pass_textureCoordinates);
    out_Color = vec4(totalDiffuse, 1.0) * totalColor + vec4(totalSpecular, 1.0);
    out_Color = mix(vec4(skyColor, 1.0), out_Color, visibility);
}
