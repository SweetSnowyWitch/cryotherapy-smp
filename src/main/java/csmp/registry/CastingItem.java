package csmp.registry;

import csmp.entity.CastProjectileEntity;
import csmp.spell.Spell;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

import java.util.function.Predicate;

import static csmp.CsmpMain.MOD_ID;
import static csmp.registry.SpellRegistry.spellsMap;

public class CastingItem extends RangedWeaponItem {
    public CastingItem(Settings settings) {
        super(settings);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity)user;
            ItemStack itemStack = playerEntity.getArrowType(stack);
            //ArrowItem arrowItem = (ArrowItem)(itemStack.getItem() instanceof ArrowItem ? itemStack.getItem() : Items.ARROW);

            if (!world.isClient) {

                Identifier spellId = new Identifier(MOD_ID, "test_spell");
                System.out.println(spellId);
                System.out.println(spellsMap);
                Spell spellObject = spellsMap.get(spellId);
                spellsMap.get(spellId).castSpell(spellId, world, playerEntity);

                //CastProjectileEntity projectileEntity = spellObject.createProjectile(world, playerEntity);
                //world.spawnEntity(projectileEntity);
            }
        }
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        boolean bl = !user.getArrowType(itemStack).isEmpty();
        if (!user.getAbilities().creativeMode && !bl) {
            return TypedActionResult.fail(itemStack);
        } else {
            user.setCurrentHand(hand);
            return TypedActionResult.consume(itemStack);
        }
    }

    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public Predicate<ItemStack> getProjectiles() {
        return BOW_PROJECTILES;
    }

    @Override
    public int getRange() {
        return 10;
    }

}
