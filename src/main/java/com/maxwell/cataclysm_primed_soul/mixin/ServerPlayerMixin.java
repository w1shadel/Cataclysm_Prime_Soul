package com.maxwell.cataclysm_primed_soul.mixin;

import com.maxwell.cataclysm_primed_soul.util.IDecayEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
    @Unique
    private boolean csp$isLoginIncomplete() {
        ServerPlayer player = (ServerPlayer) (Object) this;
        return player.connection == null;
    }
    @Inject(method = "die", at = @At("HEAD"), cancellable = true)
    private void csp$preventServerPlayerDie(DamageSource source, CallbackInfo ci) {
        if (csp$isLoginIncomplete()) {
            ci.cancel();
            return;
        }

        if ((Object) this instanceof IDecayEntity decay && decay.isSuperInvincible()) {
            ci.cancel();
        }
    }
}