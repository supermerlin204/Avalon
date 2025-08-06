package com.merlin204.avalon.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

public class AvalonAnimatedAttackGoal<T extends MobPatch<?>> extends Goal {
    protected final T mobpatch;
    protected final AvalonCombatBehaviors<T> combatBehaviors;
    private LivingEntity lastTarget; // 存储上一个目标

    public AvalonAnimatedAttackGoal(T mobpatch, AvalonCombatBehaviors<T> combatBehaviors) {
        this.mobpatch = mobpatch;
        this.combatBehaviors = combatBehaviors;
        this.lastTarget = null;
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public void start() {
        this.lastTarget = this.mobpatch.getTarget();
    }

    @Override
    public void tick() {
        LivingEntity currentTarget = this.mobpatch.getTarget();

        if (currentTarget != this.lastTarget) {
            resetCombatState();
            this.lastTarget = currentTarget;
        }

        if (currentTarget == null) {
            return;
        }

        EntityState state = this.mobpatch.getEntityState();
        this.combatBehaviors.tick();

        if (this.combatBehaviors.hasActivatedMove()) {
            if (state.canBasicAttack()) {
                AvalonCombatBehaviors.Behavior<T> result = this.combatBehaviors.tryProceed();
                if (result != null) {
                    result.execute(this.mobpatch);
                }
            }
        } else {
            if (!state.inaction()) {
                AvalonCombatBehaviors.Behavior<T> result = this.combatBehaviors.selectRandomBehaviorSeries();
                if (result != null) {
                    result.execute(this.mobpatch);
                }
            }
        }
    }

    private void resetCombatState() {
        AvalonCombatBehaviors.BehaviorSeries<T> currentSeries = this.combatBehaviors.getCurrentBehaviorSeries();
        this.combatBehaviors.stopCurrentBehavior();
    }

    protected boolean checkTargetValid() {
        LivingEntity livingentity = this.mobpatch.getTarget();
        if (livingentity == null) {
            return false;
        } else if (!livingentity.isAlive()) {
            return false;
        } else {
            return !(livingentity instanceof Player player) || !player.isSpectator() && !player.isCreative();
        }
    }

    @Override
    public void stop() {
        this.lastTarget = null;
        resetCombatState();
    }
}