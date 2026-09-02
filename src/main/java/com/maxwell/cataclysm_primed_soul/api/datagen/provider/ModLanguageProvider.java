package com.maxwell.cataclysm_primed_soul.api.datagen.provider;

import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import com.maxwell.cataclysm_primed_soul.init.ModEntities;
import com.maxwell.cataclysm_primed_soul.init.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public abstract class ModLanguageProvider extends LanguageProvider {
    public ModLanguageProvider(PackOutput output, String locale) {
        super(output, Primed_Soul.MODID, locale);
    }

    public static class English extends ModLanguageProvider {
        public English(PackOutput output) {
            super(output, "en_us");
        }

        @Override
        protected void addTranslations() {
            this.add(ModItems.ABYSSAL_ASHES.get(), "§bAbyssal Ashes");
            this.add("tooltip.cataclysm_primed_soul.abyssal_ashes.desc", "§6 If you offer it to the Altar of Fire again, something might happen.");
            this.add(ModItems.LAVATEIN.get(), "Lavatein");
            this.add("item.cataclysm_primed_soul.lavatein.desc", "A sword that gains speed with every strike, its outer layer peeling away.");
            this.add("item.cataclysm_primed_soul.lavatein.desc2", "Applies debuffs to the target based on the three stages of release.");
            this.add(ModItems.RUSTED_KNIGHT_SWORD.get(), "Unknown Sword");
            this.add("tooltip.cataclysm_primed_soul.rusted_knight_sword.desc", "§3You must offer a sacrifice at the tombstone of the king who became a great sinner—one of the relics of a king from ancient times.");
            this.add("creative_tab.cataclysm_primed_soul.prime_tab", "Cataclysm: Primed Soul");
            this.add("advancement.cataclysm_primed_soul.root.title", "Cataclysm: Primed Souls");
            this.add("advancement.cataclysm_primed_soul.root.desc", "The First Step, The Last Step");
            this.add("advancement.cataclysm_primed_soul.defeat_ignis_prime.title", "THIS HEAT, AN EVIL HEAT");
            this.add("advancement.cataclysm_primed_soul.defeat_ignis_prime.desc", "You subdued the raging white flames with sheer force.");
            this.add("advancement.cataclysm_primed_soul.defeat_maledictus_prime.title", "SOUL SURVIVOR");
            this.add("advancement.cataclysm_primed_soul.defeat_maledictus_prime.desc", "Defeat the true hero long forgotten.");
            this.add(ModEntities.IGNIS_PRIME.get(), "Ignis Prime");
            this.add(ModEntities.MALEDICTUS_PRIME.get(), "Maledictus Prime");
            this.add("chat.cataclysm_primed_soul.ignis_prime.appear", "The excitement around me is building...");
            this.add("chat.cataclysm_primed_soul.ignis_prime.half_hp", "The heat transforms into unrelenting pressure, and the equipment begins to melt.");
            this.add("chat.cataclysm_primed_soul.ignis_prime.phase_2", "A pure white soul fills my vision. All protection has turned to ash...");
            this.add("chat.cataclysm_primed_soul.ignis_prime.ultracharge", "§6...I am......");
            this.add("boss_screen.cataclysm_primed_soul.ignis_prime.name", "Ignis Prime");
            this.add("boss_screen.cataclysm_primed_soul.ignis_prime.title", "Overlord of the White Flame");
            this.add("boss_screen.cataclysm_primed_soul.ignis_prime.lore", "The incarnation of ruin who mastered the abyssal fire, cloaked in a pure white soul. Its unrelenting heat pressure melts through any armor, rendering all protection obsolete.");
            this.add("boss_screen.cataclysm_primed_soul.ignis_prime.suggestion.1", "Secure ample evasion mobility and healing methods to counter the armor reduction debuffs.");
            this.add("boss_screen.cataclysm_primed_soul.ignis_prime.suggestion.2", "When the boss takes a guarding stance, heavy axe attacks can trigger a guard break.");
            this.add("boss_screen.cataclysm_primed_soul.ignis_prime.suggestion.3", "In Phase 2, remain constantly vigilant against white flame blasts and ultra-high-speed charges.");
            this.add("boss_screen.cataclysm_primed_soul.ignis_prime.advanced_info.erosion.title", "Pressure & Erosion");
            this.add("boss_screen.cataclysm_primed_soul.ignis_prime.advanced_info.erosion.desc", "Nearby players suffer progressive reductions to armor and armor toughness.");
            this.add("boss_screen.cataclysm_primed_soul.ignis_prime.advanced_info.guard.title", "Impenetrable Guard");
            this.add("boss_screen.cataclysm_primed_soul.ignis_prime.advanced_info.guard.desc", "Nullifies incoming attacks from the front and unleashes a ferocious counterattack upon repeated strikes.");
            this.add("boss_screen.cataclysm_primed_soul.ignis_prime.advanced_info.phase2.title", "Phase 2: Pure White Awakening");
            this.add("boss_screen.cataclysm_primed_soul.ignis_prime.advanced_info.phase2.desc", "When its health is depleted, it transitions to Phase 2, launching omnidirectional attacks with projectile barrages and white flames.");
            this.add("boss_screen.cataclysm_primed_soul.ignis_prime.advanced_info.ultracharge.title", "Ultra Charge");
            this.add("boss_screen.cataclysm_primed_soul.ignis_prime.advanced_info.ultracharge.desc", "Ascends into the sky before executing a devastating dive bomb toward the surface.");
            this.add("boss_screen.cataclysm_primed_soul.maledictus_prime.name", "Maledictus Prime");
            this.add("boss_screen.cataclysm_primed_soul.maledictus_prime.title", "The Forgotten True Hero");
            this.add("boss_screen.cataclysm_primed_soul.maledictus_prime.lore", "The ancient king of a fallen realm who became a great sinner. He summons phantoms of his bygone relics, closing distance in an instant to strike.");
            this.add("boss_screen.cataclysm_primed_soul.maledictus_prime.suggestion.1", "Observe the telegraphing motions of the phantoms (Spear, Mace, Bow) to dodge in the appropriate direction.");
            this.add("boss_screen.cataclysm_primed_soul.maledictus_prime.suggestion.2", "Careless attacks during its stance will trigger an instant teleportation counter behind your back.");
            this.add("boss_screen.cataclysm_primed_soul.maledictus_prime.suggestion.3", "If caught by its grab attack, prepare to endure consecutive impacts and a lethal ground slam.");
            this.add("boss_screen.cataclysm_primed_soul.maledictus_prime.advanced_info.phantoms.title", "Phantom Weaponry");
            this.add("boss_screen.cataclysm_primed_soul.maledictus_prime.advanced_info.phantoms.desc", "Summons phantoms that execute spear charges, mace smashes, and bow snipes.");
            this.add("boss_screen.cataclysm_primed_soul.maledictus_prime.advanced_info.counter.title", "Phantom Counter");
            this.add("boss_screen.cataclysm_primed_soul.maledictus_prime.advanced_info.counter.desc", "Instantly teleports behind the attacker upon receiving a hit to deliver a deadly riposte.");
            this.add("boss_screen.cataclysm_primed_soul.maledictus_prime.advanced_info.grab.title", "Grip of Condemnation");
            this.add("boss_screen.cataclysm_primed_soul.maledictus_prime.advanced_info.grab.desc", "Grabs a player with one hand, ascends into the air, and slams them down into the ground.");
            this.add("boss_screen.cataclysm_primed_soul.maledictus_prime.advanced_info.phase2.title", "Armor Shatter");
            this.add("boss_screen.cataclysm_primed_soul.maledictus_prime.advanced_info.phase2.desc", "Below 50% HP, its armor shatters, drastically boosting attack speed and offensive aggression.");
            this.add("dialogue.cataclysm_primed_soul.maledictus_prime.name", "Maledictus Prime");
            this.add("dialogue.cataclysm_primed_soul.maledictus_prime.line.0", "Ah... such brilliant light...");
            this.add("dialogue.cataclysm_primed_soul.maledictus_prime.line.1", "The curse of ages... finally begins to fade.");
            this.add("dialogue.cataclysm_primed_soul.maledictus_prime.line.2", "Go, warrior. The crown was always a burden.");
        }
    }

    public static class Japanese extends ModLanguageProvider {
        public Japanese(PackOutput output) {
            super(output, "ja_jp");
        }

        @Override
        protected void addTranslations() {
            this.add(ModItems.ABYSSAL_ASHES.get(), "§b深淵の灰");
            this.add("tooltip.cataclysm_primed_soul.abyssal_ashes.desc", "§6再び炎の祭壇に捧げれば、何かが起こるかもしれない。");
            this.add(ModItems.LAVATEIN.get(), "ラーヴァテイン");
            this.add("item.cataclysm_primed_soul.lavatein.desc", "攻撃すればするたび、外装が剥がれて加速していく剣。");
            this.add("item.cataclysm_primed_soul.lavatein.desc2", "解放段階3つに応じて、対象にデバフを付与する。");
            this.add(ModItems.RUSTED_KNIGHT_SWORD.get(), "謎の剣");
            this.add("tooltip.cataclysm_primed_soul.rusted_knight_sword.desc", "§3古の時代の王の遺品の一つ、大罪人となった王の墓石に手向ける必要がある。");
            this.add("creative_tab.cataclysm_primed_soul.prime_tab", "Primed Souls");
            this.add("advancement.cataclysm_primed_soul.root.title", "Cataclysm: Primed Souls");
            this.add("advancement.cataclysm_primed_soul.root.desc", "最初の一歩、最後の一歩");
            this.add("advancement.cataclysm_primed_soul.defeat_ignis_prime.title", "THIS HEAT, AN EVIL HEAT");
            this.add("advancement.cataclysm_primed_soul.defeat_ignis_prime.desc", "猛る白炎を力でもって制圧した。");
            this.add("advancement.cataclysm_primed_soul.defeat_maledictus_prime.title", "SOUL SURVIVOR");
            this.add("advancement.cataclysm_primed_soul.defeat_maledictus_prime.desc", "古に忘れ去られた真なる英雄を倒す。");
            this.add(ModEntities.IGNIS_PRIME.get(), "イグニス・プライム");
            this.add(ModEntities.MALEDICTUS_PRIME.get(), "マレディクタス・プライム");
            this.add("chat.cataclysm_primed_soul.ignis_prime.appear", "周囲の熱気が上昇していく...");
            this.add("chat.cataclysm_primed_soul.ignis_prime.half_hp", "熱気が逃げ場のない圧力へと変わり、装備は溶け出す。");
            this.add("chat.cataclysm_primed_soul.ignis_prime.phase_2", "純白のソウルが視界を染め上げる。あらゆる加護は灰に消えた...");
            this.add("chat.cataclysm_primed_soul.ignis_prime.ultracharge", "§6...私は......");
            this.add("boss_screen.cataclysm_primed_soul.ignis_prime.name", "イグニス・プライム");
            this.add("boss_screen.cataclysm_primed_soul.ignis_prime.title", "白炎の覇王");
            this.add("boss_screen.cataclysm_primed_soul.ignis_prime.lore", "深淵の炎を極めし、純白のソウルを纏う破滅の化身。その熱圧はあらゆる防具を融解させ、加護を無力化する。");
            this.add("boss_screen.cataclysm_primed_soul.ignis_prime.suggestion.1", "防具減少デバフに対抗するため、回避性能や回復手段を十分に確保してください。");
            this.add("boss_screen.cataclysm_primed_soul.ignis_prime.suggestion.2", "ガード体勢時は斧による攻撃でガードブレイクを狙えます。");
            this.add("boss_screen.cataclysm_primed_soul.ignis_prime.suggestion.3", "第2形態では白炎の爆風と超高速突進に常に警戒が必要です。");
            this.add("boss_screen.cataclysm_primed_soul.ignis_prime.advanced_info.erosion.title", "熱圧と浸食");
            this.add("boss_screen.cataclysm_primed_soul.ignis_prime.advanced_info.erosion.desc", "ボス周辺にいるプレイヤーは段階的に防御力と防具強度が低下します。");
            this.add("boss_screen.cataclysm_primed_soul.ignis_prime.advanced_info.guard.title", "堅牢なガード");
            this.add("boss_screen.cataclysm_primed_soul.ignis_prime.advanced_info.guard.desc", "正面からのダメージを無効化し、攻撃を重ねると強烈なカウンターを繰り出します。");
            this.add("boss_screen.cataclysm_primed_soul.ignis_prime.advanced_info.phase2.title", "第2形態：純白の覚醒");
            this.add("boss_screen.cataclysm_primed_soul.ignis_prime.advanced_info.phase2.desc", "HPが尽きると第2形態へ移行し、弾幕と白炎による全方位攻撃を開始します。");
            this.add("boss_screen.cataclysm_primed_soul.ignis_prime.advanced_info.ultracharge.title", "ウルトラチャージ");
            this.add("boss_screen.cataclysm_primed_soul.ignis_prime.advanced_info.ultracharge.desc", "上空へ飛び去った後、地表へ向けて壊滅的な急降下爆撃を行います。");
            this.add("boss_screen.cataclysm_primed_soul.maledictus_prime.name", "マレディクタス・プライム");
            this.add("boss_screen.cataclysm_primed_soul.maledictus_prime.title", "古の真なる英雄");
            this.add("boss_screen.cataclysm_primed_soul.maledictus_prime.lore", "大罪を背負いし亡国の王。かつての亡者どもの怒りを背に、新なる大戦へと赴く。");
            this.add("boss_screen.cataclysm_primed_soul.maledictus_prime.suggestion.1", "幻影(槍・メイス・弓)の予備動作を見極めて適切な方向に回避してください。");
            this.add("boss_screen.cataclysm_primed_soul.maledictus_prime.suggestion.2", "構え中の不用意な攻撃は背後への瞬間移動カウンターを誘発します。");
            this.add("boss_screen.cataclysm_primed_soul.maledictus_prime.suggestion.3", "拘束攻撃を受けた場合、連続ダメージと叩き落としに耐える準備が必要です。");
            this.add("boss_screen.cataclysm_primed_soul.maledictus_prime.advanced_info.phantoms.title", "幻影の武具");
            this.add("boss_screen.cataclysm_primed_soul.maledictus_prime.advanced_info.phantoms.desc", "槍の突進、メイスの叩きつけ、弓の狙撃を行うファントムを召喚します。");
            this.add("boss_screen.cataclysm_primed_soul.maledictus_prime.advanced_info.counter.title", "幻惑カウンター");
            this.add("boss_screen.cataclysm_primed_soul.maledictus_prime.advanced_info.counter.desc", "攻撃を受けると背後へテレポートし、即座に致命的な反撃を行います。");
            this.add("boss_screen.cataclysm_primed_soul.maledictus_prime.advanced_info.grab.title", "断罪の掴み");
            this.add("boss_screen.cataclysm_primed_soul.maledictus_prime.advanced_info.grab.desc", "プレイヤーを片手で拘束して上空へ飛び上がり、地面へ叩きつけます。");
            this.add("boss_screen.cataclysm_primed_soul.maledictus_prime.advanced_info.phase2.title", "鎧の崩壊");
            this.add("boss_screen.cataclysm_primed_soul.maledictus_prime.advanced_info.phase2.desc", "HPが50%以下になると鎧が破壊され、もう一体の幻影が現れ、攻撃が激化します。");
            this.add("dialogue.cataclysm_primed_soul.maledictus_prime.name", "マレディクタス・プライム");
            this.add("dialogue.cataclysm_primed_soul.maledictus_prime.line.0", "ああ...なんと眩い光だ...。");
            this.add("dialogue.cataclysm_primed_soul.maledictus_prime.line.1", "幾世代もの呪いが...ようやく薄れ始める。");
            this.add("dialogue.cataclysm_primed_soul.maledictus_prime.line.2", "行け、戦士よ。王冠はいつだって重荷だった。");
        }
    }
}
