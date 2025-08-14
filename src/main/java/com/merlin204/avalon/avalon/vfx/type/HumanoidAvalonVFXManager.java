package com.merlin204.avalon.avalon.vfx.type;

import com.merlin204.avalon.entity.AvalonEntities;
import com.merlin204.avalon.entity.vfx.VFXEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Armatures;

public class HumanoidAvalonVFXManager extends StaticAvalonVFXManager {
    public HumanoidAvalonVFXManager(Armatures.ArmatureAccessor<? extends Armature> armatureAccessor, AssetAccessor<? extends SkinnedMesh> mesh, ResourceLocation texture, ResourceLocation lightTexture, AnimationManager.AnimationAccessor<? extends StaticAnimation> defaultAnimation) {
        super(armatureAccessor, mesh, texture, lightTexture, defaultAnimation);
    }

    public HumanoidAvalonVFXManager(String armaturePath, String meshPath, String texturePath, String lightTexturePath, AnimationManager.AnimationAccessor<? extends StaticAnimation> defaultAnimation) {
        super(armaturePath, meshPath, texturePath, lightTexturePath, defaultAnimation);
    }

    public HumanoidAvalonVFXManager(String armatureAndMeshPath, String texturePath, String lightTexturePath, AnimationManager.AnimationAccessor<? extends StaticAnimation> defaultAnimation) {
        super(armatureAndMeshPath, texturePath, lightTexturePath, defaultAnimation);
    }

    public AnimationEvent.InTimeEvent createSpawnVFXEntityEvent (int startFrame, float scale, float xRotOffset, Vec3f posOffset, ItemStack mainHandItem,ItemStack offHandItem) {
        float start = startFrame / 60F;
        return AnimationEvent.InTimeEvent.create(start,(entityPatch, self, params) -> {
            VFXEntity vfxEntity = new VFXEntity(AvalonEntities.VFX.get(),entityPatch.getOriginal(),scale,xRotOffset,this.ARMATURE_ACCESSOR,this.MESH,this.TEXTURE,this.LIGHT_TEXTURE,this.DEFAULT_ANIMATION);
            Vec3 pos = entityPatch.getOriginal().position();
            if (mainHandItem != null){
                vfxEntity.setItemInHand(InteractionHand.MAIN_HAND,mainHandItem);
            }
            if (offHandItem != null){
                vfxEntity.setItemInHand(InteractionHand.OFF_HAND,offHandItem);
            }
            Vec3 totalOffset = getOffset(posOffset, entityPatch.getOriginal());
            Vec3 target = pos.add(totalOffset.x, totalOffset.y, totalOffset.z);
            vfxEntity.setYRot(entityPatch.getOriginal().getYRot());
            vfxEntity.setPos(target);
            entityPatch.getOriginal().level().addFreshEntity(vfxEntity);
            vfxEntity.setYRot(entityPatch.getOriginal().getYRot());
            vfxEntity.setPos(target);
        }, AnimationEvent.Side.SERVER);
    }



    public void spawnVFXEntity(LivingEntity owner, Vec3 pos, float scale, float xRotOffset, ItemStack mainHandItem,ItemStack offHandItem){
        if (owner.level().isClientSide){
            return;
        }

        VFXEntity vfxEntity = new VFXEntity(AvalonEntities.VFX.get(),owner,scale,xRotOffset,this.ARMATURE_ACCESSOR,this.MESH,this.TEXTURE,this.LIGHT_TEXTURE,this.DEFAULT_ANIMATION);
        if (mainHandItem != null){
            vfxEntity.setItemInHand(InteractionHand.MAIN_HAND,mainHandItem);
        }
        if (offHandItem != null){
            vfxEntity.setItemInHand(InteractionHand.OFF_HAND,offHandItem);
        }
        vfxEntity.setPos(pos);
        owner.level().addFreshEntity(vfxEntity);

        vfxEntity.setPos(pos);
    }



    public void spawnVFXEntity(LivingEntity owner, Vec3f posOffset, float scale, float xRotOffset, ItemStack mainHandItem,ItemStack offHandItem){
        if (owner.level().isClientSide){
            return;
        }
        VFXEntity vfxEntity = new VFXEntity(AvalonEntities.VFX.get(),owner,scale,xRotOffset,this.ARMATURE_ACCESSOR,this.MESH,this.TEXTURE,this.LIGHT_TEXTURE,this.DEFAULT_ANIMATION);
        if (mainHandItem != null){
            vfxEntity.setItemInHand(InteractionHand.MAIN_HAND,mainHandItem);
        }
        if (offHandItem != null){
            vfxEntity.setItemInHand(InteractionHand.OFF_HAND,offHandItem);
        }
        Vec3 pos = owner.position();
        Vec3 totalOffset = getOffset(posOffset, owner);
        Vec3 target = pos.add(totalOffset.x, totalOffset.y, totalOffset.z);
        vfxEntity.setYRot(owner.getYRot());
        vfxEntity.setPos(target);
        owner.level().addFreshEntity(vfxEntity);
        vfxEntity.setYRot(owner.getYRot());
        vfxEntity.setPos(target);
    }

}
