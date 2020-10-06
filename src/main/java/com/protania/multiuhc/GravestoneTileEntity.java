package com.protania.multiuhc;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;

import java.util.UUID;

public class GravestoneTileEntity extends TileEntity {
    private UUID owner = null;
    private String lastKnownName;

    public GravestoneTileEntity() {
        super(Gravestone.tileGravestone);
    }

    public void assignOwner(PlayerEntity owner) {
        this.owner = owner.getUniqueID();
        lastKnownName = owner.getName().getString();
        markDirty();

        MultiUHC.LOGGER.debug("Set owner as " + this.owner + ", and LKN as " + lastKnownName);
    }

    public UUID getOwner() {
        return owner;
    }

    public String getLastKnownName() {
        return lastKnownName;
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        CompoundNBT ret = super.write(compound);

        ret.putUniqueId("owner", owner);
        ret.putString("lkn", lastKnownName);

        return ret;
    }

    @Override
    public void func_230337_a_(BlockState state, CompoundNBT compound) { //Read
        super.func_230337_a_(state, compound);

        lastKnownName = compound.getString("lkn");
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

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        SUpdateTileEntityPacket packet = super.getUpdatePacket();
        if (packet != null && packet.getNbtCompound() != null) {
            packet.getNbtCompound().putUniqueId("owner", owner);
            packet.getNbtCompound().putString("lkn", lastKnownName);
        } else {
            CompoundNBT nbt = new CompoundNBT();
            nbt.putUniqueId("owner", owner);
            nbt.putString("lkn", lastKnownName);
            packet = new SUpdateTileEntityPacket(getPos(), -1, nbt);
        }
        return packet;
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        MultiUHC.LOGGER.debug("Got death data packet from server");
        owner = pkt.getNbtCompound().getUniqueId("owner");
        lastKnownName = pkt.getNbtCompound().getString("lkn");
    }
}
