package com.merlin204.avalon.network.client.common;

import com.merlin204.avalon.entity.api.collider.IMultiHitBoxEntityPatch;
import com.merlin204.avalon.main.AvalonMOD;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;

import java.util.ArrayList;
import java.util.List;

public record CPSyncHitJointList(int entityId, CompoundTag tag) implements CustomPacketPayload {
    public static final Type<CPSyncHitJointList> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(AvalonMOD.MOD_ID, "sync_hit_joint_list"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CPSyncHitJointList> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            CPSyncHitJointList::entityId,
            ByteBufCodecs.COMPOUND_TAG,
            CPSyncHitJointList::tag,
            CPSyncHitJointList::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void execute(CPSyncHitJointList packet, IPayloadContext context) {
        if(Minecraft.getInstance().player != null && Minecraft.getInstance().level != null){
            Entity entity = Minecraft.getInstance().level.getEntity(packet.entityId);
            if (EpicFightCapabilities.getEntityPatch(entity, EntityPatch.class) instanceof IMultiHitBoxEntityPatch multiHitBoxEntity){
                List<Integer> hit = new ArrayList<>();
                for (int i:packet.tag.getIntArray("hit")){
                    hit.add(i);
                }
                multiHitBoxEntity.getColliderManager().setHitList(hit);
            }
        }
    }
}
