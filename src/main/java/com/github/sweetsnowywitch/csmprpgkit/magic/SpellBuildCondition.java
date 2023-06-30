package com.github.sweetsnowywitch.csmprpgkit.magic;

import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.List;

@FunctionalInterface
public interface SpellBuildCondition {
    class Context {
        List<SpellElement> elements;
        @Nullable SpellElement element;
        Entity caster;
    }

    boolean shouldAdd(Context ctx);

    static SpellBuildCondition fromJson(JsonObject obj) {
        SpellBuildCondition cond = null;

        for (var el : obj.entrySet()) {
            SpellBuildCondition elCond;
            switch (el.getKey()) {
                case "not" -> elCond = not(fromJson(el.getValue().getAsJsonObject()));
                case "and" -> {
                    var arr = el.getValue().getAsJsonArray();
                    if (arr.size() != 2) {
                        throw new IllegalArgumentException("and condition should have exactly 2 elements");
                    }
                    elCond = and(fromJson(arr.get(0).getAsJsonObject()), fromJson(arr.get(1).getAsJsonObject()));
                }
                case "or" -> {
                    var arr = el.getValue().getAsJsonArray();
                    if (arr.size() != 2) {
                        throw new IllegalArgumentException("or condition should have exactly 2 elements");
                    }
                    elCond = or(fromJson(arr.get(0).getAsJsonObject()), fromJson(arr.get(1).getAsJsonObject()));
                }
                case "first_element" -> {
                    if (el.getValue().getAsBoolean()) {
                        elCond = sameAsFirstElement();
                    } else {
                        elCond = not(sameAsFirstElement());
                    }
                }
                default -> throw new IllegalArgumentException("unknown field in spell build condition: " + el.getKey());
            }

            if (cond == null) {
                cond = elCond;
            } else {
                cond = and(cond, elCond);
            }
        }

        return cond;
    }

    static SpellBuildCondition not(SpellBuildCondition c) {
        return (ctx) -> !c.shouldAdd(ctx);
    }

    static SpellBuildCondition and(SpellBuildCondition c1, SpellBuildCondition c2) {
        return (ctx) -> c1.shouldAdd(ctx) && c2.shouldAdd(ctx);
    }

    static SpellBuildCondition or(SpellBuildCondition c1, SpellBuildCondition c2) {
        return (ctx) -> c1.shouldAdd(ctx) || c2.shouldAdd(ctx);
    }

    static SpellBuildCondition firstElement() {
        return (ctx) -> ctx.elements.size() == 1;
    }

    static SpellBuildCondition sameAsFirstElement() {
        return (ctx) -> ctx.elements.size() == 1 || ctx.elements.get(0).equals(ctx.element);
    }

    static SpellBuildCondition hasElement(@NotNull SpellElement element, @Range(from = 1, to = Integer.MAX_VALUE) int minCount, int maxCount) {
        return (ctx) -> {
            int effectiveCnt = 0;
            for (var pending : ctx.elements) {
                if (pending.equals(element)) {
                    effectiveCnt++;
                }
            }

            return effectiveCnt >= minCount && effectiveCnt <= maxCount;
        };
    }

    static SpellBuildCondition casterIsPlayer() {
        return (ctx) -> ctx.caster instanceof PlayerEntity;
    }
}
