package com.merlin204.avalon.epicfight.gameassets.animations;

import com.merlin204.avalon.entity.vfx.shakewave.ShakeWaveEntity;
import com.merlin204.avalon.epicfight.AvalonAnimations;
import com.merlin204.avalon.epicfight.AvalonArmatures;
import com.merlin204.avalon.epicfight.animations.AutoDiscardAttackAnimation;
import com.merlin204.avalon.main.AvalonMOD;
import com.merlin204.avalon.util.AvalonAnimationUtils;
import net.minecraft.world.InteractionHand;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.collider.MultiOBBCollider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.gameasset.Armatures;

public class VFXAnimations {
    public static AnimationManager.AnimationAccessor<StaticAnimation> SHAKEWAVE_IDLE;
    public static AnimationManager.AnimationAccessor<AutoDiscardAttackAnimation> SHAKEWAVE_1;



    public static void buildVFXAnimations(AnimationManager.AnimationBuilder builder) {
        Armatures.ArmatureAccessor<Armature> shake_wave = AvalonArmatures.SHAKE_WAVE_ARMATURE;

        SHAKEWAVE_IDLE = builder.nextAccessor("vfx/shakewave_idle", accessor -> new StaticAnimation(0.15F,true, accessor, shake_wave));
        SHAKEWAVE_1 = builder.nextAccessor("vfx/shakewave_1", accessor -> new AutoDiscardAttackAnimation(0.0F, accessor, shake_wave,1F,0.1F,
                AvalonAnimationUtils.createSimplePhase(0,60,80, InteractionHand.MAIN_HAND,shake_wave.get().searchJointByName("Root_1"), new MultiOBBCollider(5, 1, 1, 1, 0, 0, 0))));




    }




}
