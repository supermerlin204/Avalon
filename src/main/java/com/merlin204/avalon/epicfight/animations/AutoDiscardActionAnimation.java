package com.merlin204.avalon.epicfight.animations;

import org.antlr.v4.codegen.model.Action;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class AutoDiscardActionAnimation extends ActionAnimation {

    private final float play_speed;
    public AutoDiscardActionAnimation(float transitionTime, AnimationManager.AnimationAccessor<? extends ActionAnimation> accessor, AssetAccessor<? extends Armature> armature, float playSpeed) {
        super(transitionTime, accessor, armature);
        play_speed = playSpeed;
    }

    public AutoDiscardActionAnimation(float transitionTime, float postDelay, AnimationManager.AnimationAccessor<? extends ActionAnimation> accessor, AssetAccessor<? extends Armature> armature, float playSpeed) {
        super(transitionTime, postDelay, accessor, armature);
        play_speed = playSpeed;
    }

    public AutoDiscardActionAnimation(float transitionTime, float postDelay, String path, AssetAccessor<? extends Armature> armature, float playSpeed) {
        super(transitionTime, postDelay, path, armature);
        play_speed = playSpeed;
    }



    @Override
    public void end(LivingEntityPatch<?> entityPatch, AssetAccessor<? extends DynamicAnimation> nextAnimation, boolean isEnd) {
        super.end(entityPatch, nextAnimation, isEnd);
        if(!entityPatch.isLogicalClient()){
            entityPatch.getOriginal().discard();
        }
    }

    @Override
    public float getPlaySpeed(LivingEntityPatch<?> entitypatch, DynamicAnimation animation) {
        return play_speed;
    }
}
