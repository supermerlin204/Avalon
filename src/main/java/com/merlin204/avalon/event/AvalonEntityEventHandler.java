package com.merlin204.avalon.event;

import com.merlin204.avalon.entity.AvalonEntities;
import com.merlin204.avalon.entity.client.renderer.*;
import com.merlin204.avalon.entity.client.renderer.patch.entity.AvalonRendererPatch;
import com.merlin204.avalon.entity.client.renderer.patch.item.RenderAnimationItem;
import com.merlin204.avalon.entity.client.renderer.patch.item.RenderChangeMeshItem;
import com.merlin204.avalon.entity.client.renderer.patch.item.RenderMeshItem;
import com.merlin204.avalon.entity.vfx.VFXEntity;
import com.merlin204.avalon.entity.vfx.VFXEntityPatch;
import com.merlin204.avalon.main.AvalonMOD;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import yesman.epicfight.api.client.forgeevent.PatchedRenderersEvent;
import yesman.epicfight.api.forgeevent.EntityPatchRegistryEvent;

@Mod.EventBusSubscriber(modid = AvalonMOD.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AvalonEntityEventHandler {

    //绑定Patch
    @SubscribeEvent
    public static void handleEntityPatchRegistry(EntityPatchRegistryEvent event) {
        event.getTypeEntry().put(AvalonEntities.SHAKE_WAVE.get(), (entity -> VFXEntityPatch::new));
        event.getTypeEntry().put(AvalonEntities.VFX.get(), (entity -> VFXEntityPatch::new));
    }
    //默认属性
    @SubscribeEvent
    public static void entityAttributeEvent(EntityAttributeCreationEvent event) {

        event.put(AvalonEntities.SHAKE_WAVE.get(), VFXEntity.getDefaultAttribute());
        event.put(AvalonEntities.VFX.get(), VFXEntity.getDefaultAttribute());

    }
    //绑定空渲染
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void handleClientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(AvalonEntities.SHAKE_WAVE.get(), EmptyRenderer::new);
        EntityRenderers.register(AvalonEntities.VFX.get(), EmptyRenderer::new);
    }
    //绑定renderPatch
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void handlePatchedRenderers(PatchedRenderersEvent.Add event) {
        event.addPatchedEntityRenderer(AvalonEntities.SHAKE_WAVE.get(),entityType -> new AvalonRendererPatch(event.getContext(),entityType).initLayerLast(event.getContext(),entityType));
        event.addPatchedEntityRenderer(AvalonEntities.VFX.get(),entityType -> new AvalonRendererPatch(event.getContext(),entityType).initLayerLast(event.getContext(),entityType));
    }





    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void registerRenderers(PatchedRenderersEvent.RegisterItemRenderer event) {
        event.addItemRenderer(
                ResourceLocation.fromNamespaceAndPath(AvalonMOD.MOD_ID, "mesh_item"),
                RenderMeshItem::new
        );
        event.addItemRenderer(
                ResourceLocation.fromNamespaceAndPath(AvalonMOD.MOD_ID, "change_mesh_item"),
                RenderChangeMeshItem::new
        );
        event.addItemRenderer(
                ResourceLocation.fromNamespaceAndPath(AvalonMOD.MOD_ID, "animation_item"),
                RenderAnimationItem::new
        );
    }
}
