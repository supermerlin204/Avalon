package com.merlin204.avalon.epicfight.api;

import net.minecraft.world.damagesource.DamageSource;

import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;

public abstract class AnimationBeAttackEvent<T extends AnimationBeAttackEvent<T>> {

    protected AnimationBeAttackEvent() {}

    protected abstract boolean checkCondition(LivingEntityPatch<?> owner, LivingIncomingDamageEvent event);

    public void execute(LivingEntityPatch<?> owner,LivingIncomingDamageEvent event) {
        if (this.checkCondition(owner,event)) {
            this.fire(owner,event);
        }

    }

    protected abstract void fire(LivingEntityPatch<?> owner,LivingIncomingDamageEvent event);


    public static class InPeriodEvent extends AnimationBeAttackEvent<SimpleEvent> {
        private final AttackHandler handler;
        final float start;
        final float end;

        private InPeriodEvent(float start, float end,AttackHandler handler) {
            this.handler = handler;
            this.start = start;
            this.end = end;
        }

        @Override
        protected boolean checkCondition(LivingEntityPatch<?> owner,LivingIncomingDamageEvent event) {
            float elapsed = owner.getAnimator().getPlayerFor(null).getElapsedTime();
            return this.start <= elapsed && this.end > elapsed;
        }

        @Override
        protected void fire(LivingEntityPatch<?> owner,LivingIncomingDamageEvent event) {
            handler.handle(owner,event);
        }

        public static InPeriodEvent create(float start, float end,AttackHandler handler) {
            return new InPeriodEvent(start,end,handler);
        }


        @FunctionalInterface
        public interface AttackHandler {
            void handle(LivingEntityPatch<?> owner,LivingIncomingDamageEvent event);
        }
    }

    public static class SimpleEvent extends AnimationBeAttackEvent<SimpleEvent> {
        private final AttackHandler handler;

        private SimpleEvent(AttackHandler handler) {
            this.handler = handler;
        }

        @Override
        protected boolean checkCondition(LivingEntityPatch<?> owner,LivingIncomingDamageEvent event) {
            return true;
        }

        @Override
        protected void fire(LivingEntityPatch<?> owner,LivingIncomingDamageEvent event) {
             handler.handle(owner,event);
        }

        public static SimpleEvent create(AttackHandler handler) {
            return new SimpleEvent(handler);
        }


        @FunctionalInterface
        public interface AttackHandler {
            void handle(LivingEntityPatch<?> owner,LivingIncomingDamageEvent event);
        }
    }
}

