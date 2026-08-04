package com.maxwell.cataclysm_primed_soul.client;

import com.github.L_Ender.cataclysm.client.particle.Options.RingParticleOptions;
import com.github.L_Ender.cataclysm.client.particle.RingParticle;
import com.github.L_Ender.cataclysm.client.particle.RingParticle.EnumRingBehavior;
import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import com.maxwell.cataclysm_primed_soul.api.entity.IShaderBoss;
import com.maxwell.cataclysm_primed_soul.api.item.IShaderItem;
import com.maxwell.cataclysm_primed_soul.network.packet.MessageIgnisVisualEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@SuppressWarnings("removal")
@Mod.EventBusSubscriber(modid = Primed_Soul.MODID, value = Dist.CLIENT)
public class ClientVisuals {
    private static ResourceLocation activeShaderPath = null;
    private static int currentDebuffLevel = 0;
    private static int tickCount = 0;
    private static boolean ascending = true;

    public static void setDebuffLevel(int level) {
        currentDebuffLevel = level;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            currentDebuffLevel = 0;
            activeShaderPath = null;
            return;
        }
        if (ascending) {
            tickCount++;
            if (tickCount >= 16000) ascending = false;
        } else {
            tickCount--;
            if (tickCount <= 0) ascending = true;
        }
        ResourceLocation nextShaderPath = null;
        int maxLevelFound = 0;
        for (Entity entity : mc.level.entitiesForRendering()) {
            if (entity instanceof IShaderBoss shaderBoss) {
                if (shaderBoss.shouldApplyDebuff(mc.player)) {
                    int bossLevel = shaderBoss.getDebuffLevel();
                    if (bossLevel > maxLevelFound) {
                        maxLevelFound = bossLevel;
                        nextShaderPath = shaderBoss.getDebuffShader();
                    }
                }
            }
        }
        ItemStack mainHandStack = mc.player.getMainHandItem();
        if (mainHandStack.getItem() instanceof IShaderItem shaderItem) {
            int itemLevel = shaderItem.getDebuffLevel(mainHandStack);
            if (itemLevel > maxLevelFound) {
                maxLevelFound = itemLevel;
                nextShaderPath = shaderItem.getDebuffShader(mainHandStack);
            }
        }
        currentDebuffLevel = maxLevelFound;
        if (currentDebuffLevel > 0 && nextShaderPath != null) {
            if (mc.gameRenderer.currentEffect() == null || !mc.gameRenderer.currentEffect().getName().equals(nextShaderPath.toString())) {
                mc.gameRenderer.loadEffect(nextShaderPath);
                activeShaderPath = nextShaderPath;
            }
            if (mc.gameRenderer.currentEffect() != null) {
                PostChain effect = mc.gameRenderer.currentEffect();
                try {
                    for (java.lang.reflect.Field f : PostChain.class.getDeclaredFields()) {
                        if (java.util.List.class.isAssignableFrom(f.getType())) {
                            f.setAccessible(true);
                            java.util.List<?> list = (java.util.List<?>) f.get(effect);
                            if (list != null) {
                                for (Object p : list) {
                                    if (p instanceof PostPass pass) {
                                        if (pass.getEffect().getUniform("DebuffLevel") != null) {
                                            pass.getEffect().getUniform("DebuffLevel").set((float) currentDebuffLevel);
                                        }
                                        if (pass.getEffect().getUniform("PrimeTime") != null) {
                                            pass.getEffect().getUniform("PrimeTime").set((float) tickCount);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (mc.gameRenderer.currentEffect() != null && activeShaderPath != null && mc.gameRenderer.currentEffect().getName().equals(activeShaderPath.toString())) {
                mc.gameRenderer.shutdownEffect();
                activeShaderPath = null;
            }
        }
    }

    public static void handleEffect(MessageIgnisVisualEffect msg) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        Entity entity = mc.level.getEntity(msg.getEntityId());
        if (!(entity instanceof LivingEntity boss)) return;
        try {
            switch (msg.getEffectType()) {
                case 0:
                    spawnUppercutVisuals(boss);
                    break;
                case 1:
                    spawnPowerSlamVisuals(boss);
                    break;
                case 2:
                    spawnChargeVisuals(boss);
                    break;
                case 3:
                    spawnPhaseChangeVisuals(boss);
                    break;
                case 4:
                    spawnGuardSuccessVisuals(boss);
                    break;
                case 5:
                    spawnGuardPrimedVisuals(boss);
                    break;
            }
        } catch (Exception e) {
            System.err.println("[Ignis Visual Effect] Error executing client visuals: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void spawnGuardPrimedVisuals(LivingEntity boss) {
        var level = boss.level();
        var rand = boss.getRandom();
        float yaw = boss.yBodyRot * ((float) Math.PI / 180F);
        double fx = boss.getX() - Math.sin(yaw) * 1.0D;
        double fz = boss.getZ() + Math.cos(yaw) * 1.0D;
        for (int i = 0; i < 8; i++) {
            double mx = (rand.nextDouble() - 0.5D) * 0.1D;
            double my = (rand.nextDouble() - 0.5D) * 0.1D;
            double mz = (rand.nextDouble() - 0.5D) * 0.1D;
            level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, fx + rand.nextGaussian() * 0.4D, boss.getY() + 1.2D + rand.nextGaussian() * 0.4D, fz + rand.nextGaussian() * 0.4D, mx, my, mz);
            level.addParticle(ParticleTypes.GLOW, fx + rand.nextGaussian() * 0.4D, boss.getY() + 1.2D + rand.nextGaussian() * 0.4D, fz + rand.nextGaussian() * 0.4D, 0.0D, 0.0D, 0.0D);
        }
        if (rand.nextInt(3) == 0) {
            level.addParticle(ParticleTypes.FLASH, fx, boss.getY() + 1.5D, fz, 0.0D, 0.0D, 0.0D);
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
        level.addParticle(
                new RingParticleOptions(
                        0.0F,                          // yaw
                        ((float) Math.PI / 2F),        // pitch
                        25,                            // duration
                        255,                           // r (以前の 1.0F から変更)
                        255,                           // g (以前の 1.0F から変更)
                        255,                           // b (以前の 1.0F から変更)
                        1.0F,                          // a
                        15.0F,                         // scale
                        false,                         // facesCamera
                        EnumRingBehavior.GROW.ordinal() // behavior (Enum から int に合わせるため ordinal() を使用)
                ),
                boss.getX(), boss.getY() + 0.1D, boss.getZ(),
                0.0D, 0.0D, 0.0D
        );
    }

    private static void spawnChargeVisuals(LivingEntity boss) {
        var level = boss.level();
        var rand = boss.getRandom();
        float yaw = boss.yBodyRot * ((float) Math.PI / 180F);
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

        level.addParticle(
                new RingParticleOptions(
                        0.0F,                          // yaw
                        ((float) Math.PI / 2F),        // pitch
                        40,                            // duration
                        255,                           // r (以前の 1.0F から変更)
                        255,                           // g (以前の 1.0F から変更)
                        255,                           // b (以前の 1.0F から変更)
                        1.0F,                          // a
                        35.0F,                         // scale
                        false,                         // facesCamera
                        EnumRingBehavior.GROW.ordinal() // behavior (Enum から int に合わせるため ordinal() を使用)
                ),
                boss.getX(), boss.getY() + 0.1D, boss.getZ(),
                0.0D, 0.0D, 0.0D
        );
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