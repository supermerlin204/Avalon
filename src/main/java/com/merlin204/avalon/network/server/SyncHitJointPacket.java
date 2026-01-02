package com.merlin204.avalon.network.server;

import com.merlin204.avalon.client.CameraShake;
import com.merlin204.avalon.entity.api.collider.IMultiHitBoxEntityPatch;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SyncHitJointPacket {
    private final CompoundTag tag;
    private final int id;

    public SyncHitJointPacket(CompoundTag tag, int id) {
        this.tag = tag;
        this.id = id;
    }


    public static void encode(SyncHitJointPacket msg, FriendlyByteBuf buffer) {
        buffer.writeNbt(msg.tag);
        buffer.writeInt(msg.id);

    }

    public static SyncHitJointPacket decode(FriendlyByteBuf buffer) {
        return new SyncHitJointPacket(
                buffer.readNbt(),
                buffer.readInt()
        );
    }

    public static void handle(SyncHitJointPacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->{
                if(Minecraft.getInstance().player != null && Minecraft.getInstance().level != null){
                    Entity entity = Minecraft.getInstance().level.getEntity(msg.id);
                    if (EpicFightCapabilities.getEntityPatch(entity, EntityPatch.class) instanceof IMultiHitBoxEntityPatch multiHitBoxEntity){
                        List<Integer> hit = new ArrayList<>();
                        for (int i:msg.tag.getIntArray("hit")){
                            hit.add(i);
                        }
                        multiHitBoxEntity.getColliderManager().setHitList(hit);
                    }
                }

                    }
            );
        });
        context.setPacketHandled(true);
    }
}
