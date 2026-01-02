package com.merlin204.avalon.api;

import com.merlin204.avalon.entity.vfx.VFXEntity;
import com.merlin204.avalon.main.AvalonMOD;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import yesman.epicfight.api.client.neoevent.PatchedRenderersEvent;
import yesman.epicfight.api.neoevent.EntityPatchRegistryEvent;
import yesman.epicfight.client.renderer.patched.entity.PatchedEntityRenderer;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 实体注解自动注册器
 */
@EventBusSubscriber(modid = AvalonMOD.MOD_ID)
public class AvalonEntityRegistryManager {

    private static final Map<DeferredHolder<EntityType<?>,EntityType<?>>, AvalonAutoRegister> ENTITY_REGISTRY = new HashMap<>();

    /**
     * 扫描并注册所有带有注解的实体
     */
    public static void scanAndRegisterEntities(Class<?> entitiesClass) {
        try {
            for (java.lang.reflect.Field field : entitiesClass.getDeclaredFields()) {
                if (field.getType().equals(DeferredHolder.class) && field.isAnnotationPresent(AvalonAutoRegister.class)) {
                    field.setAccessible(true);
                    DeferredHolder<EntityType<?>,EntityType<?>> entityRegistry = (DeferredHolder<EntityType<?>,EntityType<?>>) field.get(null);
                    AvalonAutoRegister annotation = field.getAnnotation(AvalonAutoRegister.class);

                    ENTITY_REGISTRY.put(entityRegistry, annotation);
                    AvalonMOD.LOGGER.info("自动注册实体: {} -> {}", field.getName(), annotation.value());
                }
            }
        } catch (IllegalAccessException e) {
            AvalonMOD.LOGGER.error("扫描实体注解时发生错误", e);
        }
    }

    /**
     * 处理 EntityPatch 注册
     */
    @SubscribeEvent
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
//                TDEMOD.LOGGER.debug("Avalon自动注册 EntityPatch: {} -> {}", entity.getId(), annotation.entityPatch().getSimpleName());

            } catch (Exception e) {
                AvalonMOD.LOGGER.error("注册 EntityPatch 失败: {} -> {}", entity.getId(), annotation.entityPatch().getSimpleName(), e);
            }
        }
    }

    /**
     * 处理 EntityRender 注册
     */
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void handleClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            for (Map.Entry< DeferredHolder<EntityType<?>,EntityType<?>>, AvalonAutoRegister> entry : ENTITY_REGISTRY.entrySet()) {
                DeferredHolder<EntityType<?>,EntityType<?>> entity = entry.getKey();
                AvalonAutoRegister annotation = entry.getValue();

                EntityRendererProvider<?> rendererProvider = createEntityRendererProvider(annotation.clientRenderer());


                EntityType<? extends Entity> entityType = entity.get();

                @SuppressWarnings("unchecked")
                EntityRendererProvider<Entity> provider = (EntityRendererProvider<Entity>) rendererProvider;

                EntityRenderers.register(entityType, provider);

                AvalonMOD.LOGGER.debug("Avalon自动注册 EntityRender: {} -> {}", entity.getId(), annotation.clientRenderer());
            }
        });
    }

    /**
     * 处理 RenderPatch 注册
     */
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void handlePatchedRenderers(PatchedRenderersEvent.Add event) {
        for (Map.Entry< DeferredHolder<EntityType<?>,EntityType<?>>, AvalonAutoRegister> entry : ENTITY_REGISTRY.entrySet()) {
            DeferredHolder<EntityType<?>,EntityType<?>> entity = entry.getKey();
            AvalonAutoRegister annotation = entry.getValue();

            Function<EntityType<?>, PatchedEntityRenderer> renderPatchFunction =
                    createRenderPatchFunction(annotation.renderPatch(), event.getContext());

            event.addPatchedEntityRenderer(entity.get(), renderPatchFunction);

            AvalonMOD.LOGGER.debug("Avalon自动注册 RenderPatch: {} -> {}", entity.getId(), annotation.renderPatch());
        }
    }

    /**
     * 处理实体属性注册
     */
    @SubscribeEvent
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
                AvalonMOD.LOGGER.debug("成功注册实体属性: {} -> {}", entity.getId(), annotation.entityClass().getSimpleName());
            } catch (Exception e) {
                AvalonMOD.LOGGER.error("注册实体属性失败: {}", entity.getId(), e);
            }
        }
    }

    /**
     * 获取默认属性 - 使用约定方法名"getDefaultAttributes"
     */
    private static AttributeSupplier getDefaultAttributes(Class<?> entityClass) {
        try {
            java.lang.reflect.Method method = entityClass.getDeclaredMethod("getDefaultAttributes");
            method.setAccessible(true);
            return (AttributeSupplier) method.invoke(null);
        } catch (NoSuchMethodException e) {
            try {
                AvalonMOD.LOGGER.warn("实体类 {} 没有 getDefaultAttributes 方法，使用 VFXEntity 的默认属性", entityClass.getSimpleName());
                return VFXEntity.getDefaultAttribute();
            } catch (Exception ex) {
                throw new RuntimeException("无法获取默认属性", ex);
            }
        } catch (Exception e) {
            throw new RuntimeException("无法获取实体属性: " + entityClass.getName(), e);
        }
    }


    @SuppressWarnings("unchecked")
    private static Function<Entity, EntityPatch<?>> createSimpleEntityPatchFunction(Class<?> entityClass,Class<?> patchClass) {
        return entity -> {
            try {
//                TDEMOD.LOGGER.debug("创建 EntityPatch: {} for entity: {}", patchClass.getSimpleName(), entity.getClass().getSimpleName());

                Constructor<?> constructor = patchClass.getDeclaredConstructor(entityClass);
                return (EntityPatch<?>) constructor.newInstance(entityClass.cast(entity));

            } catch (Exception e) {
                AvalonMOD.LOGGER.error("创建 EntityPatch 失败", e);
                throw new RuntimeException("Avalon无法创建 EntityPatch: " + patchClass.getName(), e);
            }
        };
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityRendererProvider<?> createEntityRendererProvider(String className) {
        return context -> {
            try {
                Class<?> rendererClass = Class.forName(className);
                Constructor<?> constructor = rendererClass.getDeclaredConstructor(EntityRendererProvider.Context.class);
                return (EntityRenderer) constructor.newInstance(context);
            } catch (Exception e) {
                throw new RuntimeException("Avalon无法创建 EntityRender: " + className, e);
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

                // 尝试调用 initLayerLast 方法（如果有）
                try {
                    java.lang.reflect.Method initLayerLast = renderPatchClass.getMethod(
                            "initLayerLast", EntityRendererProvider.Context.class, EntityType.class
                    );
                    initLayerLast.invoke(renderer, context, entityType);
                } catch (NoSuchMethodException ignored) {
                    // 方法不存在是正常情况，忽略
                }

                return renderer;
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                // 类或构造函数不存在，返回 null
                return null;
            } catch (Exception e) {
                // 其他异常也返回 null
                return null;
            }
        };
    }

}
