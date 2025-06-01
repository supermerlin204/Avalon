package com.merlin204.avalon.item;

import com.merlin204.avalon.entity.vfx.VFXEntityPatch;
import com.merlin204.avalon.entity.vfx.shakewave.ShakeWaveEntity;
import com.merlin204.avalon.epicfight.gameassets.animations.VFXAnimations;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
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

            ShakeWaveEntity shakeWaveEntity = new ShakeWaveEntity(player,5);
            world.addFreshEntity(shakeWaveEntity);
            shakeWaveEntity.setPos(player.position());





            player.getCooldowns().addCooldown(stack.getItem(), 20);
        return super.use(pLevel, player, pUsedHand);
    }
}
