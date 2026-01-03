package com.merlin204.avalon.entity.example;

import com.merlin204.avalon.entity.IAvalonMeshEntity;
import com.merlin204.avalon.main.AvalonMOD;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.gameasset.Armatures;

public class TestEntity extends Mob implements IAvalonMeshEntity {
    public TestEntity(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    @Nullable
    @Override
    public ResourceLocation getTexture() {
        return ResourceLocation.fromNamespaceAndPath(AvalonMOD.MOD_ID,"textures/merlin.png");
    }

    @Nullable
    @Override
    public Armature getArmature() {
        return Armatures.BIPED.get();
    }

    @Nullable
    @Override
    public AssetAccessor<? extends SkinnedMesh> getMesh() {
        return Meshes.BIPED;
    }



}
