package com.merlin204.avalon.item;

import net.minecraft.world.phys.Vec2;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.gameasset.Armatures;

public interface IChangeArmatureItem {


    default Armatures.ArmatureAccessor<? extends Armature> getArmature(){
        return null;
    }


    default Vec2 getHitBox(){
        return null;
    }

}
