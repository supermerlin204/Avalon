package com.merlin204.avalon.client.particle;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.Mesh;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.utils.EntitySnapshot;
import yesman.epicfight.api.utils.math.QuaternionUtils;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.particle.CustomModelParticle;
import yesman.epicfight.client.particle.EpicFightParticleRenderTypes;
import yesman.epicfight.client.renderer.EpicFightRenderTypes;
import yesman.epicfight.client.renderer.patched.entity.PatchedEntityRenderer;
import yesman.epicfight.client.renderer.patched.layer.WearableItemLayer;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class AvalonInterpolationEntityAfterImageParticle extends CustomModelParticle<SkinnedMesh> {
    protected final EntitySnapshot<?> entitySnapshot;
    protected final EntitySnapshot.RenderableFigure entityFigure;
    protected final LivingEntityPatch<?> entitypatch;
    protected final List<EntitySnapshot.RenderableFigure> armorMeshes;
    private final AssetAccessor<? extends DynamicAnimation> animation;
    private final float time;
    private final float timeO;
    private final Vec3 pos;
    private final Vec3 posO;
    protected float alphaO;
    public AvalonInterpolationEntityAfterImageParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, EntitySnapshot<?> entitySnapshot, EntitySnapshot.RenderableFigure entityFigure, LivingEntityPatch<?> entitypatch, List<EntitySnapshot.RenderableFigure> armorMeshes, AssetAccessor<? extends DynamicAnimation> animation, float time, float timeO, Vec3 pos, Vec3 posO) {
        super(level, x, y, z, xd, yd, zd, null);
        this.entityFigure = entityFigure;
        this.entitypatch = entitypatch;
        this.armorMeshes = armorMeshes;
        this.animation = animation;
        this.time = time;
        this.timeO = timeO;
        this.pos = pos;
        this.posO = posO;

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

        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        float step = (time - timeO)/20;
        Vec3 posStep = new Vec3(
                pos.subtract(posO).x/20,
                pos.subtract(posO).y/20,
                pos.subtract(posO).z/20
        );
        Vec3 startPos = pos;
        for (float f = timeO;f<time;f = f + step){
            PoseStack poseStack = new PoseStack();
            this.setupPoseStack(poseStack,startPos, camera,partialTicks);
            this.renderTextured(f,poseStack, buffers, EpicFightRenderTypes::entityAfterimageStencil, Mesh.DrawingFunction.POSITION_TEX, 0, 0.0F, 0.0F, 0.0F, 1.0F);
            buffers.endLastBatch();
            this.renderTextured(f,poseStack, buffers, EpicFightRenderTypes::entityAfterimageTranslucent, Mesh.DrawingFunction.NEW_ENTITY, lightColor, this.rCol, this.gCol, this.bCol, alpha);
            buffers.endLastBatch();
            this.revert(poseStack);
            startPos = startPos.add(posStep);
        }



    }




    protected void setupPoseStack(PoseStack poseStack,Vec3 pos, Camera camera, float partialTicks) {
        poseStack.pushPose();
        poseStack.mulPoseMatrix(RenderSystem.getModelViewStack().last().pose());
        RenderSystem.getModelViewStack().pushPose();
        RenderSystem.getModelViewStack().setIdentity();
        RenderSystem.applyModelViewMatrix();
        Vec3 cameraPosition = camera.getPosition();
        float x = (float)(Mth.lerp((double)partialTicks, pos.x, pos.x) - cameraPosition.x());
        float y = (float)(Mth.lerp((double)partialTicks, pos.y, pos.y) - cameraPosition.y());
        float z = (float)(Mth.lerp((double)partialTicks, pos.z, pos.z) - cameraPosition.z());
        poseStack.translate(x, y, z);
        Quaternionf rotation = new Quaternionf(0.0F, 0.0F, 0.0F, 1.0F);
        float roll = Mth.rotLerp(partialTicks, this.oRoll, this.roll);
        float pitch = Mth.rotLerp(partialTicks, this.pitchO, this.pitch);
        float yaw = Mth.rotLerp(partialTicks, this.yawO, this.yaw);
        rotation.mul(QuaternionUtils.YP.rotationDegrees(180.0F - yaw));
        rotation.mul(QuaternionUtils.XP.rotationDegrees(pitch));
        rotation.mul(QuaternionUtils.ZP.rotationDegrees(roll));
        poseStack.mulPose(rotation);
        float scale = Mth.lerp(partialTicks, this.scaleO, this.scale);
        poseStack.translate(0.0F, this.entitySnapshot.getHeightHalf(), 0.0F);
        poseStack.scale(scale, scale, scale);
        poseStack.translate(0.0F, -this.entitySnapshot.getHeightHalf(), 0.0F);

    }

    protected void revert(PoseStack poseStack) {
        poseStack.popPose();
        RenderSystem.getModelViewStack().popPose();
        RenderSystem.applyModelViewMatrix();
    }

    public void renderTextured(float time,PoseStack poseStack, MultiBufferSource buffers, Function<ResourceLocation, RenderType> rendertypeFunction, Mesh.DrawingFunction drawingFunction, int packedLight, float r, float g, float b, float a) {
        if (this.entityFigure.mesh() != null && this.entityFigure.texture() != null) {
            this.entityFigure.mesh().initialize();
            Pose pose =  animation.get().getRawPose(time);
            this.entityFigure.mesh().draw(poseStack, buffers, rendertypeFunction.apply(this.entityFigure.texture()), drawingFunction, packedLight,
                    r, g, b, a, OverlayTexture.NO_OVERLAY, this.entitypatch.getArmature(),this.entitypatch.getArmature().getPoseAsTransformMatrix(pose,false));

            for (EntitySnapshot.RenderableFigure armorFigures : this.armorMeshes) {
                armorFigures.mesh().initialize();
                armorFigures.mesh().draw(poseStack, buffers, rendertypeFunction.apply(armorFigures.texture()), drawingFunction, packedLight,
                        r, g, b, a, OverlayTexture.NO_OVERLAY, this.entitypatch.getArmature(), this.entitypatch.getArmature().getPoseAsTransformMatrix(pose,false));
            }

        }
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
            LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>> vanillarenderer = (LivingEntityRenderer)Minecraft.getInstance().getEntityRenderDispatcher().getRenderer((LivingEntity)entitypatch.getOriginal());
            PatchedEntityRenderer patchedrenderer = ClientEngine.getInstance().renderEngine.getEntityRenderer(entitypatch.getOriginal());
            AssetAccessor<SkinnedMesh> meshAccessor = patchedrenderer.getMeshProvider(entitypatch);
            ResourceLocation textureLocation = vanillarenderer.getTextureLocation(entitypatch.getOriginal());
            EntitySnapshot.RenderableFigure entityFigure = new EntitySnapshot.RenderableFigure(meshAccessor.get(), textureLocation);
            ImmutableList.Builder<EntitySnapshot.RenderableFigure> builder = ImmutableList.builder();
            (entitypatch.getOriginal()).getArmorSlots().forEach((itemstackx) -> {
                if (itemstackx.getItem() instanceof ArmorItem) {
                    EquipmentSlot armorSlot = itemstackx.getEquipmentSlot();
                    SkinnedMesh armor = WearableItemLayer.getCachedModel(itemstackx.getItem());
                    ResourceLocation texture = WearableItemLayer.getArmorResource(entitypatch.getOriginal(), itemstackx, armorSlot, (String)null);
                    if (armor != null) {
                        builder.add(new EntitySnapshot.RenderableFigure(armor, texture));
                    }

                }
            });

            if (ClientEngine.getInstance().renderEngine.hasRendererFor(entitypatch.getOriginal())) {
                EntitySnapshot<?> entitySnapshot = entitypatch.captureEntitySnapshot();
                if (entitySnapshot != null){
                    return  new AvalonInterpolationEntityAfterImageParticle(level, x, y, z, xSpeed, ySpeed, zSpeed,entitySnapshot,entityFigure,entitypatch,builder.build(),
                            entitypatch.getAnimator().getPlayerFor(null).getAnimation(),
                            entitypatch.getAnimator().getPlayerFor(null).getElapsedTime()+entitypatch.getAnimator().getPlayerFor(null).getAnimation().get()
                                    .getPlaySpeed(entitypatch,entitypatch.getAnimator().getPlayerFor(null).getAnimation().get()),
                            entitypatch.getAnimator().getPlayerFor(null).getPrevElapsedTime(),
                            entity.position().add(entity.getDeltaMovement()),
                            new Vec3(entity.xOld,entity.yOld,entity.zOld));
                }
            }

            return null;
        }
    }
}
