package com.merlin204.avalon.mixin;


import com.merlin204.avalon.entity.IAvalonMeshEntity;
import com.merlin204.avalon.entity.api.collider.IMultiHitBoxEntityPatch;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;

@Mixin(Armatures.class)
public class ArmaturesMixin {

    @Inject(at = @At("HEAD"), method = "getArmatureFor", cancellable = true)
    private static <A extends Armature> void avalon$getArmatureFor(EntityPatch<?> entitypatch, CallbackInfoReturnable<A> cir) {
        if (entitypatch.getOriginal() instanceof IAvalonMeshEntity meshEntity){
            cir.setReturnValue((A) meshEntity.getArmature());
            cir.cancel();
        }

    }

}
