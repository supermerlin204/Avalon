package com.merlin204.avalon.epicfight.animations;


import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.BasicAttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;


public class AutoDiscardAttackAnimation extends AvalonAttackAnimation {


    public AutoDiscardAttackAnimation(float transitionTime, float antic, float preDelay, float contact, float recovery, @Nullable Collider collider, Joint colliderJoint, AnimationManager.AnimationAccessor<? extends BasicAttackAnimation> accessor, AssetAccessor<? extends Armature> armature, float play_speed, float damageMulti) {
        super(transitionTime, antic, preDelay, contact, recovery, collider, colliderJoint, accessor, armature, play_speed, damageMulti);
    }

    public AutoDiscardAttackAnimation(float transitionTime, AnimationManager.AnimationAccessor<? extends BasicAttackAnimation> accessor, AssetAccessor<? extends Armature> armature, float play_speed, float damageMulti, AvalonPhase... phases) {
        super(transitionTime, accessor, armature, play_speed, damageMulti, phases);
    }

    public AutoDiscardAttackAnimation(float transitionTime, AnimationManager.AnimationAccessor<? extends BasicAttackAnimation> accessor, AssetAccessor<? extends Armature> armature, float damageMulti, AvalonPhase... phases) {
        super(transitionTime, accessor, armature, damageMulti, phases);
    }

    public AutoDiscardAttackAnimation(float transitionTime, AnimationManager.AnimationAccessor<? extends BasicAttackAnimation> accessor, AssetAccessor<? extends Armature> armature, AvalonPhase... phases) {
        super(transitionTime, accessor, armature, phases);
    }

    public AutoDiscardAttackAnimation(float convertTime, String path, AssetAccessor<? extends Armature> armature, float play_speed, float damageMulti, AvalonPhase... phases) {
        super(convertTime, path, armature, play_speed, damageMulti, phases);
    }




    @Override
    public void end(LivingEntityPatch<?> entityPatch, AssetAccessor<? extends DynamicAnimation> nextAnimation, boolean isEnd) {
        super.end(entityPatch, nextAnimation, isEnd);
        if(!entityPatch.isLogicalClient()){
            entityPatch.getOriginal().discard();
        }
    }
}
