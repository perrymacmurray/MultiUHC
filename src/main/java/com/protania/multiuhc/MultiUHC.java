package com.protania.multiuhc;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.GameType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("multiuhc")
public class MultiUHC
{
    protected static final Logger LOGGER = LogManager.getLogger();

    public MultiUHC() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        LOGGER.info("MultiUHC Init");
        LOGGER.info("Note that this mod is intended for Hardcore/UltraHardcore servers, and doesn't make much sense otherwise. However, setting the server to hardcore mode has unintended consequences.");
    }

    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onItemRegistry(final RegistryEvent.Register<Item> itemRegistryEvent) {
            itemRegistryEvent.getRegistry().register(ModItems.VITAPPLE);
        }

        @SubscribeEvent
        public static void onBlockRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            blockRegistryEvent.getRegistry().register(Gravestone.INSTANCE);
        }

        @SubscribeEvent
        public static void onTERegistry(final RegistryEvent.Register<TileEntityType<?>> teRegistryEvent) {
            Gravestone.tileGravestone.setRegistryName("multiuhc", "gravestone_tile_entity");
            teRegistryEvent.getRegistry().register(Gravestone.tileGravestone);
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.isCanceled())
            return;

        if (event.getEntity() instanceof PlayerEntity) {
            PlayerEntity entity = (PlayerEntity)event.getEntity();

            entity.setGameType(GameType.SPECTATOR);
            entity.inventory.clear();
            Vector3d posVec = entity.getPositionVec();
            LOGGER.debug("Creating gravestone at " + posVec.x + ' ' + posVec.y + ' ' + posVec.z);
            BlockPos pos = new BlockPos((int)(posVec.x), (int)(posVec.y), (int)(posVec.z));
            BlockState old = entity.world.getBlockState(pos);
            if (!entity.world.setBlockState(pos, Gravestone.INSTANCE.getDefaultState()))
                LOGGER.error("Could not create gravestone");

            GravestoneTileEntity tile = (GravestoneTileEntity)(entity.world.getTileEntity(pos));
            tile.assignOwner(entity);
            BlockState cur = entity.world.getBlockState(pos);
            LOGGER.debug("Calling block update - block is now " + cur.getBlock().getTranslationKey());
            entity.world.notifyBlockUpdate(pos, old, cur, 2);

            entity.sendStatusMessage(new StringTextComponent("You have died. A player may right click your gravestone with a Vitality Apple to revive you."), false);
        }
    }
}
