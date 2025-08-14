package com.merlin204.avalon.avalon.vfx;

import com.merlin204.avalon.avalon.vfx.type.StaticAvalonVFXManager;
import com.merlin204.avalon.main.AvalonMOD;
import net.minecraft.resources.ResourceLocation;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.Armatures;

public class AvalonVFXManagers {
    private static final ResourceLocation ALL_BLACK = ResourceLocation.fromNamespaceAndPath(AvalonMOD.MOD_ID,"textures/entity/test.png");

    public static final StaticAvalonVFXManager TEST = new StaticAvalonVFXManager(Armatures.BIPED, Meshes.BIPED,ALL_BLACK,ALL_BLACK, Animations.BATTOJUTSU_DASH);

}
