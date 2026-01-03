package com.merlin204.avalon.entity.client.renderer.patch.entity;

import com.merlin204.avalon.entity.IAvalonMeshEntity;
import com.merlin204.avalon.entity.api.collider.AvalonEntityOBBCollider;
import com.merlin204.avalon.entity.api.collider.IMultiHitBoxEntityPatch;
import com.merlin204.avalon.entity.client.model.EmptyEntityModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.QuaternionUtils;
import yesman.epicfight.client.renderer.patched.entity.PatchedLivingEntityRenderer;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.awt.*;

@OnlyIn(Dist.CLIENT)
public class AvalonRendererPatch extends PatchedLivingEntityRenderer<LivingEntity, LivingEntityPatch<LivingEntity>, EmptyEntityModel<LivingEntity>, LivingEntityRenderer<LivingEntity, EmptyEntityModel<LivingEntity>>, SkinnedMesh> {

    private AssetAccessor<? extends SkinnedMesh> meshAssetAccessor = null;

    public AvalonRendererPatch(EntityRendererProvider.Context context, EntityType<?> entityType) {
        super(context, entityType);

    }

    @Override
    public void render(LivingEntity entity, LivingEntityPatch entitypatch, LivingEntityRenderer renderer, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();



        if (entity instanceof IAvalonMeshEntity avalonMeshEntity){
            Armature armature = entitypatch.getArmature();

            if (avalonMeshEntity.getMesh() == null){
                return;
            }else {
                this.meshAssetAccessor = avalonMeshEntity.getMesh();
            }
            SkinnedMesh mesh = avalonMeshEntity.getMesh().get();
            ResourceLocation texture = avalonMeshEntity.getTexture();
            if (armature == null || mesh == null || texture == null){
                return;
            }

            RenderType renderType = RenderType.entityTranslucent(texture);

            ResourceLocation litTexture = avalonMeshEntity.getLitTexture();

            poseStack.pushPose();
            this.mulPoseStack(poseStack, armature, entity, entitypatch, partialTicks);
            this.setArmaturePose(entitypatch, armature, partialTicks);


            mesh.draw(poseStack, buffer, RenderType.entityTranslucent(texture), packedLight, 1.0F, 1.0F, 1.0F, 1.0F, OverlayTexture.NO_OVERLAY, entitypatch.getArmature(), armature.getPoseMatrices());

            if (litTexture != null){
                mesh.draw(poseStack, buffer, RenderType.entityTranslucentEmissive(litTexture), packedLight, 1.0F, 1.0F, 1.0F, 1.0F, OverlayTexture.NO_OVERLAY, entitypatch.getArmature(), armature.getPoseMatrices());
            }





            this.renderLayer(renderer, entitypatch, entity, armature.getPoseMatrices(), buffer, poseStack, packedLight, partialTicks);


            if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
                entitypatch.getClientAnimator().renderDebuggingInfoForAllLayers(poseStack, buffer, partialTicks);
            }


            poseStack.popPose();
        }

        if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes() && entitypatch instanceof IMultiHitBoxEntityPatch multiHitBoxEntityPatch) {
            OpenMatrix4f modelMatrix = entitypatch.getModelMatrix(partialTicks);
            poseStack.mulPose(QuaternionUtils.YP.rotationDegrees(180.0F));
            MathUtils.mulStack(poseStack, modelMatrix);
            if (LivingEntityRenderer.isEntityUpsideDown(entity)) {
                poseStack.translate(0.0, (entity.getBbHeight() + 0.1F), 0.0);
                poseStack.mulPose(QuaternionUtils.ZP.rotationDegrees(180.0F));
            }
            multiHitBoxEntityPatch.updateAllCollider();
            for (Joint joint : multiHitBoxEntityPatch.getColliderManager().getColliderMap().keySet()) {
                AvalonEntityOBBCollider collider = multiHitBoxEntityPatch.getColliderManager().getColliderMap().get(joint);
                Color color = new Color(0, 255, 249);
                if (multiHitBoxEntityPatch.getColliderManager().getHitList().contains(joint.getId())){
                    color = new Color(255, 0, 0);
                }
                collider.drawInternal(poseStack, Minecraft.getInstance().renderBuffers().bufferSource(),joint, entitypatch,partialTicks,color.getRGB());
            }
        }


    }



    @Override
    public AssetAccessor<SkinnedMesh> getDefaultMesh() {
        if (meshAssetAccessor != null && meshAssetAccessor.get() instanceof SkinnedMesh){
            return (AssetAccessor<SkinnedMesh>) meshAssetAccessor;
        }
        return Meshes.BOOTS;
    }
}