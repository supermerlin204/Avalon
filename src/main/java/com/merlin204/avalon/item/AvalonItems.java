package com.merlin204.avalon.item;

import com.merlin204.avalon.main.AvalonMOD;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import yesman.epicfight.gameasset.EpicFightSounds;

public class AvalonItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AvalonMOD.MOD_ID);

    public static final RegistryObject<Item> MERLIN_GG = ITEMS.register("merlin_gg", () ->
            new MerlinSuperGG(new Item.Properties()));


    public static final DeferredRegister<CreativeModeTab> AVALON_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AvalonMOD.MOD_ID);
    public static final RegistryObject<CreativeModeTab> DEFAULT_TAB = AVALON_TAB.register("avalon_items",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(MERLIN_GG.get()))
                    .title(Component.translatable("itemGroup.avalon.items"))
                    .displayItems((parameters, tabData) ->{
                        if (!FMLEnvironment.production) {
                            tabData.accept(MERLIN_GG.get());
                        }
                    }).build());

}
