package com.merlin204.avalon.network.server;

import com.merlin204.avalon.entity.api.collider.IMultiHitBoxEntityPatch;
import com.merlin204.avalon.main.AvalonMOD;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;


public record SPSyncElapsedTime(int entityId,float elapsedTime) implements CustomPacketPayload {
    public static final Type<SPSyncElapsedTime> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(AvalonMOD.MOD_ID, "server_sync_elapsed_time"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SPSyncElapsedTime> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            SPSyncElapsedTime::entityId,
            ByteBufCodecs.FLOAT,
            SPSyncElapsedTime::elapsedTime,
            SPSyncElapsedTime::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void execute(SPSyncElapsedTime packet, IPayloadContext context) {
        Entity entity = context.player().level().getEntity(packet.entityId);
        if (EpicFightCapabilities.getEntityPatch(entity, EntityPatch.class) instanceof IMultiHitBoxEntityPatch multiHitBoxEntityPatch){
            multiHitBoxEntityPatch.getColliderManager().setElapsedTime(packet.elapsedTime);
        }
    }

}
