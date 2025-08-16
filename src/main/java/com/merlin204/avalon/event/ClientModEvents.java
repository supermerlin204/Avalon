package com.merlin204.avalon.event;

import com.merlin204.avalon.client.particle.AvalonADAnimationTrailParticle;
import com.merlin204.avalon.client.particle.AvalonAnimationTrailParticle;
import com.merlin204.avalon.client.particle.AvalonEntityAfterImageParticle;
import com.merlin204.avalon.main.AvalonMOD;
import com.merlin204.avalon.particle.AvalonParticles;


import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;


@EventBusSubscriber(modid = AvalonMOD.MOD_ID, value = Dist.CLIENT)
public class ClientModEvents {


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onParticleRegistry(final RegisterParticleProvidersEvent event) {
        event.registerSpecial(AvalonParticles.AVALON_TRAIL.get(), new AvalonAnimationTrailParticle.Provider());
        event.registerSpecial(AvalonParticles.AVALON_AD_TRAIL.get(), new AvalonADAnimationTrailParticle.Provider());
//        event.registerSpecial(AvalonParticles.AVALON_ENTITY_AFTER_IMAGE.get(), new AvalonEntityAfterImageParticle.Provider());


    }







}