package com.merlin204.avalon.mixin;


import com.merlin204.avalon.entity.client.renderer.RenderChangeMeshItem;
import com.merlin204.avalon.item.ChangeArmatureItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.client.forgeevent.PrepareModelEvent;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.renderer.LayerRenderer;
import yesman.epicfight.client.renderer.patched.entity.PatchedEntityRenderer;
import yesman.epicfight.client.renderer.patched.entity.PatchedLivingEntityRenderer;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.client.renderer.patched.layer.PatchedLayer;
import yesman.epicfight.mixin.client.MixinLivingEntityRenderer;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.List;
import java.util.Map;

@Mixin(value = PatchedLivingEntityRenderer.class, remap = false)
public abstract class PLivingEntityRendererMixin<E extends LivingEntity, T extends LivingEntityPatch<E>, M extends EntityModel<E>, R extends LivingEntityRenderer<E, M>, AM extends SkinnedMesh> extends PatchedEntityRenderer<E, T, R, AM> implements LayerRenderer<E, T, M> {

    @Shadow
    @Final
    protected Map<Class<?>, PatchedLayer<E, T, M, ? extends RenderLayer<E, M>>> patchedLayers;

    @Shadow
    @Final
    protected List<PatchedLayer<E, T, M, ? extends RenderLayer<E, M>>> customLayers;

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;Lyesman/epicfight/world/capabilities/entitypatch/LivingEntityPatch;Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;Lnet/minecraft/client/renderer/MultiBufferSource;Lcom/mojang/blaze3d/vertex/PoseStack;IF)V", at = @At("HEAD"), cancellable = true, remap = false)
    private void avalon$replaceMesh(E entity, T entitypatch, R renderer, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks, CallbackInfo ci){
        RenderItemBase renderItemBase = ClientEngine.getInstance().renderEngine.getItemRenderer(entitypatch.getOriginal().getItemInHand(InteractionHand.MAIN_HAND));
        if (entity.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof ChangeArmatureItem changeMeshItem ) {
            if (renderItemBase instanceof RenderChangeMeshItem renderChangeMeshItem) {
                Minecraft mc = Minecraft.getInstance();
                MixinLivingEntityRenderer livingEntityRendererAccessor = (MixinLivingEntityRenderer) renderer;
                boolean isVisible = livingEntityRendererAccessor.invokeIsBodyVisible(entity);
                boolean isVisibleToPlayer = !isVisible && !entity.isInvisibleTo(mc.player);
                RenderType renderType = RenderType.entityTranslucent(renderChangeMeshItem.texture);
                Armature armature = changeMeshItem.getArmature().get();
                SkinnedMesh mesh = renderChangeMeshItem.mesh.get();
                poseStack.pushPose();
                this.mulPoseStack(poseStack, armature, entity, entitypatch, partialTicks);
                this.setArmaturePose(entitypatch, armature, partialTicks);

                PrepareModelEvent prepareModelEvent = new PrepareModelEvent(this, mesh, entitypatch, buffer, poseStack, packedLight, partialTicks);
                if (!MinecraftForge.EVENT_BUS.post(prepareModelEvent)) {
                    mesh.draw(poseStack, buffer, renderType, packedLight, 1.0F, 1.0F, 1.0F, isVisibleToPlayer ? 0.15F : 1.0F, OverlayTexture.NO_OVERLAY, armature, armature.getPoseMatrices());
                    if (renderChangeMeshItem.texture_l != null) {
                        mesh.draw(poseStack, buffer, RenderType.entityTranslucentEmissive(renderChangeMeshItem.texture_l), packedLight, 1.0F, 1.0F, 1.0F, isVisibleToPlayer ? 0.15F : 1.0F, OverlayTexture.NO_OVERLAY, armature, armature.getPoseMatrices());
                    }
                }

                if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
                    entitypatch.getClientAnimator().renderDebuggingInfoForAllLayers(poseStack, buffer, partialTicks);
                }

                poseStack.popPose();
                ci.cancel();

            }
        }
    }



}
