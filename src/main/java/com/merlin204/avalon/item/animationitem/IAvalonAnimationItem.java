package com.merlin204.avalon.item.animationitem;

import net.minecraft.world.phys.Vec2;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.gameasset.Armatures;

public interface IAvalonAnimationItem {

    Armature BIPED = Armatures.BIPED.get();



    default Armatures.ArmatureAccessor<? extends Armature> getArmature(){
        return null;
    }

    default Vec2 getHitBox(){
        return null;
    }

}
