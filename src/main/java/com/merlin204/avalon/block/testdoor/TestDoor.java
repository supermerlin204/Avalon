package com.merlin204.avalon.block.testdoor;

import com.merlin204.avalon.block.MeshBlock;
import com.merlin204.avalon.epicfight.gameassets.animations.VFXAnimations;
import com.merlin204.avalon.main.AvalonMOD;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3f;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.gameasset.Armatures;

public class TestDoor extends MeshBlock {

    private ResourceLocation TEST = ResourceLocation.fromNamespaceAndPath(AvalonMOD.MOD_ID,"textures/testdoor.png");
    // 定义碰撞箱尺寸（单位：像素，1方块=16像素）
    private static final VoxelShape COLLISION_SHAPE_NORTH_SOUTH = Block.box(
            0, 0, 6,
            48, 48, 10
    );

    private static final VoxelShape COLLISION_SHAPE_EAST_WEST = Block.box(
            6, 0, 0,
            10, 48, 48
    );

    // 方块状态属性 - 朝向
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    // 方块状态属性 - 是否打开
    public static final BooleanProperty OPEN = BooleanProperty.create("open");

    public TestDoor(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(OPEN, false));
    }

    @Override
    public AssetAccessor<? extends SkinnedMesh> getMesh() {
        return Meshes.MeshAccessor.create(AvalonMOD.MOD_ID, "testdoor", (jsonModelLoader) -> jsonModelLoader.loadSkinnedMesh(SkinnedMesh::new));
    }

    @Override
    public AssetAccessor<? extends Armature> getArmature() {
        return Armatures.ArmatureAccessor.create(AvalonMOD.MOD_ID, "testdoor", Armature::new);
    }

    // 注册方块状态属性
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN);
        super.createBlockStateDefinition(builder);
    }

    // 获取放置时的状态
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(OPEN, false);
    }

    // 获取碰撞箱形状（根据朝向）
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);

        if (facing == Direction.NORTH || facing == Direction.SOUTH) {
            return COLLISION_SHAPE_NORTH_SOUTH;
        } else {
            return COLLISION_SHAPE_EAST_WEST;
        }
    }

    // 玩家交互方法
    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!world.isClientSide) {
            // 检查玩家是否背对门
            boolean isPlayerFacingAway = isPlayerFacingAway(player, state.getValue(FACING));

            if (isPlayerFacingAway) {

                world.playSound(null, pos, SoundEvents.IRON_DOOR_OPEN, SoundSource.BLOCKS, 1.0F, 1.0F);
                world.setBlock(pos, state.setValue(OPEN, true), 3);
                this.animator.playAnimation(VFXAnimations.OPEN_TEST_DOOR,0.0F);
                return InteractionResult.SUCCESS;
            } else {
                player.displayClientMessage(Component.literal("门无法从这一侧打开"),true);


                return InteractionResult.CONSUME;
            }
        }

        return InteractionResult.SUCCESS;
    }

    // 检查玩家是否背对门
    private boolean isPlayerFacingAway(Player player, Direction doorFacing) {
        // 获取玩家面向方向（转换为水平方向）
        Direction playerFacing = player.getDirection();

        // 计算玩家与门方向的夹角
        return playerFacing == doorFacing;
    }


    @Override
    public void drawMesh(PoseStack poseStack, MultiBufferSource bufferSource, float partialTicks) {
        Pose pose = this.animator.getPose(partialTicks);

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);

        OpenMatrix4f[] poseMatrices = this.entitypatch.getArmature().getPoseAsTransformMatrix(pose, false);

        getMesh().get().draw(
                poseStack,
                bufferSource,
                RenderType.entityTranslucent(TEST),
                LightTexture.FULL_BRIGHT,
                1.0F, 1.0F, 1.0F, 1.0F,
                OverlayTexture.NO_OVERLAY,
                this.entitypatch.getArmature(),
                poseMatrices
        );

        poseStack.popPose();
    }

    // 根据朝向获取旋转角度
    private float getYRotationFromFacing(Direction facing) {
        return switch (facing) {
            case EAST -> 90f;
            case SOUTH -> 180f;
            case WEST -> 270f;
            default -> 0f; // NORTH
        };
    }
}