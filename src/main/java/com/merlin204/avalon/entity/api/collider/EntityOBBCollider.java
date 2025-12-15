package com.merlin204.avalon.entity.api.collider;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.collider.OBBCollider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class EntityOBBCollider extends OBBCollider {
    protected EntityOBBCollider(AABB outerAABB, double posX, double posY, double posZ, double center_x, double center_y, double center_z) {
        super(outerAABB, posX, posY, posZ, center_x, center_y, center_z);
    }

    public EntityOBBCollider(AABB entityCallAABB, double pos1_x, double pos1_y, double pos1_z, double pos2_x, double pos2_y, double pos2_z, double norm1_x, double norm1_y, double norm1_z, double norm2_x, double norm2_y, double norm2_z, double center_x, double center_y, double center_z) {
        super(entityCallAABB, pos1_x, pos1_y, pos1_z, pos2_x, pos2_y, pos2_z, norm1_x, norm1_y, norm1_z, norm2_x, norm2_y, norm2_z, center_x, center_y, center_z);
    }

    public EntityOBBCollider(AABB aabbCopy) {
        super(aabbCopy);
    }

    public EntityOBBCollider(double posX, double posY, double posZ, double center_x, double center_y, double center_z) {
        super(posX, posY, posZ, center_x, center_y, center_z);
    }

    public void updateOBBCollider(LivingEntityPatch<?> entitypatch,Pose pose, DynamicAnimation attackAnimation, float elapsedTime, Joint joint) {
        OpenMatrix4f transformMatrix;
        Armature armature = entitypatch.getArmature();
        if (armature.rootJoint.equals(joint)) {
            Pose rootPose = new Pose();
            rootPose.putJointData("Root", JointTransform.empty());
            attackAnimation.modifyPose(attackAnimation, rootPose, entitypatch, elapsedTime, 1.0F);
            transformMatrix = rootPose.orElseEmpty("Root").getAnimationBoundMatrix(armature.rootJoint, new OpenMatrix4f()).removeTranslation();
        } else {
            transformMatrix = armature.getBoundTransformFor(pose, joint);
        }
        OpenMatrix4f toWorldCoord = OpenMatrix4f.createTranslation(-(float) entitypatch.getOriginal().getX(), (float) entitypatch.getOriginal().getY(), -(float) entitypatch.getOriginal().getZ());
        transformMatrix.mulFront(toWorldCoord.mulBack(entitypatch.getModelMatrix(1.0F)));
        this.transform(transformMatrix);

    }


    /**
     * Transform the bounding box
     **/
    @Override
    public void transform(OpenMatrix4f modelMatrix) {
        OpenMatrix4f noTranslation = modelMatrix.removeTranslation();

        for (int i = 0; i < this.modelVertices.length; i++) {
            this.rotatedVertices[i] = OpenMatrix4f.transform(noTranslation, this.modelVertices[i]);
        }

        for (int i = 0; i < this.modelNormals.length; i++) {
            this.rotatedNormals[i] = OpenMatrix4f.transform(noTranslation, this.modelNormals[i]);
        }

        this.scale = noTranslation.toScaleVector();

        super.transform(modelMatrix);
    }

    @Override
    protected AABB getHitboxAABB() {
        return this.outerAABB.inflate((this.outerAABB.maxX - this.outerAABB.minX) * this.scale.x,
                (this.outerAABB.maxY - this.outerAABB.minY) * this.scale.y,
                (this.outerAABB.maxZ - this.outerAABB.minZ) * this.scale.z).move(-this.worldCenter.x, this.worldCenter.y, -this.worldCenter.z);
    }


    @Override
    public boolean isCollide(Entity entity) {
        OBBCollider obb = new OBBCollider(entity.getBoundingBox());
        return isCollide(obb);
    }

    @OnlyIn(Dist.CLIENT)
    public void drawInstantly(PoseStack poseStack, MultiBufferSource buffer, int color, Vec3 originPos) {
        poseStack.pushPose();
        VertexConsumer vertexConsumer = buffer.getBuffer(this.getRenderType());
        Matrix4f matrix = poseStack.last().pose();
        Vec3 vec = this.worldCenter.multiply(-1, 1, -1).subtract(originPos);


        float v1x = (float) (vec.x + this.rotatedVertices[0].multiply(-1, 1, -1).x);
        float v1y = (float) (vec.y + this.rotatedVertices[0].multiply(-1, 1, -1).y);
        float v1z = (float) (vec.z + this.rotatedVertices[0].multiply(-1, 1, -1).z);

        float v2x = (float) (vec.x + this.rotatedVertices[1].multiply(-1, 1, -1).x);
        float v2y = (float) (vec.y + this.rotatedVertices[1].multiply(-1, 1, -1).y);
        float v2z = (float) (vec.z + this.rotatedVertices[1].multiply(-1, 1, -1).z);

        float v3x = (float) (vec.x + this.rotatedVertices[2].multiply(-1, 1, -1).x);
        float v3y = (float) (vec.y + this.rotatedVertices[2].multiply(-1, 1, -1).y);
        float v3z = (float) (vec.z + this.rotatedVertices[2].multiply(-1, 1, -1).z);

        float v4x = (float) (vec.x + this.rotatedVertices[3].multiply(-1, 1, -1).x);
        float v4y = (float) (vec.y + this.rotatedVertices[3].multiply(-1, 1, -1).y);
        float v4z = (float) (vec.z + this.rotatedVertices[3].multiply(-1, 1, -1).z);

        float v5x = (float) (vec.x + -this.rotatedVertices[2].multiply(-1, 1, -1).x);
        float v5y = (float) (vec.y + -this.rotatedVertices[2].multiply(-1, 1, -1).y);
        float v5z = (float) (vec.z + -this.rotatedVertices[2].multiply(-1, 1, -1).z);

        float v6x = (float) (vec.x + -this.rotatedVertices[3].multiply(-1, 1, -1).x);
        float v6y = (float) (vec.y + -this.rotatedVertices[3].multiply(-1, 1, -1).y);
        float v6z = (float) (vec.z + -this.rotatedVertices[3].multiply(-1, 1, -1).z);

        float v7x = (float) (vec.x + -this.rotatedVertices[0].multiply(-1, 1, -1).x);
        float v7y = (float) (vec.y + -this.rotatedVertices[0].multiply(-1, 1, -1).y);
        float v7z = (float) (vec.z + -this.rotatedVertices[0].multiply(-1, 1, -1).z);

        float v8x = (float) (vec.x + -this.rotatedVertices[1].multiply(-1, 1, -1).x);
        float v8y = (float) (vec.y + -this.rotatedVertices[1].multiply(-1, 1, -1).y);
        float v8z = (float) (vec.z + -this.rotatedVertices[1].multiply(-1, 1, -1).z);


        vertexConsumer.vertex(matrix, v1x, v1y, v1z).color(color).normal(0.0F, 0.0F, 1.0F).endVertex();
        vertexConsumer.vertex(matrix, v2x, v2y, v2z).color(color).normal(0.0F, 0.0F, 1.0F).endVertex();

        vertexConsumer.vertex(matrix, v2x, v2y, v2z).color(color).normal(0.0F, 0.0F, 1.0F).endVertex();
        vertexConsumer.vertex(matrix, v3x, v3y, v3z).color(color).normal(0.0F, 0.0F, 1.0F).endVertex();

        vertexConsumer.vertex(matrix, v3x, v3y, v3z).color(color).normal(0.0F, 0.0F, 1.0F).endVertex();
        vertexConsumer.vertex(matrix, v4x, v4y, v4z).color(color).normal(0.0F, 0.0F, 1.0F).endVertex();

        vertexConsumer.vertex(matrix, v4x, v4y, v4z).color(color).normal(0.0F, 0.0F, 1.0F).endVertex();
        vertexConsumer.vertex(matrix, v1x, v1y, v1z).color(color).normal(0.0F, 0.0F, 1.0F).endVertex();

        vertexConsumer.vertex(matrix, v1x, v1y, v1z).color(color).normal(0.0F, 0.0F, 1.0F).endVertex();
        vertexConsumer.vertex(matrix, v5x, v5y, v5z).color(color).normal(0.0F, 0.0F, 1.0F).endVertex();

        vertexConsumer.vertex(matrix, v2x, v2y, v2z).color(color).normal(0.0F, 0.0F, 1.0F).endVertex();
        vertexConsumer.vertex(matrix, v6x, v6y, v6z).color(color).normal(0.0F, 0.0F, 1.0F).endVertex();

        vertexConsumer.vertex(matrix, v3x, v3y, v3z).color(color).normal(0.0F, 0.0F, 1.0F).endVertex();
        vertexConsumer.vertex(matrix, v7x, v7y, v7z).color(color).normal(0.0F, 0.0F, 1.0F).endVertex();

        vertexConsumer.vertex(matrix, v4x, v4y, v4z).color(color).normal(0.0F, 0.0F, 1.0F).endVertex();
        vertexConsumer.vertex(matrix, v8x, v8y, v8z).color(color).normal(0.0F, 0.0F, 1.0F).endVertex();

        vertexConsumer.vertex(matrix, v5x, v5y, v5z).color(color).normal(0.0F, 0.0F, 1.0F).endVertex();
        vertexConsumer.vertex(matrix, v6x, v6y, v6z).color(color).normal(0.0F, 0.0F, 1.0F).endVertex();

        vertexConsumer.vertex(matrix, v6x, v6y, v6z).color(color).normal(0.0F, 0.0F, 1.0F).endVertex();
        vertexConsumer.vertex(matrix, v7x, v7y, v7z).color(color).normal(0.0F, 0.0F, 1.0F).endVertex();

        vertexConsumer.vertex(matrix, v7x, v7y, v7z).color(color).normal(0.0F, 0.0F, 1.0F).endVertex();
        vertexConsumer.vertex(matrix, v8x, v8y, v8z).color(color).normal(0.0F, 0.0F, 1.0F).endVertex();

        vertexConsumer.vertex(matrix, v8x, v8y, v8z).color(color).normal(0.0F, 0.0F, 1.0F).endVertex();
        vertexConsumer.vertex(matrix, v5x, v5y, v5z).color(color).normal(0.0F, 0.0F, 1.0F).endVertex();

        poseStack.popPose();
    }


    @OnlyIn(Dist.CLIENT)
    public void draw(PoseStack poseStack, MultiBufferSource buffer, LivingEntityPatch<?> entitypatch, DynamicAnimation animation, Joint joint, float prevElapsedTime, float elapsedTime, float partialTicks, float attackSpeed) {
        Armature armature = entitypatch.getArmature();
        Pose prevPose;
        Pose currentPose;
        if (joint.getName().equals(armature.rootJoint.getName())) {
            prevPose = new Pose();
            currentPose = new Pose();
            prevPose.putJointData("Root", JointTransform.empty());
            currentPose.putJointData("Root", JointTransform.empty());
            animation.modifyPose(animation, prevPose, entitypatch, prevElapsedTime, 0.0F);
            animation.modifyPose(animation, currentPose, entitypatch, elapsedTime, 1.0F);
        } else {
            prevPose = animation.getPoseByTime(entitypatch, prevElapsedTime, 0.0F);
            currentPose = animation.getPoseByTime(entitypatch, elapsedTime, 1.0F);
        }

        this.drawInternal(poseStack, buffer.getBuffer(this.getRenderType()), armature, joint, prevPose, currentPose, partialTicks, -1);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void drawInternal(PoseStack poseStack, VertexConsumer vertexConsumer, Armature armature, Joint joint, Pose pose1, Pose pose2, float partialTicks, int color) {
        OpenMatrix4f poseMatrix;
        Pose interpolatedPose = Pose.interpolatePose(pose1, pose2, partialTicks);
        color = -1;
        if (armature.rootJoint.equals(joint)) {
            JointTransform jt = interpolatedPose.orElseEmpty("Root");
            jt.rotation().x = 0.0F;
            jt.rotation().y = 0.0F;
            jt.rotation().z = 0.0F;
            jt.rotation().w = 1.0F;

            poseMatrix = jt.getAnimationBoundMatrix(armature.rootJoint, new OpenMatrix4f()).removeTranslation();
        } else {
            poseMatrix = armature.getBoundTransformFor(interpolatedPose, joint);
        }

        poseStack.pushPose();
        MathUtils.mulStack(poseStack, poseMatrix);
        Matrix4f matrix = poseStack.last().pose();
        Vec3 vec = this.modelVertices[1];
        float maxX = (float) (this.modelCenter.x + vec.x);
        float maxY = (float) (this.modelCenter.y + vec.y);
        float maxZ = (float) (this.modelCenter.z + vec.z);
        float minX = (float) (this.modelCenter.x - vec.x);
        float minY = (float) (this.modelCenter.y - vec.y);
        float minZ = (float) (this.modelCenter.z - vec.z);

        vertexConsumer.vertex(matrix, minX, maxY, minZ).color(color).normal(0.0F, 0.0F, 1.0F).endVertex();
        vertexConsumer.vertex(matrix, minX, maxY, maxZ).color(color).normal(0.0F, 0.0F, 1.0F).endVertex();
        vertexConsumer.vertex(matrix, minX, maxY, maxZ).color(color).normal(1.0F, 0.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix, maxX, maxY, maxZ).color(color).normal(1.0F, 0.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix, maxX, maxY, maxZ).color(color).normal(0.0F, 0.0F, -1.0F).endVertex();
        vertexConsumer.vertex(matrix, maxX, maxY, minZ).color(color).normal(0.0F, 0.0F, -1.0F).endVertex();
        vertexConsumer.vertex(matrix, maxX, maxY, minZ).color(color).normal(-1.0F, 0.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix, minX, maxY, minZ).color(color).normal(-1.0F, 0.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix, maxX, maxY, maxZ).color(color).normal(0.0F, -1.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix, maxX, minY, maxZ).color(color).normal(0.0F, -1.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix, minX, maxY, maxZ).color(color).normal(0.0F, -1.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix, minX, minY, maxZ).color(color).normal(0.0F, -1.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix, maxX, maxY, minZ).color(color).normal(0.0F, -1.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix, maxX, minY, minZ).color(color).normal(0.0F, -1.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix, minX, maxY, minZ).color(color).normal(0.0F, -1.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix, minX, minY, minZ).color(color).normal(0.0F, -1.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix, minX, minY, minZ).color(color).normal(0.0F, 0.0F, 1.0F).endVertex();
        vertexConsumer.vertex(matrix, minX, minY, maxZ).color(color).normal(0.0F, 0.0F, 1.0F).endVertex();
        vertexConsumer.vertex(matrix, minX, minY, maxZ).color(color).normal(1.0F, 0.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix, maxX, minY, maxZ).color(color).normal(1.0F, 0.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix, maxX, minY, maxZ).color(color).normal(0.0F, 0.0F, -1.0F).endVertex();
        vertexConsumer.vertex(matrix, maxX, minY, minZ).color(color).normal(0.0F, 0.0F, -1.0F).endVertex();
        vertexConsumer.vertex(matrix, maxX, minY, minZ).color(color).normal(-1.0F, 0.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix, minX, minY, minZ).color(color).normal(-1.0F, 0.0F, 0.0F).endVertex();

        poseStack.popPose();
    }
}