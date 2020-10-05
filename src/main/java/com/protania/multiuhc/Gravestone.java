package com.protania.multiuhc;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.GameType;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class Gravestone extends Block {
    public static Block INSTANCE = new Gravestone();
    public static TileEntityType<?> tileGravestone = TileEntityType.Builder.create(GravestoneTileEntity::new, INSTANCE).build(null);

    private Gravestone() {
        super(Block.Properties.create(Material.ROCK).doesNotBlockMovement().hardnessAndResistance(-1.0F, 3600000.0F));
        setRegistryName("gravestone");
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        GravestoneTileEntity entity = (GravestoneTileEntity) worldIn.getTileEntity(pos);

        if (player.getHeldItem(handIn).isItemEqual(new ItemStack(ModItems.VITAPPLE))) {
            if (entity.getOwner() != null && worldIn.getPlayerByUuid(entity.getOwner()) != null) {
                player.getHeldItem(handIn).setCount(player.getHeldItem(handIn).getCount() - 1); //Remove one item

                PlayerEntity target = worldIn.getPlayerByUuid(entity.getOwner());
                target.attemptTeleport(pos.getX(), pos.getY(), pos.getZ(), true);
                target.setGameType(GameType.SURVIVAL);

                if (worldIn.isRemote()) {
                    for (PlayerEntity p : worldIn.getPlayers())
                        player.sendStatusMessage(new StringTextComponent(target.getName().getString() + " has been revived" ), false);
                }
            } else {
                player.sendStatusMessage(new StringTextComponent("This player is offline, or does not exist" ), true);
                return ActionResultType.SUCCESS;
            }

            worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
        } else {
            if (!worldIn.isRemote)
                return ActionResultType.SUCCESS;

            player.sendStatusMessage(new StringTextComponent("This gravestone belongs to " + (worldIn.getPlayerByUuid(entity.getOwner()) == null ? "no one" : worldIn.getPlayerByUuid(entity.getOwner()).getName().getString())), true);
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return tileGravestone.create();
    }
}
