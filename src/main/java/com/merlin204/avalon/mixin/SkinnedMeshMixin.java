package com.merlin204.avalon.mixin;


import com.merlin204.avalon.item.animationitem.IAvalonAnimationItem;
import com.merlin204.avalon.main.AvalonMOD;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.client.model.Mesh;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;

import javax.annotation.Nullable;

@Mixin(SkinnedMesh.class)
public abstract class SkinnedMeshMixin {

    @Shadow
    public abstract void draw(PoseStack poseStack, MultiBufferSource bufferSources, RenderType renderType, Mesh.DrawingFunction drawingFunction, int packedLight, float r, float g, float b, float a, int overlay, @Nullable Armature armature, OpenMatrix4f[] poses);
    /**
     * 人模就应该用人骨！！！
     */
    @Inject(method = "draw(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/RenderType;IFFFFILyesman/epicfight/api/model/Armature;[Lyesman/epicfight/api/utils/math/OpenMatrix4f;)V",
            at = @At("HEAD"),
            cancellable = true, remap = false)
    private void avalon$draw(PoseStack poseStack, MultiBufferSource bufferSources, RenderType renderType, int packedLight, float r, float g, float b, float a, int overlay, Armature armature, OpenMatrix4f[] poses, CallbackInfo ci){
        if ((Object) this instanceof HumanoidMesh){
//            if (!(armature instanceof HumanoidArmature)){
//                ci.cancel();
//                return;
//            }
            if (AvalonMOD.beMerlin){
                HumanoidMesh mesh = Meshes.BIPED.get();
                if (!isRenderingInThirdPerson()){
                    mesh.head.setHidden(true);
                    mesh.hat.setHidden(true);
                }
                mesh.draw(poseStack, bufferSources, RenderType.entityTranslucent(ResourceLocation.fromNamespaceAndPath(AvalonMOD.MOD_ID,"textures/merlin.png")), Mesh.DrawingFunction.NEW_ENTITY, packedLight, r, g, b, a, overlay, armature, poses);
                mesh.draw(poseStack, bufferSources, RenderType.entityTranslucentEmissive(ResourceLocation.fromNamespaceAndPath(AvalonMOD.MOD_ID,"textures/merlin_eyes.png")), Mesh.DrawingFunction.NEW_ENTITY, packedLight, r, g, b, a, overlay, armature, poses);
                ci.cancel();
            }
        }
    }

    private static boolean isRenderingInThirdPerson() {
        CameraType cameraType = Minecraft.getInstance().options.getCameraType();
        return !cameraType.isFirstPerson();
    }

}
