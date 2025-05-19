package com.merlin204.avalon.util;


import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
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
    public static AnimationEvent.InTimeEvent simpleGroundSplit(int startFrame, double viewOffset, double xOffset, double yOffset, double zOffset, float radius) {
        float start = startFrame / 60F;
        return AnimationEvent.InTimeEvent.create(start, (entityPatch, self, params) -> {
            groundSplit(entityPatch,viewOffset,xOffset,yOffset,zOffset,radius);
        }, AnimationEvent.Side.BOTH);
    }

    public static void groundSplit(LivingEntityPatch<?> entityPatch, double viewOffset, double xOffset, double yOffset, double zOffset, float radius) {
        LivingEntity entity = entityPatch.getOriginal();
        float damage = getTotalAttackDamage(entityPatch);
        Vec3 pos = entity.position();

        float yaw = entityPatch.getYRot();
        double radians = Math.toRadians(yaw);
        Vec3 dir = new Vec3(-Math.sin(radians), 0, Math.cos(radians)).normalize().scale(viewOffset);

        Vec3 target = pos.add(dir.x + xOffset, -1 + yOffset, dir.z + zOffset);
        Vec3 damagetarget = pos.add(dir.x + xOffset, yOffset, dir.z + zOffset);

        if (entity.level() instanceof ServerLevel level) {
            LevelUtil.circleSlamFracture(entity, level, target, radius, false);
            dealAreaDamage(level, damagetarget, entity, damage, radius, StunType.LONG);
        }
    }

    private static void dealAreaDamage(ServerLevel level, Vec3 center, LivingEntity source, float damage, float radius, StunType stunType) {
        if (radius <= 0) return;
        AABB area = new AABB(center.x() - radius, center.y() - radius, center.z() - radius, center.x() + radius, center.y() + radius, center.z() + radius);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area, entity ->
                entity.isAlive()
                        && entity.distanceToSqr(center) <= radius * radius
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
