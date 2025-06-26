package com.merlin204.avalon.entity.vfx;

import com.merlin204.avalon.entity.AvalonMeshEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

import java.util.Optional;
import java.util.UUID;


public abstract class VFXEntity extends PathfinderMob implements AvalonMeshEntity {
    protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNER_UUID = SynchedEntityData.defineId(VFXEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    protected static final EntityDataAccessor<Float> SCALE = SynchedEntityData.defineId(VFXEntity.class, EntityDataSerializers.FLOAT);




    public VFXEntity(EntityType<? extends VFXEntity> entityType, LivingEntity owner,float scale) {
        super(entityType, owner.level());
        tame(owner);
        this.getEntityData().set(SCALE, scale);
        this.noPhysics = true;
        setNoGravity(true);
    }

    public VFXEntity(EntityType<? extends VFXEntity> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
        setNoGravity(true);
    }


    @Nullable
    @Override
    public Armature getArmature() {
        return AvalonMeshEntity.super.getArmature();
    }

    @Nullable
    @Override
    public AssetAccessor<? extends SkinnedMesh> getMesh() {
        return AvalonMeshEntity.super.getMesh();
    }

    @Nullable
    @Override
    public ResourceLocation getTexture() {
        return AvalonMeshEntity.super.getTexture();
    }

    public float getScale(){
        return this.entityData.get(SCALE);
    }



    @Nullable
    public AnimationManager.AnimationAccessor<? extends StaticAnimation> getDefaultAnimation() {
        return null;
    }

    /**
     * 取消血条渲染
     * {@link yesman.epicfight.client.gui.HealthBar#shouldDraw(LivingEntity, LivingEntityPatch, LocalPlayerPatch, float)}
     */
    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_OWNER_UUID, Optional.empty());
        this.entityData.define(SCALE,1F);
    }

    @Nullable
    public UUID getOwnerUUID() {
        return this.entityData.get(DATA_OWNER_UUID).orElse(null);
    }

    public void setOwnerUUID(@Nullable UUID pUuid) {
        this.entityData.set(DATA_OWNER_UUID, Optional.ofNullable(pUuid));
    }

    public void tame(LivingEntity livingEntity) {
        this.setOwnerUUID(livingEntity.getUUID());
    }

    @Nullable
    public LivingEntity getOwner() {

        UUID uuid = this.getOwnerUUID();
        if (uuid != null) {
            Player player = this.level().getPlayerByUUID(uuid);
            if (player == null) {
                if (this.level() instanceof ServerLevel serverLevel) {
                    return serverLevel.getEntity(uuid) instanceof LivingEntity livingEntity ? livingEntity : null;
                }
            } else {
                return player;
            }
        }
        return null;

    }

    public LivingEntityPatch<?> getOwnerPatch(){
        return EpicFightCapabilities.getEntityPatch(getOwner(), LivingEntityPatch.class);
    }

    public LivingEntityPatch<?> getPatch(){
        return EpicFightCapabilities.getEntityPatch(this, LivingEntityPatch.class);
    }

    public <T extends EntityPatch<?>> T getPatch(Class<T> type){
        return EpicFightCapabilities.getEntityPatch(this, type);
    }

    @Override
    public void tick() {
        super.tick();
        fallDistance = 0;
    }

    public static AttributeSupplier getDefaultAttribute() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 19.9F)
                .add(Attributes.ATTACK_DAMAGE, 3.0f)
                .add(EpicFightAttributes.MAX_STRIKES.get(), 10.0F)
                .build();
    }

    protected void moveToOwner(LivingEntity owner){
        setYRot(owner.yBodyRot);
        setYBodyRot(owner.yBodyRot);
        setYHeadRot(owner.yBodyRot);
        setPos(owner.position());
    }

    protected boolean shouldRemoveWhenOwnerLost(){
        return false;
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
