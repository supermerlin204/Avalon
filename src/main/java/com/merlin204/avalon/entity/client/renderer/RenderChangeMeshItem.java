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
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class RenderChangeMeshItem extends RenderItemBase {

    public final ResourceLocation texture;
    public final AssetAccessor<HumanoidMesh> mesh;
    public final Armatures.ArmatureAccessor<? extends Armature> armatureAccessor;

    public RenderChangeMeshItem(JsonElement jsonElement) {
        super(jsonElement);

        if (jsonElement.getAsJsonObject().has("armature")) {
            String meshLoc = jsonElement.getAsJsonObject().get("armature").getAsString();
            ResourceLocation resLoc = ResourceLocation.parse(meshLoc);
            this.armatureAccessor = Armatures.ArmatureAccessor.create(
                    resLoc.getNamespace(),
                    resLoc.getPath(),
                    Armature::new);
        } else {
            this.armatureAccessor = null;
        }

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
                    loader -> loader.loadSkinnedMesh(HumanoidMesh::new)
            );
        } else {
            this.mesh = null;
        }


    }

    @Override
    public void renderItemInHand(ItemStack stack, LivingEntityPatch<?> entitypatch, InteractionHand hand, OpenMatrix4f[] poses, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks) {



    }

}
