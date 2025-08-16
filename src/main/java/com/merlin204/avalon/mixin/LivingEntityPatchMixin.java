package com.merlin204.avalon.mixin;

import com.merlin204.avalon.item.IChangeArmatureItem;
import com.merlin204.avalon.item.animationitem.IAvalonAnimationItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin(LivingEntityPatch.class)
public abstract class LivingEntityPatchMixin<T extends LivingEntity> {


    @Inject(method = "getArmature", at = @At("HEAD"), cancellable = true, remap = false)
    private void avalon$getArmature(CallbackInfoReturnable<Armature> cir) {
        if (((LivingEntityPatch<?>) (Object) this).getOriginal() != null){
            ItemStack mainHandItem = ((LivingEntityPatch<?>) (Object) this).getOriginal().getItemInHand(InteractionHand.MAIN_HAND);

            if (mainHandItem.getItem() instanceof IChangeArmatureItem changeArmatureItem ) {
                if (changeArmatureItem.getHitBox() != null){
                    float width = changeArmatureItem.getHitBox().x;
                    float height = changeArmatureItem.getHitBox().y;
                    LivingEntity livingEntity = ((LivingEntityPatch<?>) (Object) this).getOriginal();
                    livingEntity.setBoundingBox(new AABB(
                            livingEntity.getX() + width/2, livingEntity.getY(), livingEntity.getZ() + width/2,
                            livingEntity.getX() - width/2, livingEntity.getY() + height, livingEntity.getZ() - width/2));
                }

                if (changeArmatureItem.getArmature() != null) {
                    cir.setReturnValue(changeArmatureItem.getArmature().get());
                    cir.cancel();
                }
            }else if (mainHandItem.getItem() instanceof IAvalonAnimationItem avalonAnimationItem ){
                if (avalonAnimationItem.getHitBox() != null){
                    float width = avalonAnimationItem.getHitBox().x;
                    float height = avalonAnimationItem.getHitBox().y;
                    LivingEntity livingEntity = ((LivingEntityPatch<?>) (Object) this).getOriginal();
                    livingEntity.setBoundingBox(new AABB(
                            livingEntity.getX() + width/2, livingEntity.getY(), livingEntity.getZ() + width/2,
                            livingEntity.getX() - width/2, livingEntity.getY() + height, livingEntity.getZ() - width/2));
                }
                if (avalonAnimationItem.MANAGER.useAnimationArmature){
                    cir.setReturnValue(avalonAnimationItem.getArmature().get());
                    cir.cancel();
                }else {
                    if (((LivingEntityPatch<?>) (Object) this).getOriginal().level().isClientSide){
                        cir.setReturnValue(avalonAnimationItem.BIPED);
                        cir.cancel();
                    }else {
                        cir.setReturnValue(avalonAnimationItem.getArmature().get());
                        cir.cancel();
                    }

                }

            }
        }
    }
}