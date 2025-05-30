package com.merlin204.avalon.entity;

import com.merlin204.avalon.entity.vfx.VFXEntity;
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
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, AvalonMOD.MOD_ID);
//    public static final RegistryObject<EntityType<BladeWaveEntity>> BLADEWAVE = register("blade_wave",
//            EntityType.Builder.<BladeWaveEntity>of(BladeWaveEntity::new, MobCategory.MISC).sized(0, 0).clientTrackingRange(64).updateInterval(1).noSave());

    public static final RegistryObject<EntityType<ShakeWaveEntity>> SHAKE_WAVE = register("shake_wave",
            EntityType.Builder.<ShakeWaveEntity>of(ShakeWaveEntity::new, MobCategory.MISC).sized(0, 0).clientTrackingRange(64).updateInterval(1).noSave());



    private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, EntityType.Builder<T> entityTypeBuilder) {
        return ENTITIES.register(name, () -> entityTypeBuilder.build(ResourceLocation.fromNamespaceAndPath(AvalonMOD.MOD_ID, name).toString()));
    }


}
