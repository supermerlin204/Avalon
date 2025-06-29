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

@Mixin(value = LivingEntityRenderer.class, remap = true)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>>
        extends EntityRenderer<T>
        implements RenderLayerParent<T, M> {

    @Shadow
    protected M model;

    protected LivingEntityRendererMixin(EntityRendererProvider.Context context) {
        super(context);
    }

    @Inject(
            method = "getRenderType(Lnet/minecraft/world/entity/LivingEntity;ZZZ)Lnet/minecraft/client/renderer/RenderType;",
            at = @At("HEAD"),
            cancellable = true,
            remap = true
    )
    private void avalon$replaceTexture(T livingEntity, boolean bodyVisible, boolean translucent, boolean glowing, CallbackInfoReturnable<RenderType> cir
    ) {
        if (this.model == null) {return;}

        PlayerPatch<?> playerPatch = EpicFightCapabilities.getEntityPatch(livingEntity, PlayerPatch.class);if (playerPatch == null) {return;}

        if (playerPatch.getOriginal() == null) {return;}

        RenderItemBase renderItemBase = ClientEngine.getInstance().renderEngine.getItemRenderer(
                playerPatch.getOriginal().getItemInHand(InteractionHand.MAIN_HAND)
        );

        if (renderItemBase instanceof RenderChangeMeshItem renderChangeMeshItem) {
            ResourceLocation textureLocation = this.getTextureLocation(livingEntity);

            if (renderChangeMeshItem.texture != null) {
                textureLocation = renderChangeMeshItem.texture;
            }

            if (translucent) {
                cir.setReturnValue(RenderType.itemEntityTranslucentCull(textureLocation));
            } else if (bodyVisible) {
                cir.setReturnValue(this.model.renderType(textureLocation));
            } else {
                cir.setReturnValue(glowing ? RenderType.outline(textureLocation) : null);
            }
            cir.cancel();
        }
    }
}
