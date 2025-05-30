package com.merlin204.avalon.epicfight;

import com.merlin204.avalon.entity.AvalonEntities;
import com.merlin204.avalon.main.AvalonMOD;
import net.minecraft.world.entity.EntityType;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.gameasset.Armatures;

public class AvalonArmatures {
    public static Armatures.ArmatureAccessor<Armature> SHAKE_WAVE_ARMATURE = Armatures.ArmatureAccessor.create(AvalonMOD.MOD_ID, "entity/vfx/shakewave", Armature::new);


    public static void registerArmatures(){
        Armatures.registerEntityTypeArmature(AvalonEntities.SHAKE_WAVE.get(), SHAKE_WAVE_ARMATURE);


    }

}
