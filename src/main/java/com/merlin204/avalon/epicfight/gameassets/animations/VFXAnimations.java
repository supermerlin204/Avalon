package com.merlin204.avalon.epicfight.gameassets.animations;

import com.merlin204.avalon.epicfight.animations.AutoDiscardActionAnimation;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation;
import com.merlin204.avalon.main.AvalonMOD;
import net.minecraft.world.InteractionHand;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.gameasset.Armatures;

import static com.merlin204.avalon.util.AvalonAnimationUtils.createSimplePhase;

public class VFXAnimations {
    public static AnimationManager.AnimationAccessor<StaticAnimation> SHAKEWAVE_IDLE;
    public static AnimationManager.AnimationAccessor<AutoDiscardActionAnimation> SHAKEWAVE_1;

    public static AnimationManager.AnimationAccessor<ActionAnimation> TEST;
    public static AnimationManager.AnimationAccessor<StaticAnimation> EMPTY;

    public static AnimationManager.AnimationAccessor<StaticAnimation> OPEN_TEST_DOOR;



    public static void buildVFXAnimations(AnimationManager.AnimationBuilder builder) {
        Armatures.ArmatureAccessor<Armature> shake_wave = Armatures.ArmatureAccessor.create(AvalonMOD.MOD_ID, "entity/vfx/shakewave", Armature::new);
        Armatures.ArmatureAccessor<Armature> test_door = Armatures.ArmatureAccessor.create(AvalonMOD.MOD_ID, "testdoor", Armature::new);

        SHAKEWAVE_IDLE = builder.nextAccessor("vfx/shakewave_idle", accessor -> new StaticAnimation(0.15F,true, accessor, shake_wave));
        SHAKEWAVE_1 = builder.nextAccessor("vfx/shakewave_1", accessor -> new AutoDiscardActionAnimation(0.0F, accessor, shake_wave,1.5F));
        EMPTY = builder.nextAccessor("vfx/empty", accessor -> new StaticAnimation(0.15F,true, accessor, shake_wave));
        OPEN_TEST_DOOR = builder.nextAccessor("testdoor", accessor -> new StaticAnimation(0.15F,false, accessor, test_door));


        TEST = builder.nextAccessor("ymds1", accessor -> new ActionAnimation(0.1F, accessor, Armatures.ArmatureAccessor.create(AvalonMOD.MOD_ID, "ymd1", Armature::new)));




    }




}
