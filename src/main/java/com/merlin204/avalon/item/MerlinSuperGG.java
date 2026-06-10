package com.merlin204.avalon.item;

import com.merlin204.avalon.avalon.vfx.AvalonVFXManagers;
import com.merlin204.avalon.item.animationitem.AvalonAnimationItemManager;
import com.merlin204.avalon.item.animationitem.IAvalonAnimationItem;
import com.merlin204.avalon.main.AvalonMOD;
import com.merlin204.avalon.particle.AvalonParticles;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class MerlinSuperGG extends Item implements IAvalonAnimationItem{


    @Override
    public Armatures.ArmatureAccessor<? extends Armature> getArmature() {
        return Armatures.BIPED;
    }



    public MerlinSuperGG(Properties pProperties) {
        super(pProperties);
    }



    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player player, @NotNull InteractionHand pUsedHand) {
        Level world = player.level();
        LivingEntityPatch livingEntityPatch = EpicFightCapabilities.getEntityPatch(player,LivingEntityPatch.class);



        if (world.isClientSide){
            Vec3 pos = player.position();
            pLevel.addParticle(AvalonParticles.AVALON_INTERPOLATION_ENTITY_AFTER_IMAGE.get(),pos.x,pos.y,pos.z,Double.longBitsToDouble(player.getId()),0,0);
            return super.use(pLevel, player, pUsedHand);
        }






        return super.use(pLevel, player, pUsedHand);
    }
}
