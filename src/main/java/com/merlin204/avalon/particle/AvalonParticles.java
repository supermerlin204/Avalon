package com.merlin204.avalon.particle;

import com.merlin204.avalon.main.AvalonMOD;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AvalonParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, AvalonMOD.MOD_ID);


    public static final RegistryObject<SimpleParticleType> AVALON_TRAIL = PARTICLES.register("avalon_trail", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> AVALON_ENTITY_AFTER_IMAGE = PARTICLES.register("avalon_entity_after_image", () -> new SimpleParticleType(true));


}
