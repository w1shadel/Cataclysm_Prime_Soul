#version 150

uniform sampler2D DiffuseSampler;
uniform float DebuffLevel;
uniform float PrimeTime;

in vec2 texCoord;
out vec4 fragColor;

// 画面の彩度を調整するためのヘルパー関数
vec3 adjustSaturation(vec3 color, float saturation) {
    float luma = dot(color, vec3(0.2126, 0.7152, 0.0722));
    return mix(vec3(luma), color, saturation);
}

void main() {
    // デバフが有効（1.0以上）でない場合は、元の画面をそのまま出力
    if (DebuffLevel < 1.0) {
        fragColor = texture(DiffuseSampler, texCoord);
        return;
    }

    vec2 uv = texCoord;
    float t = PrimeTime * 0.04;

    // 画面中心からの距離（外側ほどブレを強くするための円形マスク）
    vec2 ndc = uv * 2.0 - 1.0;
    float distFromCenter = length(ndc);
    float edgeMask = smoothstep(0.1, 1.2, distFromCenter);

    // プレイヤーの動揺を表現する「心臓の鼓動（ドクン、ドクン）」を計算
    // 鋭く跳ね上がるキレのある衝撃を表現するため、サイン波を8乗します
    float pulse = pow(sin(t * 0.3) * 0.5 + 0.5, 8.0);

    // 残像（ゴースト）が左右にずれる幅（オフセット）の計算
    // 蓄積されたデバフ（DebuffLevel）でベースのズレが大きくなり、鼓動の瞬間にさらに一瞬大きくブレます
    float baseOffset = 0.015 * (DebuffLevel - 0.5);
    float pulseOffset = 0.025 * pulse * DebuffLevel;
    float totalOffset = (baseOffset + pulseOffset) * (0.3 + 0.7 * edgeMask);

    // 1. 【中央】本来の実体（メインの通常画面）
    vec4 colCenter = texture(DiffuseSampler, uv);

    // 2. 【左側】にずれる翡翠色の幻影（エメラルドグリーンを乗算）
    vec2 uvLeft = clamp(uv - vec2(totalOffset, 0.0), 0.0, 1.0);
    vec4 colLeft = texture(DiffuseSampler, uvLeft);
    vec3 greenGhost = colLeft.rgb * vec3(0.0, 1.0, 0.7);

    // 3. 【右側】にずれる薄青色の幻影（シアンを乗算）
    vec2 uvRight = clamp(uv + vec2(totalOffset, 0.0), 0.0, 1.0);
    vec4 colRight = texture(DiffuseSampler, uvRight);
    vec3 blueGhost = colRight.rgb * vec3(0.1, 0.5, 1.0);

    // 各幻影の「重なり（不透明度）」の調整
    // デバフレベル（分裂の深刻さ）が高くなるほど、幻影の自己主張が強くなります
    float ghostAlpha = clamp(0.2 + (DebuffLevel * 0.15), 0.2, 0.6);

    vec3 finalColor = colCenter.rgb;
    finalColor = mix(finalColor, greenGhost, ghostAlpha);
    finalColor = mix(finalColor, blueGhost, ghostAlpha);

    // 【第2形態・完全分裂時（DebuffLevel 3.0）限定の重厚な演出】
    // 画面全体の彩度を落として冷たい死霊の雰囲気を強め、四隅を監獄の闇（暗闇ヴィネット）に深く沈めます
    if (DebuffLevel >= 3.0) {
        finalColor = adjustSaturation(finalColor, 0.65);// 彩度を35%低下させ、モノクローム寄りに
        float vignette = smoothstep(1.3, 0.5, distFromCenter);
        finalColor = mix(vec3(0.01, 0.04, 0.04), finalColor, vignette);// 周囲を不気味な暗緑色の闇に沈める
    }

    fragColor = vec4(finalColor, 1.0);
}