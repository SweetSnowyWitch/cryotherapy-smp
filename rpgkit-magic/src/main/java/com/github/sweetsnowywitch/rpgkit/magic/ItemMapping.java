package com.github.sweetsnowywitch.rpgkit.magic;

import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;

public interface ItemMapping {
    ItemStack apply(ItemStack in, LootContext context);
}
