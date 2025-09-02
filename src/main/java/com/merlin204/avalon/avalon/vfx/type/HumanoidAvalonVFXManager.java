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


    public HumanoidAvalonVFXManager(Armatures.ArmatureAccessor<? extends Armature> armatureAccessor, ResourceLocation meshPath, ResourceLocation texture, ResourceLocation lightTexture, AnimationManager.AnimationAccessor<? extends StaticAnimation> defaultAnimation) {
        super(armatureAccessor, meshPath, texture, lightTexture, defaultAnimation);
    }

    public HumanoidAvalonVFXManager(String armaturePath, String meshPath, String texturePath, String lightTexturePath, AnimationManager.AnimationAccessor<? extends StaticAnimation> defaultAnimation) {
        super(armaturePath, meshPath, texturePath, lightTexturePath, defaultAnimation);
    }

    public HumanoidAvalonVFXManager(String armatureAndMeshPath, String texturePath, String lightTexturePath, AnimationManager.AnimationAccessor<? extends StaticAnimation> defaultAnimation) {
        super(armatureAndMeshPath, texturePath, lightTexturePath, defaultAnimation);
    }

    public void spawnVFXEntity(LivingEntity owner, Vec3 pos, Vec3f rotOffset, float scale, ItemStack mainHandItem, ItemStack offHandItem){
        if (owner.level().isClientSide){
            return;
        }

        VFXEntity vfxEntity = new VFXEntity(AvalonEntities.VFX.get(),owner,scale,rotOffset,this.ARMATURE_ACCESSOR,this.MESH_PATH,this.TEXTURE,this.LIGHT_TEXTURE,this.DEFAULT_ANIMATION);
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



    public void spawnVFXEntity(LivingEntity owner, Vec3f posOffset,  Vec3f rotOffset,float scale, ItemStack mainHandItem,ItemStack offHandItem){
        if (owner.level().isClientSide){
            return;
        }
        VFXEntity vfxEntity = new VFXEntity(AvalonEntities.VFX.get(),owner,scale,rotOffset,this.ARMATURE_ACCESSOR,this.MESH_PATH,this.TEXTURE,this.LIGHT_TEXTURE,this.DEFAULT_ANIMATION);
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
