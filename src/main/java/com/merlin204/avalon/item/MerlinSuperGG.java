package com.merlin204.avalon.item;

import com.merlin204.avalon.main.AvalonMOD;
import com.merlin204.avalon.particle.AvalonParticles;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.gameasset.Armatures;

public class MerlinSuperGG extends Item implements ChangeArmatureItem {


    public MerlinSuperGG(Properties pProperties) {
        super(pProperties);
    }


    @Override
    public Armatures.ArmatureAccessor<? extends Armature> getArmature() {
        return Armatures.ArmatureAccessor.create(AvalonMOD.MOD_ID, "entity/vfx/shakewave", Armature::new);
    }



    @Override
    public Vec2 getHitBox() {
        return new Vec2(5,7);
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
