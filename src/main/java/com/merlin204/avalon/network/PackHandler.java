package com.merlin204.avalon.network;

import com.merlin204.avalon.main.AvalonMOD;
import com.merlin204.avalon.network.client.common.CPSyncHitJointList;
import com.merlin204.avalon.network.client.normal.CPCameraShake;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = AvalonMOD.MOD_ID)
public class PackHandler {



    @SubscribeEvent
    public static void registerPackets(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(AvalonMOD.MOD_ID).versioned("1.0.0").optional();

        // CLIENTBOUND
        registrar.playToClient(CPSyncHitJointList.TYPE, CPSyncHitJointList.STREAM_CODEC, CPSyncHitJointList::execute);
        registrar.playToClient(CPCameraShake.TYPE, CPCameraShake.STREAM_CODEC, CPCameraShake::execute);
        // SERVERBOUND


    }
}
