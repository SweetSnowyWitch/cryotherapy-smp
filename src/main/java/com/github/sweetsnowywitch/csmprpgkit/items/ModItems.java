package com.github.sweetsnowywitch.csmprpgkit.items;

import com.github.sweetsnowywitch.csmprpgkit.RPGKitMod;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModItems {
    //public static final Item ITEM_CASTER = new CasterItem(new FabricItemSettings());
    public static final Item TEST_ITEM = new CasterItem(5, new FabricItemSettings());

    public static void register() {
        // Registry.register(Registries.ITEM, new Identifier(RPGKitMod.MOD_ID, "caster"), ITEM_CASTER);
        Registry.register(Registry.ITEM, new Identifier(RPGKitMod.MOD_ID, "test_item"), TEST_ITEM);
    }
}
