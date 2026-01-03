package com.merlin204.avalon.network;


import com.merlin204.avalon.main.AvalonMOD;
import com.merlin204.avalon.network.server.ShakeCameraPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(AvalonMOD.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int nextPacketId = 0;

    public static void registerPackets() {
        registerPacket(ShakeCameraPacket.class, ShakeCameraPacket::encode, ShakeCameraPacket::decode, ShakeCameraPacket::handle);
    }

    private static <T> void registerPacket(Class<T> packetClass,
                                           BiConsumer<T, FriendlyByteBuf> encoder,
                                           Function<FriendlyByteBuf, T> decoder,
                                           BiConsumer<T, Supplier<NetworkEvent.Context>> handler) {

        INSTANCE.registerMessage(
                nextPacketId++,
                packetClass,
                encoder,
                decoder,
                (msg, ctx) -> {
                    handler.accept(msg, ctx);
                    ctx.get().setPacketHandled(true);
                }
        );
    }

    public static void sendToServer(Object packet) {
        INSTANCE.sendToServer(packet);
    }

    public static void sendToClient(Object packet, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    public static void sendToAllClient(Object packet) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), packet);
    }
}
