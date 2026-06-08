package com.maxwell.cataclysm_primed_soul;

import com.maxwell.cataclysm_primed_soul.api.config.ModConfig;
import com.maxwell.cataclysm_primed_soul.init.ModEntities;
import com.maxwell.cataclysm_primed_soul.init.ModItems;
import com.maxwell.cataclysm_primed_soul.init.ModTabs;
import com.maxwell.cataclysm_primed_soul.network.ModMessages;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
@Mod.EventBusSubscriber(modid = Primed_Soul.MODID)
@Mod(Primed_Soul.MODID)
public class Primed_Soul {
    public static final String MODID = "cataclysm_primed_soul";

    public Primed_Soul(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModTabs.CREATIVE_TABS.register(modEventBus);

        ModConfig.register(context);

        modEventBus.addListener(this::addCreativeContents);
        modEventBus.addListener(this::commonSetup);
    }

    private void addCreativeContents(net.minecraftforge.event.BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == ModTabs.PRIME_TAB.get()) {
            ModItems.ITEMS.getEntries().forEach(item -> event.accept(item.get()));
        }
    }
    private void commonSetup(final net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent event) {
        event.enqueueWork(ModMessages::register);
    }

    @SubscribeEvent
    public static void onPlayerRightClickItem(net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem event) {
        net.minecraft.world.level.Level level = event.getLevel();
        if (level.isClientSide()) return; 

        net.minecraft.world.entity.player.Player player = event.getEntity();
        net.minecraft.world.item.ItemStack stack = event.getItemStack();

        if (stack.is(net.minecraft.world.item.Items.STICK)) {
            net.minecraft.nbt.CompoundTag tag = stack.getOrCreateTag();
            int currentType = tag.getInt("DebugPhantomType"); 

            net.minecraft.world.phys.Vec3 spawnPos = player.getEyePosition().add(player.getLookAngle().scale(2.0D));

            com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.MaledictusPhantomEntity phantom =
                    new com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.MaledictusPhantomEntity(
                            com.maxwell.cataclysm_primed_soul.init.ModEntities.MALEDICTUS_PHANTOM.get(), level
                    );

            if (phantom != null) {
                phantom.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
                phantom.setPhantomType(currentType);
                phantom.setSummonerYRot(player.getYRot()); 
                phantom.setSummoner(player);
                phantom.setTarget(player); 

                boolean success = level.addFreshEntity(phantom);
                if (success) {
                    String weaponName = switch (currentType) {
                        case 1  -> "メイス (MACE)";
                        case 2  -> "弓 (BOW)";
                        default -> "ハルバード (SPEAR)";
                    };
                    player.displayClientMessage(net.minecraft.network.chat.Component.literal(
                            "§a[デバッグ] 幻影「" + weaponName + "」をスポーンさせました！"
                    ), true);

                    tag.putInt("DebugPhantomType", (currentType + 1) % 3);
                }
            }

            player.getCooldowns().addCooldown(net.minecraft.world.item.Items.STICK, 20);
            event.setCanceled(true);
        }
    }
}