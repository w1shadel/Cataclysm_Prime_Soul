package com.maxwell.cataclysm_primed_soul.client;

import com.maxwell.cataclysm_primed_soul.network.packet.MessageIgnisVisualEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

// Cataclysmのインポート
import com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity;
import com.github.L_Ender.cataclysm.client.particle.RingParticle;
import com.github.L_Ender.cataclysm.client.particle.RingParticle.EnumRingBehavior;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.resources.ResourceLocation;
import com.maxwell.cataclysm_primed_soul.Primed_Soul;

@Mod.EventBusSubscriber(modid = Primed_Soul.MODID, value = Dist.CLIENT)
public class ClientVisuals {
    private static int currentDebuffLevel = 0;
    private static final ResourceLocation DEBUFF_SHADER = new ResourceLocation(Primed_Soul.MODID, "shaders/post/ignis_debuff.json");
    private static int tickCount = 0;

    public static void setDebuffLevel(int level) {
        currentDebuffLevel = level;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            currentDebuffLevel = 0;
            return;
        }

        tickCount++;

        if (currentDebuffLevel > 0) {
            // 安全対策: 周囲80m以内に生存しているIgnis_PrimeEntityが存在するか確認
            boolean bossExists = false;
            for (Entity entity : mc.level.entitiesForRendering()) {
                if (entity instanceof com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime.Ignis_PrimeEntity boss) {
                    if (boss.isAlive() && mc.player.distanceToSqr(boss) <= 80 * 80) {
                        bossExists = true;
                        break;
                    }
                }
            }
            if (!bossExists) {
                currentDebuffLevel = 0;
            }
        }

