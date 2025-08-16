package com.merlin204.avalon.entity.vfx;

import com.merlin204.avalon.entity.IAvalonMeshEntity;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.registry.entries.EpicFightAttributes;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.Optional;
import java.util.UUID;


public class VFXEntity extends PathfinderMob implements IAvalonMeshEntity {
    protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNER_UUID = SynchedEntityData.defineId(VFXEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    protected static final EntityDataAccessor<Integer> DATA_OWNER_ID = SynchedEntityData.defineId(VFXEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Float> SCALE = SynchedEntityData.defineId(VFXEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> X_ROT_OFFSET = SynchedEntityData.defineId(VFXEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> START_Y_ROT = SynchedEntityData.defineId(VFXEntity.class, EntityDataSerializers.FLOAT);

    protected static final EntityDataAccessor<Boolean> PLAY_ANIMATION = SynchedEntityData.defineId(VFXEntity.class, EntityDataSerializers.BOOLEAN);

    protected static final EntityDataAccessor<String> MESH_PATH = SynchedEntityData.defineId(VFXEntity.class, EntityDataSerializers.STRING);
    protected static final EntityDataAccessor<String> TEXTURE_PATH = SynchedEntityData.defineId(VFXEntity.class, EntityDataSerializers.STRING);
    protected static final EntityDataAccessor<String> LIGHT_TEXTURE_PATH = SynchedEntityData.defineId(VFXEntity.class, EntityDataSerializers.STRING);

    protected Armatures.ArmatureAccessor<? extends Armature> ARMATURE_ACCESSOR;
    protected AssetAccessor<? extends SkinnedMesh> MESH;
    protected ResourceLocation TEXTURE;
    protected ResourceLocation LIGHT_TEXTURE;
    protected AnimationManager.AnimationAccessor<? extends StaticAnimation> DEFAULT_ANIMATION;





    public VFXEntity(EntityType<? extends VFXEntity> entityType, LivingEntity owner,float scale) {
        super(entityType, owner.level());
        tame(owner);
        this.getEntityData().set(SCALE, scale);
        this.noPhysics = true;
        setNoGravity(true);
        ARMATURE_ACCESSOR = null;
        MESH = null;
        TEXTURE = null;
        DEFAULT_ANIMATION = null;
        LIGHT_TEXTURE = null;
    }

    public VFXEntity(EntityType<? extends VFXEntity> entityType, LivingEntity owner, float scale, Armatures.ArmatureAccessor<? extends Armature> armatureAccessor, AssetAccessor<? extends SkinnedMesh> mesh, ResourceLocation texture, ResourceLocation lightTexture, AnimationManager.AnimationAccessor<? extends StaticAnimation> defaultAnimation) {
        super(entityType, owner.level());
        LIGHT_TEXTURE = lightTexture;
        tame(owner);
        this.getEntityData().set(SCALE, scale);
        this.noPhysics = true;
        setNoGravity(true);
        ARMATURE_ACCESSOR = armatureAccessor;
        MESH = mesh;
        TEXTURE = texture;
        DEFAULT_ANIMATION = defaultAnimation;
        this.entityData.set(MESH_PATH,MESH.registryName().toString());
        this.entityData.set(TEXTURE_PATH,TEXTURE.toString());
        this.entityData.set(LIGHT_TEXTURE_PATH,LIGHT_TEXTURE.toString());
    }

    public VFXEntity(EntityType<? extends VFXEntity> entityType, LivingEntity owner, float scale,float xRotOffset, Armatures.ArmatureAccessor<? extends Armature> armatureAccessor, AssetAccessor<? extends SkinnedMesh> mesh, ResourceLocation texture, ResourceLocation lightTexture, AnimationManager.AnimationAccessor<? extends StaticAnimation> defaultAnimation) {
        super(entityType, owner.level());
        LIGHT_TEXTURE = lightTexture;
        tame(owner);
        this.getEntityData().set(SCALE, scale);
        this.getEntityData().set(X_ROT_OFFSET, xRotOffset);
        this.noPhysics = true;
        setNoGravity(true);
        ARMATURE_ACCESSOR = armatureAccessor;
        float ownerYRot = getOwner().getYHeadRot();
        setStartYRot(ownerYRot);

        MESH = mesh;
        TEXTURE = texture;
        DEFAULT_ANIMATION = defaultAnimation;
        this.entityData.set(MESH_PATH,MESH.registryName().toString());
        this.entityData.set(TEXTURE_PATH,TEXTURE.toString());
        this.entityData.set(LIGHT_TEXTURE_PATH,LIGHT_TEXTURE.toString());

    }


    public VFXEntity(EntityType<? extends VFXEntity> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
        setNoGravity(true);
        ARMATURE_ACCESSOR = null;
        MESH = null;
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
            if (MESH == null){
                MESH = Meshes.MeshAccessor.create(ResourceLocation.parse(this.entityData.get(MESH_PATH)).getNamespace(),ResourceLocation.parse(this.entityData.get(MESH_PATH)).getPath(), (jsonModelLoader) -> jsonModelLoader.loadSkinnedMesh(SkinnedMesh::new));
            }
            if (TEXTURE == null){
                TEXTURE = ResourceLocation.parse(this.entityData.get(TEXTURE_PATH));
            }
            if (LIGHT_TEXTURE == null){
                LIGHT_TEXTURE = ResourceLocation.parse(this.entityData.get(LIGHT_TEXTURE_PATH));
            }
        }
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
        return MESH;
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






    @Nullable
    public AnimationManager.AnimationAccessor<? extends StaticAnimation> getDefaultAnimation() {
        return DEFAULT_ANIMATION;
    }



    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_OWNER_UUID, Optional.empty());
        builder.define(DATA_OWNER_ID, 0);
        builder.define(SCALE,1F);
        builder.define(X_ROT_OFFSET,0F);
        builder.define(START_Y_ROT,0F);
        builder.define(PLAY_ANIMATION,false);
        builder.define(MESH_PATH,"");
        builder.define(TEXTURE_PATH,"");
        builder.define(LIGHT_TEXTURE_PATH,"");
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




    public static AttributeSupplier getDefaultAttribute() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 19.9F)
                .add(Attributes.ATTACK_DAMAGE, 3.0f)
                .add(EpicFightAttributes.MAX_STRIKES, 10.0F)
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
