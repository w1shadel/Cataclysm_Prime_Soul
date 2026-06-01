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
            this.add("advancement.cataclysm_primed_soul.root.title", "Cataclysm: Primed Souls");
            this.add("advancement.cataclysm_primed_soul.root.desc", "The First Step, The Last Step");
            this.add("advancement.cataclysm_primed_soul.defeat_ignis_prime.title", "THIS HEAT, AN EVIL HEAT");
            this.add("advancement.cataclysm_primed_soul.defeat_ignis_prime.desc", "You subdued the raging white flames with sheer force.");
            this.add("advancement.cataclysm_primed_soul.defeat_maledictus_prime.title", "SOUL SURVIVOR");
            this.add("advancement.cataclysm_primed_soul.defeat_maledictus_prime.desc", "Defeat the true hero long forgotten.");
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
            this.add("advancement.cataclysm_primed_soul.root.title", "Cataclysm: Primed Souls");
            this.add("advancement.cataclysm_primed_soul.root.desc", "最初の一歩、最後の一歩");
            this.add("advancement.cataclysm_primed_soul.defeat_ignis_prime.title", "THIS HEAT, AN EVIL HEAT");
            this.add("advancement.cataclysm_primed_soul.defeat_ignis_prime.desc", "猛る白炎を力でもって制圧した。");
            this.add("advancement.cataclysm_primed_soul.defeat_maledictus_prime.title", "SOUL SURVIVOR");
            this.add("advancement.cataclysm_primed_soul.defeat_maledictus_prime.desc", "古に忘れ去られた真なる英雄を倒す。");
            this.add("entity.cataclysm_primed_soul.ignis_prime", "イグニス・プライム");
            this.add("chat.cataclysm_primed_soul.ignis_prime.appear", "周囲の熱気が上昇していく...");
            this.add("chat.cataclysm_primed_soul.ignis_prime.half_hp", "熱気が逃げ場のない圧力へと変わり、装備は溶け出す。");
            this.add("chat.cataclysm_primed_soul.ignis_prime.phase_2", "純白のソウルが視界を染め上げる。あらゆる加護は灰に消えた...");
            this.add("chat.cataclysm_primed_soul.ignis_prime.ultracharge", "§6...私は......");
        }
    }
}