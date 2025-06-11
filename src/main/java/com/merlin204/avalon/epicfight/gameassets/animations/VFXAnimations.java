package com.merlin204.avalon.epicfight.gameassets.animations;

import com.merlin204.avalon.entity.vfx.shakewave.ShakeWaveEntity;
import com.merlin204.avalon.epicfight.AvalonAnimations;
import com.merlin204.avalon.epicfight.AvalonArmatures;
import com.merlin204.avalon.epicfight.animations.AutoDiscardActionAnimation;
import com.merlin204.avalon.epicfight.animations.AutoDiscardAttackAnimation;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation;
import com.merlin204.avalon.main.AvalonMOD;
import com.merlin204.avalon.util.AvalonAnimationUtils;
import net.minecraft.world.InteractionHand;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.collider.MultiOBBCollider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.gameasset.Armatures;

import static com.merlin204.avalon.util.AvalonAnimationUtils.createSimplePhase;

public class VFXAnimations {
    public static AnimationManager.AnimationAccessor<StaticAnimation> SHAKEWAVE_IDLE;
    public static AnimationManager.AnimationAccessor<AutoDiscardActionAnimation> SHAKEWAVE_1;

    public static AnimationManager.AnimationAccessor<AvalonAttackAnimation> TEST;




    public static void buildVFXAnimations(AnimationManager.AnimationBuilder builder) {
        Armatures.ArmatureAccessor<Armature> shake_wave = AvalonArmatures.SHAKE_WAVE_ARMATURE;

        SHAKEWAVE_IDLE = builder.nextAccessor("vfx/shakewave_idle", accessor -> new StaticAnimation(0.15F,true, accessor, shake_wave));
        SHAKEWAVE_1 = builder.nextAccessor("vfx/shakewave_1", accessor -> new AutoDiscardActionAnimation(0.0F, accessor, shake_wave,1.5F));

        TEST = builder.nextAccessor("vfx/test", accessor -> new AvalonAttackAnimation(0.1F, accessor, Armatures.BIPED,1,0.1F,
                createSimplePhase(30,36,50, InteractionHand.MAIN_HAND,0.5F,Armatures.BIPED.get().toolR,null),
                createSimplePhase(63,70,96, InteractionHand.MAIN_HAND,0.5F,Armatures.BIPED.get().toolR,null)));




    }




}
