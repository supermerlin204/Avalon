package com.merlin204.avalon.event;


import com.merlin204.avalon.entity.api.collider.IMultiHitBoxEntityPatch;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation;
import com.merlin204.avalon.epicfight.api.AnimationBeAttackEvent;
import com.merlin204.avalon.epicfight.api.AvalonAnimationProperty;
import com.merlin204.avalon.main.AvalonMOD;
import com.merlin204.avalon.particle.AvalonParticles;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@EventBusSubscriber(modid = AvalonMOD.MOD_ID)
public class ForgeEvents {




    @SubscribeEvent
    public static void coreTickEvent(EntityTickEvent.Pre event) {
        if (event.getEntity().level().isClientSide){
            if (EpicFightCapabilities.getEntityPatch(event.getEntity(), EntityPatch.class) instanceof IMultiHitBoxEntityPatch multiHitBoxEntityPatch){
                multiHitBoxEntityPatch.getColliderManager().syncElapsedTimeToServer();
            }
        }


    }


    @SubscribeEvent
    public static void attackEvent(LivingIncomingDamageEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }
        if (event.getEntity().isInvulnerableTo(event.getSource())) {
            return;
        }
        if (event.getEntity().invulnerableTime > 10 && event.getAmount() <= event.getEntity().lastHurt) {
            return;
        }
        if (event.getEntity().getHealth() <= 0.0F) {
            return;
        }
        LivingEntityPatch<?> entityPatch = EpicFightCapabilities.getEntityPatch(event.getEntity(), LivingEntityPatch.class);
        if (entityPatch == null){
            return;
        }
        if (entityPatch.getAnimator().getPlayerFor(null).getRealAnimation().get() instanceof AvalonAttackAnimation avalonAttackAnimation){
            avalonAttackAnimation.getProperty(AvalonAnimationProperty.BE_ATTACK_EVENTS).ifPresent(events -> {
                for (AnimationBeAttackEvent<?> beAttackEvent : events) {
                    beAttackEvent.execute(entityPatch,event);
                }
            });
        }
    }







}