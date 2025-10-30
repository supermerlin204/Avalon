package com.merlin204.avalon.avalon.vfx.type;

import com.merlin204.avalon.entity.AvalonEntities;
import com.merlin204.avalon.entity.vfx.VFXEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Armatures;

public class StaticAvalonVFXManager {

    protected final Armatures.ArmatureAccessor<? extends Armature> ARMATURE_ACCESSOR;


    protected final ResourceLocation MESH_PATH;
    protected final ResourceLocation TEXTURE;
    protected final ResourceLocation LIGHT_TEXTURE;
    protected final AnimationManager.AnimationAccessor<? extends StaticAnimation> DEFAULT_ANIMATION;

    public StaticAvalonVFXManager(String modID,String name, AnimationManager.AnimationAccessor<? extends StaticAnimation> defaultAnimation) {
        ARMATURE_ACCESSOR = Armatures.ArmatureAccessor.create(modID, "/avalon/vfx/"+ name, Armature::new);
        MESH_PATH = ResourceLocation.fromNamespaceAndPath(modID,"/avalon/vfx/"+ name);
        TEXTURE = ResourceLocation.fromNamespaceAndPath(modID,"animmodels/avalon/vfx/" + name +".png");
        LIGHT_TEXTURE =  ResourceLocation.fromNamespaceAndPath(modID,"animmodels/avalon/vfx/" + name +"_l.png");
        DEFAULT_ANIMATION = defaultAnimation;
    }

    public StaticAvalonVFXManager(Armatures.ArmatureAccessor<? extends Armature> armatureAccessor, ResourceLocation meshPath, ResourceLocation texture, ResourceLocation lightTexture, AnimationManager.AnimationAccessor<? extends StaticAnimation> defaultAnimation) {
        ARMATURE_ACCESSOR = armatureAccessor;
        MESH_PATH = meshPath;
        TEXTURE = texture;
        LIGHT_TEXTURE = lightTexture;
        DEFAULT_ANIMATION = defaultAnimation;
    }

    public StaticAvalonVFXManager(String armaturePath, String meshPath, String texturePath, String lightTexturePath, AnimationManager.AnimationAccessor<? extends StaticAnimation> defaultAnimation) {
        ARMATURE_ACCESSOR = Armatures.ArmatureAccessor.create(ResourceLocation.parse(armaturePath).getNamespace(), ResourceLocation.parse(armaturePath).getPath(), Armature::new);
        MESH_PATH = ResourceLocation.parse(meshPath);
        TEXTURE = ResourceLocation.parse(texturePath);
        LIGHT_TEXTURE = ResourceLocation.parse(lightTexturePath);
        DEFAULT_ANIMATION = defaultAnimation;
    }

    public StaticAvalonVFXManager(String armatureAndMeshPath, String texturePath, String lightTexturePath, AnimationManager.AnimationAccessor<? extends StaticAnimation> defaultAnimation) {
        ARMATURE_ACCESSOR = Armatures.ArmatureAccessor.create(ResourceLocation.parse(armatureAndMeshPath).getNamespace(), ResourceLocation.parse(armatureAndMeshPath).getPath(), Armature::new);
        MESH_PATH = ResourceLocation.parse(armatureAndMeshPath);
        TEXTURE = ResourceLocation.parse(texturePath);
        LIGHT_TEXTURE = ResourceLocation.parse(lightTexturePath);
        DEFAULT_ANIMATION = defaultAnimation;
    }


    public AnimationEvent.InTimeEvent createSpawnVFXEntityEvent (int startFrame, Vec3f rotOffset, Vec3f posOffset, float scale) {
        float start = startFrame / 60F;
        return AnimationEvent.InTimeEvent.create(start,(entityPatch, self, params) -> {
            Vec3 pos = entityPatch.getOriginal().position();
            Vec3 totalOffset = getOffset(posOffset, entityPatch.getOriginal());
            Vec3 target = pos.add(totalOffset.x, totalOffset.y, totalOffset.z);
            spawnVFXEntity(entityPatch.getOriginal(),target,rotOffset,scale);
        }, AnimationEvent.Side.SERVER);
    }



    public void spawnVFXEntity(LivingEntity owner, Vec3 pos, Vec3f rotOffset, float scale){
        if (owner.level().isClientSide){
            return;
        }
        VFXEntity vfxEntity = new VFXEntity(AvalonEntities.VFX.get(),owner,scale,rotOffset,this.ARMATURE_ACCESSOR,this.MESH_PATH,this.TEXTURE,this.LIGHT_TEXTURE,this.DEFAULT_ANIMATION);

        vfxEntity.setPos(pos);
        owner.level().addFreshEntity(vfxEntity);
    }



    public void spawnVFXEntity(LivingEntity owner, Vec3f posOffset, Vec3f rotOffset,float scale){
        if (owner.level().isClientSide){
            return;
        }
        VFXEntity vfxEntity = new VFXEntity(AvalonEntities.VFX.get(),owner,scale,rotOffset,this.ARMATURE_ACCESSOR,this.MESH_PATH,this.TEXTURE,this.LIGHT_TEXTURE,this.DEFAULT_ANIMATION);
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
