package com.merlin204.avalon.entity.client.renderer;

import com.google.gson.JsonElement;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class RenderMeshItem extends RenderItemBase {


    private final ResourceLocation texture;
    private final AssetAccessor<? extends SkinnedMesh> mesh_main;
    private final AssetAccessor<? extends SkinnedMesh> mesh_off;

    public RenderMeshItem(JsonElement jsonElement) {
        super(jsonElement);

        if (jsonElement.getAsJsonObject().has("texture")) {

            this.texture = ResourceLocation.parse(jsonElement.getAsJsonObject().get("texture").getAsString());
        } else {
            this.texture = null;
        }

        if (jsonElement.getAsJsonObject().has("mesh_main")) {
            String meshLoc = jsonElement.getAsJsonObject().get("mesh_main").getAsString();
            ResourceLocation resLoc = ResourceLocation.parse(meshLoc);
            this.mesh_main = Meshes.MeshAccessor.create(
                    resLoc.getNamespace(),
                    resLoc.getPath(),
                    loader -> loader.loadSkinnedMesh(SkinnedMesh::new)
            );
        } else {
            this.mesh_main = null;
        }

        if (jsonElement.getAsJsonObject().has("mesh_off")) {
            String meshLoc = jsonElement.getAsJsonObject().get("mesh_off").getAsString();
            ResourceLocation resLoc = ResourceLocation.parse(meshLoc);
            this.mesh_off = Meshes.MeshAccessor.create(
                    resLoc.getNamespace(),
                    resLoc.getPath(),
                    loader -> loader.loadSkinnedMesh(SkinnedMesh::new)
            );
        } else {
            this.mesh_off = null;
        }


    }

    @Override
    public void renderItemInHand(ItemStack stack, LivingEntityPatch<?> entitypatch, InteractionHand hand, OpenMatrix4f[] poses, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks) {

        SkinnedMesh renderMesh = null;
        if (hand == InteractionHand.MAIN_HAND){
            renderMesh = mesh_main.get();
        }else if (hand == InteractionHand.OFF_HAND){
            renderMesh = mesh_off.get();
        }

        Armature armature = entitypatch.getArmature();
        poseStack.pushPose();

        if (renderMesh != null) {
            renderMesh.draw(poseStack, buffer, RenderType.entityTranslucent(texture), packedLight, 1.0F, 1.0F, 1.0F, 1.0F, OverlayTexture.NO_OVERLAY, entitypatch.getArmature(), armature.getPoseMatrices());
        }

        poseStack.popPose();
    }





}
