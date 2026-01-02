package com.merlin204.avalon.ai.condition;

import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.utils.HitEntityList;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

import java.util.List;

public class AtkPredictCondition<T extends LivingEntityPatch<?>> implements Condition<T> {


    private final AnimationManager.AnimationAccessor<? extends AttackAnimation> animationAccessor;
    private final int interpolation;

    public AtkPredictCondition(AnimationManager.AnimationAccessor<? extends AttackAnimation> animationAccessor, int interpolation) {
        this.animationAccessor = animationAccessor;
        this.interpolation = interpolation;
    }

    @Override
    public boolean predicate(T livingEntityPatch) {
        AttackAnimation animation = animationAccessor.get();
        for (AttackAnimation.Phase phase : animation.phases) {
            float atkTime = phase.contact - phase.preDelay;
            float timeStep = atkTime/interpolation;
            for (int i = 0;i<interpolation;i++){
                float start = phase.preDelay + i*timeStep;
                float end = phase.preDelay + (i+1)*timeStep;
                List<Entity> list = phase.getCollidingEntities(livingEntityPatch, animation, start, end,
                        animation.getPlaySpeed(livingEntityPatch, animation));
                for (Entity entity:list){
                    if (entity == livingEntityPatch.getTarget()){
                        return true;
                    }else if (entity instanceof Mob mob && mob.getTarget()==livingEntityPatch.getOriginal()){
                        return true;
                    }
                }
            }
        }
        return false;
    }


    @Override
    public Condition<T> read(CompoundTag compoundTag) throws IllegalArgumentException {
        return null;
    }

    @Override
    public CompoundTag serializePredicate() {
        return null;
    }

    @Override
    public List<ParameterEditor> getAcceptingParameters(Screen screen) {
        return null;
    }
}
