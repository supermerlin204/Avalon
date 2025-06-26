package com.merlin204.avalon.entity.example;

import com.merlin204.avalon.entity.AvalonMeshEntity;
import com.merlin204.avalon.main.AvalonMOD;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.gameasset.Armatures;

public class BOSS extends PathfinderMob implements AvalonMeshEntity {
    protected BOSS(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


    @Nullable
    @Override
    public Armature getArmature() {
        return Armatures.ArmatureAccessor.create(AvalonMOD.MOD_ID, "entity/vfx/shakewave", Armature::new).get();
    }

    @Nullable
    @Override
    public AssetAccessor<? extends SkinnedMesh> getMesh() {
        return Meshes.MeshAccessor.create(AvalonMOD.MOD_ID, "entity/vfx/shakewave", (jsonModelLoader) -> jsonModelLoader.loadSkinnedMesh(SkinnedMesh::new));
    }

    @Nullable
    @Override
    public ResourceLocation getTexture() {
        return ResourceLocation.fromNamespaceAndPath(AvalonMOD.MOD_ID,"textures/entity/shakewave.png");
    }

}
