#version 150

uniform sampler2D DiffuseSampler;
uniform float DebuffLevel;
uniform float PrimeTime;

in vec2 texCoord;
out vec4 fragColor;

// 疑似ランダム関数（ノイズ用）
float rand(vec2 co) {
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

void main() {
    if (DebuffLevel < 1.0) {
        fragColor = texture(DiffuseSampler, texCoord);
        return;
    }

    vec2 uv = texCoord;
    float time = PrimeTime * 0.05;

    // 中心からの距離
    vec2 center = uv - 0.5;
    float dist = length(center);

    // 中心部は0、外側ほど1に近づく（0.2以下は完全にクリアにする）
    float edgeMask = smoothstep(0.15, 1.0, dist);

    // 性格の悪い「空間の歪み」
    // 鼓動に合わせて画面端が微妙に拡大縮小し、距離感を狂わせる
    float pulse = pow(sin(time * 0.4) * 0.5 + 0.5, 10.0);
    float distortion = edgeMask * (0.02 * DebuffLevel) * pulse;
    vec2 distortedUV = uv - center * distortion;

    // 色収差（Chromatic Aberration）
    // 画面の端だけ、赤と青が分離する。中心部は分離しないのでプレイを阻害しない。
    float aberration = 0.015 * DebuffLevel * edgeMask;
    // 鼓動の瞬間にだけ、色が強く分離する
    aberration += pulse * 0.02 * DebuffLevel;

    float r = texture(DiffuseSampler, distortedUV + vec2(aberration, 0.0)).r;
    float g = texture(DiffuseSampler, distortedUV).g;
    float b = texture(DiffuseSampler, distortedUV - vec2(aberration, 0.0)).b;

    vec3 color = vec3(r, g, b);

    // 亡霊の走査線（Ghostly Scanlines）
    // 画面全体を覆うのではなく、非常に細い横線がチラつく
    float scanline = sin(uv.y * 800.0 + time * 10.0) * 0.02;
    color -= scanline * edgeMask * DebuffLevel;

    // グリッチ・スパイク（たまに一瞬だけ横にピッと走るノイズ）
    if (DebuffLevel >= 2.0) {
        float flicker = step(0.98, rand(vec2(floor(time * 1.5), 0.0)));
        float lineNoise = step(0.1, rand(vec2(uv.y * 20.0, time))) * flicker;
        float offset = (rand(vec2(time, uv.y)) - 0.5) * 0.05 * DebuffLevel;
        if (lineNoise > 0.5) {
            color = texture(DiffuseSampler, clamp(distortedUV + vec2(offset, 0.0), 0.0, 1.0)).rgb;
            color.g += 0.1; // わずかにエメラルドグリーンを混ぜる
        }
    }

    // 第2形態（Level 3）: 世界が「色褪せ、凍りつく」
    if (DebuffLevel >= 3.0) {
        float luma = dot(color, vec3(0.2126, 0.7152, 0.0722));
        // 完全にモノクロにするのではなく、青白く冷たい色を残す
        vec3 spectralColor = mix(vec3(luma), color, 0.4);
        spectralColor *= vec3(0.9, 1.0, 1.1); // 青みを強調

        // 周囲を暗くするが、黒ではなく「深海の底」のような暗緑色へ
        float vignette = smoothstep(1.2, 0.4, dist);
        color = mix(vec3(0.0, 0.02, 0.03), spectralColor, vignette);
    }

    fragColor = vec4(color, 1.0);
}