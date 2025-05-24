package com.merlin204.avalon.util;


import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.utils.LevelUtil;
import yesman.epicfight.gameasset.Animations;
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
            entityPatch.getTarget().hurt(
                    entityPatch.getOriginal().level().damageSources().sonicBoom(entityPatch.getOriginal()),
                    damage
            );
            double verticalKnockback = 0.5 * (1.0 - entityPatch.getTarget().getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
            double horizontalKnockback = 2.5 * (1.0 - entityPatch.getTarget().getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));

            entityPatch.getTarget().push(
                    direction.x() * horizontalKnockback,
                    direction.y() * verticalKnockback,
                    direction.z() * horizontalKnockback
            );
        }, AnimationEvent.Side.SERVER);
    }

    public static AnimationEvent.InTimeEvent simpleSound(int startFrame, SoundEvent soundEvent,float volume,float pitch ) {
        float start = startFrame / 60F;
        return AnimationEvent.InTimeEvent.create(start, (entityPatch, self, params) -> {
           entityPatch.getOriginal().playSound(soundEvent,volume,pitch);
        }, AnimationEvent.Side.SERVER);
    }


















    public static void groundSplit(LivingEntityPatch<?> entityPatch, double viewOffset, double xOffset, double yOffset, double zOffset, float radius,boolean teamProtect) {
        LivingEntity entity = entityPatch.getOriginal();
        float damage = getTotalAttackDamage(entityPatch);
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

        if (entity.level() instanceof ServerLevel level) {
            LevelUtil.circleSlamFracture(entity, level, target, radius, false);
            dealAreaDamage(level, damagetarget, entity, damage, radius, StunType.LONG,teamProtect);
        }
    }

    private static void dealAreaDamage(ServerLevel level, Vec3 center, LivingEntity source, float damage, float radius, StunType stunType,boolean teamProtect) {
        if (radius <= 0) return;
        AABB area = new AABB(center.x() - radius, center.y() - radius, center.z() - radius, center.x() + radius, center.y() + radius, center.z() + radius);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area, entity ->
                entity.isAlive()
                        && entity.distanceToSqr(center) <= radius * radius
                        && entity.getType().getCategory() != source.getType().getCategory()
                        && entity != source);
        for (LivingEntity entity : new ArrayList<>(entities)) {
            if (entity.invulnerableTime >= 0 && source != null) {
                LivingEntityPatch<?> entityPatch = EpicFightCapabilities.getEntityPatch(source, LivingEntityPatch.class);
                if (entityPatch != null) {
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
    }

    public static float getTotalAttackDamage(LivingEntityPatch<?> entityPatch) {
        LivingEntity owner = entityPatch.getOriginal();
        double baseDamage = owner.getAttributeValue(Attributes.ATTACK_DAMAGE);
        return (float) baseDamage;
    }


}
