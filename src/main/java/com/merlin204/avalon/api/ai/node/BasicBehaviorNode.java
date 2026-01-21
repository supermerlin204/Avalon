package com.merlin204.avalon.api.ai.node;


import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

import java.util.List;

/**
 *  行为的基本节点
 */
public abstract class BasicBehaviorNode<T extends MobPatch<?>>  {

    public final int weight;
    public final boolean canInterrupt;

    private final int totalWeight;
    private boolean action = false;
    private int tickNow = 0;

    private final List<? extends BasicBehaviorNode<T>> nodes;
    private final List<Condition<T>> conditions;

    protected BasicBehaviorNode(int weight, boolean canInterrupt, List<? extends BasicBehaviorNode<T>> nodes, List<Condition<T>> conditions) {
        this.weight = weight;
        this.canInterrupt = canInterrupt;
        this.nodes = nodes;
        this.conditions = conditions;
        int mathWeight = 0;
        for (BasicBehaviorNode<T> basicBehaviorNode:nodes){
            mathWeight = mathWeight + basicBehaviorNode.weight;
        }
        totalWeight = mathWeight;
    }


    public final BasicBehaviorNode<T> getNextNode(T mobPatch){
        float randomWeight = Math.abs(mobPatch.getOriginal().getRandom().nextFloat()) * totalWeight;
        int mathWeight = 0;
        for (BasicBehaviorNode<T> basicBehaviorNode:nodes){
            mathWeight = mathWeight + basicBehaviorNode.weight;
            if (mathWeight>=randomWeight){
                return basicBehaviorNode;
            }
        }
        return null;
    }

    public int getTickNow() {
        return tickNow;
    }

    public final boolean isAction() {
        return action;
    }

    public void start(T mobPatch){
        action = true;
    }

    public void end(T mobPatch){
        action = false;
        tickNow = 0;
    }

    public void actionTick(T mobPatch){
        tickNow++;
    }

    public abstract boolean checkCanBeEnd(T mobPatch);

    public final boolean checkCanBeStart(T mobPatch){
        for (Condition<T> condition:conditions){
            if (!condition.predicate(mobPatch)){
                return false;
            }
        }
        return true;
    }

}
