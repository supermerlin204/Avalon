package com.merlin204.avalon.epicfight.animations;

import com.google.common.collect.Sets;

import com.merlin204.avalon.epicfight.api.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.property.MoveCoordFunctions;
import yesman.epicfight.api.animation.types.*;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.HitEntityList;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.EpicFightDamageSources;
import yesman.epicfight.world.entity.eventlistener.AttackPhaseEndEvent;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class AvalonAttackAnimation extends BasicAttackAnimation {

    private final float play_speed;
    private final float damageMulti;

    public AvalonAttackAnimation(float transitionTime, float antic, float preDelay, float contact, float recovery, @Nullable Collider collider, Joint colliderJoint, AnimationManager.AnimationAccessor<? extends BasicAttackAnimation> accessor, AssetAccessor<? extends Armature> armature, float play_speed, float damageMulti) {
        super(transitionTime, antic, preDelay, contact, recovery, collider, colliderJoint, accessor, armature);
        this.play_speed = play_speed;
        this.damageMulti = damageMulti;
        this.addProperty(AnimationProperty.StaticAnimationProperty.POSE_MODIFIER, null);
        this.addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false);
    }


    public AvalonAttackAnimation(float transitionTime, AnimationManager.AnimationAccessor<? extends BasicAttackAnimation> accessor, AssetAccessor<? extends Armature> armature, float play_speed, float damageMulti, AvalonPhase... phases) {
        super(transitionTime, accessor, armature, phases);
        this.play_speed = play_speed;
        this.damageMulti = damageMulti;
        this.addProperty(AnimationProperty.StaticAnimationProperty.POSE_MODIFIER, null);
        this.addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false);
    }


    public AvalonAttackAnimation(float transitionTime, AnimationManager.AnimationAccessor<? extends BasicAttackAnimation> accessor, AssetAccessor<? extends Armature> armature, float damageMulti, AvalonPhase... phases) {
        super(transitionTime, accessor, armature, phases);
        this.damageMulti = damageMulti;
        this.play_speed = 1;
        this.addProperty(AnimationProperty.StaticAnimationProperty.POSE_MODIFIER, null);
        this.addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false);
    }


    public AvalonAttackAnimation(float transitionTime, AnimationManager.AnimationAccessor<? extends BasicAttackAnimation> accessor, AssetAccessor<? extends Armature> armature, AvalonPhase... phases) {
        super(transitionTime, accessor, armature, phases);
        this.damageMulti = 1;
        this.play_speed = 1;
        this.addProperty(AnimationProperty.StaticAnimationProperty.POSE_MODIFIER, null);
        this.addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false);
    }


    public AvalonAttackAnimation(float convertTime, String path, AssetAccessor<? extends Armature> armature, float play_speed, float damageMulti, AvalonPhase... phases) {
        super(convertTime, path, armature, phases);
        this.play_speed = play_speed;
        this.damageMulti = damageMulti;
        this.addProperty(AnimationProperty.StaticAnimationProperty.POSE_MODIFIER, null);
        this.addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false);
    }


    @Override
    public void begin(LivingEntityPatch<?> entitypatch) {
        super.begin(entitypatch);
        entitypatch.getCurrentlyActuallyHitEntities().clear();
        entitypatch.getCurrentlyAttackTriedEntities().clear();

        for (Phase phase : phases) {
            if (phase instanceof AvalonPhase avalonPhase) {
                avalonPhase.resetAttackRecord(entitypatch);
            }
        }
    }



    @Override
    protected void attackTick(LivingEntityPatch<?> entitypatch, AssetAccessor<? extends DynamicAnimation> animation) {
        AnimationPlayer player = entitypatch.getAnimator().getPlayerFor(this.getAccessor());
        float prevElapsedTime = player.getPrevElapsedTime();
        float elapsedTime = player.getElapsedTime();


        EntityState prevState = ((DynamicAnimation) animation.get()).getState(entitypatch, prevElapsedTime);
        EntityState state = ((DynamicAnimation) animation.get()).getState(entitypatch, elapsedTime);

        List<Phase> activePhases = getActivePhases(elapsedTime);
        for (Phase phase : activePhases) {
            if (prevElapsedTime < phase.start && elapsedTime >= phase.start) {
                if (phase instanceof AvalonPhase avalonPhase) {
                    avalonPhase.resetAttackRecord(entitypatch);
                }
            }
            if (state.getLevel() == 1 && !state.turningLocked() && entitypatch instanceof MobPatch<?> mobpatch) {

                (mobpatch.getOriginal()).getNavigation().stop();
                ((LivingEntity) entitypatch.getOriginal()).attackAnim = 2.0F;
                LivingEntity target = entitypatch.getTarget();
                if (target != null) {
                    entitypatch.rotateTo(target, entitypatch.getYRotLimit(), false);
                }
            }

            if (prevState.attacking() || state.attacking() || prevState.getLevel() <= 2 && state.getLevel() > 2) {
                if (!prevState.attacking() || (prevElapsedTime < phase.start || prevElapsedTime >= phase.end) && (state.attacking() || prevState.getLevel() <= 2 && state.getLevel() > 2)) {
                    entitypatch.playSound(this.getSwingSound(entitypatch, phase), 0.0F, 0.0F);
                    entitypatch.removeHurtEntities();
                }

                this.hurtCollidingEntities(entitypatch, prevElapsedTime, elapsedTime, prevState, state, phase);

                if ((!state.attacking() || elapsedTime >= this.getTotalTime()) && entitypatch instanceof ServerPlayerPatch playerpatch) {
                    playerpatch.getEventListener().triggerEvents(PlayerEventListener.EventType.ATTACK_PHASE_END_EVENT, new AttackPhaseEndEvent(playerpatch, this.getAccessor(), phase, this.getPhaseOrderByTime(elapsedTime)));
                }
            }
        }
    }

    @Override
    protected void hurtCollidingEntities(LivingEntityPatch<?> entitypatch, float prevElapsedTime, float elapsedTime,
                                         EntityState prevState, EntityState state, Phase phase) {
        LivingEntity entity = (LivingEntity) entitypatch.getOriginal();

        if (prevElapsedTime < phase.start && elapsedTime >= phase.start) {
            entitypatch.getCurrentlyActuallyHitEntities().clear();
            entitypatch.getCurrentlyAttackTriedEntities().clear();
            if (phase instanceof AvalonPhase avalonPhase) {
                avalonPhase.resetAttackRecord(entitypatch);
            }
        }

        float phasePrevTime = Math.max(prevElapsedTime, phase.start);
        float phaseCurrentTime = Math.min(elapsedTime, phase.end);

        float phasePreDelay = phase.start + phase.preDelay;
        float phaseContact = phase.start + phase.contact;

        if (phaseCurrentTime < phasePreDelay || phasePrevTime >= phaseContact) {
            return;
        }

        List<Entity> list = phase.getCollidingEntities(entitypatch, this, phasePrevTime, phaseCurrentTime,
                this.getPlaySpeed(entitypatch, this));
        if (!list.isEmpty()) {
            HitEntityList hitEntities = new HitEntityList(entitypatch, list,
                    phase.getProperty(AnimationProperty.AttackPhaseProperty.HIT_PRIORITY)
                            .orElse(HitEntityList.Priority.DISTANCE));
            int maxStrikes = 10;

            while (hitEntities.next()) {
                Entity target = hitEntities.getEntity();
                LivingEntity trueEntity = this.getTrueEntity(target);

                boolean canAttack = trueEntity != null && trueEntity.isAlive() &&
                        !entitypatch.getCurrentlyActuallyHitEntities().contains(trueEntity) &&
                        !entitypatch.isTargetInvulnerable(target);

                if (phase instanceof AvalonPhase avalonPhase) {
                    canAttack = canAttack && avalonPhase.tryAttack(entitypatch, trueEntity);
                }

                if (canAttack) {
                    if (entity.hasLineOfSight(target)) {
                        EpicFightDamageSource damagesource = this.getEpicFightDamageSource(entitypatch, target, phase);
                        int prevInvulTime = target.invulnerableTime;
                        target.invulnerableTime = 0;

                        this.getProperty(AvalonAnimationProperty.ATTACK_EVENTS).ifPresent(events -> {
                            for (AnimationAttackEvent<?> event : events) {
                                event.execute(entitypatch,target,damagesource);
                            }
                        });

                        AttackResult attackResult = entitypatch.attack(damagesource, target, phase.hand);
                        target.invulnerableTime = prevInvulTime;

                        this.getProperty(AvalonAnimationProperty.ATTACK_RESULT_EVENTS).ifPresent(events -> {
                            for (AnimationAttackResultEvent<?> event : events) {
                                event.execute(entitypatch,target,attackResult);
                            }
                        });

                        if (attackResult.resultType.dealtDamage()) {
                            target.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                                    this.getHitSound(entitypatch, phase), target.getSoundSource(), 1.0F, 1.0F);
                            this.spawnHitParticle((ServerLevel) target.level(), entitypatch, target, phase);
                        }

                        entitypatch.getCurrentlyActuallyHitEntities().add(trueEntity);
                        if (attackResult.resultType.shouldCount()) {
                            entitypatch.getCurrentlyAttackTriedEntities().add(trueEntity);
                        }
                    }
                }
            }
        }
    }


    public <A extends AvalonAttackAnimation> A noPhysics(){
        this.addProperty(AnimationProperty.StaticAnimationProperty.NO_PHYSICS,true);
        return (A)this;
    }
    public <A extends AvalonAttackAnimation> A noPhysics(int startFrame,int endFrame){
        this.newTimePair(startFrame/60F,endFrame/60F).addProperty(AnimationProperty.StaticAnimationProperty.NO_PHYSICS,true);
        return (A)this;
    }
    public <A extends AvalonAttackAnimation> A noGravity(){
        this.addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0,9999));
        return (A)this;
    }
    public <A extends AvalonAttackAnimation> A noGravity(int startFrame,int endFrame){
        this.addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(startFrame/60F,endFrame/60F));
        return (A)this;
    }
    public <A extends AvalonAttackAnimation> A useVerticalMove(){
        this.addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true);
        return (A)this;
    }
    public <A extends AvalonAttackAnimation> A useVerticalMove(int startFrame,int endFrame){
        this.newTimePair(startFrame/60F,endFrame/60F).addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true);
        return (A)this;
    }
    public <A extends AvalonAttackAnimation> A useRawMove(){
        this.addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.RAW_COORD)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.RAW_COORD);
        return (A)this;
    }

    public <A extends AvalonAttackAnimation> A turningLocked(){
        this.newTimePair(0,999).addState(EntityState.TURNING_LOCKED,true);
        return (A)this;
    }

    public <A extends AvalonAttackAnimation> A damageMiss(){
        this.newTimePair(0,999) .addState(EntityState.ATTACK_RESULT, (damageSource) -> {return AttackResult.ResultType.MISSED;});
        return (A)this;
    }

    public <A extends AvalonAttackAnimation> A damageBlock(){
        this.newTimePair(0,999) .addState(EntityState.ATTACK_RESULT, (damageSource) -> {return AttackResult.ResultType.BLOCKED;});
        return (A)this;
    }


    @SuppressWarnings("unchecked")
    public <A extends AvalonAttackAnimation> A addAttackEvents(AnimationAttackEvent<?>... events) {
        this.properties.computeIfPresent(AvalonAnimationProperty.ATTACK_EVENTS, (k, v) -> {
            return Stream.concat(((Collection<?>)v).stream(), List.of(events).stream()).toList();
        });

        this.properties.computeIfAbsent(AvalonAnimationProperty.ATTACK_EVENTS, (k) -> {
            return List.of(events);
        });
        return (A)this;
    }

    public <A extends AvalonAttackAnimation> A addAttackResultEvents(AnimationAttackResultEvent<?>... events) {
        this.properties.computeIfPresent(AvalonAnimationProperty.ATTACK_RESULT_EVENTS, (k, v) -> {
            return Stream.concat(((Collection<?>)v).stream(), List.of(events).stream()).toList();
        });

        this.properties.computeIfAbsent(AvalonAnimationProperty.ATTACK_RESULT_EVENTS, (k) -> {
            return List.of(events);
        });
        return (A)this;
    }

    public <A extends AvalonAttackAnimation> A addBeAttackEvents(AnimationBeAttackEvent<?>... events) {
        this.properties.computeIfPresent(AvalonAnimationProperty.BE_ATTACK_EVENTS, (k, v) -> {
            return Stream.concat(((Collection<?>)v).stream(), List.of(events).stream()).toList();
        });

        this.properties.computeIfAbsent(AvalonAnimationProperty.BE_ATTACK_EVENTS, (k) -> {
            return List.of(events);
        });
        return (A)this;
    }

    @SuppressWarnings("unchecked")
    public <A extends AvalonAttackAnimation> A addRenderEvents(AnimationRenderEvent<?>... events) {
        if (FMLEnvironment.dist == Dist.CLIENT){
            this.properties.computeIfPresent(AvalonAnimationProperty.RENDER_EVENTS, (k, v) -> {
                return Stream.concat(((Collection<?>)v).stream(), List.of(events).stream()).toList();
            });

            this.properties.computeIfAbsent(AvalonAnimationProperty.RENDER_EVENTS, (k) -> {
                return List.of(events);
            });
        }
        return (A)this;
    }

    @Override
    public void end(LivingEntityPatch<?> entitypatch, AssetAccessor<? extends DynamicAnimation> nextAnimation, boolean isEnd) {
        super.end(entitypatch, nextAnimation, isEnd);

        int entityId = entitypatch.getOriginal().getId();
        for (Phase phase : phases) {
            if (phase instanceof AvalonPhase avalonPhase) {
                avalonPhase.clearAttackRecord(entityId);
            }
        }
    }


    @Override
    public float getPlaySpeed(LivingEntityPatch<?> entitypatch, DynamicAnimation animation) {
        if (entitypatch instanceof PlayerPatch<?> playerpatch) {
            Phase phase = this.getPhaseByTime(playerpatch.getAnimator().getPlayerFor(this.getAccessor()).getElapsedTime());
            return playerpatch.getAttackSpeed(phase.hand) * play_speed;
        } else {
            return play_speed;
        }
    }

    @Override
    protected void bindPhaseState(Phase phase) {
        float preDelay = phase.preDelay;
        this.stateSpectrumBlueprint.newTimePair(0, preDelay).addState(EntityState.PHASE_LEVEL, 1)
                .newTimePair(phase.start, phase.recovery).addState(EntityState.CAN_SKILL_EXECUTION, false)
                .newTimePair(phase.start, phase.recovery + 0.1F).addState(EntityState.MOVEMENT_LOCKED, true)
                .addState(EntityState.UPDATE_LIVING_MOTION, false)
                .newTimePair(phase.start, phase.recovery)
                .addState(EntityState.CAN_BASIC_ATTACK, false)
                .newTimePair(phase.start, phase.end).addState(EntityState.INACTION, true)
                .newTimePair(phase.antic, phase.end).addState(EntityState.TURNING_LOCKED, true)
                .newTimePair(preDelay, phase.contact).addState(EntityState.ATTACKING, true).addState(EntityState.PHASE_LEVEL, 2)
                .newTimePair(phase.contact, phase.end).addState(EntityState.PHASE_LEVEL, 3);
    }

    @Override
    public EpicFightDamageSource getEpicFightDamageSource(DamageSource originalSource, LivingEntityPatch<?> entitypatch, Entity target, Phase phase) {
        if (phase == null) {
            phase = this.getPhaseByTime(entitypatch.getAnimator().getPlayerFor(this.getAccessor()).getElapsedTime());
        }

        EpicFightDamageSource extendedSource;
        if (originalSource instanceof EpicFightDamageSource epicfightDamageSource) {
            extendedSource = epicfightDamageSource;
        } else {
            extendedSource = EpicFightDamageSources.fromVanillaDamageSource(originalSource).setAnimation(this.getAccessor());
        }
        float phaseDamageMulti = 1;
        float phaseImpactMulti = 1;
        float phaseArmorNegationMulti = 1;
        if (phase instanceof AvalonPhase avalonPhase) {
            phaseDamageMulti = avalonPhase.phaseDamageMulti;
            phaseImpactMulti = avalonPhase.phaseImpactMulti;
            phaseArmorNegationMulti = avalonPhase.phaseArmorNegationMulti;
        }

        ValueModifier damageModifier = ValueModifier.multiplier(damageMulti * phaseDamageMulti);
        extendedSource.attachDamageModifier(damageModifier);

        extendedSource.setBaseImpact(extendedSource.getBaseImpact() * phaseImpactMulti);

        extendedSource.setBaseArmorNegation(extendedSource.getBaseArmorNegation() * phaseArmorNegationMulti);


        phase.getProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE).ifPresent((opt) -> {
            extendedSource.setStunType(opt);
        });
        phase.getProperty(AnimationProperty.AttackPhaseProperty.SOURCE_TAG).ifPresent((opt) -> {
            Objects.requireNonNull(extendedSource);
            opt.forEach(extendedSource::addRuntimeTag);
        });
        phase.getProperty(AnimationProperty.AttackPhaseProperty.EXTRA_DAMAGE).ifPresent((opt) -> {
            Objects.requireNonNull(extendedSource);
            opt.forEach(extendedSource::addExtraDamage);
        });
        phase.getProperty(AnimationProperty.AttackPhaseProperty.SOURCE_LOCATION_PROVIDER).ifPresent((opt) -> {
            extendedSource.setInitialPosition((Vec3) opt.apply(entitypatch));
        });
        phase.getProperty(AnimationProperty.AttackPhaseProperty.SOURCE_LOCATION_PROVIDER).ifPresentOrElse((opt) -> {
            extendedSource.setInitialPosition((Vec3) opt.apply(entitypatch));
        }, () -> {
            extendedSource.setInitialPosition(((LivingEntity) entitypatch.getOriginal()).position());
        });
        return extendedSource;
    }




    @OnlyIn(Dist.CLIENT)
    public void renderDebugging(PoseStack poseStack, MultiBufferSource buffer, LivingEntityPatch<?> entitypatch, float playbackTime, float partialTicks) {
        AnimationPlayer animPlayer = entitypatch.getAnimator().getPlayerFor(this.getAccessor());
        float prevElapsedTime = animPlayer.getPrevElapsedTime();
        float elapsedTime = animPlayer.getElapsedTime();

        List<Phase> activePhases = this.getActivePhases(playbackTime);

        for (Phase phase : activePhases) {
            JointColliderPair[] colliders = phase.getColliders();
            if (colliders == null) continue;

            for (JointColliderPair colliderInfo : colliders) {
                Collider collider = colliderInfo.getSecond();
                if (collider == null) {
                    collider = entitypatch.getColliderMatching(phase.hand);
                }
                float phaseStart = phase.start;
                float phaseEnd = phase.end;
                float phasePrevTime = Math.max(prevElapsedTime, phaseStart);
                float phaseCurrentTime = Math.min(elapsedTime, phaseEnd);
                collider.draw(
                        poseStack, buffer, entitypatch, this,
                        colliderInfo.getFirst(),
                        phasePrevTime, phaseCurrentTime,
                        partialTicks,
                        this.getPlaySpeed(entitypatch, this)
                );
            }
        }
    }


    public static class AvalonPhase extends Phase {
        public final float phaseDamageMulti;
        public final float phaseImpactMulti;
        public final float phaseArmorNegationMulti;


        private final Map<Integer, Set<Integer>> attackedEntitiesMap = new ConcurrentHashMap<>();

        public void resetAttackRecord(LivingEntityPatch<?> entitypatch) {
            int entityId = entitypatch.getOriginal().getId();
            attackedEntitiesMap.computeIfAbsent(entityId, k -> Collections.newSetFromMap(new ConcurrentHashMap<>())).clear();
        }

        public boolean tryAttack(LivingEntityPatch<?> entitypatch, Entity target) {
            int attackerId = entitypatch.getOriginal().getId();
            int targetId = target.getId();

            Set<Integer> attackedEntities = attackedEntitiesMap.computeIfAbsent(
                    attackerId,
                    k -> Collections.newSetFromMap(new ConcurrentHashMap<>())
            );

            return attackedEntities.add(targetId);
        }

        // 清理特定实体的攻击记录
        public void clearAttackRecord(int entityId) {
            attackedEntitiesMap.remove(entityId);
        }

        public <V> AvalonPhase setHitSound(SoundEvent soundEvent){
            this.addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, soundEvent);
            return this;
        }

        public <V> AvalonPhase setSwingSound(SoundEvent soundEvent){
            this.addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, soundEvent);
            return this;
        }

        @Override
        public <V> AvalonPhase addProperty(AnimationProperty.AttackPhaseProperty<V> propertyType, V value) {
            return (AvalonPhase) super.addProperty(propertyType, value);
        }

        public AvalonPhase(float start, float antic, float preDelay, float contact, float recovery, float end, InteractionHand hand, float damageMulti, Joint joint, Collider collider) {
            super(start, antic, preDelay, contact, recovery, end, hand, joint, collider);
            this.phaseDamageMulti = damageMulti;
            this.phaseImpactMulti = 1;
            this.phaseArmorNegationMulti = 1;
        }

        public AvalonPhase(float start, float antic, float preDelay, float contact, float recovery, float end, InteractionHand hand, float damageMulti, float phaseImpactMulti, Joint joint, Collider collider) {
            super(start, antic, preDelay, contact, recovery, end, hand, joint, collider);
            this.phaseDamageMulti = damageMulti;
            this.phaseImpactMulti = phaseImpactMulti;
            this.phaseArmorNegationMulti = 1;
        }

        public AvalonPhase(float start, float antic, float preDelay, float contact, float recovery, float end, InteractionHand hand, float damageMulti, float phaseImpactMulti, float phaseArmorNegationMulti, Joint joint, Collider collider) {
            super(start, antic, preDelay, contact, recovery, end, hand, joint, collider);
            this.phaseDamageMulti = damageMulti;
            this.phaseImpactMulti = phaseImpactMulti;
            this.phaseArmorNegationMulti = phaseArmorNegationMulti;
        }

        public AvalonPhase(float start, float antic, float preDelay, float contact, float recovery, float end, InteractionHand hand, Joint joint, Collider collider) {
            super(start, antic, preDelay, contact, recovery, end, hand, joint, collider);
            this.phaseDamageMulti = 1;
            this.phaseImpactMulti = 1;
            this.phaseArmorNegationMulti = 1;
        }

        public AvalonPhase(InteractionHand hand, Joint joint, Collider collider) {
            super(hand, joint, collider);
            this.phaseDamageMulti = 1;
            this.phaseImpactMulti = 1;
            this.phaseArmorNegationMulti = 1;
        }

        public AvalonPhase(float start, float antic, float preDelay, float contact, float recovery, float end, InteractionHand hand, float damageMulti, JointColliderPair... colliders) {
            super(start, antic, preDelay, contact, recovery, end, hand, colliders);
            this.phaseDamageMulti = damageMulti;
            this.phaseImpactMulti = 1;
            this.phaseArmorNegationMulti = 1;
        }

        public AvalonPhase(float start, float antic, float preDelay, float contact, float recovery, float end, InteractionHand hand, float damageMulti, float phaseImpactMulti, JointColliderPair... colliders) {
            super(start, antic, preDelay, contact, recovery, end, hand, colliders);
            this.phaseDamageMulti = damageMulti;
            this.phaseImpactMulti = phaseImpactMulti;
            this.phaseArmorNegationMulti = 1;
        }

        public AvalonPhase(float start, float antic, float preDelay, float contact, float recovery, float end, InteractionHand hand, float damageMulti, float phaseImpactMulti, float phaseArmorNegationMulti, JointColliderPair... colliders) {
            super(start, antic, preDelay, contact, recovery, end, hand, colliders);
            this.phaseDamageMulti = damageMulti;
            this.phaseImpactMulti = phaseImpactMulti;
            this.phaseArmorNegationMulti = phaseArmorNegationMulti;
        }

        public AvalonPhase(float start, float antic, float preDelay, float contact, float recovery, float end, InteractionHand hand, JointColliderPair... colliders) {
            super(start, antic, preDelay, contact, recovery, end, hand, colliders);
            this.phaseDamageMulti = 1;
            this.phaseImpactMulti = 1;
            this.phaseArmorNegationMulti = 1;
        }


        @Override
        public List<Entity> getCollidingEntities(LivingEntityPatch<?> entitypatch, AttackAnimation animation, float prevElapsedTime, float elapsedTime, float attackSpeed) {
            Set<Entity> entities = Sets.newHashSet();
            for (JointColliderPair colliderInfo : this.colliders) {
                Collider collider = colliderInfo.getSecond();
                if (collider == null) {
                    collider = entitypatch.getColliderMatching(this.hand);
                }
                float phasePrev = Math.max(prevElapsedTime, this.start);
                float phaseCurrent = Math.min(elapsedTime, this.end);
                entities.addAll(collider.updateAndSelectCollideEntity(entitypatch, animation, phasePrev, phaseCurrent, colliderInfo.getFirst(), attackSpeed));
            }
            return new ArrayList<>(entities);
        }
    }

    public List<Phase> getActivePhases(float elapsedTime) {
        List<Phase> activePhases = new ArrayList<>();
        for (Phase phase : this.phases) {
            if (elapsedTime >= phase.start && elapsedTime < phase.end) {
                activePhases.add(phase);
            }
        }
        return activePhases;
    }

    public List<Phase> getAttackingPhases(float elapsedTime) {
        List<Phase> activePhases = new ArrayList<>();
        for (Phase phase : this.phases) {
            if (elapsedTime >= phase.start && elapsedTime < phase.end) {
                activePhases.add(phase);
            }
        }
        return activePhases;
    }
}