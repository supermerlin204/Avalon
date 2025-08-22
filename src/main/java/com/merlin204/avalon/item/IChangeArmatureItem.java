package com.merlin204.avalon.item;

import com.merlin204.avalon.mixin.LivingEntityPatchMixin;
import net.minecraft.world.phys.Vec2;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public interface IChangeArmatureItem {


    default Armatures.ArmatureAccessor<? extends Armature> getArmature(){
        return null;
    }


    default Vec2 getHitBox(){
        return null;
    }

    default boolean change(LivingEntityPatch<?> livingEntityPatch){
        return true;
    }

}
