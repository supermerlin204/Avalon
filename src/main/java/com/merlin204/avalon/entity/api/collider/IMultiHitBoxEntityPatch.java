package com.merlin204.avalon.entity.api.collider;

import net.minecraft.world.entity.LivingEntity;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public interface IMultiHitBoxEntityPatch {

    ColliderManager getColliderManager();

    default void updateAllCollider(){
        if (this instanceof LivingEntityPatch<?> parentEntityPatch){
            AnimationPlayer player = parentEntityPatch.getAnimator().getPlayerFor(null);

            DynamicAnimation nowAnimation = player.getAnimation().get();
            float elapsedTime = player.getElapsedTime();
            if (!parentEntityPatch.isLogicalClient() && nowAnimation == Animations.EMPTY_ANIMATION) {
                LivingMotion livingMotion = parentEntityPatch.getCurrentLivingMotion();
                nowAnimation = parentEntityPatch.getAnimator().getLivingAnimation(livingMotion, Animations.EMPTY_ANIMATION).get();
                elapsedTime = getColliderManager().getElapsedTime();
                System.out.println(elapsedTime);
            }

            Pose pose;
            pose = nowAnimation.getPoseByTime(parentEntityPatch, elapsedTime, 1.0F);

            for (Joint joint : this.getColliderManager().getColliderMap().keySet()) {
                this.getColliderManager().getColliderMap().get(joint).updateOBBCollider(parentEntityPatch,pose, nowAnimation, elapsedTime, joint);
            }
        }

    }

    default boolean shouldRenderAABBHitBox(){
        return false;
    }


    default boolean isColling(LivingEntity livingEntity){
        for (Joint joint : this.getColliderManager().getColliderMap().keySet()) {
           if ( this.getColliderManager().getColliderMap().get(joint).isCollide(livingEntity)){
               return true;
           }
        }
        return false;
    }


}
