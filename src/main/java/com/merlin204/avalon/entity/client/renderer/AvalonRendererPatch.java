package com.merlin204.avalon.entity.client.renderer;

import com.merlin204.avalon.entity.AvalonMeshEntity;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.client.renderer.patched.entity.PatchedLivingEntityRenderer;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class AvalonRendererPatch extends PatchedLivingEntityRenderer<LivingEntity, LivingEntityPatch<LivingEntity>, EmptyEntityModel<LivingEntity>, LivingEntityRenderer<LivingEntity, EmptyEntityModel<LivingEntity>>, SkinnedMesh> {


    public AvalonRendererPatch(EntityRendererProvider.Context context, EntityType<?> entityType) {
        super(context, entityType);

    }

    @Override
    public void render(LivingEntity entity, LivingEntityPatch entitypatch, LivingEntityRenderer renderer, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();


        if (entity instanceof AvalonMeshEntity avalonMeshEntity){
            Armature armature = entitypatch.getArmature();
            SkinnedMesh mesh = avalonMeshEntity.getMesh().get();
            ResourceLocation texture = avalonMeshEntity.getTexture();

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

    }



    @Override
    public AssetAccessor<SkinnedMesh> getDefaultMesh() {
        return null;
    }
}