package com.merlin204.avalon.event;

import com.merlin204.avalon.block.AvalonBlocks;
import com.merlin204.avalon.block.client.MeshBlockEntityRender;
import com.merlin204.avalon.client.particle.AvalonAnimationTrailParticle;
import com.merlin204.avalon.client.particle.AvalonEntityAfterImageParticle;
import com.merlin204.avalon.client.particle.AvalonInterpolationEntityAfterImageParticle;
import com.merlin204.avalon.entity.vfx.VFXEntityPatch;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation;
import com.merlin204.avalon.epicfight.api.AnimationBeAttackEvent;
import com.merlin204.avalon.epicfight.api.AvalonAnimationProperty;
import com.merlin204.avalon.main.AvalonMOD;
import com.merlin204.avalon.particle.AvalonParticles;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mod.EventBusSubscriber(modid = AvalonMOD.MOD_ID)
public class ForgeEvents {

    @SubscribeEvent
    public static void attackEvent(LivingAttackEvent event) {
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

        if (EpicFightCapabilities.getEntityPatch(event.getSource().getEntity(),LivingEntityPatch.class) instanceof VFXEntityPatch<?> vfxEntityPatch && vfxEntityPatch.getOriginal().getOwner() == event.getEntity()){
            event.setCanceled(true);
        }

        if (EpicFightCapabilities.getEntityPatch(event.getSource().getDirectEntity(),LivingEntityPatch.class) instanceof VFXEntityPatch<?> vfxEntityPatch && vfxEntityPatch.getOriginal().getOwner() == event.getEntity()){
            event.setCanceled(true);
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