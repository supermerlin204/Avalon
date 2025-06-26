package com.merlin204.avalon.util;


import com.merlin204.avalon.client.CameraShake;
import com.merlin204.avalon.entity.vfx.VFXEntityPatch;
import com.merlin204.avalon.entity.vfx.shakewave.ShakeWaveEntity;
import com.merlin204.avalon.epicfight.gameassets.animations.VFXAnimations;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.utils.LevelUtil;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSources;
import yesman.epicfight.world.damagesource.StunType;

import java.util.ArrayList;
import java.util.List;

public class AvalonEventUtils {
    public static AnimationEvent.InTimeEvent simpleGroundSplit(int startFrame, double viewOffset, double xOffset, double yOffset, double zOffset, float radius,boolean teamProtect) {
        float start = startFrame / 60F;
        return AnimationEvent.InTimeEvent.create(start, (entityPatch, self, params) -> {
            groundSplit(entityPatch,viewOffset,xOffset,yOffset,zOffset,radius,teamProtect);
        }, AnimationEvent.Side.BOTH);
    }

    public static AnimationEvent.InTimeEvent simpleSonicBoom(int startFrame, Joint startJoint,float damage) {
        float start = startFrame / 60F;
        return AnimationEvent.InTimeEvent.create(start, (entityPatch, self, params) -> {
            Vec3 startPos = AvalonAnimationUtils.getJointWorldPos(entityPatch,startJoint);
            if (entityPatch.getTarget() != null){
                entityPatch.rotateTo(entityPatch.getTarget(),90F,true);

                Vec3 toTarget = entityPatch.getTarget().getEyePosition().subtract(startPos);
                Vec3 direction = toTarget.normalize();
                for(int step = 1; step < Mth.floor(toTarget.length()) + 7; ++step) {
                    Vec3 particlePos = startPos.add(direction.scale(step));
                    if (entityPatch.getOriginal().level() instanceof ServerLevel serverLevel){
                        serverLevel.sendParticles(
                                ParticleTypes.SONIC_BOOM,
                                particlePos.x, particlePos.y, particlePos.z,
                                1,
                                0.0, 0.0, 0.0, 0.0
                        );
                    }

                }
                entityPatch.getOriginal().playSound(SoundEvents.WARDEN_SONIC_BOOM, 3.0F, 1.0F);

                entityPatch.getTarget().invulnerableTime = 0;
                EpicFightDamageSources damageSources = EpicFightDamageSources.of(entityPatch.getOriginal().level());


                entityPatch.getTarget().hurt(damageSources.shockwave(entityPatch.getOriginal())
                        .setAnimation(Animations.EMPTY_ANIMATION)
                        .setImpact(damage*10F), damage);
                double verticalKnockback = 0.5 * (1.0 - entityPatch.getTarget().getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                double horizontalKnockback = 2.5 * (1.0 - entityPatch.getTarget().getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));

                entityPatch.getTarget().push(
                        direction.x() * horizontalKnockback,
                        direction.y() * verticalKnockback,
                        direction.z() * horizontalKnockback
                );
            }

        }, AnimationEvent.Side.SERVER);
    }

    public static AnimationEvent.InPeriodEvent particleTrail(int startFrame, int endFrame, InteractionHand hand,float timeInterpolation,int particleCount ,ParticleOptions particleOptions) {
        return particleTrail(startFrame,endFrame,hand,timeInterpolation,particleCount,particleOptions,0);
    }


    public static AnimationEvent.InPeriodEvent particleTrail(int startFrame, int endFrame, InteractionHand hand,float timeInterpolation,int particleCount, ParticleOptions particleOptions,float random) {
        float start = startFrame / 60F;
        float end = endFrame / 60F;
        Joint joint = null;
        switch (hand){
            case MAIN_HAND -> joint = Armatures.BIPED.get().toolR;
            case OFF_HAND -> joint = Armatures.BIPED.get().toolL;
        }
        Joint finalJoint = joint;
        return AnimationEvent.InPeriodEvent.create(start, end, (entityPatch, self, params) -> {

            AnimationPlayer player = entityPatch.getAnimator().getPlayerFor(null);
            float prevElapsedTime = player.getPrevElapsedTime();
            float elapsedTime = player.getElapsedTime();
            float step = (elapsedTime - prevElapsedTime) / timeInterpolation;

            ItemStack stack = entityPatch.getOriginal().getItemInHand(hand);
            RenderItemBase renderItemBase = ClientEngine.getInstance().renderEngine.getItemRenderer(stack);

            Vec3 trailStartOffset = renderItemBase.trailInfo().start();
            Vec3 trailEndOffset = renderItemBase.trailInfo().end();
            Vec3f trailDirection = new Vec3f((float)(trailEndOffset.x - trailStartOffset.x), (float)(trailEndOffset.y - trailStartOffset.y), (float)(trailEndOffset.z - trailStartOffset.z));
            for (float f = prevElapsedTime; f <= elapsedTime; f += step) {
                for (int i = 0; i <= particleCount; i++) {
                    float ratio = i / (float)particleCount;
                    Vec3f pointOffset = new Vec3f(
                            (float)(trailStartOffset.x + trailDirection.x * ratio),
                            (float)(trailStartOffset.y + trailDirection.y * ratio),
                            (float)(trailStartOffset.z + trailDirection.z * ratio));

                    double randX = (Math.random() - 0.5) * random;
                    double randY = (Math.random() - 0.5) * random;
                    double randZ = (Math.random() - 0.5) * random;

                    Vec3 worldPos = AvalonAnimationUtils.getJointWorldRawPos(entityPatch, finalJoint, f + step, pointOffset);
                    if (entityPatch.getOriginal().level().isClientSide){
                        entityPatch.getOriginal().level().addParticle(particleOptions, worldPos.x + randX, worldPos.y +randY, worldPos.z + randZ, 0, 0, 0);
                    }
                }
            }
        }, AnimationEvent.Side.BOTH);
    }

