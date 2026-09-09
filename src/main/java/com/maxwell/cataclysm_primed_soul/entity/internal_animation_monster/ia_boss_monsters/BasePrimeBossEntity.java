package com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters;

import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.IABossMonsters.IABoss_monster;
import com.maxwell.hyperdamagelib.util.InvincibleHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/**
 * 被ダメージ後の無敵時間（i-frame）の間だけ InvincibleHelper の無敵を有効化するボス基底クラス
 */
public abstract class BasePrimeBossEntity extends IABoss_monster {
    private boolean wasIFrameInvincible = false;

    protected BasePrimeBossEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        super.tick();

        // サーバーサイドでのみ無敵状態の切り替わりを監視
        if (!this.level().isClientSide()) {
            this.updateIFrameInvincibleState();
        }
    }

    /**
     * 無敵時間（invulnerableTime > 0）の状態変化を検知して InvincibleHelper を更新
     */
    protected void updateIFrameInvincibleState() {
        // 生存しており、かつ無敵時間が残っているかどうか
        boolean isCurrentlyIFrame = this.isAlive() && !this.isDeadOrDying() && this.invulnerableTime > 0;

        if (this.wasIFrameInvincible != isCurrentlyIFrame) {
            this.wasIFrameInvincible = isCurrentlyIFrame;
            InvincibleHelper.setInvincible(this, isCurrentlyIFrame);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean hurtResult = super.hurt(source, amount);

        // ダメージが実際に通った場合、即座に無敵化フラグを反映させる
        if (hurtResult && !this.level().isClientSide() && this.invulnerableTime > 0) {
            if (!this.wasIFrameInvincible) {
                this.wasIFrameInvincible = true;
                InvincibleHelper.setInvincible(this, true);
            }
        }

        return hurtResult;
    }

    @Override
    public void die(DamageSource source) {
        // 死亡時は確実に無敵を解除
        this.cleanupInvincibleState();
        super.die(source);
    }

    @Override
    public void remove(RemovalReason reason) {
        // ワールドから除外される際も確実にクリーンアップ
        this.cleanupInvincibleState();
        super.remove(reason);
    }

    @Override
    public void onRemovedFromWorld() {
        this.cleanupInvincibleState();
        super.onRemovedFromWorld();
    }

    /**
     * 無敵フラグのお掃除処理
     */
    protected void cleanupInvincibleState() {
        if (this.wasIFrameInvincible || InvincibleHelper.isInvincible(this)) {
            this.wasIFrameInvincible = false;
            InvincibleHelper.setInvincible(this, false);
        }
    }
}