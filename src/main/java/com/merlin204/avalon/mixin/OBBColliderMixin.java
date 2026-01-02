package com.merlin204.avalon.mixin;

import com.merlin204.avalon.entity.api.collider.IMultiHitBoxEntityPatch;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.entity.PartEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.collider.OBBCollider;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = OBBCollider.class, remap = false)
public abstract class OBBColliderMixin {

    @Shadow
    public abstract boolean isCollide(OBBCollider opponent);


    @Inject(
            method = "isCollide(Lnet/minecraft/world/entity/Entity;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onIsCollideWithEntity(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof PartEntity<?> partEntity){
            entity = partEntity.getParent();
        }
        if (EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class) instanceof IMultiHitBoxEntityPatch multiHitBoxEntity) {
            multiHitBoxEntity.updateAllCollider();
            boolean flag = false;
            List<Integer> list = new ArrayList<>();
            for (Joint joint : multiHitBoxEntity.getColliderManager().getColliderMap().keySet()) {
                OBBCollider entityJointObbCollider =  multiHitBoxEntity.getColliderManager().getColliderMap().get(joint);
                if (this.isCollide(entityJointObbCollider)) {
                    list.add(joint.getId());
                    flag = true;
                }
            }
            if (!list.isEmpty()){
                multiHitBoxEntity.getColliderManager().syncHitJointToClient(list);
            }
            cir.setReturnValue(flag);
            cir.cancel();
        }

    }
}
