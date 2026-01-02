package com.merlin204.avalon.entity.client.renderer.patch.item;

import com.google.gson.JsonElement;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class AbstractRenderAnimationItem extends RenderItemBase {
    public AbstractRenderAnimationItem(JsonElement jsonElement) {
        super(jsonElement);
    }

    public void renderAnimationItem(LivingEntityPatch<?> entityPatch,OpenMatrix4f[] poses, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks ){

    }

    protected void setArmaturePose(LivingEntityPatch<?> entityPatch, Armature armature, float partialTicks) {
        Pose pose = entityPatch.getAnimator().getPose(partialTicks);
        armature.setPose(pose);
    }



    @Override
    public void renderItemInHand(ItemStack stack, LivingEntityPatch<?> entityPatch, InteractionHand hand, OpenMatrix4f[] poses, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks) {

    }
}
