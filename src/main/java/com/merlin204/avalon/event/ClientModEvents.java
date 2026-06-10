package com.merlin204.avalon.event;

import com.merlin204.avalon.block.AvalonBlocks;
import com.merlin204.avalon.block.client.MeshBlockEntityRender;
import com.merlin204.avalon.client.particle.AvalonAnimationTrailParticle;
import com.merlin204.avalon.client.particle.AvalonEntityAfterImageParticle;
import com.merlin204.avalon.client.particle.AvalonInterpolationEntityAfterImageParticle;
import com.merlin204.avalon.main.AvalonMOD;
import com.merlin204.avalon.particle.AvalonParticles;


import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = AvalonMOD.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onParticleRegistry(final RegisterParticleProvidersEvent event) {
        event.registerSpecial(AvalonParticles.AVALON_TRAIL.get(), new AvalonAnimationTrailParticle.Provider());
        event.registerSpecial(AvalonParticles.AVALON_ENTITY_AFTER_IMAGE.get(), new AvalonEntityAfterImageParticle.Provider());
        event.registerSpecial(AvalonParticles.AVALON_INTERPOLATION_ENTITY_AFTER_IMAGE.get(), new AvalonInterpolationEntityAfterImageParticle.Provider());
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        BlockEntityRenderers.register(
                AvalonBlocks.MESH_BLOCK_ENTITY.get(),
                context -> new MeshBlockEntityRender()
        );
        BlockEntityRenderers.register(
                AvalonBlocks.TEST_DOOR_ENTITY.get(),
                context -> new MeshBlockEntityRender()
        );
    }







}