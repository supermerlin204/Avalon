package com.merlin204.avalon.item;

import com.merlin204.avalon.main.AvalonMOD;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import yesman.epicfight.gameasset.EpicFightSounds;

public class AvalonItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AvalonMOD.MOD_ID);

    public static final RegistryObject<Item> MERLIN_GG = ITEMS.register("merlin_gg", () ->
            new MerlinSuperGG(new Item.Properties()));


}
