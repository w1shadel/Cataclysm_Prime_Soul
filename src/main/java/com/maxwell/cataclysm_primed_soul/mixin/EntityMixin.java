package com.maxwell.cataclysm_primed_soul.mixin;

import com.maxwell.cataclysm_primed_soul.util.IDecayEntity;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "kill", at = @At("HEAD"), cancellable = true)
    private void csp$preventKill(CallbackInfo ci) {
        if ((Object) this instanceof IDecayEntity decay && decay.isSuperInvincible()) {
            ci.cancel();
        }
    }

    @Inject(method = "setRemoved", at = @At("HEAD"), cancellable = true)
    private void csp$preventRemoval(Entity.RemovalReason reason, CallbackInfo ci) {
        if ((Object) this instanceof IDecayEntity decay && decay.isSuperInvincible()) {
            if (!decay.isRemoveBypass()) {
                ci.cancel();
            }
        }
    }
}