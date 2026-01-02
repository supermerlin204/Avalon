package com.merlin204.avalon.ai.condition;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.utils.HitEntityList;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.List;

public class HurtPredictCondition<T extends LivingEntityPatch<?>> implements Condition<T> {


    private final int interpolation;

    public HurtPredictCondition(int interpolation) {
        this.interpolation = interpolation;
    }

    @Override
    public boolean predicate(T livingEntityPatch) {
        LivingEntityPatch<?> targetPatch = EpicFightCapabilities.getEntityPatch(livingEntityPatch.getTarget(),LivingEntityPatch.class);
        if (targetPatch == null){
            return false;
        }
        DynamicAnimation animation = targetPatch.getAnimator().getPlayerFor(null).getAnimation().get();
        if (animation instanceof AttackAnimation attackAnimation){
            for (AttackAnimation.Phase phase : attackAnimation.phases) {
                float atkTime = phase.contact - phase.preDelay;
                float timeStep = atkTime/interpolation;
                for (int i = 0;i<interpolation;i++){
                    float start = phase.preDelay + i*timeStep;
                    float end = phase.preDelay + (i+1)*timeStep;
                    List<Entity> list = phase.getCollidingEntities(livingEntityPatch, attackAnimation, start, end,
                            animation.getPlaySpeed(livingEntityPatch, animation));
                    HitEntityList hitEntities = new HitEntityList(livingEntityPatch, list,
                            phase.getProperty(AnimationProperty.AttackPhaseProperty.HIT_PRIORITY)
                                    .orElse(HitEntityList.Priority.DISTANCE));
                    while (hitEntities.next()) {
                        Entity target = hitEntities.getEntity();
                        if (target == livingEntityPatch.getOriginal()){
                            return true;
                        }
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
