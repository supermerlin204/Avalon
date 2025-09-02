package com.merlin204.avalon.entity.vfx;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Armatures;

public class AnimationTextureVFXEntity extends VFXEntity{


    protected static final EntityDataAccessor<Float> ANIMATION_TIME = SynchedEntityData.defineId(AnimationTextureVFXEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> ANIMATION_SPEED = SynchedEntityData.defineId(AnimationTextureVFXEntity.class, EntityDataSerializers.FLOAT);

    protected static final EntityDataAccessor<Integer> ANIMATION_TICK_FROM = SynchedEntityData.defineId(AnimationTextureVFXEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> ANIMATION_TICK_TO = SynchedEntityData.defineId(AnimationTextureVFXEntity.class, EntityDataSerializers.INT);

    protected static final EntityDataAccessor<String> TEXTURE_PACK_PATH = SynchedEntityData.defineId(AnimationTextureVFXEntity.class, EntityDataSerializers.STRING);
    protected static final EntityDataAccessor<String> LIGHT_TEXTURE_PACK_PATH = SynchedEntityData.defineId(AnimationTextureVFXEntity.class, EntityDataSerializers.STRING);


    public AnimationTextureVFXEntity(EntityType<? extends VFXEntity> entityType, LivingEntity owner,
                                     float scale, Armatures.ArmatureAccessor<? extends Armature> armatureAccessor, ResourceLocation mesh,
                                     ResourceLocation texture, ResourceLocation lightTexture, AnimationManager.AnimationAccessor<? extends StaticAnimation> defaultAnimation) {
        super(entityType, owner, scale, armatureAccessor, mesh, texture, lightTexture, defaultAnimation);
    }

    public AnimationTextureVFXEntity(EntityType<? extends VFXEntity> entityType, LivingEntity owner, float scale) {
        super(entityType, owner, scale);
    }

    public AnimationTextureVFXEntity(EntityType<? extends VFXEntity> entityType, Level level) {
        super(entityType, level);
    }

    public AnimationTextureVFXEntity(EntityType<? extends VFXEntity> entityType, LivingEntity owner,
                                     float scale, Vec3f rotOffset, Armatures.ArmatureAccessor<? extends Armature> armatureAccessor, ResourceLocation mesh,
                                     int textureFrom, int textureTo, float speed,
                                     String texturePackPath, String lightTexturePackPath,
                                     AnimationManager.AnimationAccessor<? extends StaticAnimation> defaultAnimation) {
        super(entityType, owner, scale, rotOffset, armatureAccessor, mesh, ResourceLocation.parse(texturePackPath), ResourceLocation.parse(lightTexturePackPath), defaultAnimation);
        this.entityData.set(ANIMATION_TICK_FROM,textureFrom);
        this.entityData.set(ANIMATION_TICK_TO,textureTo);
        this.entityData.set(ANIMATION_SPEED,speed);
        this.entityData.set(TEXTURE_PACK_PATH,texturePackPath);
        this.entityData.set(LIGHT_TEXTURE_PACK_PATH,lightTexturePackPath);



    }

    @Override
    public void tick() {
        super.tick();
        this.entityData.set(ANIMATION_TIME,Math.max(entityData.get(ANIMATION_TIME),entityData.get(ANIMATION_TICK_FROM)));
        if (this.getShouldRender() && this.level().isClientSide){
            this.entityData.set(ANIMATION_TIME,Math.min(entityData.get(ANIMATION_TIME) + entityData.get(ANIMATION_SPEED),entityData.get(ANIMATION_TICK_TO)));
        }
    }

    @Override
    public @Nullable ResourceLocation getTexture() {
        float time = entityData.get(ANIMATION_TIME);
        int number = (int) time;
        return ResourceLocation.parse(entityData.get(TEXTURE_PACK_PATH)+"/" + number + ".png");
    }

    @Override
    public @Nullable ResourceLocation getLitTexture() {
        float time = entityData.get(ANIMATION_TIME);
        int number = (int) time;
        return ResourceLocation.parse(entityData.get(LIGHT_TEXTURE_PACK_PATH)+"/" + number + ".png");
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIMATION_TIME, 0F);
        this.entityData.define(ANIMATION_SPEED, 0F);
        this.entityData.define(ANIMATION_TICK_FROM, 0);
        this.entityData.define(ANIMATION_TICK_TO, 0);
        this.entityData.define(TEXTURE_PACK_PATH, "");
        this.entityData.define(LIGHT_TEXTURE_PACK_PATH, "");

    }














}
