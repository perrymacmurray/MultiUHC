package com.protania.multiuhc;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;

import java.util.UUID;

public class GravestoneTileEntity extends TileEntity {
    private UUID owner = null;

    public GravestoneTileEntity() {
        super(Gravestone.tileGravestone);
    }

    public void assignOwner(PlayerEntity owner) {
        this.owner = owner.getUniqueID();
        markDirty();
        MultiUHC.LOGGER.debug("Set owner as " + owner.getUniqueID());
    }

    public UUID getOwner() {
        return owner;
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        CompoundNBT ret = super.write(compound);

        ret.putUniqueId("owner", owner);

        return ret;
    }

    @Override
    public void func_230337_a_(BlockState state, CompoundNBT compound) { //Read
        super.func_230337_a_(state, compound);

        owner = compound.getUniqueId("owner");
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return write(new CompoundNBT());
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        func_230337_a_(state, tag);
    }
}
