package com.merlin204.avalon.mixin;


import com.merlin204.avalon.entity.client.renderer.patch.item.RenderAnimationItem;
import com.merlin204.avalon.entity.client.renderer.patch.item.RenderChangeMeshItem;
import com.merlin204.avalon.item.IChangeArmatureItem;
import com.merlin204.avalon.item.animationitem.IAvalonAnimationItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.client.forgeevent.PrepareModelEvent;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec2i;
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

    @Shadow
    protected abstract void prepareModel(AM mesh, E entity, T entitypatch, R renderer);
    @Shadow
    protected abstract void renderLayer(LivingEntityRenderer<E, M> renderer, T entitypatch, E entity, OpenMatrix4f[] poses, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks);





    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;Lyesman/epicfight/world/capabilities/entitypatch/LivingEntityPatch;Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;Lnet/minecraft/client/renderer/MultiBufferSource;Lcom/mojang/blaze3d/vertex/PoseStack;IF)V", at = @At("HEAD"), cancellable = true, remap = false)
    private void avalon$replaceMesh(E entity, T entitypatch, R renderer, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks, CallbackInfo ci) {


        RenderItemBase renderItemBase = ClientEngine.getInstance().renderEngine.getItemRenderer(entitypatch.getOriginal().getItemInHand(InteractionHand.MAIN_HAND));
        if (entity.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof IAvalonAnimationItem avalonAnimationItem) {
            avalonAnimationItem.MANAGER.useAnimationArmature = false;
            Minecraft mc = Minecraft.getInstance();
            MixinLivingEntityRenderer livingEntityRendererAccessor = (MixinLivingEntityRenderer) renderer;
            boolean isVisible = livingEntityRendererAccessor.invokeIsBodyVisible(entity);
            boolean isVisibleToPlayer = !isVisible && !entity.isInvisibleTo(mc.player);
            boolean isGlowing = mc.shouldEntityAppearGlowing(entity);
            RenderType renderType = livingEntityRendererAccessor.invokeGetRenderType(entity, isVisible, isVisibleToPlayer, isGlowing);
            Armature armature = entitypatch.getArmature();
            AM mesh = this.getMeshProvider(entitypatch).get();

            poseStack.pushPose();
            this.mulPoseStack(poseStack, armature, entity, entitypatch, partialTicks);
            this.setArmaturePose(entitypatch, armature, partialTicks);
            this.prepareModel(mesh, entity, entitypatch, renderer);

            PrepareModelEvent prepareModelEvent = new PrepareModelEvent(this, mesh, entitypatch, buffer, poseStack, packedLight, partialTicks);


            if (!MinecraftForge.EVENT_BUS.post(prepareModelEvent)) {
                Vector4f color = new Vector4f(1.0F, 1.0F, 1.0F, isVisibleToPlayer ? 0.15F : 1.0F);
                entitypatch.getEntityDecorations().modifyColor(color, partialTicks);

                int blockLight = (packedLight & 0xF0) >> 4;
                int skyLight = (packedLight & 0xF00000) >> 20;
                Vec2i lightUv = new Vec2i(blockLight, skyLight);
                entitypatch.getEntityDecorations().modifyLight(lightUv, partialTicks);
                int modifiedLight = LightTexture.pack(lightUv.x, lightUv.y);
                mesh.draw(poseStack, buffer, renderType, modifiedLight, color.x(), color.y(), color.z(), color.w(), this.getOverlayCoord(entity, entitypatch, partialTicks), armature, armature.getPoseMatrices());

                entitypatch.getEntityDecorations().listDecorationOverlays().forEach(decorationOverlay -> {
                    if (!decorationOverlay.shouldRemove() && decorationOverlay.shouldRender()) {
                        Vector4f overlayColor = decorationOverlay.color(partialTicks);
                        mesh.draw(poseStack, buffer, decorationOverlay.getRenderType(), modifiedLight, overlayColor.x(), overlayColor.y(), overlayColor.z(), overlayColor.w(), OverlayTexture.NO_OVERLAY, armature, armature.getPoseMatrices());
                    }
                });
            }

            if (!entity.isSpectator()) {
                this.renderLayer(renderer, entitypatch, entity, armature.getPoseMatrices(), buffer, poseStack, packedLight, partialTicks);
            }

            avalonAnimationItem.MANAGER.useAnimationArmature = true;
            if (renderType != null && Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
                entitypatch.getClientAnimator().renderDebuggingInfoForAllLayers(poseStack, buffer, partialTicks);
            }
            avalonAnimationItem.MANAGER.useAnimationArmature = false;

            if (renderItemBase instanceof RenderAnimationItem renderAnimationItem) {
                avalonAnimationItem.MANAGER.useAnimationArmature = true;
                Armature realArmature = entitypatch.getArmature();
                SkinnedMesh itemMesh = renderAnimationItem.mesh.get();

                this.setArmaturePose(entitypatch, realArmature, partialTicks);
                if (renderAnimationItem.texture != null){

                }

                itemMesh.draw(poseStack, buffer,RenderType.entityTranslucent(renderAnimationItem.texture), packedLight, 1.0F, 1.0F, 1.0F, isVisibleToPlayer ? 0.15F : 1.0F, OverlayTexture.NO_OVERLAY, realArmature, realArmature.getPoseMatrices());

                if (renderAnimationItem.texture_l != null) {
                    itemMesh.draw(poseStack, buffer, RenderType.entityTranslucentEmissive(renderAnimationItem.texture_l), packedLight, 1.0F, 1.0F, 1.0F, isVisibleToPlayer ? 0.15F : 1.0F, OverlayTexture.NO_OVERLAY, realArmature, realArmature.getPoseMatrices());
                }
            }

            poseStack.popPose();
            ci.cancel();
        }

        if (entity.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof IChangeArmatureItem changeArmatureItem && changeArmatureItem.change(entitypatch)) {
             if (renderItemBase instanceof RenderChangeMeshItem renderChangeMeshItem) {

                Minecraft mc = Minecraft.getInstance();
                MixinLivingEntityRenderer livingEntityRendererAccessor = (MixinLivingEntityRenderer) renderer;
                boolean isVisible = livingEntityRendererAccessor.invokeIsBodyVisible(entity);
                boolean isVisibleToPlayer = !isVisible && !entity.isInvisibleTo(mc.player);
                RenderType renderType = RenderType.entityTranslucent(renderChangeMeshItem.texture);
                Armature armature = changeArmatureItem.getArmature().get();
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

    protected int getOverlayCoord(E entity, T entitypatch, float partialTicks) {
        int initU = 0;
        int initV = OverlayTexture.v(entity.hurtTime > 0 || entity.deathTime > 0);

        Vec2i coord = new Vec2i(initU, initV);
        entitypatch.getEntityDecorations().modifyOverlay(coord, partialTicks);

        return OverlayTexture.pack(coord.x, coord.y);
    }


}
