package com.merlin204.avalon.epicfight;

import com.merlin204.avalon.epicfight.gameassets.animations.VFXAnimations;
import com.merlin204.avalon.main.AvalonMOD;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import yesman.epicfight.api.animation.AnimationManager;

@EventBusSubscriber(modid = AvalonMOD.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class AvalonAnimations {

    @SubscribeEvent
    public static void registerAnimations(AnimationManager.AnimationRegistryEvent event) {
        event.newBuilder(AvalonMOD.MOD_ID, (builder)->{
            VFXAnimations.buildVFXAnimations(builder);
        });
    }


}
