package com.github.sweetsnowywitch.csmprpgkit.magic;

import com.github.sweetsnowywitch.csmprpgkit.ModRegistries;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

public class ItemElement implements SpellElement {
    private final Item item;

    public ItemElement(Item stack) {
        this.item = stack;
    }

    @Override
    public float getBaseCost(String key) {
        var itemId = Registry.ITEM.getId(this.item);
        if (itemId.equals(Registry.ITEM.getDefaultId())) {
            return 0;
        }

        var costs = ModRegistries.ITEM_COSTS.get(itemId);
        if (costs == null) {
            return 0;
        }
        return costs.getOrDefault(key, 0f);
    }

    public int getColor() {
        return 0xFFFFFFFF;
    }

    public Item getItem() {
        return this.item;
    }

    public String toString() {
        return Registry.ITEM.getId(this.item).toString();
    }
}
