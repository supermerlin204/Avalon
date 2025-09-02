package com.merlin204.avalon.block;

import com.google.common.collect.ImmutableMap;
import com.merlin204.avalon.block.client.MeshBlockEntityRender;
import com.merlin204.avalon.main.AvalonMOD;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.*;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.LayerOffAnimation;
import yesman.epicfight.api.animation.types.LinkAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.api.client.animation.Layer;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.client.physics.cloth.ClothSimulatable;
import yesman.epicfight.api.client.physics.cloth.ClothSimulator;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.physics.PhysicsSimulator;
import yesman.epicfight.api.physics.SimulatableObject;
import yesman.epicfight.api.physics.SimulationTypes;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.main.EpicFightSharedConstants;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.StunType;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class MeshBlock extends Block implements EntityBlock {

    private float yRotO = 180.0F;
    private float yRot = 180.0F;
    public final AssetAccessor<? extends StaticAnimation> animation = Animations.BIPED_WALK;

    @OnlyIn(Dist.CLIENT)
    public MeshBlock.MeshBlockEntityPatch entitypatch;
    @OnlyIn(Dist.CLIENT)
    public MeshBlock.MeshBlockEntityAnimator animator;

    private boolean started = false;

    private ResourceLocation TEST = ResourceLocation.fromNamespaceAndPath(AvalonMOD.MOD_ID,"textures/all_black.png");

    public MeshBlock(Properties pProperties) {
        super(BlockBehaviour.Properties.of()
                .noOcclusion()
                .noLootTable()
                .isViewBlocking((state, world, pos) -> false)
        );
    }

    public AssetAccessor<? extends SkinnedMesh> getMesh() {
        return Meshes.SKELETON;
    }

    public AssetAccessor<? extends Armature> getArmature() {
        return Armatures.BIPED;
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new MeshBlockEntity(blockPos,blockState);
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {

        return (level1, pos, state1, blockEntity) -> {
            if (blockEntity instanceof MeshBlockEntity meshEntity) {
                _tick();
            }
        };
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected ImmutableMap<BlockState, VoxelShape> getShapeForEachState(Function<BlockState, VoxelShape> pShapeGetter) {
        return super.getShapeForEachState(pShapeGetter);
    }


    @OnlyIn(Dist.CLIENT)
    public void _tick() {
        if (getArmature() != null && !started) {
            started = true;
            this.entitypatch = new MeshBlockEntityPatch(getArmature().get());
            this.animator = new MeshBlockEntityAnimator(this.entitypatch);
            this.entitypatch.initAnimator(this.animator);
        }
        if (this.animator != null) {
            this.animator.tick();
        }
    }


    @OnlyIn(Dist.CLIENT)
    public void drawMesh(PoseStack poseStack, MultiBufferSource bufferSource, float partialTicks){
        Pose pose = this.animator.getPose(partialTicks);
        this.getMesh().get().initialize();
        poseStack.translate(0.5,0,0.5);
        OpenMatrix4f[] poseMatrices = this.entitypatch.getArmature().getPoseAsTransformMatrix(pose, false);

        getMesh().get().draw(poseStack,bufferSource, RenderType.entityTranslucent(TEST), LightTexture.FULL_BRIGHT,1.0F,1.0F,1.0F,1.0F, OverlayTexture.NO_OVERLAY,this.entitypatch.getArmature(),poseMatrices);

    }



    @OnlyIn(Dist.CLIENT)
    public class MeshBlockEntityPatch extends LivingEntityPatch<LivingEntity> implements SimulatableObject, ClothSimulatable {
        public MeshBlockEntityPatch(Armature armature) {
            super();
            this.armature = armature.deepCopy();
        }

        public void setAnimator() {
            this.animator = MeshBlock.this.animator;
        }

        public void initAnimator(Animator animator) {
            this.animator = animator;
        }

        public void updateMotion(boolean considerInaction) {
        }

        public AssetAccessor<? extends StaticAnimation> getHitAnimation(StunType stunType) {
            return null;
        }

        public boolean isLogicalClient() {
            return true;
        }


        public OpenMatrix4f getModelMatrix(float partialTicks) {
            return MathUtils.getModelMatrixIntegral(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, partialTicks, 1.0F, 1.0F, 1.0F);
        }

        public void poseTick(DynamicAnimation animation, Pose pose, float time, float partialTicks) {
        }

        public void updateEntityState() {
        }

        public boolean invalid() {
            return false;
        }

        public Vec3 getObjectVelocity() {
            return Vec3.ZERO;
        }

        public Vec3 getAccurateCloakLocation(float partialFrame) {
            return Vec3.ZERO;
        }

        public Vec3 getAccuratePartialLocation(float partialFrame) {
            return Vec3.ZERO;
        }

        public float getAccurateYRot(float partialFrame) {
            return MeshBlock.this.yRot;
        }

        public float getYRotDelta(float partialFrame) {
            return MeshBlock.this.yRot - MeshBlock.this.yRotO;
        }

        public float getYRot() {
            return MeshBlock.this.yRot;
        }

        public float getYRotO() {
            return MeshBlock.this.yRotO;
        }

        public <SIM extends PhysicsSimulator<?, ?, ?, ?, ?>> Optional<SIM> getSimulator(SimulationTypes<?, ?, ?, ?, ?, SIM> simulationType) {
            return Optional.empty();
        }

        public float getScale() {
            return 1.0F;
        }

        public Animator getSimulatableAnimator() {
            return this.animator;
        }

        public float getGravity() {
            return 9.8F;
        }

        public ClothSimulator getClothSimulator() {
            return null;
        }

        public Faction getFaction() {
            return null;
        }

    }

    @OnlyIn(Dist.CLIENT)
    public class MeshBlockEntityAnimator extends ClientAnimator {
        public MeshBlockEntityAnimator(LivingEntityPatch<?> entitypatch) {
            super(entitypatch, MeshBlockEntityBaseLayer::new);
        }

        @Override
        public void tick() {
            this.baseLayer.update(this.entitypatch);

        }

        public LivingEntityPatch<?> getEntityPatch() {
            return this.entitypatch;
        }

        @OnlyIn(Dist.CLIENT)
        static class NoEntityAnimationPlayer extends AnimationPlayer {
            @Override
            public void tick(LivingEntityPatch<?> entitypatch) {
                DynamicAnimation nowPlay = this.getAnimation().get();

                float tickDelta = EpicFightSharedConstants.A_TICK;

                this.prevElapsedTime = this.elapsedTime;
                this.elapsedTime += tickDelta;

                if (this.elapsedTime >= nowPlay.getTotalTime()) {
                    if (nowPlay.isRepeat()) {
                        this.elapsedTime %= nowPlay.getTotalTime();
                        this.prevElapsedTime = 0;
                    } else {
                        this.elapsedTime = nowPlay.getTotalTime();
                        this.isEnd = true;
                    }
                }
            }

            @Override
            public Pose getCurrentPose(LivingEntityPatch<?> entitypatch, float partialTicks) {
                float interpolatedTime = this.prevElapsedTime + (this.elapsedTime - this.prevElapsedTime) * partialTicks;
                return this.play.get().getRawPose(interpolatedTime);
            }

            @Override
            public void begin(AssetAccessor<? extends DynamicAnimation> animation, LivingEntityPatch<?> entitypatch) {
            }


        }


        @OnlyIn(Dist.CLIENT)
        static class MeshBlockEntityLayer extends Layer {
            public MeshBlockEntityLayer(Priority priority) {
                super(priority, NoEntityAnimationPlayer::new);
            }

            public void playAnimation(AssetAccessor<? extends StaticAnimation> nextAnimation, LivingEntityPatch<?> entitypatch, float convertTimeModifier) {
                Pose lastPose = entitypatch.getAnimator().getPose(1.0F);
                this.resume();

                if (!nextAnimation.get().isMetaAnimation()) {
                    this.setLinkAnimation(nextAnimation, entitypatch, lastPose, convertTimeModifier);
                    this.linkAnimation.putOnPlayer(this.animationPlayer, entitypatch);
                    this.nextAnimation = nextAnimation;
                }
            }

            @Override
            public void playAnimationInstantly(AssetAccessor<? extends DynamicAnimation> nextAnimation, LivingEntityPatch<?> entitypatch) {
                this.resume();
                nextAnimation.get().putOnPlayer(this.animationPlayer, entitypatch);
                this.nextAnimation = null;
            }

            @Override
            protected void setLinkAnimation(AssetAccessor<? extends StaticAnimation> nextAnimation, LivingEntityPatch<?> entitypatch, Pose lastPose, float convertTimeModifier) {
                Pose currentPose = this.animationPlayer.getAnimation().get().getRawPose(this.animationPlayer.getElapsedTime());
                Pose nextAnimationPose = nextAnimation.get().getRawPose(0.0F);
                float totalTime = nextAnimation.get().getTransitionTime();

                AssetAccessor<? extends DynamicAnimation> fromAnimation = this.animationPlayer.isEmpty() ? entitypatch.getClientAnimator().baseLayer.animationPlayer.getAnimation() : this.animationPlayer.getAnimation();

                if (fromAnimation instanceof LinkAnimation linkAnimation) {
                    fromAnimation = linkAnimation.getFromAnimation();
                }

                this.linkAnimation.getTransfroms().clear();
                this.linkAnimation.setTotalTime(totalTime);
                this.linkAnimation.setConnectedAnimations(fromAnimation, nextAnimation);

                Map<String, JointTransform> data1 = currentPose.getJointTransformData();
                Map<String, JointTransform> data2 = nextAnimationPose.getJointTransformData();

                for (String jointName : data1.keySet()) {
                    if (data1.containsKey(jointName) && data2.containsKey(jointName)) {
                        Keyframe[] keyframes = new Keyframe[2];
                        keyframes[0] = new Keyframe(0.0F, data1.get(jointName));
                        keyframes[1] = new Keyframe(totalTime, data2.get(jointName));
                        TransformSheet sheet = new TransformSheet(keyframes);
                        this.linkAnimation.getAnimationClip().addJointTransform(jointName, sheet);
                    }
                }

                this.animationPlayer.setPlayAnimation(this.linkAnimation);
            }

            public void update(LivingEntityPatch<?> entitypatch) {


                this.animationPlayer.tick(entitypatch);


                if ( this.animationPlayer.isEnd()) {
                    if (this.nextAnimation != null) {
                        this.nextAnimation.get().putOnPlayer(this.animationPlayer, entitypatch);
                        this.nextAnimation = null;
                    } else {
                        if (this.animationPlayer.getAnimation() instanceof LayerOffAnimation) {
                            this.animationPlayer.getAnimation().get().end(entitypatch, Animations.EMPTY_ANIMATION, true);
                        } else {
                            this.off(entitypatch);
                        }
                    }
                }
            }

            public Pose getEnabledPose(LivingEntityPatch<?> entitypatch, float partialTick) {
                DynamicAnimation animation = this.animationPlayer.getAnimation().get();
                Pose pose = animation.getRawPose(this.animationPlayer.getPrevElapsedTime() + (this.animationPlayer.getElapsedTime() - this.animationPlayer.getPrevElapsedTime()) * partialTick);
                pose.disableJoint((entry) -> !animation.hasTransformFor(entry.getKey()));

                return pose;
            }

            public void off(LivingEntityPatch<?> entitypatch) {
                if (!this.isDisabled() && !(this.animationPlayer.getAnimation() instanceof LayerOffAnimation)) {
                    float convertTime = entitypatch.getClientAnimator().baseLayer.animationPlayer.getAnimation().get().getTransitionTime();
                    setLayerOffAnimation(this.animationPlayer.getAnimation(), this.getEnabledPose(entitypatch, 1.0F), this.layerOffAnimation, convertTime);
                    this.playAnimationInstantly(this.layerOffAnimation, entitypatch);
                }
            }
        }

        @OnlyIn(Dist.CLIENT)
        static class MeshBlockEntityBaseLayer extends Layer.BaseLayer {
            public MeshBlockEntityBaseLayer() {
                super(NoEntityAnimationPlayer::new);

                this.compositeLayers.clear();

                for (Priority priority : Priority.values()) {
                    this.compositeLayers.computeIfAbsent(priority, MeshBlockEntityLayer::new);
                }

                this.baseLayerPriority = Priority.LOWEST;
            }

            @Override
            public void playAnimation(AssetAccessor<? extends StaticAnimation> nextAnimation, LivingEntityPatch<?> entitypatch, float convertTimeModifier) {
                Priority priority = nextAnimation.get().getPriority();
                this.baseLayerPriority = priority;
                this.offCompositeLayersLowerThan(entitypatch, nextAnimation);

                Pose lastPose = entitypatch.getAnimator().getPose(1.0F);
                this.resume();

                if (!nextAnimation.get().isMetaAnimation()) {
                    this.setLinkAnimation(nextAnimation, entitypatch, lastPose, convertTimeModifier);
                    this.linkAnimation.putOnPlayer(this.animationPlayer, entitypatch);
                    entitypatch.updateEntityState();
                    this.nextAnimation = nextAnimation;
                }
            }

            @Override
            public void playAnimationInstantly(AssetAccessor<? extends DynamicAnimation> nextAnimation, LivingEntityPatch<?> entitypatch) {
                this.resume();
                nextAnimation.get().putOnPlayer(this.animationPlayer, entitypatch);
                this.nextAnimation = null;
            }

            @Override
            protected void playLivingAnimation(AssetAccessor<? extends StaticAnimation> nextAnimation, LivingEntityPatch<?> entitypatch) {
                this.resume();

                if (!nextAnimation.get().isMetaAnimation()) {
                    this.concurrentLinkAnimation.acceptFrom(this.animationPlayer.getAnimation().get().getRealAnimation(), nextAnimation, this.animationPlayer.getElapsedTime());
                    this.concurrentLinkAnimation.putOnPlayer(this.animationPlayer, entitypatch);
                    this.nextAnimation = nextAnimation;
                }
            }

            @Override
            public void update(LivingEntityPatch<?> entitypatch) {

                this.animationPlayer.tick(entitypatch);


                if (!this.paused && this.animationPlayer.isEnd()) {
                    if (this.nextAnimation != null) {
                        this.nextAnimation.get().putOnPlayer(this.animationPlayer, entitypatch);
                        this.nextAnimation = null;
                    } else {
                        if (this.animationPlayer.getAnimation() instanceof LayerOffAnimation) {
                            this.animationPlayer.getAnimation().get().end(entitypatch, Animations.EMPTY_ANIMATION, true);
                        } else {
                            this.off(entitypatch);
                        }
                    }
                }

                for (Layer layer : this.compositeLayers.values()) {
                    layer.update(entitypatch);
                }
            }

            @Override
            protected void setLinkAnimation(AssetAccessor<? extends StaticAnimation> nextAnimation, LivingEntityPatch<?> entitypatch, Pose lastPose, float convertTimeModifier) {
                Pose currentPose = this.animationPlayer.getAnimation().get().getRawPose(this.animationPlayer.getElapsedTime());
                Pose nextAnimationPose = nextAnimation.get().getRawPose(0.0F);
                float totalTime = nextAnimation.get().getTransitionTime();

                AssetAccessor<? extends DynamicAnimation> fromAnimation = this.animationPlayer.isEmpty() ? entitypatch.getClientAnimator().baseLayer.animationPlayer.getAnimation() : this.animationPlayer.getAnimation();

                if (fromAnimation instanceof LinkAnimation linkAnimation) {
                    fromAnimation = linkAnimation.getFromAnimation();
                }

                this.linkAnimation.getTransfroms().clear();
                this.linkAnimation.setTotalTime(totalTime);
                this.linkAnimation.setConnectedAnimations(fromAnimation, nextAnimation);

                Map<String, JointTransform> data1 = currentPose.getJointTransformData();
                Map<String, JointTransform> data2 = nextAnimationPose.getJointTransformData();

                for (String jointName : data1.keySet()) {
                    if (data1.containsKey(jointName) && data2.containsKey(jointName)) {
                        Keyframe[] keyframes = new Keyframe[2];
                        keyframes[0] = new Keyframe(0.0F, data1.get(jointName));
                        keyframes[1] = new Keyframe(totalTime, data2.get(jointName));
                        TransformSheet sheet = new TransformSheet(keyframes);
                        this.linkAnimation.getAnimationClip().addJointTransform(jointName, sheet);
                    }
                }

                this.animationPlayer.setPlayAnimation(this.linkAnimation);
            }

            public void offCompositeLayerLowerThan(LivingEntityPatch<?> entitypatch, StaticAnimation nextAnimation) {
                for (Priority p : nextAnimation.getPriority().lowersAndEqual()) {
                    if (p == Priority.LOWEST && !nextAnimation.isMainFrameAnimation()) {
                        continue;
                    }

                    this.compositeLayers.get(p).off(entitypatch);
                }
            }

            public Layer getLayer(Priority priority) {
                return this.compositeLayers.get(priority);
            }

            @Override
            public void off(LivingEntityPatch<?> entitypatch) {
            }

            @Override
            protected boolean isDisabled() {
                return false;
            }

            @Override
            protected boolean isBaseLayer() {
                return true;
            }
        }

    }
}
