package com.merlin204.avalon.entity.ai;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.entity.*;
import yesman.epicfight.network.server.SPAnimatorControl;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch.ServerAnimationPacketProvider;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class AvalonCombatBehaviors<T extends MobPatch<?>> {
    private final List<BehaviorSeries<T>> behaviorSeriesList;
    private final T mobpatch;
    private int currentBehaviorPointer;



    protected AvalonCombatBehaviors(Builder<T> builder, T mobpatch) {
        this.behaviorSeriesList = builder.behaviorSeriesList.stream().map(BehaviorSeries.Builder::build).toList();
        this.mobpatch = mobpatch;
        this.currentBehaviorPointer = -1;
    }

    public void reset() {
        this.currentBehaviorPointer = -1;

        for (BehaviorSeries<T> series : this.behaviorSeriesList) {
            series.nextBehaviorPointer = 0;
            series.loopFinished = false;
        }
    }
    public BehaviorSeries<T> getCurrentBehaviorSeries() {
        if (this.currentBehaviorPointer >= 0 && this.currentBehaviorPointer < this.behaviorSeriesList.size()) {
            return this.behaviorSeriesList.get(this.currentBehaviorPointer);
        }
        return null;
    }

    public void stopCurrentBehavior() {
        BehaviorSeries<T> currentSeries = getCurrentBehaviorSeries();
        if (currentSeries != null) {
            currentSeries.nextBehaviorPointer = 0;
            currentSeries.loopFinished = false;
        }
        this.currentBehaviorPointer = -1;
    }


    private int getRandomCombatBehaviorSeries() {
        IntList candidates = new IntArrayList();
        FloatList rescaledWeight = new FloatArrayList();
        float weightSum = 0.0F;

        for (int i = 0; i < this.behaviorSeriesList.size(); i++) {
            if (this.currentBehaviorPointer != i) {
                BehaviorSeries<T> move = this.behaviorSeriesList.get(i);

                if (move.canBeSelected(this.mobpatch)) {
                    weightSum += move.weight;
                    candidates.add(i);
                }
            }
        }

        for (int i : candidates) {
            rescaledWeight.add(this.behaviorSeriesList.get(i).weight / weightSum);
        }

        float random = this.mobpatch.getOriginal().getRandom().nextFloat();
        float delta = 0.0F;

        for (int i = 0; i < candidates.size(); i++) {
            int index = candidates.getInt(i);
            delta += rescaledWeight.getFloat(i);

            if (random < delta) {
                this.resetCooldown(index, true);
                return index;
            }
        }

        return -1;
    }

    public void execute(int seriesPointer) {
        this.currentBehaviorPointer = seriesPointer;
        BehaviorSeries<T> behaviorSeries = this.behaviorSeriesList.get(seriesPointer);
        Behavior<T> behavior = behaviorSeries.behaviors.get(behaviorSeries.nextBehaviorPointer);
        behaviorSeries.count();
        behavior.execute(this.mobpatch);
    }

    public void resetCooldown(int seriesPointer, boolean resetSharingCooldown) {
        this.behaviorSeriesList.get(seriesPointer).resetCooldown(this, resetSharingCooldown);
    }

    public Behavior<T> selectRandomBehaviorSeries() {
        int seriesPointer = this.getRandomCombatBehaviorSeries();

        if (seriesPointer >= 0) {
            this.currentBehaviorPointer = seriesPointer;
            BehaviorSeries<T> behaviorSeries = this.behaviorSeriesList.get(seriesPointer);
            Behavior<T> behavior = behaviorSeries.behaviors.get(behaviorSeries.nextBehaviorPointer);
            behaviorSeries.count();

            if (behaviorSeries.loopFinished && !behaviorSeries.looping) {
                behaviorSeries.loopFinished = false;
                this.currentBehaviorPointer = -1;
            }

            return behavior;
        }

        return null;
    }

    public Behavior<T> tryProceed() {
        BehaviorSeries<T> currentBehaviorSeries = this.behaviorSeriesList.get(this.currentBehaviorPointer);

        if (currentBehaviorSeries.canBeInterrupted) {
            int seriesPointer = this.getRandomCombatBehaviorSeries();

            if (seriesPointer >= 0 && this.currentBehaviorPointer != seriesPointer) {
                this.currentBehaviorPointer = seriesPointer;
                BehaviorSeries<T> newCombatBehaviorSeries = this.behaviorSeriesList.get(seriesPointer);
                return newCombatBehaviorSeries.behaviors.get(newCombatBehaviorSeries.nextBehaviorPointer);
            }
        }

        if (currentBehaviorSeries.loopFinished && !currentBehaviorSeries.looping) {
            currentBehaviorSeries.loopFinished = false;
            this.currentBehaviorPointer = -1;
            return null;
        }

        Behavior<T> nextBehavior = currentBehaviorSeries.behaviors.get(currentBehaviorSeries.nextBehaviorPointer);

        if (nextBehavior.checkPredicates(this.mobpatch)) {
            currentBehaviorSeries.count();
            return nextBehavior;
        } else {
            this.currentBehaviorPointer = -1;

            if (!currentBehaviorSeries.looping) {
                currentBehaviorSeries.nextBehaviorPointer = 0;
            }

            return null;
        }
    }

    public boolean hasActivatedMove() {
        return this.currentBehaviorPointer >= 0;
    }

    public void tick() {
        if (this.mobpatch.getEntityState().inaction()) {
            return;
        }

        for (BehaviorSeries<T> behaviorSeries : this.behaviorSeriesList) {
            behaviorSeries.tick();
        }
    }

    public static <T extends MobPatch<?>> Builder<T> builder() {
        return new Builder<T>();
    }

    public static class Builder<T extends MobPatch<?>> {
        private final List<BehaviorSeries.Builder<T>> behaviorSeriesList = Lists.newArrayList();

        public Builder<T> newBehaviorSeries(BehaviorSeries.Builder<T> builder) {
            this.behaviorSeriesList.add(builder);
            return this;
        }

        public AvalonCombatBehaviors<T> build(T mobpatch) {
            return new AvalonCombatBehaviors<T>(this, mobpatch);
        }
    }

    public static class BehaviorSeries<T extends MobPatch<?>> {
        private final List<Behavior<T>> behaviors;
        private final boolean looping;
        private final boolean canBeInterrupted;
        private final float weight;
        private final int maxCooldown;
        private final IntList cooldownShares;
        private boolean loopFinished;
        private int cooldown;
        private int nextBehaviorPointer;

        private BehaviorSeries(Builder<T> builder) {
            this.behaviors = builder.behaviors.stream().map(Behavior.Builder::build).toList();
            this.looping = builder.looping;
            this.canBeInterrupted = builder.canBeInterrupted;
            this.weight = builder.weight;
            this.cooldownShares = builder.cooldownSharingPointers;
            this.maxCooldown = builder.cooldown;
        }

        public boolean canBeSelected(T mobpatch) {
            if (this.cooldown > 0) {
                return false;
            }

            return this.behaviors.get(this.nextBehaviorPointer).checkPredicates(mobpatch);
        }

        public void count() {
            ++this.nextBehaviorPointer;
            this.loopFinished = false;
            int behaviorsNum = this.behaviors.size();

            if (this.nextBehaviorPointer >= behaviorsNum) {
                this.nextBehaviorPointer %= behaviorsNum;
                this.loopFinished = true;
            }
        }

        public void tick() {
            if (this.cooldown > 0) {
                this.cooldown--;
            }
        }

        public void resetCooldown(AvalonCombatBehaviors<T> mobBehavior, boolean resetSharingCooldown) {
            this.cooldown = this.maxCooldown;

            if (resetSharingCooldown) {
                for (int i : this.cooldownShares) {
                    BehaviorSeries<T> behaviorSeries = mobBehavior.behaviorSeriesList.get(i);
                    behaviorSeries.cooldown = behaviorSeries.maxCooldown;
                }
            }
        }

        public static <T extends MobPatch<?>> Builder<T> builder() {
            return new Builder<T>();
        }

        public static class Builder<T extends MobPatch<?>> {
            private final List<Behavior.Builder<T>> behaviors = Lists.newArrayList();
            private boolean looping = false;
            private boolean canBeInterrupted = true;
            private float weight;
            private int cooldown;
            private final IntList cooldownSharingPointers = new IntArrayList();

            public Builder<T> weight(float weight) {
                this.weight = weight;
                return this;
            }

            public Builder<T> cooldown(int cooldown) {
                this.cooldown = cooldown;
                return this;
            }

            public Builder<T> simultaneousCooldown(int... cooldownSharingPointers) {
                for (int pointer : cooldownSharingPointers) {
                    this.cooldownSharingPointers.add(pointer);
                }

                return this;
            }

            public Builder<T> nextBehavior(Behavior.Builder<T> motion) {
                this.behaviors.add(motion);
                return this;
            }

            public Builder<T> looping(boolean looping) {
                this.looping = looping;
                return this;
            }

            public Builder<T> canBeInterrupted(boolean canBeInterrupted) {
                this.canBeInterrupted = canBeInterrupted;
                return this;
            }

            public BehaviorSeries<T> build() {
                return new BehaviorSeries<T>(this);
            }
        }
    }

    public static class Behavior<T extends MobPatch<?>> {
        private final Consumer<T> behavior;
        private final List<Condition<T>> conditions;

        private Behavior(Builder<T> builder) {
            this.behavior = builder.behavior;
            this.conditions = builder.conditions;
        }

        private boolean checkPredicates(T mobpatch) {
            for (Condition<T> condition : this.conditions) {
                if (!condition.predicate(mobpatch)) {
                    return false;
                }
            }

            return true;
        }

        public void execute(T mobpatch) {
            this.behavior.accept(mobpatch);
            mobpatch.updateEntityState();
        }

        public static <T extends MobPatch<?>> Builder<T> builder() {
            return new Builder<T>();
        }

        public static class Builder<T extends MobPatch<?>> {
            private Consumer<T> behavior;
            private final List<Condition<T>> conditions = Lists.newArrayList();
            private ServerAnimationPacketProvider packetProvider = SPAnimatorControl::new;

            public Builder<T> behavior(Consumer<T> behavior) {
                this.behavior = behavior;
                return this;
            }

            public Builder<T> emptyBehavior() {
                this.behavior = (mobpatch) -> {};
                return this;
            }

            public Builder<T> animationBehavior(AnimationAccessor<? extends StaticAnimation> motion) {
                this.behavior = (mobpatch) -> {
                    mobpatch.playAnimationSynchronized(motion, 0.0F, this.packetProvider);
                };

                return this;
            }

            public Builder<T> withinEyeHeight() {
                this.condition(new TargetInEyeHeight());
                return this;
            }

            public Builder<T> randomChance(float chance) {
                this.condition(new RandomChance(chance));
                return this;
            }

            public Builder<T> withinDistance(double minDistance, double maxDistance) {
                this.condition(new TargetInDistance(minDistance, maxDistance));
                return this;
            }

            public Builder<T> withinAngle(double minDegree, double maxDegree) {
                this.condition(new TargetInPov(minDegree, maxDegree));
                return this;
            }

            public Builder<T> withinAngleHorizontal(double minDegree, double maxDegree) {
                this.condition(new TargetInPov.TargetInPovHorizontal(minDegree, maxDegree));
                return this;
            }

            public Builder<T> health(float health, HealthPoint.Comparator comparator) {
                this.condition(new HealthPoint(health, comparator));
                return this;
            }

            public Builder<T> custom(Function<T, Boolean> customPredicate) {
                this.condition(new CustomCondition<T>(customPredicate));
                return this;
            }

            @SuppressWarnings("unchecked")
            public void condition(Condition<?> predicate) {
                this.conditions.add((Condition<T>)predicate);
            }

            public Builder<T> predicate(Condition<T> predicate) {
                this.conditions.add(predicate);
                return this;
            }

            public Builder<T> packetProvider(ServerAnimationPacketProvider packetProvider) {
                this.packetProvider = packetProvider;
                return this;
            }

            public Behavior<T> build() {
                return new Behavior<T>(this);
            }
        }
    }
}
