package com.merlin204.avalon.entity;

import com.merlin204.avalon.api.register.AvalonAutoRegister;
import com.merlin204.avalon.api.register.AvalonEntityRegistryManager;
import com.merlin204.avalon.entity.example.TestEntity;
import com.merlin204.avalon.entity.example.TestPatch;
import com.merlin204.avalon.entity.vfx.AnimationTextureVFXEntity;
import com.merlin204.avalon.entity.vfx.VFXEntity;
import com.merlin204.avalon.entity.vfx.VFXEntityPatch;
import com.merlin204.avalon.main.AvalonMOD;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AvalonEntities {

    //注册实体
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, AvalonMOD.MOD_ID);


    @AvalonAutoRegister(value = "vfx",
            entityClass = VFXEntity.class,
            entityPatch = VFXEntityPatch.class,
            renderPatch = "com.merlin204.avalon.entity.client.renderer.patch.entity.AvalonVFXRendererPatch",
            registerAttributes = true)
    public static final DeferredHolder<EntityType<?>,EntityType<VFXEntity>> VFX = register("vfx",
            EntityType.Builder.<VFXEntity>of(VFXEntity::new, MobCategory.MISC).sized(0, 0).clientTrackingRange(64).updateInterval(1).noSave());

    @AvalonAutoRegister(value = "test",
            entityClass = TestEntity.class,
            entityPatch = TestPatch.class,
            registerAttributes = true)
    public static final DeferredHolder<EntityType<?>,EntityType<TestEntity>> TEST = register("test",
            EntityType.Builder.<TestEntity>of(TestEntity::new, MobCategory.MISC).sized(1, 2).clientTrackingRange(64).updateInterval(1).noSave());

    @AvalonAutoRegister(value = "animation_texture_vfx",
            entityClass = AnimationTextureVFXEntity.class,
            entityPatch = VFXEntityPatch.class,
            renderPatch = "com.merlin204.avalon.entity.client.renderer.patch.entity.AvalonVFXRendererPatch",
            registerAttributes = true)
    public static final DeferredHolder<EntityType<?>,EntityType<AnimationTextureVFXEntity>> ANIMATION_TEXTURE_VFX = register("animation_texture_vfx",
            EntityType.Builder.<AnimationTextureVFXEntity>of(AnimationTextureVFXEntity::new, MobCategory.MISC).sized(0, 0).clientTrackingRange(64).updateInterval(1).noSave());



    private static <T extends Entity> DeferredHolder<EntityType<?>,EntityType<T>> register(String name, EntityType.Builder<T> entityTypeBuilder) {
        return ENTITIES.register(name, () -> entityTypeBuilder.build(ResourceLocation.fromNamespaceAndPath(AvalonMOD.MOD_ID, name).toString()));
    }

    static {AvalonEntityRegistryManager.scanAndRegisterEntities(AvalonEntities.class);}

}
