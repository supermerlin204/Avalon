package com.merlin204.avalon.entity.condition;



import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TargetInGirdCondition<T extends LivingEntityPatch> implements Condition<T> {

    public static class Rectangle {
        public final int xMin, xMax;
        public final int zMin, zMax;

        public Rectangle(int x1, int z1, int x2, int z2) {
            this.xMin = Math.min(x1, x2);
            this.xMax = Math.max(x1, x2);
            this.zMin = Math.min(z1, z2);
            this.zMax = Math.max(z1, z2);
        }
    }

    public final boolean test;

    private final List<Rectangle> rectangles;

    public TargetInGirdCondition(int x, int z) {
        this.test = false;
        this.rectangles = Collections.singletonList(
                new Rectangle(x, z, x, z)
        );
    }

    public TargetInGirdCondition(Rectangle... rects) {
        this.test = false;
        this.rectangles = Arrays.asList(rects);
    }


    public TargetInGirdCondition(boolean test, int x, int z) {
        this.test = test;
        this.rectangles = Collections.singletonList(
                new Rectangle(x, z, x, z)
        );
    }

    public TargetInGirdCondition(boolean test, Rectangle... rects) {
        this.test = test;
        this.rectangles = Arrays.asList(rects);
    }

    @Override
    public boolean predicate(T livingEntityPatch) {
        Vec3 Pos = livingEntityPatch.getOriginal().position();
        Level world = livingEntityPatch.getOriginal().level();

        AABB searchArea = new AABB(
                Pos.x - 30, Pos.y - 2, Pos.z - 30,
                Pos.x + 30, Pos.y + 2, Pos.z + 30
        );

        List<LivingEntity> entities = world.getEntitiesOfClass(
                LivingEntity.class,
                searchArea,
                e -> e.isAlive() && e != livingEntityPatch.getOriginal()
        );
        if (test){
            drawAllRectangles(livingEntityPatch);
        }

        float yRot = livingEntityPatch.getOriginal().getYRot();
        double theta = Math.toRadians(yRot);
        double cos = Math.cos(theta);
        double sin = Math.sin(theta);

        List<Rectangle> rects = new ArrayList<>(rectangles);

        for (LivingEntity entity : entities) {
            Vec3 entityPos = entity.position();
            double dx = entityPos.x - Pos.x;
            double dz = entityPos.z - Pos.z;
            double localX = dx * cos + dz * sin;
            double localZ = -dx * sin + dz * cos;

            if (Math.abs(localX) >= 24 || Math.abs(localZ) >= 24) continue;
            int gridX = (int) Math.floor(localX + 0.5);
            int gridZ = (int) Math.floor(localZ + 0.5);

            for (Rectangle rect : rects) {
                if (gridX >= rect.xMin &&
                        gridX <= rect.xMax &&
                        gridZ >= rect.zMin &&
                        gridZ <= rect.zMax) {
                    return true;
                }
            }
        }
        return false;
    }

    private void drawAllRectangles(T livingEntityPatch) {
        Vec3 Pos = livingEntityPatch.getOriginal().position();
        float yRot = livingEntityPatch.getOriginal().getYRot();
        LivingEntity livingEntity = (LivingEntity) livingEntityPatch.getOriginal();

        for (Rectangle rect : rectangles) {
            double minX = rect.xMin - 0.5;
            double maxX = rect.xMax + 0.5;
            double minZ = rect.zMin - 0.5;
            double maxZ = rect.zMax + 0.5;

            drawLine(Pos, yRot, minX, minZ, maxX, minZ, livingEntity);
            drawLine(Pos, yRot, maxX, minZ, maxX, maxZ, livingEntity);
            drawLine(Pos, yRot, maxX, maxZ, minX, maxZ, livingEntity);
            drawLine(Pos, yRot, minX, maxZ, minX, minZ, livingEntity);
        }
    }




    private void drawLine(Vec3 Pos, float yRot,
                          double x1, double z1, double x2, double z2, LivingEntity entity) {
        final double step = 0.1;
        double dx = x2 - x1;
        double dz = z2 - z1;
        double length = Math.sqrt(dx*dx + dz*dz);
        int steps = (int) (length / step);

        for (int i = 0; i <= steps; i++) {
            double progress = i / (double) steps;
            double localX = x1 + dx * progress;
            double localZ = z1 + dz * progress;

            Vec3 worldPos = localToWorld(
                    new Vec3(localX, 0, localZ),
                    Pos,
                    yRot
            );
            if (entity.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(
                        ParticleTypes.SMOKE,
                        worldPos.x,
                        worldPos.y + 0.5,
                        worldPos.z,
                        5,
                        0,
                        0,
                        0,
                        0);

            }

        }
    }

    private Vec3 localToWorld(Vec3 local, Vec3 origin, float yRot) {
        double radian = Math.toRadians(yRot);
        double cos = Math.cos(radian);
        double sin = Math.sin(radian);

        return new Vec3(
                origin.x + local.x * cos - local.z * sin,
                origin.y,
                origin.z + local.x * sin + local.z * cos
        );
    }


    @Override
    public Condition<T> read(CompoundTag compoundTag) throws IllegalArgumentException {
        return null;
    }

    @Override
    public CompoundTag serializePredicate() {
        return null;
    }

    @Override
    public List<ParameterEditor> getAcceptingParameters(Screen screen) {
        return null;
    }
}
