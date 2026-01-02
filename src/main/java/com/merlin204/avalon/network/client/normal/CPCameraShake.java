package com.merlin204.avalon.network.client.normal;


import com.merlin204.avalon.client.CameraShake;
import com.merlin204.avalon.main.AvalonMOD;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import yesman.epicfight.api.utils.math.Vec3f;

public record CPCameraShake(int duration, float intensity, float frequency, Vector3f pos, float radius) implements CustomPacketPayload {
    public static final Type<CPCameraShake> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(AvalonMOD.MOD_ID, "camera_shake"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CPCameraShake> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            CPCameraShake::duration,
            ByteBufCodecs.FLOAT,
            CPCameraShake::intensity,
            ByteBufCodecs.FLOAT,
            CPCameraShake::frequency,
            ByteBufCodecs.VECTOR3F,
            CPCameraShake::pos,
            ByteBufCodecs.FLOAT,
            CPCameraShake::radius,
            CPCameraShake::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void execute(CPCameraShake packet, IPayloadContext context) {
        if(Minecraft.getInstance().player != null && Minecraft.getInstance().level != null){
            CameraShake.shake(packet.duration, packet.intensity, packet.frequency, new Vec3(packet.pos), packet.radius);
        }
    }
}
