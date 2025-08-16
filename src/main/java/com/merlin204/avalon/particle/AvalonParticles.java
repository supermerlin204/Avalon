package com.merlin204.avalon.particle;

import com.merlin204.avalon.main.AvalonMOD;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;


public class AvalonParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, AvalonMOD.MOD_ID);


    public static final DeferredHolder<ParticleType<?>,SimpleParticleType> AVALON_TRAIL = PARTICLES.register("avalon_trail", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>,SimpleParticleType> AVALON_AD_TRAIL = PARTICLES.register("avalon_ad_trail", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>,SimpleParticleType> AVALON_ENTITY_AFTER_IMAGE = PARTICLES.register("avalon_entity_after_image", () -> new SimpleParticleType(true));


}
