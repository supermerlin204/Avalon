package com.merlin204.avalon.shaderpass;

import com.merlin204.avalon.main.AvalonMOD;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.joml.Vector4f;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class AirDistortionPost {
    private static AirDistortionPost instance;
    private ShaderInstance distortionShader;
    private final List<Vector4f> activeParticles = new ArrayList<>();
    private static final int MAX_PARTICLES = 10;

    public static AirDistortionPost getInstance() {
        if (instance == null) {
            instance = new AirDistortionPost();
        }
        return instance;
    }

    @SubscribeEvent
    public void registerShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(
                new ShaderInstance(
                        event.getResourceProvider(),
                       ResourceLocation.fromNamespaceAndPath(AvalonMOD.MOD_ID, "air_distortion"),
                        DefaultVertexFormat.POSITION_TEX
                ),
                shader -> this.distortionShader = shader
        );
    }

    public void addParticlePosition(Vec3 position, float intensity) {
        if (activeParticles.size() < MAX_PARTICLES) {
            activeParticles.add(new Vector4f((float)position.x, (float)position.y, (float)position.z, intensity));
        }
    }

    public void applyPostEffect(float partialTick) {

        System.out.println(distortionShader);

    }
}
