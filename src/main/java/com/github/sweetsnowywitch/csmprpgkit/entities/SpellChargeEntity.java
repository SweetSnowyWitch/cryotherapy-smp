package com.github.sweetsnowywitch.csmprpgkit.entities;

import com.github.sweetsnowywitch.csmprpgkit.items.ModItems;
import com.github.sweetsnowywitch.csmprpgkit.magic.ServerSpellCast;
import com.github.sweetsnowywitch.csmprpgkit.magic.SpellCast;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class SpellChargeEntity extends PersistentProjectileEntity implements FlyingItemEntity {
    public ServerSpellCast cast = null;
    public static final TrackedData<SpellCast> CAST = DataTracker.registerData(SpellChargeEntity.class, SpellCast.TRACKED_HANDLER);

    private final float growthSpeed = 2;

    public double powerX;
    public double powerY;
    public double powerZ;

    public SpellChargeEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public void setCast(@NotNull ServerSpellCast cast) {
        this.cast = cast;
        this.dataTracker.set(CAST, cast);
    }

    public SpellCast getCast() {
        if (this.cast != null) {
            return this.cast;
        }
        return this.dataTracker.get(CAST);
    }

    /**
    * NBT
    */
    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(CAST, SpellCast.EMPTY);
        super.initDataTracker();
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        this.cast = ServerSpellCast.readFromNbt(nbt.getCompound("Cast")); // full data for server
        this.dataTracker.set(CAST, this.cast); // sync some spell data to client
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        var castNBT = new NbtCompound();
        if (this.cast != null) {
            // Server with full data.
            this.cast.writeToNbt(castNBT);
        } else {
            // Client with partial data.
            this.dataTracker.get(CAST).writeToNbt(castNBT);
        }

        nbt.put("Cast", castNBT);
    }

    /**
     * etc
     */
    @Override
    protected void onBlockHit(BlockHitResult bhr) {
        super.onBlockHit(bhr);
        if (this.cast == null) {
            return;
        }
        this.cast.getSpell().onSingleBlockHit(this.cast, (ServerWorld)this.world, bhr.getBlockPos(), bhr.getSide());
    }

    @Override
    protected void onEntityHit(EntityHitResult ehr) {
        super.onEntityHit(ehr);
        this.cast.getSpell().onSingleEntityHit(this.cast, ehr.getEntity());
    }

    @Override
    protected ItemStack asItemStack() {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getStack() {
        ItemStack itemStack = this.asItemStack();
        return itemStack.isEmpty() ? new ItemStack(ModItems.CHARGE) : itemStack;
    }

    protected float getDrag() {
        return 0.95F;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.world.isClient) {
            return;
        }
        float g = this.getDrag();
        this.setVelocity(this.getVelocity().add(this.powerX, this.powerY, this.powerZ).multiply(g));
    }

    protected ParticleEffect getParticleType() {
        return ParticleTypes.SMOKE;
    }

    @Override
    public float getBrightnessAtEyes() {
        return 1.0F;
    }
}
