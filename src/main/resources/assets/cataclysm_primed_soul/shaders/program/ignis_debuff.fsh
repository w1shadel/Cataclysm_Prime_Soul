#version 150

uniform sampler2D DiffuseSampler;
uniform float DebuffLevel;
uniform float Time;

in vec2 texCoord;
out vec4 fragColor;

// 疑似乱数ジェネレータ (sin不使用・浮動小数点数精度低下対策)
float hash(vec2 p) {
    vec3 p3  = fract(vec3(p.xyx) * 0.1031);
    p3 += dot(p3, p3.yzx + 33.33);
    return fract((p3.x + p3.y) * p3.z);
}

// 簡易的なノイズ関数
float noise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    vec2 u = f * f * (3.0 - 2.0 * f);
    return mix(mix(hash(i + vec2(0.0, 0.0)), hash(i + vec2(1.0, 0.0)), u.x),
               mix(hash(i + vec2(0.0, 1.0)), hash(i + vec2(1.0, 1.0)), u.x), u.y);
}

// FBM (Fractal Brownian Motion)
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
    float t = Time * 0.05;

    // NDC座標（-1〜1）で距離を計算
    vec2 ndc = uv * 2.0 - 1.0;
    float distFromCenter = length(ndc);

    // ビネット（端ほど強くするマスク）
    float edgeMask = smoothstep(0.6, 1.3, distFromCenter);

    // 蜃気楼の歪み計算 (こちらも精度落ち対策のため高精度ハッシュベースFBMを使用)
    vec2 noiseUV = uv * 5.0 + vec2(0.0, -t * 1.5);
    float n1 = fbm(noiseUV);
    float n2 = fbm(noiseUV + vec2(5.2, 1.3));

    // 端のみ歪む（中心は歪まない）
    vec2 distortion = vec2(n1 - 0.5, n2 - 0.5) * 0.06 * edgeMask;

    // Level 3の場合は歪みをやや強く
    if (DebuffLevel >= 3.0) {
        distortion *= 1.4;
    }

    vec2 distortedUV = clamp(uv + distortion, 0.0, 1.0);
    vec4 baseColor = texture(DiffuseSampler, distortedUV);

    if (DebuffLevel >= 2.0) {
        // 周期的なリセット感やガタつきを防ぐため、異なる速度・方向でスクロールする2つのノイズをブレンドする
        vec2 fireUV1 = uv * 3.5 + vec2(t * 0.4, -t * 1.8);
        vec2 fireUV2 = uv * 3.5 + vec2(-t * 0.5 + sin(t * 0.5) * 0.3, -t * 1.3);
        
        float f1 = fbm(fireUV1);
        float f2 = fbm(fireUV2);
        
        // 2つのノイズをスムーズに行き来させる
        float blend = sin(t * 0.3) * 0.5 + 0.5;
        float fireNoise = mix(f1, f2, blend);
        
        // 炎が端でダイナミックに動くよう、マスクの乗数と閾値を調整（1.4倍にして少し大きく）
        float fireIntensity = smoothstep(0.3, 0.8, fireNoise * edgeMask * 1.4);

        vec3 fireColor;
        if (DebuffLevel >= 3.0) {
            // Level 3: 青白の炎（極高温のイメージ）
            fireColor = mix(vec3(0.0, 0.3, 1.0), vec3(0.85, 0.95, 1.0), fireIntensity);
        } else {
            // Level 2: オレンジの炎
            fireColor = mix(vec3(0.8, 0.1, 0.0), vec3(1.0, 0.7, 0.05), fireIntensity);
        }

        // ブレンド（端で最大80%のブレンド率）
        baseColor.rgb = mix(baseColor.rgb, fireColor, fireIntensity * 0.8);
    }

    fragColor = vec4(baseColor.rgb, 1.0);
}
