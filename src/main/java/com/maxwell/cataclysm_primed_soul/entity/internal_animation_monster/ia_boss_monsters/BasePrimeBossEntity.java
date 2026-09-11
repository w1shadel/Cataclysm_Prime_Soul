package com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters;

import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.IABossMonsters.IABoss_monster;
import com.maxwell.hyperdamagelib.util.InvincibleHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public abstract class BasePrimeBossEntity extends IABoss_monster {

    private int customIFrameCounter = 0;
    protected static final int I_FRAME_DURATION = 20; 

    protected BasePrimeBossEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide()) {

            if (this.customIFrameCounter > 0) {
                this.customIFrameCounter--;

                if (this.customIFrameCounter == 0) {
                    InvincibleHelper.setInvincible(this, false);
                }
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {

        if (this.customIFrameCounter > 0 || InvincibleHelper.isInvincible(this)) {
            return false;
        }

        boolean hurtResult = super.hurt(source, amount);

        if (!this.level().isClientSide()) {
            this.customIFrameCounter = I_FRAME_DURATION;
            InvincibleHelper.setInvincible(this, true);
        }

        return hurtResult;
    }

    @Override
    public void die(DamageSource source) {
        this.cleanupInvincibleState();
        super.die(source);
    }

    @Override
    public void remove(RemovalReason reason) {
        this.cleanupInvincibleState();
        super.remove(reason);
    }

    @Override
    public void onRemovedFromWorld() {
        this.cleanupInvincibleState();
        super.onRemovedFromWorld();
    }

    protected void cleanupInvincibleState() {
        this.customIFrameCounter = 0;
        InvincibleHelper.setInvincible(this, false);
    }
}