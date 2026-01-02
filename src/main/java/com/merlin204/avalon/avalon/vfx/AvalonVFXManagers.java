package com.merlin204.avalon.avalon.vfx;

import com.merlin204.avalon.avalon.vfx.type.AnimationTextureAvalonVFXManager;
import com.merlin204.avalon.avalon.vfx.type.HumanoidAvalonVFXManager;
import com.merlin204.avalon.avalon.vfx.type.StaticAvalonVFXManager;
import com.merlin204.avalon.epicfight.gameassets.animations.VFXAnimations;
import com.merlin204.avalon.main.AvalonMOD;
import net.minecraft.resources.ResourceLocation;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.Armatures;

public class AvalonVFXManagers {
    public static final ResourceLocation EMPTY_TEX = ResourceLocation.fromNamespaceAndPath(AvalonMOD.MOD_ID,"textures/empty.png");
    private static final ResourceLocation ALL_BLACK = ResourceLocation.fromNamespaceAndPath(AvalonMOD.MOD_ID,"textures/entity/test.png");



    public static final StaticAvalonVFXManager TEST2 = new StaticAvalonVFXManager("epic_fight_avalon:test" ,"epic_fight_avalon:textures/test.png","epic_fight_avalon:textures/test.png", VFXAnimations.TEST);

    public static final AnimationTextureAvalonVFXManager TEST3 = new AnimationTextureAvalonVFXManager("epic_fight_avalon:test" ,"epic_fight_avalon:textures/testpack","epic_fight_avalon:textures/testpack",1,5,1, VFXAnimations.TEST);

    public static final HumanoidAvalonVFXManager TEST4 = new HumanoidAvalonVFXManager(Armatures.BIPED ,ResourceLocation.parse("epic_fight_avalon:textures/test.png"),ResourceLocation.parse("epic_fight_avalon:textures/test.png"),ResourceLocation.parse("epic_fight_avalon:textures/test.png"), VFXAnimations.TEST);
}
