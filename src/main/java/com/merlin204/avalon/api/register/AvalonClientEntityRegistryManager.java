package com.merlin204.avalon.api.register;

import com.merlin204.avalon.main.AvalonMOD;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import yesman.epicfight.api.client.event.EpicFightClientEventHooks;
import yesman.epicfight.api.client.event.types.registry.RegisterPatchedRenderersEvent;
import yesman.epicfight.client.renderer.patched.entity.PatchedEntityRenderer;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.function.Function;

public final class AvalonClientEntityRegistryManager {
    private AvalonClientEntityRegistryManager() {
    }

    public static void init(IEventBus modEventBus) {
        modEventBus.addListener(AvalonClientEntityRegistryManager::handleClientSetup);
        EpicFightClientEventHooks.Registry.ADD_PATCHED_ENTITY.registerEvent(AvalonClientEntityRegistryManager::handlePatchedRenderers);
    }

    private static void handleClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            for (Map.Entry<DeferredHolder<EntityType<?>, EntityType<?>>, AvalonAutoRegister> entry : AvalonEntityRegistryManager.entityRegistryEntries()) {
                DeferredHolder<EntityType<?>, EntityType<?>> entity = entry.getKey();
                AvalonAutoRegister annotation = entry.getValue();

                EntityRendererProvider<?> rendererProvider = createEntityRendererProvider(annotation.clientRenderer());
                EntityType<? extends Entity> entityType = entity.get();

                @SuppressWarnings("unchecked")
                EntityRendererProvider<Entity> provider = (EntityRendererProvider<Entity>) rendererProvider;

                EntityRenderers.register(entityType, provider);
                AvalonMOD.LOGGER.debug("Avalon registered EntityRender: {} -> {}", entity.getId(), annotation.clientRenderer());
            }
        });
    }

    private static void handlePatchedRenderers(RegisterPatchedRenderersEvent.AddEntity event) {
        for (Map.Entry<DeferredHolder<EntityType<?>, EntityType<?>>, AvalonAutoRegister> entry : AvalonEntityRegistryManager.entityRegistryEntries()) {
            DeferredHolder<EntityType<?>, EntityType<?>> entity = entry.getKey();
            AvalonAutoRegister annotation = entry.getValue();

            Function<EntityType<?>, PatchedEntityRenderer> renderPatchFunction =
                    createRenderPatchFunction(annotation.renderPatch(), event.getContext());

            event.addPatchedEntityRenderer(entity.get(), renderPatchFunction);
            AvalonMOD.LOGGER.debug("Avalon registered RenderPatch: {} -> {}", entity.getId(), annotation.renderPatch());
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityRendererProvider<?> createEntityRendererProvider(String className) {
        return context -> {
            try {
                Class<?> rendererClass = Class.forName(className);
                Constructor<?> constructor = rendererClass.getDeclaredConstructor(EntityRendererProvider.Context.class);
                return (EntityRenderer) constructor.newInstance(context);
            } catch (Exception e) {
                throw new RuntimeException("Avalon cannot create EntityRender: " + className, e);
            }
        };
    }

    private static Function<EntityType<?>, PatchedEntityRenderer> createRenderPatchFunction(
            String className, EntityRendererProvider.Context context) {
        return entityType -> {
            try {
                Class<?> renderPatchClass = Class.forName(className);
                Constructor<?> constructor = renderPatchClass.getDeclaredConstructor(
                        EntityRendererProvider.Context.class, EntityType.class
                );

                constructor.setAccessible(true);
                PatchedEntityRenderer renderer = (PatchedEntityRenderer) constructor.newInstance(context, entityType);

                try {
                    java.lang.reflect.Method initLayerLast = renderPatchClass.getMethod(
                            "initLayerLast", EntityRendererProvider.Context.class, EntityType.class
                    );
                    initLayerLast.invoke(renderer, context, entityType);
                } catch (NoSuchMethodException ignored) {
                }

                return renderer;
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                return null;
            } catch (Exception e) {
                return null;
            }
        };
    }
}
