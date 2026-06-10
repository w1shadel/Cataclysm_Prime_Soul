package com.maxwell.cataclysm_primed_soul.api.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public interface IShaderBoss {
    boolean isAlive();

    ResourceLocation getDebuffShader();

    int getDebuffLevel();

    double getDebuffRangeSq();

    default boolean shouldApplyDebuff(Player player) {
        return this.isAlive() && ((net.minecraft.world.entity.Entity) this).distanceToSqr(player) <= this.getDebuffRangeSq();
    }
}