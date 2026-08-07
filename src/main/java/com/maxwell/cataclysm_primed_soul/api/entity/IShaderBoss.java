package com.maxwell.cataclysm_primed_soul.api.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public interface IShaderBoss {
    ResourceLocation getDebuffShader();

    int getDebuffLevel();

    double getDebuffRangeSq();

    default boolean shouldApplyDebuff(Player player) {
        if (this instanceof Entity entity) {
            return entity.isAlive() && entity.distanceToSqr(player) <= this.getDebuffRangeSq();
        }
        return false;
    }
}