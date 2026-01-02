package com.merlin204.avalon.epicfight.api;

import yesman.epicfight.api.animation.property.AnimationProperty;

import java.util.List;

public class AvalonAnimationProperty {

    public static final AnimationProperty.AttackAnimationProperty<List<AnimationAttackEvent<?>>> ATTACK_EVENTS = new AnimationProperty.AttackAnimationProperty<List<AnimationAttackEvent<?>>>();

    public static final AnimationProperty.AttackAnimationProperty<List<AnimationAttackResultEvent<?>>> ATTACK_RESULT_EVENTS = new AnimationProperty.AttackAnimationProperty<List<AnimationAttackResultEvent<?>>>();

    public static final AnimationProperty.AttackAnimationProperty<List<AnimationRenderEvent<?>>> RENDER_EVENTS = new AnimationProperty.AttackAnimationProperty<List<AnimationRenderEvent<?>>>();

    public static final AnimationProperty.AttackAnimationProperty<List<AnimationBeAttackEvent<?>>> BE_ATTACK_EVENTS = new AnimationProperty.AttackAnimationProperty<List<AnimationBeAttackEvent<?>>>();
}

