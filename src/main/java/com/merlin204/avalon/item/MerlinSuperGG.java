package com.merlin204.avalon.item;

import com.merlin204.avalon.item.animationitem.IAvalonAnimationItem;
import com.merlin204.avalon.main.AvalonMOD;
import com.merlin204.avalon.particle.AvalonParticles;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class MerlinSuperGG extends Item implements IAvalonAnimationItem {


    public MerlinSuperGG(Properties pProperties) {
        super(pProperties);
    }


    @Override
    public Armatures.ArmatureAccessor<? extends Armature> getArmature() {
        return Armatures.ArmatureAccessor.create(AvalonMOD.MOD_ID, "test", Armature::new);
    }



    @Override
    public Vec2 getHitBox() {
        return new Vec2(5,7);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player player, @NotNull InteractionHand pUsedHand) {
        ItemStack stack = player.getItemInHand(pUsedHand);
        Level world = player.level();
        LivingEntityPatch livingEntityPatch = EpicFightCapabilities.getEntityPatch(player,LivingEntityPatch.class);
        System.out.println(livingEntityPatch.getArmature());
        System.out.println(livingEntityPatch.getArmature().searchJointByName("Hand_L"));

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
