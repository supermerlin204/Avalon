package com.merlin204.avalon.client.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.Mesh;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.EntitySnapshot;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.QuaternionUtils;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.events.engine.RenderEngine;
import yesman.epicfight.client.particle.CustomModelParticle;
import yesman.epicfight.client.particle.EpicFightParticleRenderTypes;
import yesman.epicfight.client.renderer.EpicFightRenderTypes;
import yesman.epicfight.client.renderer.patched.entity.PatchedEntityRenderer;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class AvalonEntityAfterImageParticle extends CustomModelParticle<SkinnedMesh> {
    protected final EntitySnapshot<?> entitySnapshot;
    protected float alphaO;
    public AvalonEntityAfterImageParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd,EntitySnapshot<?> entitySnapshot ) {
        super(level, x, y, z, xd, yd, zd, null);

        this.lifetime = 20;
        this.rCol = 1.0F;
        this.gCol = 1.0F;
        this.bCol = 1.0F;
        this.alphaO = 0.3F;
        this.alpha = 0.3F;
        this.entitySnapshot = entitySnapshot;
        this.yawO = entitySnapshot.getYRot();
        this.yaw = entitySnapshot.getYRot();
    }

    @Override
    public void tick() {
        super.tick();

        this.alphaO = this.alpha;
        this.alpha = (float)(this.lifetime - this.age) / (float)this.lifetime * 0.8F;
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTicks) {
        float alpha = (this.alphaO + (this.alpha - this.alphaO) * partialTicks);
        int lightColor = this.getLightColor(partialTicks);
        PoseStack poseStack = new PoseStack();
        this.setupPoseStack(poseStack, camera, partialTicks);
        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        this.entitySnapshot.renderTextured(poseStack, buffers, EpicFightRenderTypes::entityAfterimageStencil, Mesh.DrawingFunction.POSITION_TEX, 0, 0.0F, 0.0F, 0.0F, 1.0F);
        this.entitySnapshot.renderItems(poseStack, buffers, EpicFightRenderTypes.itemAfterimageStencil(), Mesh.DrawingFunction.POSITION_TEX, lightColor, 1.0F);
        buffers.endLastBatch();
        this.entitySnapshot.renderTextured(poseStack, buffers, EpicFightRenderTypes::entityAfterimageTranslucent, Mesh.DrawingFunction.NEW_ENTITY, lightColor, this.rCol, this.gCol, this.bCol, alpha);
        this.entitySnapshot.renderItems(poseStack, buffers, EpicFightRenderTypes.itemAfterimageTranslucent(), Mesh.DrawingFunction.NEW_ENTITY, lightColor, alpha);
        buffers.endLastBatch();
        this.revert(poseStack);


    }


    protected void setupPoseStack(PoseStack poseStack, Camera camera, float partialTicks) {
        //TODO 待修复
    }

    protected void revert(PoseStack poseStack) {
        //TODO 待修复
    }

    @Override
    public ParticleRenderType getRenderType() {
        return EpicFightParticleRenderTypes.ENTITY_PARTICLE;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        @SuppressWarnings({ "rawtypes", "unchecked" })
        @Override
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            Entity entity = level.getEntity((int)Double.doubleToLongBits(xSpeed));
            LivingEntityPatch<?> entitypatch = EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);

            if (entitypatch != null && RenderEngine.getInstance().hasRendererFor(entitypatch.getOriginal())) {
                EntitySnapshot<?> entitySnapshot = entitypatch.captureEntitySnapshot();
                if (entitySnapshot != null){
                    return  new AvalonEntityAfterImageParticle(level, x, y, z, xSpeed, ySpeed, zSpeed,entitySnapshot);
                }
            }

            return null;
        }
    }
}
