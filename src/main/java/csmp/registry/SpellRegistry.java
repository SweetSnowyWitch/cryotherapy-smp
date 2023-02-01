package csmp.registry;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import csmp.spell.Spell;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class SpellRegistry {
    public static Map<Identifier, Spell> spellsMap = new HashMap<>();

    public static void loadSpells(ResourceManager resourceManager) {
        String dir = "spells";
        Gson gson = new Gson();

        for (var spellEntry : resourceManager.findResources(dir, fileName -> fileName.getPath().endsWith(".json")).entrySet()) {
            Identifier key = spellEntry.getKey();
            Resource value = spellEntry.getValue();

            // Подготовка значения, чтобы создать Identifier
            var spellName = key.toString().replace(dir + "/", "");
            spellName = spellName.substring(0, spellName.lastIndexOf('.'));


            try {
                JsonReader jsonReader = new JsonReader(new InputStreamReader(value.getInputStream()));
                Spell spellObject = gson.fromJson(jsonReader, Spell.class);

                spellsMap.put(Identifier.tryParse(spellName), new Spell(spellObject.getCastDirection()));
                System.out.println("Parsed " + spellObject);
                System.out.println("Spell: " + spellName + "   " + spellObject.getCastDirection());
                //parseSpellJson(jsonReader, direction);

            } catch (Exception e) {
                System.err.println("Unable to parse spell: " + spellName + " with value " + value);
                e.printStackTrace();
            }
        }
        System.out.println("All spellsMap: " + spellsMap); //TODO: This is a Debug output.
    }

    // Делаем вид, что его не существует
    public static void parseSpellJson(@NotNull JsonReader spellJson, CastDirection direction) throws IOException  {
        spellJson.beginObject();
            switch (spellJson.nextName()) {
                case "direction":
                    String jsonSpellDirection = spellJson.nextString();
                    switch (jsonSpellDirection) {
                        case "self":
                            direction = CastDirection.SELF;
                            break;
                    }
            }
    }
}
