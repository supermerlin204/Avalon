package com.merlin204.avalon.entity.api.collider;


import com.merlin204.avalon.api.collider.AvalonColliderUtil;
import com.merlin204.avalon.api.collider.IAvalonOBBCollier;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.collider.OBBCollider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class AvalonEntityOBBCollider extends OBBCollider implements IAvalonOBBCollier {



    protected AvalonEntityOBBCollider(AABB outerAABB, double posX, double posY, double posZ, double center_x, double center_y, double center_z) {
        super(outerAABB, posX, posY, posZ, center_x, center_y, center_z);
    }

    public AvalonEntityOBBCollider(AABB entityCallAABB, double pos1_x, double pos1_y, double pos1_z, double pos2_x, double pos2_y, double pos2_z, double norm1_x, double norm1_y, double norm1_z, double norm2_x, double norm2_y, double norm2_z, double center_x, double center_y, double center_z) {
        super(entityCallAABB, pos1_x, pos1_y, pos1_z, pos2_x, pos2_y, pos2_z, norm1_x, norm1_y, norm1_z, norm2_x, norm2_y, norm2_z, center_x, center_y, center_z);
    }

    public AvalonEntityOBBCollider(AABB aabbCopy) {
        super(aabbCopy);
    }


    public AvalonEntityOBBCollider(double posX, double posY, double posZ, double center_x, double center_y, double center_z) {
        super(posX, posY, posZ, center_x, center_y, center_z);
    }

    public void updateOBBCollider(LivingEntityPatch<?> entityPatch,Pose pose, DynamicAnimation attackAnimation, float elapsedTime, Joint joint) {
        OpenMatrix4f transformMatrix;
        Armature armature = entityPatch.getArmature();
        if (armature.rootJoint.equals(joint)) {
            Pose rootPose = new Pose();
            rootPose.putJointData("Root", JointTransform.empty());
            attackAnimation.modifyPose(attackAnimation, rootPose, entityPatch, elapsedTime, 1.0F);
            transformMatrix = rootPose.orElseEmpty("Root").getAnimationBoundMatrix(armature.rootJoint, new OpenMatrix4f()).removeTranslation();
        } else {
            transformMatrix = armature.getBoundTransformFor(pose, joint);
        }
        OpenMatrix4f toWorldCoord = OpenMatrix4f.createTranslation(-(float) entityPatch.getOriginal().getX(), (float) entityPatch.getOriginal().getY(), -(float) entityPatch.getOriginal().getZ());
        transformMatrix.mulFront(toWorldCoord.mulBack(entityPatch.getModelMatrix(1.0F)));
        this.transform(transformMatrix);

    }





    //使用更精确的碰撞检测
    public boolean isCollide(Entity entity) {
        AvalonEntityOBBCollider obb = new AvalonEntityOBBCollider(entity.getBoundingBox());
        return AvalonColliderUtil.isColliding(this,obb);
    }


    @Override
    public Vector3f[] getAxes() {
        Vector3f[] axes = new Vector3f[3];
        if (rotatedNormals != null && rotatedNormals.length >= 3) {
            axes[0] = rotatedNormals[0].normalize().toVector3f();
            axes[1] = rotatedNormals[1].normalize().toVector3f();
            axes[2] = rotatedNormals[2].normalize().toVector3f();
        } else {
            // 默认轴
            axes[0] = new Vector3f(1, 0, 0);
            axes[1] = new Vector3f(0, 1, 0);
            axes[2] = new Vector3f(0, 0, 1);
        }

        return axes;
    }

    @Override
    public Vector3f getHalfExtents() {
        if (modelVertices != null && modelVertices.length > 0) {
            Vec3 baseExtent;

            if (modelVertices.length >= 4) {
                baseExtent = modelVertices[0];
            } else if (modelVertices.length >= 2) {
                baseExtent = modelVertices[1];
            } else {
                baseExtent = new Vec3(0.5, 0.5, 0.5);
            }
            if (scale != null) {
                return new Vec3(
                        Math.abs(baseExtent.x) * scale.x,
                        Math.abs(baseExtent.y) * scale.y,
                        Math.abs(baseExtent.z) * scale.z
                ).toVector3f();
            }

            return new Vec3(Math.abs(baseExtent.x), Math.abs(baseExtent.y), Math.abs(baseExtent.z)).toVector3f();
        }
        return new Vec3(0.5, 0.5, 0.5).toVector3f();
    }

    @Override
    public void setHalfExtents(Vector3f halfExtents) {
        //无需实现
    }

    @Override
    public Vector3f getCenter() {
        return new Vector3f((float) worldCenter.x, (float) worldCenter.y, (float) worldCenter.z);
    }

    @Override
    public void setCenter(Vector3f center) {
        //无需实现
    }

    @Override
    public Quaternionf getRotation() {
        //无需实现
        return null;
    }

    @Override
    public void setRotation(Quaternionf rotation) {
        //无需实现
    }

    @Override
    public Vector3f[] getVertices() {
        return new Vector3f[0];
    }



    @OnlyIn(Dist.CLIENT)
    public void drawInternal(PoseStack poseStack, MultiBufferSource buffer, Joint joint,LivingEntityPatch<?> entityPatch, float partialTicks, int color) {
        Pose prevPose = entityPatch.getAnimator().getPose(0.0F);
        Pose currentPose = entityPatch.getAnimator().getPose(1.0F);
        this.drawInternal(poseStack, buffer.getBuffer(getRenderType()), entityPatch.getArmature(), joint, prevPose, currentPose, partialTicks,color);
    }


}