    public static AnimationEvent.InTimeEvent simpleSound(int startFrame, SoundEvent soundEvent,float volume,float pitch ) {
        float start = startFrame / 60F;
        return AnimationEvent.InTimeEvent.create(start, (entityPatch, self, params) -> {
           entityPatch.getOriginal().playSound(soundEvent,volume,pitch);
        }, AnimationEvent.Side.SERVER);
    }


    /**
     * duration:持续tick
     * intensity:强度
     * frequency:频率
     * radius:半径
     */
    public static AnimationEvent.InTimeEvent simpleCameraShake(int startFrame,int duration, float intensity, float frequency, float radius) {
        float start = startFrame / 60F;
        return AnimationEvent.InTimeEvent.create(start, (entityPatch, self, params) -> {
            CameraShake.shake(duration,intensity,frequency,entityPatch.getOriginal().position(),radius);
        }, AnimationEvent.Side.CLIENT);
    }



    public static void groundSplit(LivingEntityPatch<?> entityPatch, double viewOffset, double xOffset, double yOffset, double zOffset, float radius,boolean teamProtect) {
        LivingEntity entity = entityPatch.getOriginal();
        float damage = getTotalAttackDamage(entityPatch) * 0.5F;
        Vec3 pos = entity.position();

        float yaw = entityPatch.getYRot();
        double radians = Math.toRadians(yaw);

        double cosYaw = Math.cos(radians);
        double sinYaw = Math.sin(radians);

        double worldX = xOffset * cosYaw + zOffset * sinYaw;
        double worldZ = -xOffset * sinYaw + zOffset * cosYaw;

        Vec3 viewDir = new Vec3(-sinYaw, 0, cosYaw).scale(viewOffset);

        Vec3 totalOffset = viewDir.add(worldX, yOffset, worldZ);

        Vec3 target = pos.add(totalOffset.x, -1 + totalOffset.y, totalOffset.z);
        Vec3 damagetarget = pos.add(totalOffset.x, totalOffset.y, totalOffset.z);

        if (entity.level() instanceof ServerLevel level && target !=null) {
            ShakeWaveEntity shakeWaveEntity = new ShakeWaveEntity(entity,radius);
            level.addFreshEntity(shakeWaveEntity);
            shakeWaveEntity.setPos(damagetarget.add(0 ,0.9F,0));

            LevelUtil.circleSlamFracture(entity, level, target, radius, false);
            dealAreaDamage(level, damagetarget, entity, damage, radius, StunType.LONG,teamProtect);
        }
    }

    private static void dealAreaDamage(ServerLevel level, Vec3 center, LivingEntity source, float damage, float radius, StunType stunType,boolean teamProtect) {
        if (radius <= 0) return;
        AABB area = new AABB(center.x() - radius, center.y() - radius, center.z() - radius, center.x() + radius, center.y() + radius, center.z() + radius);
        if (teamProtect){
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area, entity ->
                    entity.isAlive()
                            && entity.distanceToSqr(center) <= radius * radius
                            && entity.getType().getCategory() != source.getType().getCategory()
                            && entity != source);
            for (LivingEntity entity : new ArrayList<>(entities)) {
                if (entity.invulnerableTime >= 0 && source != null) {


                    entity.invulnerableTime = 0;
                    EpicFightDamageSources damageSources = EpicFightDamageSources.of(entity.level());
                    entity.hurt(damageSources.shockwave(source)
                                    .setAnimation(Animations.EMPTY_ANIMATION)
                                    .setInitialPosition(center)
                                    .setStunType(stunType).setImpact(damage / 5.0F)
                                    .addRuntimeTag(DamageTypes.EXPLOSION)
                                , damage);
                    }
                    entity.invulnerableTime = 0;
                }
            }
        else {
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area, entity ->
                    entity.isAlive()
                            && entity.distanceToSqr(center) <= radius * radius
                            && entity.getType() != source.getType()
                            && entity != source);
            for (LivingEntity entity : new ArrayList<>(entities)) {
                if (entity.invulnerableTime >= 0 && source != null) {

                    entity.invulnerableTime = 0;
                    EpicFightDamageSources damageSources = EpicFightDamageSources.of(entity.level());
                    entity.hurt(damageSources.shockwave(source)
                                    .setAnimation(Animations.EMPTY_ANIMATION)
                                    .setInitialPosition(center)
                                    .setStunType(stunType).setImpact(damage / 5.0F)
                                    .addRuntimeTag(DamageTypes.EXPLOSION)
                            , damage);
                    entity.invulnerableTime = 0;
                }
            }
        }

    }

    public static float getTotalAttackDamage(LivingEntityPatch<?> entityPatch) {
        LivingEntity owner = entityPatch.getOriginal();
        double baseDamage = owner.getAttributeValue(Attributes.ATTACK_DAMAGE);
        return (float) baseDamage;
    }


}
