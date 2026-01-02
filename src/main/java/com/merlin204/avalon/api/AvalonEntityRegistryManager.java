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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;
import yesman.epicfight.api.client.forgeevent.PatchedRenderersEvent;
import yesman.epicfight.api.forgeevent.EntityPatchRegistryEvent;
import yesman.epicfight.client.renderer.patched.entity.PatchedEntityRenderer;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 实体注解自动注册器
 */
@Mod.EventBusSubscriber(modid = AvalonMOD.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AvalonEntityRegistryManager {

    private static final Map<RegistryObject<EntityType<?>>, AvalonAutoRegister> ENTITY_REGISTRY = new HashMap<>();

    /**
     * 扫描并注册所有带有注解的实体
     */
    public static void scanAndRegisterEntities(Class<?> entitiesClass) {
        try {
            for (java.lang.reflect.Field field : entitiesClass.getDeclaredFields()) {
                if (field.getType().equals(RegistryObject.class) && field.isAnnotationPresent(AvalonAutoRegister.class)) {
                    field.setAccessible(true);
                    RegistryObject<EntityType<?>> entityRegistry = (RegistryObject<EntityType<?>>) field.get(null);
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
        for (Map.Entry<RegistryObject<EntityType<?>>, AvalonAutoRegister> entry : ENTITY_REGISTRY.entrySet()) {
            RegistryObject<EntityType<?>> entity = entry.getKey();
            AvalonAutoRegister annotation = entry.getValue();

            Function<Entity, Supplier<EntityPatch<?>>> patchSupplier = createEntityPatchSupplier(annotation.entityPatch());
            event.getTypeEntry().put(entity.get(), patchSupplier);

            AvalonMOD.LOGGER.debug("Avalon自动注册 EntityPatch: {} -> {}", entity.getId(), annotation.entityPatch().getSimpleName());
        }
    }

    /**
     * 处理 EntityRender 注册
     */
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void handleClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            for (Map.Entry<RegistryObject<EntityType<?>>, AvalonAutoRegister> entry : ENTITY_REGISTRY.entrySet()) {
                RegistryObject<EntityType<?>> entity = entry.getKey();
                AvalonAutoRegister annotation = entry.getValue();

                // 使用未经检查的类型转换来解决泛型问题
                EntityRendererProvider<?> rendererProvider = createEntityRendererProvider(annotation.clientRenderer());

                // 关键修复：进行类型转换
                EntityType<? extends Entity> entityType = (EntityType<? extends Entity>) entity.get();

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
        for (Map.Entry<RegistryObject<EntityType<?>>, AvalonAutoRegister> entry : ENTITY_REGISTRY.entrySet()) {
            RegistryObject<EntityType<?>> entity = entry.getKey();
            AvalonAutoRegister annotation = entry.getValue();

            Function<EntityType<?>, PatchedEntityRenderer> renderPatchFunction =
                    createRenderPatchFunction(annotation.renderPatch(), event.getContext());

            event.addPatchedEntityRenderer(entity.get(), renderPatchFunction);

            AvalonMOD.LOGGER.debug("Avalon自动注册 RenderPatch: {} -> {}", entity.getId(), annotation.renderPatch());
        }
    }

    /**
     * 处理实体属性注册 - 使用约定方式
     */
    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        for (Map.Entry<RegistryObject<EntityType<?>>, AvalonAutoRegister> entry : ENTITY_REGISTRY.entrySet()) {
            RegistryObject<EntityType<?>> entity = entry.getKey();
            AvalonAutoRegister annotation = entry.getValue();

            // 如果不注册属性，跳过
            if (!annotation.registerAttributes()) {
                continue;
            }

            try {
                AttributeSupplier attributeSupplier = getDefaultAttributes(entity);
                event.put((EntityType<? extends LivingEntity>) entity.get(), attributeSupplier);
                AvalonMOD.LOGGER.debug("成功注册实体属性: {}", entity.getId());
            } catch (Exception e) {
                AvalonMOD.LOGGER.error("注册实体属性失败: {}", entity.getId(), e);
            }
        }
    }

    /**
     * 获取默认属性 - 使用约定方法名"getDefaultAttributes"
     */
    private static AttributeSupplier getDefaultAttributes(RegistryObject<EntityType<?>> entity) {
        try {
            // 获取实体类
            Class<?> entityClass = entity.get().getBaseClass();

            // 尝试调用 getDefaultAttributes 方法
            java.lang.reflect.Method method = entityClass.getDeclaredMethod("getDefaultAttributes");
            method.setAccessible(true);
            return (AttributeSupplier) method.invoke(null);

        } catch (NoSuchMethodException e) {
            // 如果实体类没有 getDefaultAttributes 方法，使用 VFXEntity 的默认属性
            try {
                return VFXEntity.getDefaultAttribute();
            } catch (Exception ex) {
                throw new RuntimeException("无法获取默认属性", ex);
            }
        } catch (Exception e) {
            throw new RuntimeException("无法获取实体属性: " + entity.getId(), e);
        }
    }


    private static Function<Entity, Supplier<EntityPatch<?>>> createEntityPatchSupplier(Class<?> patchClass) {
        return entity -> {
            return () -> {
                try {
                    try {
                        Constructor<?> constructor = patchClass.getDeclaredConstructor(Entity.class);
                        return (EntityPatch<?>) constructor.newInstance(entity);
                    } catch (NoSuchMethodException e) {
                        Constructor<?> constructor = patchClass.getDeclaredConstructor();
                        EntityPatch<?> patch = (EntityPatch<?>) constructor.newInstance();
                        // 如果有 setCustomData 方法，设置实体
                        try {
                            java.lang.reflect.Method setCustomData = patchClass.getMethod("setCustomData", Object.class);
                            setCustomData.invoke(patch, entity);
                        } catch (NoSuchMethodException e2) {
                            // 忽略，使用默认方式
                        }
                        return patch;
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Avalon无法创建 EntityPatch: " + patchClass.getName(), e);
                }
            };
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

                PatchedEntityRenderer renderer = (PatchedEntityRenderer) constructor.newInstance(context, entityType);
                try {
                    java.lang.reflect.Method initLayerLast = renderPatchClass.getMethod(
                            "initLayerLast", EntityRendererProvider.Context.class, EntityType.class
                    );
                    initLayerLast.invoke(renderer, context, entityType);
                } catch (NoSuchMethodException ignored) {
                }

                return renderer;
            } catch (Exception e) {
                throw new RuntimeException("Avalon无法创建 RenderPatch: " +className, e);
            }
        };
    }
}
