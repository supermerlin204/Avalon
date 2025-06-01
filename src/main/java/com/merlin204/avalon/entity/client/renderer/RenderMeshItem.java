package com.merlin204.avalon.entity.client.renderer;

import com.google.gson.JsonElement;
import com.merlin204.avalon.main.AvalonMOD;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.QuaternionUtils;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class RenderMeshItem extends RenderItemBase {

    private final ResourceLocation texture;
    private final AssetAccessor<? extends SkinnedMesh> mesh;

    public RenderMeshItem(JsonElement jsonElement) {
        super(jsonElement);

        if (jsonElement.getAsJsonObject().has("texture")) {

            this.texture = ResourceLocation.parse(jsonElement.getAsJsonObject().get("texture").getAsString());
        } else {
            this.texture = null;
        }

        if (jsonElement.getAsJsonObject().has("mesh")) {
            String meshLoc = jsonElement.getAsJsonObject().get("mesh").getAsString();
            ResourceLocation resLoc = ResourceLocation.parse(meshLoc);
            this.mesh = Meshes.MeshAccessor.create(
                    resLoc.getNamespace(),
                    resLoc.getPath(),
                    loader -> loader.loadSkinnedMesh(SkinnedMesh::new)
            );
        } else {
            this.mesh = null;
        }


    }

    @Override
    public void renderItemInHand(ItemStack stack, LivingEntityPatch<?> entitypatch, InteractionHand hand, OpenMatrix4f[] poses, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks) {


        Armature armature = entitypatch.getArmature();
        poseStack.pushPose();

        mesh.get().draw(poseStack, buffer, RenderType.entityTranslucent(texture), packedLight, 1.0F, 1.0F, 1.0F, 1.0F, OverlayTexture.NO_OVERLAY, entitypatch.getArmature(), armature.getPoseMatrices());

        poseStack.popPose();
    }





}
