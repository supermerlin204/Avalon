package com.merlin204.avalon.entity.example;

import com.merlin204.avalon.entity.api.collider.AvalonEntityOBBCollider;
import com.merlin204.avalon.entity.api.collider.ColliderManager;
import com.merlin204.avalon.entity.api.collider.IMultiHitBoxEntityPatch;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.event.entity.EntityEvent;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.collider.OBBCollider;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.main.EpicFightSharedConstants;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.damagesource.StunType;

import java.util.HashMap;

public class TestPatch extends MobPatch<TestEntity> implements IMultiHitBoxEntityPatch {

    private ColliderManager colliderManager;



    public TestPatch(TestEntity entity) {
        super(entity);
    }

    @Override
    public void onConstructed(EntityEvent.EntityConstructing event) {
        this.armature = original.getArmature();
        Animator animator = EpicFightSharedConstants.getAnimator(this);
        this.animator = animator;
        this.initAnimator(animator);
        animator.postInit();
    }



    @Override
    protected void initAnimator(Animator animator) {
        super.initAnimator(animator);
        animator.addLivingAnimation(LivingMotions.IDLE, Animations.BIPED_IDLE);
        animator.addLivingAnimation(LivingMotions.WALK, Animations.BIPED_WALK);
        animator.addLivingAnimation(LivingMotions.CHASE, Animations.BIPED_WALK);
    }

    @Override
    public void updateMotion(boolean b) {
        commonAggressiveMobUpdateMotion(b);
    }

    @Override
    public boolean applyStun(StunType stunType, float v) {
        return false;
    }

    @Override
    public boolean isStunned() {
        return false;
    }

    @Override
    public AssetAccessor<? extends StaticAnimation> getHitAnimation(StunType stunType) {
        return null;
    }

    @Override
    public ColliderManager getColliderManager() {
        if (colliderManager == null){
            HashMap<Joint, AvalonEntityOBBCollider> map = new HashMap<>();
            map.put(getArmature().searchJointByName("Head"),new AvalonEntityOBBCollider(0.3,0.3,0.3,0,0.3,0));
            map.put(getArmature().searchJointByName("Chest"),new AvalonEntityOBBCollider(0.3,0.2,0.2,0,0.2,0));
            map.put(getArmature().searchJointByName("Torso"),new AvalonEntityOBBCollider(0.3,0.15,0.2,0,0.15,0));
            map.put(getArmature().searchJointByName("Arm_L"),new AvalonEntityOBBCollider(0.15,0.2,0.15,0,0.1,0));
            map.put(getArmature().searchJointByName("Hand_L"),new AvalonEntityOBBCollider(0.15,0.2,0.15,0,0.2,0));
            map.put(getArmature().searchJointByName("Arm_R"),new AvalonEntityOBBCollider(0.15,0.2,0.15,0,0.1,0));
            map.put(getArmature().searchJointByName("Hand_R"),new AvalonEntityOBBCollider(0.15,0.2,0.15,0,0.2,0));
            map.put(getArmature().searchJointByName("Thigh_L"),new AvalonEntityOBBCollider(0.15,0.2,0.15,0,0.15,0));
            map.put(getArmature().searchJointByName("Leg_L"),new AvalonEntityOBBCollider(0.15,0.2,0.15,0,0.2,0));
            map.put(getArmature().searchJointByName("Thigh_R"),new AvalonEntityOBBCollider(0.15,0.2,0.15,0,0.15,0));
            map.put(getArmature().searchJointByName("Leg_R"),new AvalonEntityOBBCollider(0.15,0.2,0.15,0,0.2,0));
            colliderManager = new ColliderManager(this.original,map);
        }
        return colliderManager;
    }

}
