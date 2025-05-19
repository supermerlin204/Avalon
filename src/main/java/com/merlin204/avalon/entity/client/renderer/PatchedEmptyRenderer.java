package com.merlin204.avalon.entity.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.Mesh;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.client.renderer.patched.entity.PHumanoidRenderer;

@OnlyIn(Dist.CLIENT)
public class PatchedEmptyRenderer extends PHumanoidRenderer {


    public PatchedEmptyRenderer(Meshes.MeshAccessor<? extends Mesh> mesh, EntityRendererProvider.Context context, EntityType<?> entityType) {
        super(mesh,context, entityType);

    }



}