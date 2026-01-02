package com.merlin204.avalon.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;



public class MeshBlockEntity extends BlockEntity {
    public MeshBlockEntity(BlockPos pos, BlockState state) {
        super(AvalonBlocks.TEST_DOOR_ENTITY.get(), pos, state);
    }



}
