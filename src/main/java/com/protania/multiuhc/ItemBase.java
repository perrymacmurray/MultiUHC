package com.protania.multiuhc;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ItemBase extends Item {

    public ItemBase(String name) {
        super(new Item.Properties().group(ItemGroup.MISC));
        setRegistryName(name);
    }

    public ItemBase(String name, ItemGroup itemGroup) {
        super(new Item.Properties().group(itemGroup));
        setRegistryName(name);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
