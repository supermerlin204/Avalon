package com.merlin204.avalon.epicfight.api;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public abstract class AnimationRenderEvent<T extends AnimationRenderEvent<T>> {

    protected AnimationRenderEvent() {}

    protected abstract boolean checkCondition(LivingEntity entity, LivingEntityPatch<?> entityPatch, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks);

    public void execute(LivingEntity entity, LivingEntityPatch<?> entityPatch, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks) {
        if (this.checkCondition(entity,entityPatch, buffer,poseStack,packedLight,partialTicks)) {
            this.fire(entity,entityPatch, buffer,poseStack,packedLight,partialTicks);
        }

    }

    protected abstract void fire(LivingEntity entity, LivingEntityPatch<?> entityPatch, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks);


    public static class SimpleEvent extends AnimationRenderEvent<SimpleEvent> {
        private final RenderHandler handler;

        private SimpleEvent(RenderHandler handler) {
            this.handler = handler;
        }

        @Override
        protected boolean checkCondition(LivingEntity entity, LivingEntityPatch<?> entityPatch, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks) {
            return true;
        }

        @Override
        protected void fire(LivingEntity entity, LivingEntityPatch<?> entityPatch, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks) {

            handler.handle(entity,entityPatch, buffer,poseStack,packedLight,partialTicks);
        }

        public static SimpleEvent create(RenderHandler handler) {
            return new SimpleEvent(handler);
        }


        @FunctionalInterface
        public interface RenderHandler {
            void handle(LivingEntity entity, LivingEntityPatch<?> entityPatch, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks);
        }
    }
}

