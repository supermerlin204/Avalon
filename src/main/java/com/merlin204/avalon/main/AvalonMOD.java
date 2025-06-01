package com.merlin204.avalon.main;


import com.merlin204.avalon.entity.AvalonEntities;
import com.merlin204.avalon.item.AvalonItems;
import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;


@Mod(AvalonMOD.MOD_ID)
public class AvalonMOD {

    public static final String MOD_ID = "epic_fight_avalon";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AvalonMOD(FMLJavaModLoadingContext context){


        IEventBus bus = context.getModEventBus();
        AvalonEntities.ENTITIES.register(bus);
        AvalonItems.ITEMS.register(bus);


    }





}
