package com.merlin204.avalon.entity.example;

import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.main.EpicFightSharedConstants;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.capabilities.entitypatch.Factions;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.StunType;

public class BOSSPatch extends LivingEntityPatch<BOSS> {
    @Override
    public void updateMotion(boolean considerInaction) {
        if(considerInaction){
            this.currentLivingMotion = LivingMotions.IDLE;
        }
    }

    public void onConstructed(BOSS entityIn) {
        this.original = entityIn;
        this.armature = entityIn.getArmature();
        Animator animator = EpicFightSharedConstants.getAnimator(this);
        this.animator = animator;
        this.initAnimator(animator);
        animator.postInit();
    }

    @Override
    protected void initAnimator(Animator animator) {
        super.initAnimator(animator);
        animator.addLivingAnimation(LivingMotions.IDLE, original.getIdleAnimation());
    }

    @Override
    public AssetAccessor<? extends StaticAnimation> getHitAnimation(StunType stunType) {
        return null;
    }

    @Override
    public Faction getFaction() {
        return Factions.WITHER;
    }


}
