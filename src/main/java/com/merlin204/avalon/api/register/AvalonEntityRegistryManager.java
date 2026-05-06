package com.merlin204.avalon.api.register;

import com.merlin204.avalon.entity.vfx.VFXEntity;
import com.merlin204.avalon.main.AvalonMOD;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import yesman.epicfight.api.event.EpicFightEventHooks;
import yesman.epicfight.api.event.types.registry.EntityPatchRegistryEvent;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@EventBusSubscriber(modid = AvalonMOD.MOD_ID)
public class AvalonEntityRegistryManager {
    private static final Map<DeferredHolder<EntityType<?>, EntityType<?>>, AvalonAutoRegister> ENTITY_REGISTRY = new HashMap<>();

    public static void registerEpicFightHooks() {
        EpicFightEventHooks.Registry.ENTITY_PATCH.registerEvent(AvalonEntityRegistryManager::handleEntityPatchRegistry);
    }

    static Iterable<Map.Entry<DeferredHolder<EntityType<?>, EntityType<?>>, AvalonAutoRegister>> entityRegistryEntries() {
        return ENTITY_REGISTRY.entrySet();
    }

    public static void scanAndRegisterEntities(Class<?> entitiesClass) {
        try {
            for (java.lang.reflect.Field field : entitiesClass.getDeclaredFields()) {
                if (field.getType().equals(DeferredHolder.class) && field.isAnnotationPresent(AvalonAutoRegister.class)) {
                    field.setAccessible(true);
                    @SuppressWarnings("unchecked")
                    DeferredHolder<EntityType<?>, EntityType<?>> entityRegistry = (DeferredHolder<EntityType<?>, EntityType<?>>) field.get(null);
                    AvalonAutoRegister annotation = field.getAnnotation(AvalonAutoRegister.class);

                    ENTITY_REGISTRY.put(entityRegistry, annotation);
                    AvalonMOD.LOGGER.info("Avalon auto-registered entity: {} -> {}", field.getName(), annotation.value());
                }
            }
        } catch (IllegalAccessException e) {
            AvalonMOD.LOGGER.error("Failed to scan Avalon entity annotations", e);
        }
    }

    public static void handleEntityPatchRegistry(EntityPatchRegistryEvent event) {
        for (Map.Entry<DeferredHolder<EntityType<?>, EntityType<?>>, AvalonAutoRegister> entry : ENTITY_REGISTRY.entrySet()) {
            DeferredHolder<EntityType<?>, EntityType<?>> entity = entry.getKey();
            AvalonAutoRegister annotation = entry.getValue();

            try {
                Function<Entity, EntityPatch<?>> patchFunction = createSimpleEntityPatchFunction(
                        annotation.entityClass(),
                        annotation.entityPatch()
                );

                event.getTypeEntry().put(entity.get(), patchFunction);
            } catch (Exception e) {
                AvalonMOD.LOGGER.error("Failed to register EntityPatch: {} -> {}", entity.getId(), annotation.entityPatch().getSimpleName(), e);
            }
        }
    }

    @net.neoforged.bus.api.SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        for (Map.Entry<DeferredHolder<EntityType<?>, EntityType<?>>, AvalonAutoRegister> entry : ENTITY_REGISTRY.entrySet()) {
            DeferredHolder<EntityType<?>, EntityType<?>> entity = entry.getKey();
            AvalonAutoRegister annotation = entry.getValue();

            if (!annotation.registerAttributes()) {
                continue;
            }

            try {
                AttributeSupplier attributeSupplier = getDefaultAttributes(annotation.entityClass());
                event.put((EntityType<? extends LivingEntity>) entity.get(), attributeSupplier);
                AvalonMOD.LOGGER.debug("Registered Avalon entity attributes: {} -> {}", entity.getId(), annotation.entityClass().getSimpleName());
            } catch (Exception e) {
                AvalonMOD.LOGGER.error("Failed to register Avalon entity attributes: {}", entity.getId(), e);
            }
        }
    }

    private static AttributeSupplier getDefaultAttributes(Class<?> entityClass) {
        try {
            java.lang.reflect.Method method = entityClass.getDeclaredMethod("getDefaultAttributes");
            method.setAccessible(true);
            return (AttributeSupplier) method.invoke(null);
        } catch (NoSuchMethodException e) {
            try {
                AvalonMOD.LOGGER.warn("Entity class {} has no getDefaultAttributes method, using VFX defaults", entityClass.getSimpleName());
                return VFXEntity.getDefaultAttribute();
            } catch (Exception ex) {
                throw new RuntimeException("Unable to resolve default entity attributes", ex);
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to resolve entity attributes: " + entityClass.getName(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private static Function<Entity, EntityPatch<?>> createSimpleEntityPatchFunction(Class<?> entityClass, Class<?> patchClass) {
        return entity -> {
            try {
                Constructor<?> constructor = patchClass.getDeclaredConstructor(entityClass);
                return (EntityPatch<?>) constructor.newInstance(entityClass.cast(entity));
            } catch (Exception e) {
                AvalonMOD.LOGGER.error("Failed to create Avalon EntityPatch", e);
                throw new RuntimeException("Avalon cannot create EntityPatch: " + patchClass.getName(), e);
            }
        };
    }
}
