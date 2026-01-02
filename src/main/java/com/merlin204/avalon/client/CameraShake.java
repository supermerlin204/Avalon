package com.merlin204.avalon.client;

import com.merlin204.avalon.main.AvalonMOD;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = AvalonMOD.MOD_ID, value = Dist.CLIENT)
public class CameraShake {

    private static final float DECAY_RATE = 0.95f;  // 衰减率
    private static int shakeDuration = 0;
    private static float currentIntensity = 0.0f;
    private static float baseIntensity = 0.0f;
    private static float frequency = 3.0f;

    private static @Nullable Vec3 center;
    private static float radius;             // 影响半径
    private static float minIntensity;



    /**
     * duration 持续时间（tick）
     * intensity 强度
     * frequency 频率
     * center 中心
     * radius 半径
     */
    public static void shake(int duration, float intensity, float frequency, Vec3 center, float radius) {
        if (intensity > baseIntensity) {
            shakeDuration = duration;
            baseIntensity = intensity;
            currentIntensity = intensity;
            CameraShake.frequency = frequency;
            CameraShake.center = center;
            CameraShake.radius = radius;
        }
    }

    @SubscribeEvent
    public static void onCameraUpdate(ViewportEvent.ComputeCameraAngles event) {
        if (shouldApplyShake()) {
            applyShakeEffect(event, (float) event.getPartialTick());
            updateShakeState();
        }
    }

    private static boolean shouldApplyShake() {
        Player player = Minecraft.getInstance().player;
        return player != null
                && shakeDuration > 0
                && !Minecraft.getInstance().isPaused()
                && center != null;
    }
    private static void applyShakeEffect(ViewportEvent.ComputeCameraAngles event,
                                         float partialTicks) {
        Player player = Minecraft.getInstance().player;
        Vec3 playerPos = player.position();

        double distanceToCenter = playerPos.distanceTo(center);
        float distanceFactor = (float) Math.max(0, 1 - (distanceToCenter / radius));
        float attenuatedIntensity = currentIntensity * distanceFactor;

        float time = (player.tickCount + partialTicks) * frequency;
        float progress = 1.0f - (float)shakeDuration / (shakeDuration + partialTicks);
        float dynamicIntensity = attenuatedIntensity * (1.0f - progress * progress);

        Vec3f shakeOffset = calculateShakeOffset(time, dynamicIntensity);
        event.setPitch(event.getPitch() + shakeOffset.x);
        event.setYaw(event.getYaw() + shakeOffset.y);
        event.setRoll(event.getRoll() + shakeOffset.z);
    }
    private static Vec3f calculateShakeOffset(float time, float intensity) {
        return new Vec3f(
                intensity * (float)(Math.sin(time * 1.1) * 0.6f),
                intensity * (float)(Math.cos(time * 0.9) * 0.7f),
                intensity * (float)(Math.sin(time * 1.3 + 2)) * 0.4f
                );
    }
    private static void updateShakeState() {
        if (!Minecraft.getInstance().isPaused()) {
            shakeDuration--;
            currentIntensity *= DECAY_RATE;
            currentIntensity = Math.max(currentIntensity, 0.01f);
            if (shakeDuration <= 0) {
                reset();
            }
        }
    }
    public static void reset() {
        shakeDuration = 0;
        currentIntensity = 0f;
        baseIntensity = 0f;
    }
}
