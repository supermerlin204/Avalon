package com.merlin204.avalon.mixin;

import com.merlin204.avalon.entity.client.renderer.RenderChangeMeshItem;
import com.merlin204.avalon.entity.client.renderer.RenderMeshItem;
import com.merlin204.avalon.item.ChangeMeshItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

@Mixin(LivingEntityPatch.class)
public abstract class LivingEntityPatchMixin<T extends LivingEntity> {


    @Inject(method = "getArmature", at = @At("HEAD"), cancellable = true, remap = false)
    private void avalon$getArmature(CallbackInfoReturnable<Armature> cir) {
        ItemStack mainHandItem = ((LivingEntityPatch<?>) (Object) this).getOriginal().getItemInHand(InteractionHand.MAIN_HAND);
        if (mainHandItem.getItem() instanceof ChangeMeshItem changeMeshItem) {
            if (changeMeshItem.getHitBox() != null){
                float width = changeMeshItem.getHitBox().x;
                float height = changeMeshItem.getHitBox().y;
                LivingEntity livingEntity = ((LivingEntityPatch<?>) (Object) this).getOriginal();
                livingEntity.setBoundingBox(new AABB(
                        livingEntity.getX() + width/2, livingEntity.getY(), livingEntity.getZ() + width/2,
                        livingEntity.getX() - width/2, livingEntity.getY() + height, livingEntity.getZ() - width/2));
            }

            if (changeMeshItem.getArmature() != null) {
                cir.setReturnValue(changeMeshItem.getArmature().get());
                cir.cancel();
            }
        }
    }
}