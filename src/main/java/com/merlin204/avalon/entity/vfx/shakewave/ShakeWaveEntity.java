package com.merlin204.avalon.entity.vfx.shakewave;

import com.merlin204.avalon.entity.AvalonEntities;
import com.merlin204.avalon.entity.AvalonMeshEntity;
import com.merlin204.avalon.entity.vfx.VFXEntity;
import com.merlin204.avalon.epicfight.gameassets.animations.VFXAnimations;
import com.merlin204.avalon.main.AvalonMOD;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.gameasset.Armatures;

public class ShakeWaveEntity extends VFXEntity {
    public ShakeWaveEntity(LivingEntity owner,float scale) {
        super(AvalonEntities.SHAKE_WAVE.get(), owner, scale);
        this.noPhysics = false;
        setNoGravity(false);
    }
    public ShakeWaveEntity(EntityType<? extends VFXEntity> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = false;
        setNoGravity(false);
    }

    @Nullable
    @Override
    public AnimationManager.AnimationAccessor<? extends StaticAnimation> getIdleAnimation() {
        return VFXAnimations.SHAKEWAVE_IDLE;
    }

    @Override
    public @Nullable AnimationManager.AnimationAccessor<? extends StaticAnimation> getDefaultAnimation() {
        return VFXAnimations.SHAKEWAVE_1;
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
