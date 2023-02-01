package csmp.spell;

import csmp.registry.CastDirection;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class Spell {
    public CastDirection spellDirection;
    private static boolean isCasting;

    public Spell(CastDirection spellDirection) {
        this.spellDirection = spellDirection;
    }


    public CastDirection getCastDirection() {
        return this.spellDirection;
    }

    public void startCasting(Identifier spellId, World world, PlayerEntity player) {
        player.sendMessage(Text.of("Кастанул: " + spellId));
        player.sendMessage(Text.of("Направление: " + getCastDirection()));

        if (!world.isClient) {
            switch (getCastDirection()) {
                case RADIUS:
                    player.sendMessage(Text.of("*звуки заклинания радиуса*"));
                    break;
                case RAYCAST:
                    player.sendMessage(Text.of("*звуки заклинания рейкаста*"));
                    break;
                case SELF:
                    player.sendMessage(Text.of("*звуки заклинания на себя*"));
                    break;
            }
        }
    }

    public void castSpell(Identifier spellId, World world, PlayerEntity player) {
        startCasting(spellId, world, player);
    }


}
