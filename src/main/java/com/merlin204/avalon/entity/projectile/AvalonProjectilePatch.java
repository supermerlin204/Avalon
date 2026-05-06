package com.merlin204.avalon.entity.projectile;

import com.merlin204.avalon.entity.vfx.VFXEntity;
import com.merlin204.avalon.epicfight.AvalonFactions;
import com.merlin204.avalon.util.AvalonAnimationUtils;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.main.EpicFightSharedConstants;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.StunType;

import java.util.List;


public class AvalonProjectilePatch<T extends AvalonProjectileEntity> extends MobPatch<T> {
    private @Nullable LivingEntityPatch<?> ownerPatch;

    public AvalonProjectilePatch(T entity) {
        super(entity);
    }

    @Override
    public boolean applyStun(StunType stunType, float stunTime) {
        return false;
    }

    @Override
    public AssetAccessor<? extends StaticAnimation> getHitAnimation(StunType stunType) {
        return null;
    }

    @Override
    public Faction getFaction() {
        return AvalonFactions.EMPTY;
    }

    public void onConstructed(T entityIn) {
        this.original = entityIn;

        this.armature = getArmature();


        Animator animator = EpicFightSharedConstants.getAnimator(this);
        this.animator = animator;
        this.initAnimator(animator);
        animator.postInit();
    }

    @Override
    public void updateMotion(boolean b) {
        if (b) {
            this.currentLivingMotion = LivingMotions.IDLE;
        }
    }


    @Override
    public void preTick() {
        super.preTick();
        boolean playAnimation = (this.getOriginal()).getPlayAnimation();
        if ((!this.isLogicalClient() || (this.original).getDefaultAnimation() != null || playAnimation) && !playAnimation) {

            (this.original).setPlayAnimation(true);
            if (this.isLogicalClient()) {
                this.getClientAnimator().playAnimation((this.original).getDefaultAnimation(), 0.0F);
            } else {
                this.playAnimationSynchronized((this.original).getDefaultAnimation(), 0.0F);
            }
        }
    }



    public @Nullable LivingEntityPatch<?> getOwnerPatch() {
        if (this.ownerPatch != null) {
            return this.ownerPatch;
        } else if ((this.getOriginal()).getOwner() != null) {
            this.ownerPatch = (LivingEntityPatch) EpicFightCapabilities.getEntityPatch((this.getOriginal()).getOwner(), LivingEntityPatch.class);
            return this.ownerPatch;
        } else {
            return null;
        }
    }

    @Override
    public OpenMatrix4f getModelMatrix(float partialTicks) {
        return super.getModelMatrix(partialTicks).scale(original.getScale(),original.getScale(),original.getScale());
    }

    @Override
    public OpenMatrix4f getMatrix(float partialTicks) {
        return super.getMatrix(partialTicks).scale(original.getScale(),original.getScale(),original.getScale());
    }

    @Override
    public Armature getArmature() {
        return original.getArmature();
    }

    public LivingEntity target() {
        if (this.getOwnerPatch().getTarget() != null) return this.getOwnerPatch().getTarget();
        if (this.getTarget() != null) return this.getTarget();
        Level level = this.getOriginal().level();
        double range = 16.0;

        List<Entity> nearbyEntities = level.getEntities(getOwnerPatch().getOriginal(),
                getOwnerPatch().getOriginal().getBoundingBox().inflate(range),
                entity -> isHostileMob(entity) && entity != getOwnerPatch().getOriginal()
        );

        Entity nearestTarget = null;
        double minDistance = Double.MAX_VALUE;
        Entity owner = getOwnerPatch().getOriginal();

        for (Entity entity : nearbyEntities) {
            double distance = owner.distanceToSqr(entity);

            if (level.clip(new ClipContext(
                    owner.getEyePosition(1.0F),
                    entity.getEyePosition(1.0F),
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    owner
            )).getType() == HitResult.Type.MISS) {

                if (distance < minDistance) {
                    minDistance = distance;
                    nearestTarget = entity;
                }
            }
        }

        if (nearestTarget instanceof LivingEntity) {
            return (LivingEntity) nearestTarget;
        }
        return null;
    }





    @Override
    public void poseTick(DynamicAnimation animation, Pose pose, float elapsedTime, float partialTick) {
        AvalonAnimationUtils.joinRotationInPose(pose, this, "Root",-original.getSyncXRot() , 0, 0);
    }

    public static boolean isHostileMob(Entity entity) {
        if (!(entity instanceof LivingEntity)) return false;
        return entity instanceof Enemy ||
                entity instanceof Monster ||
                entity.getType() == EntityType.SLIME ||
                entity.getType() == EntityType.MAGMA_CUBE ||
                entity.getType() == EntityType.PHANTOM ||
                entity.getType() == EntityType.GHAST;
    }

    @Override
    public AttackResult attack(EpicFightDamageSource damageSource, Entity target, InteractionHand hand) {
        if (getOwnerPatch() != null && shouldUseOwnerAttack()) {
            return getOwnerPatch().attack(damageSource, target, hand);
        }
        return super.attack(damageSource, target, hand);
    }

    public boolean shouldUseOwnerAttack(){
        return true;
    }

    @Override
    protected void initAnimator(Animator animator) {
        super.initAnimator(animator);
        animator.addLivingAnimation(LivingMotions.IDLE, original.getIdleAnimation());
    }

    @Nullable
    @Override
    public EpicFightDamageSource getEpicFightDamageSource() {
        if (getOwnerPatch() != null) {
            return getOwnerPatch().getEpicFightDamageSource();
        }
        return super.getEpicFightDamageSource();
    }

    @Override
    public EpicFightDamageSource getDamageSource(AnimationManager.AnimationAccessor<? extends StaticAnimation> animation, InteractionHand hand) {
        if (getOwnerPatch() != null) {
            return getOwnerPatch().getDamageSource(animation, hand);
        }
        return super.getDamageSource(animation, hand);
    }

    @Override
    public SoundEvent getSwingSound(InteractionHand hand) {
        if (getOwnerPatch() == null) {
            return super.getSwingSound(hand);
        }
        return getOwnerPatch().getSwingSound(hand);
    }

    @Override
    public SoundEvent getWeaponHitSound(InteractionHand hand) {
        if (getOwnerPatch() == null) {
            return super.getWeaponHitSound(hand);
        }
        return getOwnerPatch().getWeaponHitSound(hand);
    }


    @Override
    public boolean isTargetInvulnerable(Entity entity) {
        if(entity.equals(this.getOriginal().getOwner())){
            return true;
        }
        if(entity instanceof VFXEntity artifactSpiritEntity && getOwnerPatch() != null){
            return getOwnerPatch().getOriginal().equals(artifactSpiritEntity.getOwner());
        }
        return false;
    }


}
