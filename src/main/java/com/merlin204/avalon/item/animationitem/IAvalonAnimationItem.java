package com.merlin204.avalon.item.animationitem;

import net.minecraft.world.phys.Vec2;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.gameasset.Armatures;

import java.util.HashMap;
import java.util.Map;

public interface IAvalonAnimationItem {

    Map<Integer,Boolean> ARMATURE_MANAGER = new HashMap<>();

    Armatures.ArmatureAccessor<? extends Armature> BIPED = Armatures.BIPED;


    default Armatures.ArmatureAccessor<? extends Armature> getArmature(){
        return null;
    }

    default Vec2 getHitBox(){
        return null;
    }

    default boolean useAnimationArmature(int entityId){
        if (!ARMATURE_MANAGER.containsKey(entityId)) {
            ARMATURE_MANAGER.put(entityId, false);
        }
        return ARMATURE_MANAGER.get(entityId);
    }

    default void setUseAnimationArmature(int entityId,boolean b){
        if (ARMATURE_MANAGER.containsKey(entityId)){
            ARMATURE_MANAGER.replace(entityId,b);
        }else {
            ARMATURE_MANAGER.put(entityId,b);
        }
    }


}
