package com.merlin204.avalon.util;

import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class AvalonAnimationUtils {

    public static Vec3 getJointWorldPos(LivingEntityPatch<?> entityPatch, Joint joint) {
        return getJointWorldPos(entityPatch,joint,Vec3f.ZERO);
    };
    public static Vec3 getJointWorldRawPos(LivingEntityPatch<?> entityPatch, Joint joint,float time) {
        return getJointWorldRawPos(entityPatch,joint,time,Vec3f.ZERO);
    };

    public static Vec3 getJointWorldPos(LivingEntityPatch<?> entityPatch, Joint joint,Vec3f offset) {

        LivingEntity entity = entityPatch.getOriginal();
        OpenMatrix4f transformMatrix = entityPatch.getArmature().getBoundTransformFor(entityPatch.getAnimator().getPose(0.1f), joint);
        transformMatrix.translate(offset);
        OpenMatrix4f rotation = new OpenMatrix4f().rotate(-(float) Math.toRadians(entity.yBodyRotO + 180.0F), new Vec3f(0.0F, 1.0F, 0.0F));
        OpenMatrix4f.mul(rotation, transformMatrix, transformMatrix);
        Vec3 pos = new Vec3(transformMatrix.m30 + (float) entity.getX(), transformMatrix.m31 + (float) entity.getY(), transformMatrix.m32 + (float) entity.getZ());

        return pos;
    }

    public static Vec3 getJointWorldPos(LivingEntityPatch<?> entityPatch, Joint joint,Vec3f offset,float partialTicks) {

        LivingEntity entity = entityPatch.getOriginal();
        OpenMatrix4f transformMatrix = entityPatch.getArmature().getBoundTransformFor(entityPatch.getAnimator().getPose(partialTicks), joint);
        transformMatrix.translate(offset);
        OpenMatrix4f rotation = new OpenMatrix4f().rotate(-(float) Math.toRadians(entity.yBodyRotO + 180.0F), new Vec3f(0.0F, 1.0F, 0.0F));
        OpenMatrix4f.mul(rotation, transformMatrix, transformMatrix);
        Vec3 pos = new Vec3(transformMatrix.m30 + (float) entity.getX(), transformMatrix.m31 + (float) entity.getY(), transformMatrix.m32 + (float) entity.getZ());

        return pos;
    }

    public static Vec3 getJointWorldRawPos(LivingEntityPatch<?> entityPatch, Joint joint,float time,Vec3f offset) {
        Animator animator = entityPatch.getAnimator();
        LivingEntity entity = entityPatch.getOriginal();
        Pose pose = animator.getPlayerFor(null).getAnimation().get().getRawPose(time);
        OpenMatrix4f transformMatrix = entityPatch.getArmature().getBoundTransformFor(pose, joint);
        transformMatrix.translate(offset);
        OpenMatrix4f rotation = new OpenMatrix4f().rotate(-(float) Math.toRadians(entity.yBodyRotO + 180.0F), new Vec3f(0.0F, 1.0F, 0.0F));
        OpenMatrix4f.mul(rotation, transformMatrix, transformMatrix);
        Vec3 pos = new Vec3(transformMatrix.m30 + (float) entity.getX(), transformMatrix.m31 + (float) entity.getY(), transformMatrix.m32 + (float) entity.getZ());

        return pos;
    }



    public static AvalonAttackAnimation.AvalonPhase createSimplePhase(int startFrame, int endFrame, int waitFrame, InteractionHand interactionHand, Joint joint, Collider collider) {
        float start = startFrame / 60F;
        float end = endFrame / 60F;
        float wait = waitFrame / 60F;
        return new AvalonAttackAnimation.AvalonPhase(0, start, start, end, wait, Float.MAX_VALUE, interactionHand, joint,collider);
    }

    public static AvalonAttackAnimation.AvalonPhase createSimplePhase(int startFrame, int endFrame, int waitFrame, InteractionHand interactionHand ,float damageMulti, Joint joint, Collider collider) {
        float start = startFrame / 60F;
        float end = endFrame / 60F;
        float wait = waitFrame / 60F;
        return new AvalonAttackAnimation.AvalonPhase(0, start, start, end, wait, Float.MAX_VALUE, interactionHand,damageMulti,joint,collider);
    }

    public static AvalonAttackAnimation.AvalonPhase createSimplePhase(int startFrame, int endFrame, int waitFrame, InteractionHand interactionHand ,float damageMulti,float impactDamageMulti, Joint joint, Collider collider) {
        float start = startFrame / 60F;
        float end = endFrame / 60F;
        float wait = waitFrame / 60F;
        return new AvalonAttackAnimation.AvalonPhase(0, start, start, end, wait, Float.MAX_VALUE, interactionHand,damageMulti,impactDamageMulti,joint,collider);
    }

    public static AvalonAttackAnimation.AvalonPhase createSimplePhase(int startFrame, int endFrame, int waitFrame, InteractionHand interactionHand ,float damageMulti,float impactDamageMulti,float phaseArmorNegationMulti ,Joint joint, Collider collider) {
        float start = startFrame / 60F;
        float end = endFrame / 60F;
        float wait = waitFrame / 60F;
        return new AvalonAttackAnimation.AvalonPhase(0, start, start, end, wait, Float.MAX_VALUE, interactionHand,damageMulti,impactDamageMulti,phaseArmorNegationMulti,joint,collider);
    }

    public static AvalonAttackAnimation.AvalonPhase createSimplePhase(int startFrame, int endFrame, int waitFrame, InteractionHand interactionHand, AttackAnimation.JointColliderPair... colliders) {
        float start = startFrame / 60F;
        float end = endFrame / 60F;
        float wait = waitFrame / 60F;
        return new AvalonAttackAnimation.AvalonPhase(0F, start, start, end, wait, Float.MAX_VALUE,interactionHand,colliders);
    }

    public static AvalonAttackAnimation.AvalonPhase createSimplePhase(int startFrame, int endFrame, int waitFrame, InteractionHand interactionHand,float damageMulti, AttackAnimation.JointColliderPair... colliders) {
        float start = startFrame / 60F;
        float end = endFrame / 60F;
        float wait = waitFrame / 60F;
        return new AvalonAttackAnimation.AvalonPhase(0F, start, start, end, wait, Float.MAX_VALUE,interactionHand,damageMulti,colliders);
    }

    public static AvalonAttackAnimation.AvalonPhase createSimplePhase(int startFrame, int endFrame, int waitFrame, InteractionHand interactionHand,float damageMulti,float impactDamageMulti, AttackAnimation.JointColliderPair... colliders) {
        float start = startFrame / 60F;
        float end = endFrame / 60F;
        float wait = waitFrame / 60F;
        return new AvalonAttackAnimation.AvalonPhase(0F, start, start, end, wait, Float.MAX_VALUE,interactionHand,damageMulti,impactDamageMulti,colliders);
    }

    public static AvalonAttackAnimation.AvalonPhase createSimplePhase(int startFrame, int endFrame, int waitFrame, InteractionHand interactionHand,float damageMulti,float impactDamageMulti, float phaseArmorNegationMulti,AttackAnimation.JointColliderPair... colliders) {
        float start = startFrame / 60F;
        float end = endFrame / 60F;
        float wait = waitFrame / 60F;
        return new AvalonAttackAnimation.AvalonPhase(0F, start, start, end, wait, Float.MAX_VALUE,interactionHand,damageMulti,impactDamageMulti,phaseArmorNegationMulti,colliders);
    }













}