package com.merlin204.avalon.epicfight.api;

import net.minecraft.world.entity.Entity;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public abstract class AnimationAttackResultEvent<T extends AnimationAttackResultEvent<T>> {

    protected AnimationAttackResultEvent() {}

    protected abstract boolean checkCondition(LivingEntityPatch<?> entityPatch, Entity hurtEntity, AttackResult attackResult);

    public void execute(LivingEntityPatch<?> entityPatch, Entity hurtEntity, AttackResult attackResult) {
        if (this.checkCondition(entityPatch,hurtEntity, attackResult)) {
            this.fire(entityPatch,hurtEntity, attackResult);
        }

    }

    protected abstract void fire(LivingEntityPatch<?> entityPatch, Entity hurtEntity, AttackResult attackResult);


    public static class SimpleEvent extends AnimationAttackResultEvent<SimpleEvent> {
        private final AttackHandler handler;

        private SimpleEvent(AttackHandler handler) {
            this.handler = handler;
        }

        @Override
        protected boolean checkCondition(LivingEntityPatch<?> entityPatch, Entity hurtEntity, AttackResult attackResult) {
            return true;
        }

        @Override
        protected void fire(LivingEntityPatch<?> entityPatch, Entity hurtEntity, AttackResult attackResult) {
            handler.handle(entityPatch, hurtEntity, attackResult);
        }

        public static SimpleEvent create(AttackHandler handler) {
            return new SimpleEvent(handler);
        }


        @FunctionalInterface
        public interface AttackHandler {
            void handle(LivingEntityPatch<?> entityPatch, Entity hurtEntity, AttackResult attackResult);
        }
    }
}

