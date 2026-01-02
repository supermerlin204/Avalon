package com.merlin204.avalon.entity.projectile;

import com.merlin204.avalon.entity.IAvalonMeshEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

import java.util.Optional;
import java.util.UUID;

public abstract class AvalonProjectileEntity extends Mob implements IAvalonMeshEntity {

    protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNER_UUID = SynchedEntityData.defineId(AvalonProjectileEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    protected static final EntityDataAccessor<Integer> DATA_OWNER_ID = SynchedEntityData.defineId(AvalonProjectileEntity.class, EntityDataSerializers.INT);

    protected static final EntityDataAccessor<Float> SYNC_X_ROT = SynchedEntityData.defineId(AvalonProjectileEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> SCALE = SynchedEntityData.defineId(AvalonProjectileEntity.class, EntityDataSerializers.FLOAT);

    protected static final EntityDataAccessor<Boolean> PLAY_ANIMATION = SynchedEntityData.defineId(AvalonProjectileEntity.class, EntityDataSerializers.BOOLEAN);

    public boolean initialVelocityApplied = false;

    public AvalonProjectileEntity(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.noCulling = true;
        this.noPhysics = true;
        setNoGravity(true);
        this.setInvisible(true);

    }

    protected AvalonProjectileEntity(EntityType<? extends Mob> pEntityType,LivingEntity owner,float scale ,Level pLevel) {
        super(pEntityType, pLevel);
        tame(owner);
        this.noCulling = true;
        this.noPhysics = true;
        this.entityData.set(SCALE,scale);
        setNoGravity(true);
        this.setInvisible(true);
    }


    @Override
    public boolean addEffect(MobEffectInstance pEffectInstance, @Nullable Entity pEntity) {
        return false;
    }

    public boolean getPlayAnimation(){
        return this.entityData.get(PLAY_ANIMATION);
    }

    public void setPlayAnimation(boolean b){
        this.entityData.set(PLAY_ANIMATION,b);
    }

    public AvalonProjectilePatch<?> getPatch(){
        return EpicFightCapabilities.getEntityPatch(this,AvalonProjectilePatch.class);
    }


    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            return;
        }
        fly();
    }

    public void fly(){
        if (!initialVelocityApplied) {
            applyInitialVelocity(2);
            initialVelocityApplied = true;
        }
    }


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_OWNER_UUID, Optional.empty());
        this.entityData.define(DATA_OWNER_ID, 0);
        this.entityData.define(SYNC_X_ROT, 0F);
        this.entityData.define(SCALE, 1F);

        this.entityData.define(PLAY_ANIMATION,false);

    }

    public void tame(LivingEntity livingEntity) {
        this.setOwnerUUID(livingEntity.getUUID());
        this.setOwnerID(livingEntity.getId());
    }

    public void setSyncXRot(float f){
        this.entityData.set(SYNC_X_ROT,f);
    }
    public float getSyncXRot(){
     return this.entityData.get(SYNC_X_ROT);
    }


    @Nullable
    public LivingEntity getOwner() {

        UUID uuid = this.getOwnerUUID();
        if (uuid != null) {
            Player player = this.level().getPlayerByUUID(uuid);
            if (player == null) {
                if (this.level() instanceof ServerLevel serverLevel) {
                    return serverLevel.getEntity(uuid) instanceof LivingEntity livingEntity ? livingEntity : null;
                }else {
                    return this.level().getEntity(getOwnerID()) instanceof LivingEntity livingEntity ? livingEntity : null;
                }
            } else {
                return player;
            }
        }
        return null;

    }

    public void aimAtEntity(Entity target) {
        if (target == null) return;

        Vec3 eyePos = this.getEyePosition();
        Vec3 targetPos = target.getEyePosition();
        Vec3 direction = targetPos.subtract(eyePos).normalize();

        double horizontalDistance = direction.horizontalDistance();
        float yRot = (float)(Mth.atan2(direction.x, direction.z) * (-180F / Math.PI));
        float xRot = (float)(Mth.atan2(direction.y, horizontalDistance) * (-180F / Math.PI));

        this.setYRot(yRot);
        this.setSyncXRot(xRot);
        this.yRotO = yRot;
    }

    public void applyInitialVelocity(float flightSpeed) {
        // 根据当前旋转计算方向向量
        float yRotRad = this.getYRot() * ((float) Math.PI / 180F);
        float xRotRad = this.getSyncXRot() * ((float) Math.PI / 180F);
        double motionX = -Math.sin(yRotRad) * Math.cos(xRotRad);
        double motionY = -Math.sin(xRotRad) ;
        double motionZ = Math.cos(yRotRad) * Math.cos(xRotRad);

        // 设置速度向量
        Vec3 motion = new Vec3(motionX, motionY, motionZ).normalize().scale(flightSpeed);
        this.setDeltaMovement(motion);
    }



    @Nullable
    public UUID getOwnerUUID() {
        return this.entityData.get(DATA_OWNER_UUID).orElse(null);
    }

    public void setOwnerUUID(@Nullable UUID pUuid) {
        this.entityData.set(DATA_OWNER_UUID, Optional.ofNullable(pUuid));
    }


    public int getOwnerID() {
        return this.entityData.get(DATA_OWNER_ID);
    }

    public void setOwnerID(int id) {
        this.entityData.set(DATA_OWNER_ID,id);
    }

    public static AttributeSupplier getDefaultAttribute() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 19.9F)
                .add(Attributes.ATTACK_DAMAGE, 3.0f)
                .add(EpicFightAttributes.MAX_STRIKES.get(), 10.0F)
                .build();
    }

    public float getScale(){
        return this.entityData.get(SCALE);
    }

    @Nullable
    @Override
    public AnimationManager.AnimationAccessor<? extends StaticAnimation> getIdleAnimation() {
        return Animations.EMPTY_ANIMATION;
    }


    public @Nullable AnimationManager.AnimationAccessor<? extends StaticAnimation> getDefaultAnimation() {
        return null;
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float p_21017_) {
        return false;
    }

    @Override
    protected void playStepSound(@NotNull BlockPos pPos, @NotNull BlockState pState) {
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, @NotNull DamageSource pSource) {
        return false;
    }

    @Override
    public boolean canSpawnSprintParticle() {
        return false;
    }
}