package com.merlin204.avalon.entity.vfx;

import com.merlin204.avalon.epicfight.AvalonFctions;
import com.merlin204.avalon.epicfight.gameassets.animations.VFXAnimations;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.*;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.QuaternionUtils;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.main.EpicFightSharedConstants;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.StunType;

public class VFXEntityPatch<T extends VFXEntity> extends MobPatch<T> {

    public VFXEntityPatch() {
        super();
    }


    @Override
    public Faction getFaction() {
        return AvalonFctions.EMPTY;
    }

    public void onConstructed(T entityIn) {
        this.original = entityIn;
        this.armature = entityIn.getArmature();
        Animator animator = EpicFightSharedConstants.getAnimator(this);
        this.animator = animator;
        this.initAnimator(animator);
        animator.postInit();
    }

    @Override
    public void onJoinWorld(T entity, EntityJoinLevelEvent event) {
        super.onJoinWorld(entity, event);
        if(this.isLogicalClient()){
            this.getClientAnimator().playAnimation(original.getDefaultAnimation(), 0.0F);
        }else {
            playAnimationSynchronized(original.getDefaultAnimation(),0F);
        }
    }

    public VFXEntityPatch(Faction faction) {
        super(faction);
    }

    @Nullable
    private PlayerPatch<?> ownerPatch;



    @Override
    public OpenMatrix4f getModelMatrix(float partialTicks) {
        return super.getModelMatrix(partialTicks).scale(original.getScale(),original.getScale(),original.getScale());
    }

    @Override
    public OpenMatrix4f getMatrix(float partialTicks) {
        return super.getMatrix(partialTicks).scale(original.getScale(),original.getScale(),original.getScale());
    }

    @Override
    public void updateMotion(boolean considerInaction) {
        if(considerInaction){
            this.currentLivingMotion = LivingMotions.IDLE;
        }
    }



    @Override
    public AssetAccessor<? extends StaticAnimation> getHitAnimation(StunType stunType) {
        return null;
    }


    @Nullable
    public PlayerPatch<?> getOwnerPatch() {
        if (ownerPatch != null) {
            return ownerPatch;
        }
        if (getOriginal().getOwner() != null) {
            ownerPatch = EpicFightCapabilities.getEntityPatch(getOriginal().getOwner(), PlayerPatch.class);
            return ownerPatch;
        }
        return null;
    }

    /**
     * 视为主人攻击，并触发事件
     */
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

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean flashTargetIndicator(LocalPlayerPatch playerPatch) {
        return false;
    }

}