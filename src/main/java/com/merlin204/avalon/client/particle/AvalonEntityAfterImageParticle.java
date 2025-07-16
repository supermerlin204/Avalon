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
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.Mesh;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.QuaternionUtils;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.particle.CustomModelParticle;
import yesman.epicfight.client.particle.EpicFightParticleRenderTypes;
import yesman.epicfight.client.renderer.EpicFightRenderTypes;
import yesman.epicfight.client.renderer.patched.entity.PatchedEntityRenderer;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class AvalonEntityAfterImageParticle extends CustomModelParticle<SkinnedMesh> {
    public static final ResourceLocation WHITE = ResourceLocation.fromNamespaceAndPath(EpicFightMod.MODID, "textures/particle/white.png");

    private final OpenMatrix4f[] poseMatrices;
    private final Matrix4f modelMatrix;
    private float alphaO;
    private Armature armature;
    private final Entity entity;

    public AvalonEntityAfterImageParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, AssetAccessor<SkinnedMesh> particleMesh, OpenMatrix4f[] matrices, Matrix4f modelMatrix, Armature armature,Entity entity) {
        super(level, x, y, z, xd, yd, zd, particleMesh);
        this.poseMatrices = matrices;
        this.modelMatrix = modelMatrix;
        this.lifetime = 20;
        this.rCol = 1.0F;
        this.gCol = 1.0F;
        this.bCol = 1.0F;
        this.alphaO = 0.3F;
        this.alpha = 0.3F;
        this.armature = armature;
        this.entity = entity;
    }

    @Override
    public void tick() {
        super.tick();

        this.alphaO = this.alpha;
        this.alpha = (float)(this.lifetime - this.age) / (float)this.lifetime * 0.8F;
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTicks) {
        PoseStack poseStack = new PoseStack();



        poseStack.mulPoseMatrix(RenderSystem.getModelViewMatrix());

        this.setupPoseStack(poseStack, camera, partialTicks);
        poseStack.mulPoseMatrix(this.modelMatrix);

        float alpha = (this.alphaO + (this.alpha - this.alphaO) * partialTicks);
        RenderSystem.setShaderTexture(0, WHITE);
        var bufferSources = Minecraft.getInstance().renderBuffers().bufferSource();
        var renderType = RenderType.entityTranslucent(Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity).getTextureLocation(entity));
        this.particleMeshProvider.get().drawPosed(poseStack, bufferSources.getBuffer(EpicFightRenderTypes.getTriangulated(renderType)), Mesh.DrawingFunction.NEW_ENTITY, this.getLightColor(partialTicks), this.rCol, this.gCol, this.bCol, alpha,
                OverlayTexture.NO_OVERLAY,null , this.poseMatrices);


    }

    @Override
    protected void setupPoseStack(PoseStack poseStack, Camera camera, float partialTicks) {
        Quaternionf rotation = new Quaternionf(0.0F, 0.0F, 0.0F, 1.0F);
        float roll = Mth.lerp(partialTicks, this.oRoll, this.roll);
        float pitch = Mth.lerp(partialTicks, this.pitchO, this.pitch);
        float yaw = Mth.lerp(partialTicks, this.yawO, this.yaw);
        rotation.mul(QuaternionUtils.YP.rotationDegrees(yaw));
        rotation.mul(QuaternionUtils.XP.rotationDegrees(pitch));
        rotation.mul(QuaternionUtils.ZP.rotationDegrees(roll));

        Vec3 vec3 = camera.getPosition();
        float x = (float)(Mth.lerp(partialTicks, this.xo, this.x) - vec3.x());
        float y = (float)(Mth.lerp(partialTicks, this.yo, this.y) - vec3.y());
        float z = (float)(Mth.lerp(partialTicks, this.zo, this.z) - vec3.z());
        float scale = (float)Mth.lerp((double)partialTicks, this.scaleO, this.scale);

        poseStack.translate(x, y, z);
        poseStack.mulPose(rotation);
        poseStack.scale(scale, scale, scale);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return EpicFightParticleRenderTypes.PARTICLE_MODEL_NO_NORMAL;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        @SuppressWarnings({ "rawtypes", "unchecked" })
        @Override
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            Entity entity = level.getEntity((int)Double.doubleToLongBits(xSpeed));
            LivingEntityPatch<?> entitypatch = EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);

            if (entitypatch != null && ClientEngine.getInstance().renderEngine.hasRendererFor(entitypatch.getOriginal())) {
                PatchedEntityRenderer renderer = ClientEngine.getInstance().renderEngine.getEntityRenderer(entitypatch.getOriginal());
                Armature armature = entitypatch.getArmature();
                PoseStack poseStack = new PoseStack();
                renderer.mulPoseStack(poseStack, armature, entitypatch.getOriginal(), entitypatch, 1.0F);

                Pose pose = entitypatch.getAnimator().getPose(1.0F);
                renderer.setJointTransforms(entitypatch, armature, pose, 1.0F);
                OpenMatrix4f[] matrices = armature.getPoseAsTransformMatrix(pose, true);

                AssetAccessor<SkinnedMesh> meshProvider = ClientEngine.getInstance().renderEngine.getEntityRenderer(entitypatch.getOriginal()).getMeshProvider(entitypatch);
                AvalonEntityAfterImageParticle particle = new AvalonEntityAfterImageParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, meshProvider, matrices, poseStack.last().pose(),armature,entity);

                return particle;
            }

            return null;
        }
    }
}
