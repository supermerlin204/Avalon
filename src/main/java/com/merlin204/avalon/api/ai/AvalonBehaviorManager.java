package com.merlin204.avalon.api.ai;


import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

import java.util.List;

/**
 *  行为集的核心类
 */
public class AvalonBehaviorManager<T extends MobPatch<?>> {
    private final String id;
    private final List<? extends AvalonBehavior<T>> behaviors;
    private final int totalWeight;
    private AvalonBehavior<T> behaviorNow;

    @SafeVarargs
    public AvalonBehaviorManager(String id, AvalonBehavior<T>... behaviors) {
        this.id = id;
        this.behaviors = List.of(behaviors);
        int weight = 0;
        for (AvalonBehavior<T> behavior:behaviors){
            weight = weight + behavior.weight;
        }
        totalWeight = weight;
    }

    /**
     *  尝试开始一个行为
     */
    private void startBehavior(T mobPatch, AvalonBehavior<T> behavior){
        if (!behavior.checkCanBeStart(mobPatch)){
            return;
        }
        if (!behaviorNow.isAction()){
            behaviorNow = behavior;
            behaviorNow.start(mobPatch);
        }else if (behavior.canInterrupt){
            behaviorNow.end(mobPatch);
            behaviorNow = behavior;
            behaviorNow.start(mobPatch);
        }
    }
    /**
     *  尝试开始一个随机的行为
     */
    protected void startRandomBehavior(T mobPatch){
        float randomWeight = Math.abs(mobPatch.getOriginal().getRandom().nextFloat()) * totalWeight;
        float weightNow = 0;
        for (AvalonBehavior<T> behavior:behaviors){
            weightNow = weightNow + behavior.weight;
            if (weightNow >= randomWeight){
                startBehavior(mobPatch,behavior);
            }
        }

    }

    protected void tick(T mobPatch){
        for (AvalonBehavior<T> behavior:behaviors){
            behavior.tick(mobPatch);
        }
        if (behaviorNow.isAction()){
            behaviorNow.actionTick(mobPatch);

        }
    }

    public String getId() {
        return id;
    }


}

