package com.merlin204.avalon.entity.client.renderer.patch.entity;

import com.merlin204.avalon.entity.IAvalonMeshEntity;
import com.merlin204.avalon.entity.client.model.EmptyEntityModel;
import com.merlin204.avalon.entity.vfx.VFXEntity;
import com.merlin204.avalon.entity.vfx.VFXEntityPatch;
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
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.client.renderer.patched.entity.PatchedLivingEntityRenderer;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class AvalonVFXRendererPatch extends PatchedLivingEntityRenderer<VFXEntity, VFXEntityPatch<VFXEntity>, EmptyEntityModel<VFXEntity>, LivingEntityRenderer<VFXEntity, EmptyEntityModel<VFXEntity>>, SkinnedMesh> {

    private AssetAccessor<? extends SkinnedMesh> meshAssetAccessor = null;

    public AvalonVFXRendererPatch(EntityRendererProvider.Context context, EntityType<?> entityType) {
        super(context, entityType);

    }

    @Override
    public void render(VFXEntity entity, VFXEntityPatch entityPatch, LivingEntityRenderer renderer, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();


        if (entityPatch.getAnimator().getPlayerFor(null).getAnimation().get().isLinkAnimation() || entityPatch.getClientAnimator().getPlayerFor(null).getAnimation().get().isLinkAnimation()){
            return;
        }

        Armature armature = entityPatch.getArmature();

        if (entity.getMesh() == null){
            return;
        }else {
            this.meshAssetAccessor = entity.getMesh();
        }
        SkinnedMesh mesh = entity.getMesh().get();
        ResourceLocation texture = entity.getTexture();
        if (armature == null || mesh == null || texture == null){
            return;
        }

        ResourceLocation litTexture = entity.getLitTexture();
        if (!entity.getShouldRender()){
            return;
        }

        poseStack.pushPose();
        this.mulPoseStack(poseStack, armature, entity, entityPatch, partialTicks);
        this.setArmaturePose(entityPatch, armature, partialTicks);



        mesh.draw(poseStack, buffer, RenderType.entityTranslucent(texture), packedLight, 1.0F, 1.0F, 1.0F, 1.0F, OverlayTexture.NO_OVERLAY, entityPatch.getArmature(), armature.getPoseMatrices());

        if (litTexture != null){
            mesh.draw(poseStack, buffer, RenderType.entityTranslucentEmissive(litTexture), packedLight, 1.0F, 1.0F, 1.0F, 1.0F, OverlayTexture.NO_OVERLAY, entityPatch.getArmature(), armature.getPoseMatrices());
        }



        this.renderLayer(renderer, entityPatch, entity, armature.getPoseMatrices(), buffer, poseStack, packedLight, partialTicks);


        if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
            entityPatch.getClientAnimator().renderDebuggingInfoForAllLayers(poseStack, buffer, partialTicks);
        }


        poseStack.popPose();



    }



    @Override
    public AssetAccessor<SkinnedMesh> getDefaultMesh() {
        if (meshAssetAccessor != null && meshAssetAccessor.get() instanceof SkinnedMesh){
            return (AssetAccessor<SkinnedMesh>) meshAssetAccessor;
        }
        return Meshes.BOOTS;
    }
}