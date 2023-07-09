package com.github.sweetsnowywitch.rpgkit.magic.effects.use;

import com.github.sweetsnowywitch.rpgkit.magic.RPGKitMagicMod;
import com.github.sweetsnowywitch.rpgkit.magic.effects.SpellEffect;
import com.github.sweetsnowywitch.rpgkit.magic.json.IntModifier;
import com.github.sweetsnowywitch.rpgkit.magic.spell.ServerSpellCast;
import com.github.sweetsnowywitch.rpgkit.magic.spell.SpellReaction;
import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class PotionEffect extends SimpleUseEffect {
    public static class Reaction extends SpellReaction {
        private final @Nullable StatusEffect effect;
        private final IntModifier amplifier;
        private final IntModifier durationTicks;

        public Reaction(JsonObject obj) {
            super(Type.EFFECT, obj);

            if (obj.has("id")) {
                var effectId = new Identifier(obj.get("id").getAsString());
                var effect = Registry.STATUS_EFFECT.get(effectId);
                RPGKitMagicMod.LOGGER.debug("PotionEffect.Reaction populated with potion effect {}", effectId);
                if (effect == null) {
                    throw new IllegalStateException("unknown potion effect");
                }
                this.effect = effect;
            } else {
                this.effect = null;
            }

            if (obj.has("amplifier")) {
                this.amplifier = new IntModifier(obj.get("amplifier"));
            } else {
                this.amplifier = IntModifier.NOOP;
            }
            if (obj.has("duration")) {
                this.durationTicks = new IntModifier(obj.get("duration"));
            } else {
                this.durationTicks = IntModifier.NOOP;
            }
        }

        @Override
        public boolean appliesTo(SpellEffect effect) {
            return effect instanceof PotionEffect;
        }

        public void toJson(@NotNull JsonObject obj) {
            super.toJson(obj);

            if (this.effect != null) {
                var id = Registry.STATUS_EFFECT.getId(this.effect);
                if (id == null) {
                    throw new IllegalStateException("potion effect with unregistered effect");
                }
                obj.addProperty("id", id.toString());
            }

            obj.add("amplifier", this.amplifier.toJson());
            obj.add("duration", this.durationTicks.toJson());
        }

        @Override
        public String toString() {
            return "PotionEffect.Reaction[" +
                    "amplifier=" + amplifier +
                    ", durationTicks=" + durationTicks +
                    ']';
        }
    }

    public static final int DEFAULT_DURATION = 20 * 10;
    public static final int DEFAULT_AMPLIFIER = 1;

    private final StatusEffect statusEffect;
    private final int baseDuration;
    private final int baseAmplifier;

    public PotionEffect(Identifier id) {
        super(id);
        this.statusEffect = null;
        this.baseAmplifier = DEFAULT_AMPLIFIER;
        this.baseDuration = DEFAULT_DURATION;
    }

    public PotionEffect(Identifier id, JsonObject obj) {
        super(id, obj);

        if (obj.has("id")) {
            var effectId = new Identifier(obj.get("id").getAsString());
            var effect = Registry.STATUS_EFFECT.get(effectId);
            RPGKitMagicMod.LOGGER.debug("PotionEffect populated with potion effect {}", effectId);
            if (effect == null) {
                throw new IllegalStateException("unknown potion effect");
            }
            this.statusEffect = effect;
        } else {
            this.statusEffect = null;
        }
        if (obj.has("amplifier")) {
            this.baseAmplifier = obj.get("amplifier").getAsInt();
        } else {
            this.baseAmplifier = DEFAULT_AMPLIFIER;
        }

        if (obj.has("duration")) {
            this.baseDuration = obj.get("duration").getAsInt();
        } else {
            this.baseDuration = DEFAULT_DURATION;
        }
    }

    public String toString() {
        if (this.statusEffect == null) {
            return "PotionEffect[]";
        }
        return "PotionEffect[%s,amp=%d,dur=%s]".formatted(
                Objects.requireNonNull(Registry.STATUS_EFFECT.getId(this.statusEffect)).toString(),
                this.baseAmplifier, this.baseDuration);
    }

    @Override
    protected @NotNull ActionResult useOnEntity(ServerSpellCast cast, SimpleUseEffect.Used used, Entity entity, List<SpellReaction> reactions) {
        if (this.statusEffect == null) {
            RPGKitMagicMod.LOGGER.warn("Cast {} with empty status effect", cast);
            return ActionResult.PASS;
        }
        if (!(entity instanceof LivingEntity le)) {
            return ActionResult.PASS;
        }

        var amplifier = this.baseAmplifier;
        var duration = this.baseDuration;

        for (var reaction : reactions) {
            if (reaction instanceof Reaction r && (r.effect == null || r.effect.equals(this.statusEffect))) {
                amplifier = r.amplifier.applyMultiple(amplifier, used.reactionStackSize);
                duration = r.durationTicks.applyMultiple(duration, used.reactionStackSize);
            }
        }
        if (amplifier <= 0) {
            amplifier = 0;
        }
        if (duration <= 2) {
            duration = 2;
        }

        var caster = ((ServerWorld) entity.getWorld()).getEntity(cast.getCasterUuid());

        le.addStatusEffect(
                new StatusEffectInstance(this.statusEffect, duration, amplifier, false, false),
                caster
        );
        return ActionResult.SUCCESS;
    }

    @Override
    public void toJson(@NotNull JsonObject obj) {
        super.toJson(obj);
        if (this.statusEffect != null) {
            var id = Registry.STATUS_EFFECT.getId(this.statusEffect);
            if (id == null) {
                throw new IllegalStateException("potion effect with unregistered effect");
            }
            obj.addProperty("id", id.toString());
        }
        obj.addProperty("amplifier", this.baseAmplifier);
        obj.addProperty("duration", this.baseDuration);
    }
}
