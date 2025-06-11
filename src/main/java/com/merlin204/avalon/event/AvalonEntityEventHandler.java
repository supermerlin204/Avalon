package com.merlin204.avalon.event;

import com.google.gson.JsonElement;
import com.merlin204.avalon.entity.AvalonEntities;
import com.merlin204.avalon.entity.client.renderer.AvalonRendererPatch;
import com.merlin204.avalon.entity.client.renderer.EmptyRenderer;
import com.merlin204.avalon.entity.client.renderer.RenderMeshItem;
import com.merlin204.avalon.entity.vfx.VFXEntity;
import com.merlin204.avalon.entity.vfx.VFXEntityPatch;
import com.merlin204.avalon.epicfight.AvalonArmatures;
import com.merlin204.avalon.item.AvalonItems;
import com.merlin204.avalon.main.AvalonMOD;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import yesman.epicfight.api.client.forgeevent.PatchedRenderersEvent;
import yesman.epicfight.api.forgeevent.EntityPatchRegistryEvent;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.main.EpicFightMod;

import java.util.Map;
import java.util.function.Function;

@Mod.EventBusSubscriber(modid = AvalonMOD.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AvalonEntityEventHandler {

    @SubscribeEvent
    public static void handleEntityPatchRegistry(EntityPatchRegistryEvent event) {
        event.getTypeEntry().put(AvalonEntities.SHAKE_WAVE.get(), (entity -> VFXEntityPatch::new));
    }

    @SubscribeEvent
    public static void entityAttributeEvent(EntityAttributeCreationEvent event) {

        event.put(AvalonEntities.SHAKE_WAVE.get(), VFXEntity.getDefaultAttribute());
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(AvalonArmatures::registerArmatures);

    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void registerRenderers(PatchedRenderersEvent.RegisterItemRenderer event) {

            event.addItemRenderer(
                    ResourceLocation.fromNamespaceAndPath(AvalonMOD.MOD_ID, "mesh_item"),
                    RenderMeshItem::new
            );
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void handleClientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(AvalonEntities.SHAKE_WAVE.get(), EmptyRenderer::new);
    }


    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void handlePatchedRenderers(PatchedRenderersEvent.Add event) {
        event.addPatchedEntityRenderer(AvalonEntities.SHAKE_WAVE.get(),entityType -> new AvalonRendererPatch(event.getContext(),entityType).initLayerLast(event.getContext(),entityType));
    }
}
