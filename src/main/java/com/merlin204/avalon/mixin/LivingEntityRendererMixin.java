package com.merlin204.avalon.mixin;

import com.merlin204.avalon.entity.client.renderer.RenderChangeMeshItem;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@Mixin(value = LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements RenderLayerParent<T, M> {

    /**
     * 取自EFMM by P1nero
     */

    @Shadow
    protected M model;

    protected LivingEntityRendererMixin(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }

    @Inject(method = "getRenderType", at = @At("HEAD"), cancellable = true)
    private void avalon$replaceTexture(T livingEntity, boolean p_115323_, boolean p_115324_, boolean p_115325_, CallbackInfoReturnable<RenderType> cir){
        PlayerPatch<?> playerPatch = EpicFightCapabilities.getEntityPatch(livingEntity, PlayerPatch.class);
        if (playerPatch == null){
            return;
        }
        RenderItemBase renderItemBase = ClientEngine.getInstance().renderEngine.getItemRenderer(playerPatch.getOriginal().getItemInHand(InteractionHand.MAIN_HAND));
        if (renderItemBase instanceof RenderChangeMeshItem renderChangeMeshItem){
            ResourceLocation resourcelocation = this.getTextureLocation(livingEntity);
            if(renderChangeMeshItem.texture != null){
                resourcelocation = renderChangeMeshItem.texture;
            }

            if (p_115324_) {
                cir.setReturnValue(RenderType.itemEntityTranslucentCull(resourcelocation));
            } else if (p_115323_) {
                cir.setReturnValue(this.model.renderType(resourcelocation));
            } else {
                cir.setReturnValue(p_115325_ ? RenderType.outline(resourcelocation) : null);
            }
        }
    }
}
