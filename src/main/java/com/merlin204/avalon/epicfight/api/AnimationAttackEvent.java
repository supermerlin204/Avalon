package com.merlin204.avalon.epicfight.api;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;

public abstract class AnimationAttackEvent<T extends AnimationAttackEvent<T>> {

    protected AnimationAttackEvent() {}

    protected abstract boolean checkCondition(LivingEntityPatch<?> entityPatch, Entity hurtEntity, EpicFightDamageSource damageSource);

    public void execute(LivingEntityPatch<?> entityPatch, Entity hurtEntity, EpicFightDamageSource damageSource) {
        if (this.checkCondition(entityPatch,hurtEntity, damageSource)) {
            this.fire(entityPatch,hurtEntity, damageSource);
        }

    }

    protected abstract void fire(LivingEntityPatch<?> entityPatch, Entity hurtEntity, EpicFightDamageSource damageSource);


    public static class SimpleEvent extends AnimationAttackEvent<SimpleEvent> {
        private final AttackHandler handler;

        private SimpleEvent(AttackHandler handler) {
            this.handler = handler;
        }

        @Override
        protected boolean checkCondition(LivingEntityPatch<?> entityPatch, Entity hurtEntity, EpicFightDamageSource damageSource) {
            return true;
        }

        @Override
        protected void fire(LivingEntityPatch<?> entityPatch, Entity hurtEntity, EpicFightDamageSource damageSource) {
            handler.handle(entityPatch, hurtEntity, damageSource);
        }

        public static SimpleEvent create(AttackHandler handler) {
            return new SimpleEvent(handler);
        }


        @FunctionalInterface
        public interface AttackHandler {
            void handle(LivingEntityPatch<?> entityPatch, Entity hurtEntity, EpicFightDamageSource damageSource);
        }
    }
}

