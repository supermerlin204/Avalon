package com.merlin204.avalon.main;




import com.merlin204.avalon.entity.AvalonEntities;
import com.merlin204.avalon.epicfight.gameassets.AvalonCategories;
import com.merlin204.avalon.item.AvalonItems;
import com.merlin204.avalon.particle.AvalonParticles;
import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

import java.io.IOException;


@Mod(AvalonMOD.MOD_ID)
public class AvalonMOD {

    public static final String MOD_ID = "epic_fight_avalon";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static boolean beMerlin = false;

    public AvalonMOD(IEventBus bus){
        if (FMLEnvironment.production) {
            // 生产环境
            System.out.println("Running in production environment");
        } else {
            beMerlin = true;
            // 开发环境
            System.out.println("Running in development environment");
        }

        AvalonEntities.ENTITIES.register(bus);
        AvalonItems.ITEMS.register(bus);
        AvalonParticles.PARTICLES.register(bus);
        CapabilityItem.WeaponCategories.ENUM_MANAGER.registerEnumCls(AvalonMOD.MOD_ID, AvalonCategories.class);
    }







}
