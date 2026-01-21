package com.merlin204.avalon.api.ai;


import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

import java.util.List;

/**
 *  AvalonGoal的核心类
 */
public class AvalonGoal<T extends MobPatch<?>> extends Goal {
    protected final T mobPatch;
    protected final List<AvalonBehaviorManager<T>> avalonBehaviorManagers;
    protected final BehaviorManagerHandler<T> behaviorManagerHandler;

    public AvalonGoal(T mobPatch, AvalonBehaviorManager<T> avalonBehaviorManager) {
        this.mobPatch = mobPatch;
        this.behaviorManagerHandler = null;
        this.avalonBehaviorManagers = List.of(avalonBehaviorManager);
    }

    public AvalonGoal(T mobPatch, BehaviorManagerHandler<T> behaviorManagerHandler, AvalonBehaviorManager<T>... avalonBehaviorManagers) {
        this.mobPatch = mobPatch;
        this.behaviorManagerHandler = behaviorManagerHandler;
        this.avalonBehaviorManagers = List.of(avalonBehaviorManagers);
    }

    @Override
    public boolean canUse() {
        return this.checkTargetValid();
    }



    @Override
    public void tick() {
        if (this.mobPatch.getTarget() == null) {
            return;
        }
        AvalonBehaviorManager<T> avalonBehaviorManager = getBehaviorManagerNow();

        avalonBehaviorManager.tick(mobPatch);

        EntityState state = this.mobPatch.getEntityState();

    }

    private AvalonBehaviorManager<T> getBehaviorManagerNow(){
        if (behaviorManagerHandler != null){
            String id = behaviorManagerHandler.handle(mobPatch);
            for (AvalonBehaviorManager<T> avalonBehaviorManager:avalonBehaviorManagers){
                if (avalonBehaviorManager.getId().equals(id)){
                    return avalonBehaviorManager;
                }
            }
        }
        return avalonBehaviorManagers.getFirst();
    }


    /**
     *  检查目标是否有效
     */
    protected boolean checkTargetValid() {
        LivingEntity livingentity = this.mobPatch.getTarget();

        if (livingentity == null) {
            return false;
        } else if (!livingentity.isAlive()) {
            return false;
        } else {
            return !(livingentity instanceof Player player) || !livingentity.isSpectator() && !player.isCreative();
        }
    }


    @FunctionalInterface
    public interface BehaviorManagerHandler<T extends MobPatch<?>> {
        String handle(T entityPatch);
    }

}
