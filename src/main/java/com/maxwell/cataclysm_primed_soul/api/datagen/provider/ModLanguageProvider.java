package com.maxwell.cataclysm_primed_soul.api.datagen.provider;

import com.maxwell.cataclysm_primed_soul.Primed_Soul;
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
            this.add("tooltip.cataclysm_primed_soul.abyssal_ashes.desc","§6 If you offer it to the Altar of Fire again, something might happen.");
            this.add(ModItems.LAVATEIN.get(),"Lavatein");
            this.add("item.cataclysm_primed_soul.lavatein.desc","A sword that gains speed with every strike, its outer layer peeling away.");
            this.add("item.cataclysm_primed_soul.lavatein.desc2","Applies debuffs to the target based on the three stages of release.");
            this.add("creative_tab.cataclysm_primed_soul.prime_tab", "Cataclysm: Primed Soul");
            this.add("advancement.cataclysm_primed_soul.root.title", "Dormant Embers");
            this.add("advancement.cataclysm_primed_soul.root.desc", "Obtain Abyssal Ashes, the catalyst of the forbidden ritual.");
            this.add("advancement.cataclysm_primed_soul.defeat_prime.title", "Slayer of the Prime Soul");
            this.add("advancement.cataclysm_primed_soul.defeat_prime.desc", "Defeat Ignis Prime, the Lord of Black Flame.");
            this.add("entity.cataclysm_primed_soul.ignis_prime", "Ignis Prime");
            this.add("chat.cataclysm_primed_soul.ignis_prime.appear", "The excitement around me is building...");
            this.add("chat.cataclysm_primed_soul.ignis_prime.half_hp", "The heat transforms into unrelenting pressure, and the equipment begins to melt.");
            this.add("chat.cataclysm_primed_soul.ignis_prime.phase_2", "A pure white soul fills my vision. All protection has turned to ash...");
            this.add("chat.cataclysm_primed_soul.ignis_prime.ultracharge", "§6...I am......");
        }
    }

    public static class Japanese extends ModLanguageProvider {
        public Japanese(PackOutput output) {
            super(output, "ja_jp");
        }

        @Override
        protected void addTranslations() {
            this.add(ModItems.ABYSSAL_ASHES.get(), "§b深淵の灰");
            this.add("tooltip.cataclysm_primed_soul.abyssal_ashes.desc","§6再び炎の祭壇に捧げれば、何かが起こるかもしれない。");
            this.add(ModItems.LAVATEIN.get(),"ラーヴァテイン");
            this.add("item.cataclysm_primed_soul.lavatein.desc","攻撃すればするたび、外装が剥がれて加速していく剣。");
            this.add("item.cataclysm_primed_soul.lavatein.desc2","解放段階3つに応じて、対象にデバフを付与する。");
            this.add("creative_tab.cataclysm_primed_soul.prime_tab", "Primed Souls");
            this.add("advancement.cataclysm_primed_soul.root.title", "燻る残り火");
            this.add("advancement.cataclysm_primed_soul.root.desc", "禁忌の儀式の触媒である『深淵の灰』を入手する。");
            this.add("advancement.cataclysm_primed_soul.defeat_prime.title", "極限のソウルを屠る者");
            this.add("advancement.cataclysm_primed_soul.defeat_prime.desc", "黒炎を統べる者、『イグニス・プライム』を討伐する。");
            this.add("entity.cataclysm_primed_soul.ignis_prime", "イグニス・プライム");
            this.add("chat.cataclysm_primed_soul.ignis_prime.appear", "周囲の熱気が上昇していく...");
            this.add("chat.cataclysm_primed_soul.ignis_prime.half_hp", "熱気が逃げ場のない圧力へと変わり、装備は溶け出す。");
            this.add("chat.cataclysm_primed_soul.ignis_prime.phase_2", "純白のソウルが視界を染め上げる。あらゆる加護は灰に消えた...");
            this.add("chat.cataclysm_primed_soul.ignis_prime.ultracharge", "§6...私は......");
        }
    }
}