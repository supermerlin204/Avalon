package com.merlin204.avalon.mixin;

import com.merlin204.avalon.entity.api.collider.IMultiHitBoxEntityPatch;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    /**
     * 取消掉原版的碰撞箱渲染
     */
    @Inject(at = @At("HEAD"), method = "renderHitbox", cancellable = true)
    private static void avalon$renderHitbox(PoseStack poseStack, VertexConsumer buffer, Entity entity, float red, float green, float blue, float alpha, CallbackInfo ci) {
        if (EpicFightCapabilities.getEntityPatch(entity, EntityPatch.class) instanceof IMultiHitBoxEntityPatch iMultiHitBoxEntityPatch && !iMultiHitBoxEntityPatch.shouldRenderAABBHitBox()){
            ci.cancel();
        }
    }
}