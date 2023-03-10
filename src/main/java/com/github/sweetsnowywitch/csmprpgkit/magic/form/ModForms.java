package com.github.sweetsnowywitch.csmprpgkit.magic.form;

import com.github.sweetsnowywitch.csmprpgkit.ModRegistries;
import com.github.sweetsnowywitch.csmprpgkit.RPGKitMod;
import com.github.sweetsnowywitch.csmprpgkit.magic.SpellForm;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModForms {
    public static final SpellForm SELF = new SelfForm();
    public static void register() {
        Registry.register(ModRegistries.SPELL_FORMS, Identifier.of(RPGKitMod.MOD_ID, "self"), SELF);
    }
}