        if (currentDebuffLevel > 0) {
            if (mc.gameRenderer.currentEffect() == null || !mc.gameRenderer.currentEffect().getName().equals(DEBUFF_SHADER.toString())) {
                mc.gameRenderer.loadEffect(DEBUFF_SHADER);
            }
            if (mc.gameRenderer.currentEffect() != null) {
                net.minecraft.client.renderer.PostChain effect = mc.gameRenderer.currentEffect();
                try {
                    java.lang.reflect.Field passesField = null;
                    for (java.lang.reflect.Field f : net.minecraft.client.renderer.PostChain.class.getDeclaredFields()) {
                        if (java.util.List.class.isAssignableFrom(f.getType())) {
                            passesField = f;
                            break;
                        }
                    }
                    if (passesField != null) {
                        passesField.setAccessible(true);
                        java.util.List<?> passes = (java.util.List<?>) passesField.get(effect);
                        for (Object p : passes) {
                            if (p instanceof net.minecraft.client.renderer.PostPass pass) {
                                if (pass.getEffect().getUniform("DebuffLevel") != null) {
                                    pass.getEffect().getUniform("DebuffLevel").set((float) currentDebuffLevel);
                                }
                                if (pass.getEffect().getUniform("Time") != null) {
                                    pass.getEffect().getUniform("Time").set((float) tickCount);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (mc.gameRenderer.currentEffect() != null && mc.gameRenderer.currentEffect().getName().equals(DEBUFF_SHADER.toString())) {
                mc.gameRenderer.shutdownEffect();
            }
        }
    }
    public static void handleEffect(MessageIgnisVisualEffect msg) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        Entity entity = mc.level.getEntity(msg.getEntityId());
        if (!(entity instanceof LivingEntity boss)) return;

        System.out.println("[Ignis Visual Effect] Received packet! Type: " + msg.getEffectType() + " for Boss ID: " + msg.getEntityId());

        try {
            switch (msg.getEffectType()) {
                case 0: // アッパーカット (火山風白い炎)
                    spawnUppercutVisuals(boss);
                    break;
                case 1: // パワースラム (地面大破裂)
                    spawnPowerSlamVisuals(boss);
                    break;
                case 2: // 突進
                    spawnChargeVisuals(boss);
                    break;
                case 3: // 第二形態移行 (極大ソウル爆発)
                    spawnPhaseChangeVisuals(boss);
                    break;
                case 4: // ガード成功
                    spawnGuardSuccessVisuals(boss);
                    break;
            }
        } catch (Exception e) {
            System.err.println("[Ignis Visual Effect] Error executing client visuals: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void spawnUppercutVisuals(LivingEntity boss) {
        var level = boss.level();
        var rand = boss.getRandom();

        for (int i = 0; i < 40; i++) {
            double mx = rand.nextGaussian() * 0.15D;
            double my = 0.5D + rand.nextDouble() * 0.5D;
            double mz = rand.nextGaussian() * 0.15D;
            level.addParticle(ParticleTypes.CLOUD, boss.getRandomX(1.0D), boss.getY(), boss.getRandomZ(1.0D), mx, my, mz);
            level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, boss.getRandomX(1.0D), boss.getY(), boss.getRandomZ(1.0D), mx, my, mz);
        }
    }

    private static void spawnPowerSlamVisuals(LivingEntity boss) {
        var level = boss.level();
        var rand = boss.getRandom();

        BlockPos below = boss.blockPosition().below();
        BlockState state = level.getBlockState(below);
        if (state.isAir()) {
            state = Blocks.STONE.defaultBlockState();
        }

        for (int i = 0; i < 80; i++) {
            double angle = rand.nextDouble() * Math.PI * 2.0D;
            double speed = 0.1D + rand.nextDouble() * 0.4D;
            double mx = Math.cos(angle) * speed;
            double my = 0.2D + rand.nextDouble() * 0.3D;
            double mz = Math.sin(angle) * speed;

            level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), boss.getX(), boss.getY() + 0.1D, boss.getZ(), mx, my, mz);
            level.addParticle(ParticleTypes.FLAME, boss.getX(), boss.getY() + 0.1D, boss.getZ(), mx, my, mz);
        }

        level.addParticle(new RingParticle.RingData(0.0F, ((float)Math.PI / 2F), 25, 1.0F, 1.0F, 1.0F, 1.0F, 15.0F, false, EnumRingBehavior.GROW), boss.getX(), boss.getY() + 0.1D, boss.getZ(), 0.0D, 0.0D, 0.0D);
    }

    private static void spawnChargeVisuals(LivingEntity boss) {
        var level = boss.level();
        var rand = boss.getRandom();

        float yaw = boss.yBodyRot * ((float)Math.PI / 180F);
        Vec3 forward = new Vec3(-Mth.sin(yaw), 0.0D, Mth.cos(yaw));

        for (int i = 0; i < 10; i++) {
            level.addParticle(ParticleTypes.SWEEP_ATTACK, boss.getX() + rand.nextGaussian() * 0.5D, boss.getY() + 1.0D + rand.nextGaussian() * 0.5D, boss.getZ() + rand.nextGaussian() * 0.5D, forward.x * 0.5D, 0.0D, forward.z * 0.5D);
            level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, boss.getX() + rand.nextGaussian() * 0.8D, boss.getY() + 0.5D, boss.getZ() + rand.nextGaussian() * 0.8D, 0.0D, 0.0D, 0.0D);
        }
    }

    private static void spawnPhaseChangeVisuals(LivingEntity boss) {
        var level = boss.level();
        var rand = boss.getRandom();

        for (int i = 0; i < 150; i++) {
            double angle = rand.nextDouble() * Math.PI * 2.0D;
            double speed = 0.2D + rand.nextDouble() * 0.8D;
            double mx = Math.cos(angle) * speed;
            double my = -0.1D + rand.nextDouble() * 0.8D;
            double mz = Math.sin(angle) * speed;

            level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, boss.getX(), boss.getY() + 1.0D, boss.getZ(), mx, my, mz);
            level.addParticle(ParticleTypes.LARGE_SMOKE, boss.getX(), boss.getY() + 1.0D, boss.getZ(), mx * 0.5D, my, mz * 0.5D);
        }

        level.addParticle(new RingParticle.RingData(0.0F, ((float)Math.PI / 2F), 40, 0.2F, 0.7F, 1.0F, 1.0F, 35.0F, false, EnumRingBehavior.GROW), boss.getX(), boss.getY() + 0.1D, boss.getZ(), 0.0D, 0.0D, 0.0D);
    }

    private static void spawnGuardSuccessVisuals(LivingEntity boss) {
        var level = boss.level();
        var rand = boss.getRandom();

        for (int i = 0; i < 20; i++) {
            double mx = rand.nextGaussian() * 0.2D;
            double my = rand.nextDouble() * 0.2D;
            double mz = rand.nextGaussian() * 0.2D;
            level.addParticle(ParticleTypes.CRIT, boss.getX() + rand.nextGaussian() * 0.5D, boss.getY() + 1.5D, boss.getZ() + rand.nextGaussian() * 0.5D, mx, my, mz);
            level.addParticle(ParticleTypes.LAVA, boss.getX() + rand.nextGaussian() * 0.5D, boss.getY() + 1.5D, boss.getZ() + rand.nextGaussian() * 0.5D, mx, my, mz);
        }
    }
}
