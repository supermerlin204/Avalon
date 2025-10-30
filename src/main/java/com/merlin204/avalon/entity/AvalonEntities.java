package com.merlin204.avalon.entity;

import com.merlin204.avalon.api.AvalonAutoRegister;
import com.merlin204.avalon.api.AvalonEntityRegistryManager;
import com.merlin204.avalon.entity.client.renderer.EmptyRenderer;
import com.merlin204.avalon.entity.client.renderer.patch.entity.AvalonVFXRendererPatch;
import com.merlin204.avalon.entity.vfx.AnimationTextureVFXEntity;
import com.merlin204.avalon.entity.vfx.VFXEntity;
import com.merlin204.avalon.entity.vfx.VFXEntityPatch;
import com.merlin204.avalon.entity.vfx.shakewave.ShakeWaveEntity;
import com.merlin204.avalon.main.AvalonMOD;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AvalonEntities {

    //注册实体
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, AvalonMOD.MOD_ID);

    @AvalonAutoRegister(value = "shake_wave",
            entityPatch = VFXEntityPatch.class,
            registerAttributes = true)
    public static final RegistryObject<EntityType<ShakeWaveEntity>> SHAKE_WAVE = register("shake_wave",
            EntityType.Builder.<ShakeWaveEntity>of(ShakeWaveEntity::new, MobCategory.MISC).sized(0, 0).clientTrackingRange(64).updateInterval(1).noSave());


    @AvalonAutoRegister(value = "vfx",
            entityPatch = VFXEntityPatch.class,
            renderPatch = AvalonVFXRendererPatch.class,
            registerAttributes = true)
    public static final RegistryObject<EntityType<VFXEntity>> VFX = register("vfx",
            EntityType.Builder.<VFXEntity>of(VFXEntity::new, MobCategory.MISC).sized(0, 0).clientTrackingRange(64).updateInterval(1).noSave());


    @AvalonAutoRegister(value = "animation_texture_vfx",
            entityPatch = VFXEntityPatch.class,
            renderPatch = AvalonVFXRendererPatch.class,
            registerAttributes = true)
    public static final RegistryObject<EntityType<AnimationTextureVFXEntity>> ANIMATION_TEXTURE_VFX = register("animation_texture_vfx",
            EntityType.Builder.<AnimationTextureVFXEntity>of(AnimationTextureVFXEntity::new, MobCategory.MISC).sized(0, 0).clientTrackingRange(64).updateInterval(1).noSave());



    private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, EntityType.Builder<T> entityTypeBuilder) {
        return ENTITIES.register(name, () -> entityTypeBuilder.build(ResourceLocation.fromNamespaceAndPath(AvalonMOD.MOD_ID, name).toString()));
    }

    static {AvalonEntityRegistryManager.scanAndRegisterEntities(AvalonEntities.class);}

}
