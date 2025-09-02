package com.merlin204.avalon.avalon.vfx.type;

import com.merlin204.avalon.entity.AvalonEntities;
import com.merlin204.avalon.entity.vfx.AnimationTextureVFXEntity;
import com.merlin204.avalon.entity.vfx.VFXEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Armatures;

public class AnimationTextureAvalonVFXManager {

    protected final Armatures.ArmatureAccessor<? extends Armature> ARMATURE_ACCESSOR;
    protected final ResourceLocation MESH;
    protected final String TEXTURE_PACK;
    protected final String LIGHT_TEXTURE_PACK;
    protected final int FROM;
    protected final int TO;
    protected final float SPEED;
    protected final AnimationManager.AnimationAccessor<? extends StaticAnimation> DEFAULT_ANIMATION;


    public AnimationTextureAvalonVFXManager(Armatures.ArmatureAccessor<? extends Armature> armatureAccessor, ResourceLocation mesh, String texture, String lightTexture, int from, int to, float speed, AnimationManager.AnimationAccessor<? extends StaticAnimation> defaultAnimation) {
        ARMATURE_ACCESSOR = armatureAccessor;
        MESH = mesh;
        TEXTURE_PACK = texture;
        LIGHT_TEXTURE_PACK = lightTexture;
        FROM = from;
        TO = to;
        SPEED = speed;
        DEFAULT_ANIMATION = defaultAnimation;
    }

    public AnimationTextureAvalonVFXManager(String armaturePath, String meshPath, String texturePath, String lightTexturePath, int from, int to, float speed, AnimationManager.AnimationAccessor<? extends StaticAnimation> defaultAnimation) {
        ARMATURE_ACCESSOR = Armatures.ArmatureAccessor.create(ResourceLocation.parse(armaturePath).getNamespace(), ResourceLocation.parse(armaturePath).getPath(), Armature::new);
        MESH = ResourceLocation.parse(meshPath);
        TEXTURE_PACK = texturePath;
        LIGHT_TEXTURE_PACK =lightTexturePath;
        FROM = from;
        TO = to;
        SPEED = speed;
        DEFAULT_ANIMATION = defaultAnimation;
    }

    public AnimationTextureAvalonVFXManager(String armatureAndMeshPath, String texturePath, String lightTexturePath, int from, int to, float speed, AnimationManager.AnimationAccessor<? extends StaticAnimation> defaultAnimation) {
        ARMATURE_ACCESSOR = Armatures.ArmatureAccessor.create(ResourceLocation.parse(armatureAndMeshPath).getNamespace(), ResourceLocation.parse(armatureAndMeshPath).getPath(), Armature::new);
        MESH = ResourceLocation.parse(armatureAndMeshPath);
        TEXTURE_PACK = texturePath;
        LIGHT_TEXTURE_PACK = lightTexturePath;
        FROM = from;
        TO = to;
        SPEED = speed;
        DEFAULT_ANIMATION = defaultAnimation;
    }


    public AnimationEvent.InTimeEvent createSpawnVFXEntityEvent (int startFrame, Vec3f rotOffset, Vec3f posOffset, float scale) {
        float start = startFrame / 60F;
        return AnimationEvent.InTimeEvent.create(start,(entityPatch, self, params) -> {
            if (entityPatch.getOriginal().level().isClientSide){

            }
            AnimationTextureVFXEntity vfxEntity = new AnimationTextureVFXEntity(AvalonEntities.ANIMATION_TEXTURE_VFX.get(),entityPatch.getOriginal(),scale,rotOffset,this.ARMATURE_ACCESSOR,this.MESH,FROM,TO,SPEED,this.TEXTURE_PACK,this.LIGHT_TEXTURE_PACK,this.DEFAULT_ANIMATION);
            Vec3 pos = entityPatch.getOriginal().position();
            Vec3 totalOffset = getOffset(posOffset, entityPatch.getOriginal());
            Vec3 target = pos.add(totalOffset.x, totalOffset.y, totalOffset.z);
            vfxEntity.setPos(target);
            entityPatch.getOriginal().level().addFreshEntity(vfxEntity);
        }, AnimationEvent.Side.BOTH);
    }



    public void spawnVFXEntity(LivingEntity owner, Vec3 pos, Vec3f rotOffset, float scale){
        if (owner.level().isClientSide){
            return;
        }
        AnimationTextureVFXEntity vfxEntity = new AnimationTextureVFXEntity(AvalonEntities.ANIMATION_TEXTURE_VFX.get(),owner,scale,rotOffset,this.ARMATURE_ACCESSOR,this.MESH,FROM,TO,SPEED,this.TEXTURE_PACK,this.LIGHT_TEXTURE_PACK,this.DEFAULT_ANIMATION);

        vfxEntity.setPos(pos);
        owner.level().addFreshEntity(vfxEntity);
    }



    public void spawnVFXEntity(LivingEntity owner, Vec3f posOffset, Vec3f rotOffset,float scale){
        if (owner.level().isClientSide){
            return;
        }
        AnimationTextureVFXEntity vfxEntity = new AnimationTextureVFXEntity(AvalonEntities.ANIMATION_TEXTURE_VFX.get(),owner,scale,rotOffset,this.ARMATURE_ACCESSOR,this.MESH,FROM,TO,SPEED,this.TEXTURE_PACK,this.LIGHT_TEXTURE_PACK,this.DEFAULT_ANIMATION);
        Vec3 pos = owner.position();
        Vec3 totalOffset = getOffset(posOffset, owner);
        Vec3 target = pos.add(totalOffset.x, totalOffset.y, totalOffset.z);

        vfxEntity.setPos(target);
        owner.level().addFreshEntity(vfxEntity);
    }

    static @NotNull Vec3 getOffset(Vec3f posOffset, LivingEntity livingEntity) {
        float xOffset = posOffset.x;
        float yOffset = posOffset.y;
        float zOffset = posOffset.z;
        float yaw = livingEntity.getYRot();
        double radians = Math.toRadians(yaw);

        double cosYaw = Math.cos(radians);
        double sinYaw = Math.sin(radians);

        double worldX = xOffset * cosYaw + zOffset * sinYaw;
        double worldZ = -xOffset * sinYaw + zOffset * cosYaw;
        Vec3 viewDir = new Vec3(-sinYaw, 0, cosYaw);

        Vec3 totalOffset = viewDir.add(worldX, yOffset, worldZ);
        return totalOffset;
    }
}
