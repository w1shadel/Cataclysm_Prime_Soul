#version 150

uniform sampler2D DiffuseSampler;
uniform float DebuffLevel;
uniform float PrimeTime;

in vec2 texCoord;
out vec4 fragColor;

float hash(vec2 p) {
    vec3 p3  = fract(vec3(p.xyx) * 0.1031);
    p3 += dot(p3, p3.yzx + 33.33);
    return fract((p3.x + p3.y) * p3.z);
}

float noise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    vec2 u = f * f * (3.0 - 2.0 * f);
    return mix(mix(hash(i + vec2(0.0, 0.0)), hash(i + vec2(1.0, 0.0)), u.x),
    mix(hash(i + vec2(0.0, 1.0)), hash(i + vec2(1.0, 1.0)), u.x), u.y);
}

float fbm(vec2 p) {
    float v = 0.0;
    float a = 0.5;
    vec2 shift = vec2(100.0);
    mat2 rot = mat2(cos(0.5), sin(0.5), -sin(0.5), cos(0.5));
    for (int i = 0; i < 4; ++i) {
        v += a * noise(p);
        p = rot * p * 2.0 + shift;
        a *= 0.5;
    }
    return v;
}

void main() {
    if (DebuffLevel < 1.0) {
        fragColor = texture(DiffuseSampler, texCoord);
        return;
    }

    vec2 uv = texCoord;
    float t = PrimeTime * 0.04;

    vec2 ndc = uv * 2.0 - 1.0;
    float distFromCenter = length(ndc);

    float edgeMask = smoothstep(0.1, 1.2, distFromCenter);

    float breathe = sin(t * 0.25) * 0.35 + 1.0;

    vec2 noiseUV = uv * 10.0 + vec2(0.0, -t * 1.2);
    float n1 = fbm(noiseUV);
    float n2 = fbm(noiseUV + vec2(5.2, 1.3));

    float activeMask = 0.2 + 0.8 * edgeMask;
    vec2 distortion = vec2(n1 - 0.5, n2 - 0.5) * 0.05 * activeMask * breathe;

    if (DebuffLevel >= 3.0) {
        distortion *= 1.4;
    }

    vec2 distortedUV = clamp(uv + distortion, 0.0, 1.0);
    vec4 baseColor = texture(DiffuseSampler, distortedUV);

    if (DebuffLevel >= 2.0) {
        vec2 fireUV1 = uv * 3.5 + vec2(t * 0.4, -t * 1.8);
        vec2 fireUV2 = uv * 3.5 + vec2(-t * 0.5 + sin(t * 0.5) * 0.3, -t * 1.3);

        float f1 = fbm(fireUV1);
        float f2 = fbm(fireUV2);

        float blend = sin(t * 0.3) * 0.5 + 0.5;
        float fireNoise = mix(f1, f2, blend);

        float fireIntensity = smoothstep(0.3, 0.8, fireNoise * edgeMask * 1.4);

        vec3 fireColor;
        if (DebuffLevel >= 3.0) {
            fireColor = mix(vec3(0.0, 0.3, 1.0), vec3(0.85, 0.95, 1.0), fireIntensity);
        } else {
            fireColor = mix(vec3(0.8, 0.1, 0.0), vec3(1.0, 0.7, 0.05), fireIntensity);
        }

        baseColor.rgb = mix(baseColor.rgb, fireColor, fireIntensity * 0.8);
    }

    fragColor = vec4(baseColor.rgb, 1.0);
}