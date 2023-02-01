package csmp;

import csmp.registry.CastingItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

import static csmp.registry.SpellRegistry.loadSpells;

public class CsmpMain implements ModInitializer {
    public static final String MOD_ID = "csmp";
    public static final CastingItem TEST_CASTING_ITEM = new CastingItem(new FabricItemSettings());

    @Override
    public void onInitialize() {
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "custom_item"), TEST_CASTING_ITEM);

        System.out.println(MOD_ID + " initialized");
        ServerLifecycleEvents.SERVER_STARTED.register((minecraftServer) -> loadSpells(minecraftServer.getResourceManager()));
    }
}
