package csmp.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;

public class CastProjectileEntity extends ProjectileEntity {
    public CastProjectileEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker() {

    }
}
