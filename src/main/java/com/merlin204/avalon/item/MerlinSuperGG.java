package com.merlin204.avalon.item;

import com.merlin204.avalon.entity.vfx.VFXEntityPatch;
import com.merlin204.avalon.entity.vfx.shakewave.ShakeWaveEntity;
import com.merlin204.avalon.epicfight.gameassets.animations.VFXAnimations;
import com.merlin204.avalon.particle.AvalonParticles;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

public class MerlinSuperGG extends Item {


    public MerlinSuperGG(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player player, @NotNull InteractionHand pUsedHand) {
        ItemStack stack = player.getItemInHand(pUsedHand);
        Level world = player.level();

        if (!world.isClientSide)return super.use(pLevel, player, pUsedHand);

        player.level().addParticle(AvalonParticles.AVALON_ENTITY_AFTER_IMAGE.get()
                , player.getX()
                , player.getY()
                , player.getZ()
                , Double.longBitsToDouble(player.getId()), 0.05, 0.05);
        ;
        Entity entity = world.getEntity((int)Double.doubleToLongBits(Double.longBitsToDouble(player.getId())));
        System.out.println(entity);
            player.getCooldowns().addCooldown(stack.getItem(), 20);
        return super.use(pLevel, player, pUsedHand);
    }
}
