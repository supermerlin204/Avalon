package com.merlin204.avalon.mixin;

import com.merlin204.avalon.entity.client.renderer.RenderMeshItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.PathfinderMob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

@Mixin(HumanoidMobPatch.class)
public abstract class HumanoidMobPatchMixin<T extends PathfinderMob> extends MobPatch<T> {

    @Inject(method = "getArmature()Lyesman/epicfight/model/armature/HumanoidArmature;", at = @At("HEAD"), cancellable = true, remap = false)
    private void avalon$getArmature(CallbackInfoReturnable<Armature> cir) {
        RenderItemBase renderItemBase = ClientEngine.getInstance().renderEngine.getItemRenderer(this.getOriginal().getItemInHand(InteractionHand.MAIN_HAND));
        if (renderItemBase instanceof RenderMeshItem renderMeshItem) {
            {
                if (renderMeshItem.armatureAccessor != null) {
                    cir.setReturnValue(renderMeshItem.armatureAccessor.get());
                }
            }
        }
    }
}