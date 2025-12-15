package com.merlin204.avalon.network.server;

import com.merlin204.avalon.client.CameraShake;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ShakeCameraPacket {
    private final int duration;
    private final float intensity;
    private final float frequency;
    private final Vec3 center;
    private final float radius;

    public ShakeCameraPacket(int duration, float intensity, float frequency, Vec3 center, float radius) {
        this.duration = duration;
        this.intensity = intensity;
        this.frequency = frequency;
        this.center = center;
        this.radius = radius;
    }

    public static void encode(ShakeCameraPacket msg, FriendlyByteBuf buffer) {
        buffer.writeInt(msg.duration);
        buffer.writeFloat(msg.intensity);
        buffer.writeFloat(msg.frequency);
        buffer.writeDouble(msg.center.x);
        buffer.writeDouble(msg.center.y);
        buffer.writeDouble(msg.center.z);
        buffer.writeFloat(msg.radius);
    }

    public static ShakeCameraPacket decode(FriendlyByteBuf buffer) {
        return new ShakeCameraPacket(
                buffer.readInt(),
                buffer.readFloat(),
                buffer.readFloat(),
                new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()),
                buffer.readFloat()
        );
    }

    public static void handle(ShakeCameraPacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            // 在客户端执行震动
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                    CameraShake.shake(msg.duration, msg.intensity, msg.frequency, msg.center, msg.radius)
            );
        });
        context.setPacketHandled(true);
    }
}
