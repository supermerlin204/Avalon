package com.merlin204.avalon.event;

import com.merlin204.avalon.client.particle.AvalonAnimationTrailParticle;
import com.merlin204.avalon.client.particle.AvalonEntityAfterImageParticle;
import com.merlin204.avalon.main.AvalonMOD;
import com.merlin204.avalon.particle.AvalonParticles;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import yesman.epicfight.api.client.forgeevent.PatchedRenderersEvent;
import yesman.epicfight.world.capabilities.provider.EntityPatchProvider;

@Mod.EventBusSubscriber(modid = AvalonMOD.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onParticleRegistry(final RegisterParticleProvidersEvent event) {
        event.registerSpecial(AvalonParticles.AVALON_TRAIL.get(), new AvalonAnimationTrailParticle.Provider());
        event.registerSpecial(AvalonParticles.AVALON_ENTITY_AFTER_IMAGE.get(), new AvalonEntityAfterImageParticle.Provider());


    }




}