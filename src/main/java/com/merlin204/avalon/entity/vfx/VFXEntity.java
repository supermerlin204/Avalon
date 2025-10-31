package com.merlin204.avalon.entity.vfx;

import com.google.common.collect.Maps;
import com.merlin204.avalon.entity.IAvalonMeshEntity;
import com.merlin204.avalon.epicfight.gameassets.animations.VFXAnimations;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


public class VFXEntity extends PathfinderMob implements IAvalonMeshEntity {
    protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNER_UUID = SynchedEntityData.defineId(VFXEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    protected static final EntityDataAccessor<Integer> DATA_OWNER_ID = SynchedEntityData.defineId(VFXEntity.class, EntityDataSerializers.INT);


    protected static final EntityDataAccessor<Float> DIS_RATIO = SynchedEntityData.defineId(VFXEntity.class, EntityDataSerializers.FLOAT);

    protected static final EntityDataAccessor<Float> DIS_SPEED = SynchedEntityData.defineId(VFXEntity.class, EntityDataSerializers.FLOAT);

    protected static final EntityDataAccessor<Float> SCALE = SynchedEntityData.defineId(VFXEntity.class, EntityDataSerializers.FLOAT);

    protected static final EntityDataAccessor<Float> Y_ROT_OFFSET = SynchedEntityData.defineId(VFXEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> X_ROT_OFFSET = SynchedEntityData.defineId(VFXEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> Z_ROT_OFFSET = SynchedEntityData.defineId(VFXEntity.class, EntityDataSerializers.FLOAT);

    protected static final EntityDataAccessor<Float> START_Y_ROT = SynchedEntityData.defineId(VFXEntity.class, EntityDataSerializers.FLOAT);

    protected static final EntityDataAccessor<Boolean> PLAY_ANIMATION = SynchedEntityData.defineId(VFXEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> SHOULD_RENDER = SynchedEntityData.defineId(VFXEntity.class, EntityDataSerializers.BOOLEAN);

    protected static final EntityDataAccessor<String> MESH_PATH = SynchedEntityData.defineId(VFXEntity.class, EntityDataSerializers.STRING);
    protected static final EntityDataAccessor<String> ARMATURE_PATH = SynchedEntityData.defineId(VFXEntity.class, EntityDataSerializers.STRING);
    protected static final EntityDataAccessor<String> TEXTURE_PATH = SynchedEntityData.defineId(VFXEntity.class, EntityDataSerializers.STRING);
    protected static final EntityDataAccessor<String> LIGHT_TEXTURE_PATH = SynchedEntityData.defineId(VFXEntity.class, EntityDataSerializers.STRING);

    protected Armatures.ArmatureAccessor<? extends Armature> ARMATURE_ACCESSOR;



    protected ResourceLocation TEXTURE;
    protected ResourceLocation LIGHT_TEXTURE;
    protected AnimationManager.AnimationAccessor<? extends StaticAnimation> DEFAULT_ANIMATION;

    public float disRatioOld;



    @Override
    public boolean addEffect(MobEffectInstance pEffectInstance, @Nullable Entity pEntity) {
        return false;
    }



    public VFXEntity(EntityType<? extends VFXEntity> entityType, LivingEntity owner,float scale) {
        super(entityType, owner.level());
        tame(owner);
        this.getEntityData().set(SCALE, scale);
        this.noCulling = true;
        this.noPhysics = true;
        setNoGravity(true);
        ARMATURE_ACCESSOR = null;
        TEXTURE = null;
        DEFAULT_ANIMATION = null;
        LIGHT_TEXTURE = null;
        this.setInvisible(true);
    }

    public VFXEntity(EntityType<? extends VFXEntity> entityType, LivingEntity owner, float scale, Armatures.ArmatureAccessor<? extends Armature> armatureAccessor, ResourceLocation mesh, ResourceLocation texture, ResourceLocation lightTexture, AnimationManager.AnimationAccessor<? extends StaticAnimation> defaultAnimation) {
        super(entityType, owner.level());
        this.noCulling = true;
        LIGHT_TEXTURE = lightTexture;
        tame(owner);
        this.getEntityData().set(SCALE, scale);
        this.noPhysics = true;
        setNoGravity(true);
        ARMATURE_ACCESSOR = armatureAccessor;
        TEXTURE = texture;
        DEFAULT_ANIMATION = defaultAnimation;
        this.entityData.set(ARMATURE_PATH,ARMATURE_ACCESSOR.registryName().toString());
        this.entityData.set(MESH_PATH,mesh.toString());
        this.entityData.set(TEXTURE_PATH,TEXTURE.toString());
        this.entityData.set(LIGHT_TEXTURE_PATH,LIGHT_TEXTURE.toString());
        this.setInvisible(true);
    }

    public VFXEntity(EntityType<? extends VFXEntity> entityType, LivingEntity owner, float scale, Vec3f rotOffset,
                     Armatures.ArmatureAccessor<? extends Armature> armatureAccessor, ResourceLocation mesh,
                     ResourceLocation texture, ResourceLocation lightTexture,

                     AnimationManager.AnimationAccessor<? extends StaticAnimation> defaultAnimation) {
        super(entityType, owner.level());
        this.noCulling = true;
        LIGHT_TEXTURE = lightTexture;
        tame(owner);
        this.getEntityData().set(SCALE, scale);
        this.getEntityData().set(X_ROT_OFFSET, rotOffset.x);
        this.getEntityData().set(Y_ROT_OFFSET, rotOffset.y);
        this.getEntityData().set(Z_ROT_OFFSET, rotOffset.z);
        this.noPhysics = true;
        setNoGravity(true);
        ARMATURE_ACCESSOR = armatureAccessor;
        float ownerYRot = getOwner().getYRot();
        setStartYRot(ownerYRot);

        TEXTURE = texture;
        DEFAULT_ANIMATION = defaultAnimation;
        this.entityData.set(ARMATURE_PATH,ARMATURE_ACCESSOR.registryName().toString());
        this.entityData.set(MESH_PATH,mesh.toString());
        this.entityData.set(TEXTURE_PATH,TEXTURE.toString());
        this.entityData.set(LIGHT_TEXTURE_PATH,LIGHT_TEXTURE.toString());
        this.setInvisible(true);

    }



    public VFXEntity(EntityType<? extends VFXEntity> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
        setNoGravity(true);
        ARMATURE_ACCESSOR = null;
        TEXTURE = null;
        DEFAULT_ANIMATION = null;
        LIGHT_TEXTURE = null;
    }


    @Override
    public void tick() {
        super.tick();


        fallDistance = 0;
        this.noPhysics = true;
        setNoGravity(true);
        boolean playAnimation = this.getPlayAnimation();
        if (!playAnimation){
            float ownerYRot = getStartYRot();
            this.setYRot(ownerYRot);
            this.setYBodyRot(ownerYRot);
            this.setYHeadRot(ownerYRot);
        }
        if (this.level().isClientSide){
            float speed = this.entityData.get(DIS_SPEED);
            if (speed >0 && speed<1){
                this.entityData.set(DIS_RATIO,this.entityData.get(DIS_RATIO)-speed);
            }
            if (ARMATURE_ACCESSOR == null){
                ARMATURE_ACCESSOR = Armatures.ArmatureAccessor.create(ResourceLocation.parse(this.entityData.get(ARMATURE_PATH)).getNamespace(), ResourceLocation.parse(this.entityData.get(ARMATURE_PATH)).getPath(), Armature::new);
            }
            if (TEXTURE == null){
                TEXTURE = ResourceLocation.parse(this.entityData.get(TEXTURE_PATH));
            }
            if (LIGHT_TEXTURE == null){
                LIGHT_TEXTURE = ResourceLocation.parse(this.entityData.get(LIGHT_TEXTURE_PATH));
            }
        }else {
            if (getOwner()!=null && !getOwner().isAlive()){
                this.discard();
            }
        }
        disRatioOld = getDisRatio();
    }


    @Nullable
    @Override
    public Armature getArmature() {
        if (ARMATURE_ACCESSOR == null){
            return Armatures.BIPED.get();
        }
        return ARMATURE_ACCESSOR.get();
    }



    @Nullable
    @Override
    public AssetAccessor<? extends SkinnedMesh> getMesh() {
        return Meshes.MeshAccessor.create(ResourceLocation.parse(this.entityData.get(MESH_PATH)).getNamespace(), ResourceLocation.parse(this.entityData.get(MESH_PATH)).getPath(), (jsonModelLoader) -> jsonModelLoader.loadSkinnedMesh(SkinnedMesh::new));
    }

    @Nullable
    @Override
    public ResourceLocation getTexture() {
        return TEXTURE;
    }

    @Nullable
    @Override
    public ResourceLocation getLitTexture() {
        return LIGHT_TEXTURE;
    }


    public float getScale(){
        return this.entityData.get(SCALE);
    }

    public float getXRotOffset(){
        return this.entityData.get(X_ROT_OFFSET);
    }
    public float getZRotOffset(){
        return this.entityData.get(Z_ROT_OFFSET);
    }
    public float getYRotOffset(){
        return this.entityData.get(Y_ROT_OFFSET);
    }


    @Nullable
    @Override
    public AnimationManager.AnimationAccessor<? extends StaticAnimation> getIdleAnimation() {

        return Animations.EMPTY_ANIMATION;
    }

    @Nullable
    public AnimationManager.AnimationAccessor<? extends StaticAnimation> getDefaultAnimation() {
        return DEFAULT_ANIMATION;
    }



    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_OWNER_UUID, Optional.empty());
        this.entityData.define(DATA_OWNER_ID, 0);
        this.entityData.define(SCALE,1F);
        this.entityData.define(DIS_RATIO,1F);
        this.entityData.define(DIS_SPEED,0F);
        this.entityData.define(Y_ROT_OFFSET,0F);
        this.entityData.define(X_ROT_OFFSET,0F);
        this.entityData.define(Z_ROT_OFFSET,0F);
        this.entityData.define(START_Y_ROT,0F);
        this.entityData.define(PLAY_ANIMATION,false);
        this.entityData.define(SHOULD_RENDER,false);
        this.entityData.define(ARMATURE_PATH,"");
        this.entityData.define(MESH_PATH,"");
        this.entityData.define(TEXTURE_PATH,"");
        this.entityData.define(LIGHT_TEXTURE_PATH,"");
    }

    public float getDisRatio() {
        return this.entityData.get(DIS_RATIO);
    }

    public void setDisSpeed(float speed){
        this.entityData.set(DIS_SPEED,speed);
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



    public float getStartYRot(){
        return this.entityData.get(START_Y_ROT);
    }

    public void setStartYRot(float f){
        this.entityData.set(START_Y_ROT,f);
    }

    public void tame(LivingEntity livingEntity) {
        this.setOwnerUUID(livingEntity.getUUID());
        this.setOwnerID(livingEntity.getId());
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

    public LivingEntityPatch<?> getOwnerPatch(){
        return EpicFightCapabilities.getEntityPatch(getOwner(), LivingEntityPatch.class);
    }

    public LivingEntityPatch<?> getPatch(){
        return EpicFightCapabilities.getEntityPatch(this, LivingEntityPatch.class);
    }

    public <T extends EntityPatch<?>> T getPatch(Class<T> type){
        return EpicFightCapabilities.getEntityPatch(this, type);
    }

    public boolean getPlayAnimation(){
        return this.entityData.get(PLAY_ANIMATION);
    }

    public void setPlayAnimation(boolean b){
        this.entityData.set(PLAY_ANIMATION,b);
    }

    public boolean getShouldRender(){

        return this.entityData.get(SHOULD_RENDER);
    }

    public void setShouldRender(boolean b){
        this.entityData.set(SHOULD_RENDER,b);
    }

    private final Map<MobEffect, MobEffectInstance> fakeActiveEffects = Maps.newHashMap();

    @Override
    public Collection<MobEffectInstance> getActiveEffects() {
        return fakeActiveEffects.values();
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
