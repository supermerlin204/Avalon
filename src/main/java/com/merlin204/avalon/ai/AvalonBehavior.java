package com.merlin204.avalon.ai;

import com.merlin204.avalon.ai.node.BasicBehaviorNode;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

import java.util.List;

/**
 *  一个行为的核心类
 */
public class AvalonBehavior<T extends MobPatch<?>>  {

    public final int weight;
    public final boolean canInterrupt;
    public final int maxCooldown;
    private final int totalWeight;
    public int cooldown;
    private boolean action = false;

    private final List<? extends BasicBehaviorNode<T>> nodes;

    private BasicBehaviorNode<T> nodeNow;


    public AvalonBehavior(int weight, boolean canInterrupt, int maxCooldown, List<? extends BasicBehaviorNode<T>> nodes) {
        this.weight = weight;
        this.canInterrupt = canInterrupt;
        this.maxCooldown = maxCooldown;
        this.nodes = nodes;
        int mathWeight = 0;
        for (BasicBehaviorNode<T> basicBehaviorNode:nodes){
            mathWeight = mathWeight + basicBehaviorNode.weight;
        }
        totalWeight = mathWeight;
    }

    public boolean isAction() {
        return action;
    }

    public void startNode(T mobPatch,BasicBehaviorNode<T> node){
        if (!node.checkCanBeStart(mobPatch)){
            return;
        }
        if (!nodeNow.isAction()){
            //没有action说明end过了
            nodeNow = node;
            nodeNow.start(mobPatch);
        }else if (node.canInterrupt){
            nodeNow.end(mobPatch);
            nodeNow = node;
            nodeNow.start(mobPatch);
        }
    }


    public void start(T mobPatch){
        action = true;
        float randomWeight = Math.abs(mobPatch.getOriginal().getRandom().nextFloat()) * totalWeight;
        int mathWeight = 0;
        for (BasicBehaviorNode<T> basicBehaviorNode:nodes){
            mathWeight = mathWeight + basicBehaviorNode.weight;
            if (mathWeight>=randomWeight){
                startNode(mobPatch,basicBehaviorNode);
            }
        }
    }

    public void end(T mobPatch){
        cooldown = maxCooldown;
        action = false;
    }

    public void actionTick(T mobPatch){
        if (action && nodeNow.isAction()){
            nodeNow.actionTick(mobPatch);
        }
        if (action && nodeNow.checkCanBeEnd(mobPatch)){
            nodeNow.end(mobPatch);
            BasicBehaviorNode<T> nextNode = nodeNow.getNextNode(mobPatch);
            if (nextNode != null){
                startNode(mobPatch,nextNode);
            }else {
                end(mobPatch);
                return;
            }
        }
        if (action && checkCanBeEnd(mobPatch)){
            end(mobPatch);
        }
    }

    public void tick(T mobPatch){
        if (!action && cooldown>0){
            cooldown--;
        }
    }

    public boolean checkCanBeEnd(T mobPatch){
        return nodeNow.checkCanBeEnd(mobPatch);
    }

    public boolean checkCanBeStart(T mobPatch){
        return cooldown==0;
    }
}
