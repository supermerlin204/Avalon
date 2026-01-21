package com.merlin204.avalon.entity.api.patch;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.animation.property.TrailInfo;
import yesman.epicfight.api.utils.math.OpenMatrix4f;

public interface IAvalonPatch {



    default TrailInfo modifierTrailInfo(TrailInfo oldInfo){
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    default void extraRender(OpenMatrix4f[] poses, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks){

    }

}
