package com.merlin204.avalon.main;


import com.merlin204.avalon.block.AvalonBlocks;
import com.merlin204.avalon.entity.AvalonEntities;

import com.merlin204.avalon.epicfight.gameassets.AvalonCategories;
import com.merlin204.avalon.item.AvalonItems;
import com.merlin204.avalon.network.NetworkHandler;
import com.merlin204.avalon.particle.AvalonParticles;
import com.mojang.logging.LogUtils;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

import java.io.IOException;


@Mod(AvalonMOD.MOD_ID)
public class AvalonMOD {

    public static final String MOD_ID = "epic_fight_avalon";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static boolean beMerlin = false;

    public AvalonMOD(FMLJavaModLoadingContext context){
        if (FMLEnvironment.production) {
            // 生产环境
            System.out.println("Running in production environment");
        } else {
            beMerlin = true;
            // 开发环境
            System.out.println("Running in development environment");
        }

        IEventBus bus = context.getModEventBus();
        AvalonItems.ITEMS.register(bus);
        AvalonParticles.PARTICLES.register(bus);
        AvalonBlocks.BLOCKS.register(bus);
        AvalonBlocks.BLOCK_ENTITIES.register(bus);
        CapabilityItem.WeaponCategories.ENUM_MANAGER.registerEnumCls(AvalonMOD.MOD_ID, AvalonCategories.class);
        NetworkHandler.registerPackets();
    }







}
