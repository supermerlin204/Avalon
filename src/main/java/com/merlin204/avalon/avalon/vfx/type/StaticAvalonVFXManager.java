package com.merlin204.avalon.avalon.vfx.type;

import com.merlin204.avalon.entity.AvalonEntities;
import com.merlin204.avalon.entity.vfx.VFXEntity;
import com.merlin204.avalon.main.AvalonMOD;
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

public class StaticAvalonVFXManager {

    protected final Armatures.ArmatureAccessor<? extends Armature> ARMATURE_ACCESSOR;
    protected final AssetAccessor<? extends SkinnedMesh> MESH;
    protected final ResourceLocation TEXTURE;
    protected final ResourceLocation LIGHT_TEXTURE;
    protected final AnimationManager.AnimationAccessor<? extends StaticAnimation> DEFAULT_ANIMATION;


    public StaticAvalonVFXManager(Armatures.ArmatureAccessor<? extends Armature> armatureAccessor, AssetAccessor<? extends SkinnedMesh> mesh, ResourceLocation texture, ResourceLocation lightTexture, AnimationManager.AnimationAccessor<? extends StaticAnimation> defaultAnimation) {
        ARMATURE_ACCESSOR = armatureAccessor;
        MESH = mesh;
        TEXTURE = texture;
        LIGHT_TEXTURE = lightTexture;
        DEFAULT_ANIMATION = defaultAnimation;
    }

    public StaticAvalonVFXManager(String armaturePath, String meshPath, String texturePath, String lightTexturePath, AnimationManager.AnimationAccessor<? extends StaticAnimation> defaultAnimation) {
        ARMATURE_ACCESSOR = Armatures.ArmatureAccessor.create(ResourceLocation.parse(armaturePath).getNamespace(), ResourceLocation.parse(armaturePath).getPath(), Armature::new);
        MESH = Meshes.MeshAccessor.create(ResourceLocation.parse(meshPath).getNamespace(), ResourceLocation.parse(meshPath).getPath(), (jsonModelLoader) -> jsonModelLoader.loadSkinnedMesh(SkinnedMesh::new));
        TEXTURE = ResourceLocation.parse(texturePath);
        LIGHT_TEXTURE = ResourceLocation.parse(lightTexturePath);
        DEFAULT_ANIMATION = defaultAnimation;
    }

    public StaticAvalonVFXManager(String armatureAndMeshPath, String texturePath, String lightTexturePath, AnimationManager.AnimationAccessor<? extends StaticAnimation> defaultAnimation) {
        ARMATURE_ACCESSOR = Armatures.ArmatureAccessor.create(ResourceLocation.parse(armatureAndMeshPath).getNamespace(), ResourceLocation.parse(armatureAndMeshPath).getPath(), Armature::new);
        MESH = Meshes.MeshAccessor.create(ResourceLocation.parse(armatureAndMeshPath).getNamespace(), ResourceLocation.parse(armatureAndMeshPath).getPath(), (jsonModelLoader) -> jsonModelLoader.loadSkinnedMesh(SkinnedMesh::new));
        TEXTURE = ResourceLocation.parse(texturePath);
        LIGHT_TEXTURE = ResourceLocation.parse(lightTexturePath);
        DEFAULT_ANIMATION = defaultAnimation;
    }


    public AnimationEvent.InTimeEvent createSpawnVFXEntityEvent (int startFrame, float scale, float xRotOffset, Vec3f posOffset) {
        float start = startFrame / 60F;
        return AnimationEvent.InTimeEvent.create(start,(entityPatch, self, params) -> {
            VFXEntity vfxEntity = new VFXEntity(AvalonEntities.VFX.get(),entityPatch.getOriginal(),scale,xRotOffset,this.ARMATURE_ACCESSOR,this.MESH,this.TEXTURE,this.LIGHT_TEXTURE,this.DEFAULT_ANIMATION);
            Vec3 pos = entityPatch.getOriginal().position();
            Vec3 totalOffset = getOffset(posOffset, entityPatch.getOriginal());
            Vec3 target = pos.add(totalOffset.x, totalOffset.y, totalOffset.z);
            vfxEntity.setYRot(entityPatch.getOriginal().getYRot());
            vfxEntity.setPos(target);
            entityPatch.getOriginal().level().addFreshEntity(vfxEntity);
            vfxEntity.setYRot(entityPatch.getOriginal().getYRot());
            vfxEntity.setPos(target);
        }, AnimationEvent.Side.SERVER);
    }



    public void spawnVFXEntity(LivingEntity owner, Vec3 pos, float scale, float xRotOffset){
        if (owner.level().isClientSide){
            return;
        }
        VFXEntity vfxEntity = new VFXEntity(AvalonEntities.VFX.get(),owner,scale,xRotOffset,this.ARMATURE_ACCESSOR,this.MESH,this.TEXTURE,this.LIGHT_TEXTURE,this.DEFAULT_ANIMATION);

        vfxEntity.setPos(pos);
        owner.level().addFreshEntity(vfxEntity);

        vfxEntity.setPos(pos);
    }



    public void spawnVFXEntity(LivingEntity owner, Vec3f posOffset, float scale, float xRotOffset){
        if (owner.level().isClientSide){
            return;
        }
        VFXEntity vfxEntity = new VFXEntity(AvalonEntities.VFX.get(),owner,scale,xRotOffset,this.ARMATURE_ACCESSOR,this.MESH,this.TEXTURE,this.LIGHT_TEXTURE,this.DEFAULT_ANIMATION);
        Vec3 pos = owner.position();
        Vec3 totalOffset = getOffset(posOffset, owner);
        Vec3 target = pos.add(totalOffset.x, totalOffset.y, totalOffset.z);
        vfxEntity.setYRot(owner.getYRot());
        vfxEntity.setPos(target);
        owner.level().addFreshEntity(vfxEntity);
        vfxEntity.setYRot(owner.getYRot());
        vfxEntity.setPos(target);
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
