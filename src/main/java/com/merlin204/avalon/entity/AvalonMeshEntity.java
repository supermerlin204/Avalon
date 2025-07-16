package com.merlin204.avalon.entity;

import net.minecraft.resources.ResourceLocation;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.model.Armature;

import javax.annotation.Nullable;

public interface AvalonMeshEntity {


    @Nullable
    default Armature getArmature() {
        return null;
    }

    @Nullable
    default AssetAccessor<? extends SkinnedMesh> getMesh() {
        return null;
    }

    @Nullable
    default ResourceLocation getTexture() {
        return null;
    }

    @Nullable
    default ResourceLocation getLitTexture() {
        return null;
    }

    @Nullable
    default AnimationManager.AnimationAccessor<? extends StaticAnimation> getIdleAnimation(){
        return null;
    }


}
