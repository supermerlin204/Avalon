package com.merlin204.avalon.item;

import com.merlin204.avalon.avalon.vfx.AvalonVFXManagers;
import com.merlin204.avalon.item.animationitem.IAvalonAnimationItem;
import com.merlin204.avalon.main.AvalonMOD;
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
        Level world = player.level();
        LivingEntityPatch livingEntityPatch = EpicFightCapabilities.getEntityPatch(player,LivingEntityPatch.class);
        if (world.isClientSide){
            return super.use(pLevel, player, pUsedHand);
        }
        AvalonVFXManagers.TEST3.spawnVFXEntity(player,new Vec3f(1,1,1),new Vec3f(0,0,-60),2);





        return super.use(pLevel, player, pUsedHand);
    }
}
