package com.merlin204.avalon.item;

import com.merlin204.avalon.main.AvalonMOD;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AvalonItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, AvalonMOD.MOD_ID);

    public static final DeferredHolder<Item,MerlinSuperGG> MERLIN_GG = ITEMS.register("merlin_gg", () ->
            new MerlinSuperGG(new Item.Properties()));


}
