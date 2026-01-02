package com.merlin204.avalon.block;

import com.merlin204.avalon.block.testdoor.TestDoor;
import com.merlin204.avalon.main.AvalonMOD;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AvalonBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, AvalonMOD.MOD_ID);
    public static final RegistryObject<Block> TEST_MESH_BLOCK = BLOCKS.register("test_mesh_block",
            () -> new MeshBlock(BlockBehaviour.Properties.of()));

    public static final RegistryObject<Block> TEST_DOOR = BLOCKS.register("test_door",
            () -> new TestDoor(BlockBehaviour.Properties.of()));

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, AvalonMOD.MOD_ID);

    public static final RegistryObject<BlockEntityType<MeshBlockEntity>> MESH_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("mesh_block_entity",
                    () -> BlockEntityType.Builder.of(MeshBlockEntity::new, AvalonBlocks.TEST_MESH_BLOCK.get()).build(null));

    public static final RegistryObject<BlockEntityType<MeshBlockEntity>> TEST_DOOR_ENTITY =
            BLOCK_ENTITIES.register("test_door_entity",
                    () -> BlockEntityType.Builder.of(MeshBlockEntity::new, AvalonBlocks.TEST_DOOR.get()).build(null));
}
