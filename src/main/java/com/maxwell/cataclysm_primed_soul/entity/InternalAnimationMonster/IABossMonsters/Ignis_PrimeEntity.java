package com.maxwell.cataclysm_primed_soul.entity.InternalAnimationMonster.IABossMonsters;

import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.IABossMonsters.IABoss_monster;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class Ignis_PrimeEntity extends IABoss_monster {
    public Ignis_PrimeEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 600.0D)   
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 20.0D) 
                .add(Attributes.ARMOR, 20.0D)         
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D); 
    }
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState spawnAnimationState = new AnimationState();
    public AnimationState charge_attackAnimationState = new AnimationState();
    public AnimationState charge_attack_loopAnimationState = new AnimationState();
    public AnimationState charge_attack_endAnimationState = new AnimationState();
    public AnimationState charge_shockwave_attackAnimationState = new AnimationState();
    public AnimationState rock_excavation_attackAnimationState = new AnimationState();
    public AnimationState rock_excavation_attack_loopAnimationState = new AnimationState();
    public AnimationState rock_excavation_attack_endAnimationState = new AnimationState();
    public AnimationState uppercutAnimationState = new AnimationState();
    public AnimationState uppercut_horizontal_comboAnimationState = new AnimationState();
    public AnimationState uppercut_vertical_comboAnimationState = new AnimationState();
    public AnimationState jab_attack_oneAnimationState = new AnimationState();
    public AnimationState jab_attack_twoAnimationState = new AnimationState();
    public AnimationState jab_attack_threeAnimationState = new AnimationState();
    public AnimationState deadAnimationState = new AnimationState();
    public AnimationState mode_changeAnimationState = new AnimationState();
    public AnimationState walkAnimationState = new AnimationState();

    public AnimationState getAnimationState(String input) {
        if (input == "swing") {
            return this.spawnAnimationState;
        } else if (input == "charge_attack") {
            return this.charge_attackAnimationState;
        } else if (input == "charge_attack_loop") {
            return this.charge_attack_loopAnimationState;
        } else if (input == "charge_attack_end") {
            return this.charge_attack_endAnimationState;
        } else if (input == "idle") {
            return this.idleAnimationState;
        } else if (input == "charge_shockwave_attack") {
            return this.charge_shockwave_attackAnimationState;
        } else if (input == "rock_excavation_attack") {
            return this.rock_excavation_attackAnimationState;
        } else if (input == "rock_excavation_attack_loop") {
            return this.rock_excavation_attack_loopAnimationState;
        } else if (input == "rock_excavation_attack_end") {
            return this.rock_excavation_attack_endAnimationState;
        } else if (input == "uppercut") {
            return this.uppercutAnimationState;
        } else if (input == "uppercut_horizontal_combo") {
            return this.uppercut_horizontal_comboAnimationState;
        } else if (input == "uppercut_vertical_combo") {
            return this.uppercut_vertical_comboAnimationState;
        } else if (input == "jab_attack_one") {
            return this.jab_attack_oneAnimationState;
        } else if (input == "jab_attack_two") {
            return this.jab_attack_twoAnimationState;
        } else if (input == "jab_attack_three") {
            return this.jab_attack_threeAnimationState;
        } else if (input == "dead") {
            return this.deadAnimationState;
        } else if (input == "mode_change") {
            return this.mode_changeAnimationState;
        } else{
            return this.walkAnimationState;
        }
    }
}
