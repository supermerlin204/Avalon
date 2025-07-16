package com.merlin204.avalon.entity.client.renderer;

import com.merlin204.avalon.entity.AvalonMeshEntity;
import com.merlin204.avalon.entity.client.model.EmptyEntityModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class EmptyRenderer extends LivingEntityRenderer {
    public EmptyRenderer(EntityRendererProvider.Context pContext, EntityModel pModel, float pShadowRadius) {
        super(pContext, pModel, pShadowRadius);
    }

    public EmptyRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new  EmptyEntityModel(), 0);
    }

    @Override
    public boolean shouldRender(Entity pLivingEntity, Frustum pCamera, double pCamX, double pCamY, double pCamZ) {
        return pLivingEntity.tickCount > 5;
    }

    @Override
    public ResourceLocation getTextureLocation(Entity entity) {
        if (entity instanceof AvalonMeshEntity avalonMeshEntity){
            return avalonMeshEntity.getTexture();
        }
        return null;

    }
}
