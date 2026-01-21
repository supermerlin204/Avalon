package com.merlin204.avalon.entity.api.collider;


import com.merlin204.avalon.network.client.common.CPSyncHitJointList;
import com.merlin204.avalon.network.server.SPSyncElapsedTime;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ColliderManager {
    private final HashMap<Joint, AvalonEntityOBBCollider> map;
    private final LivingEntity owner;
    private List<Integer> hitList = new ArrayList<>();

    //动画播放的时间(由客户端单方面同步至服务端,因为需要同步Living状态下的Pose)
    private float elapsedTime;


    public ColliderManager(LivingEntity owner,HashMap<Joint, AvalonEntityOBBCollider> map) {
        this.map = map;
        this.owner = owner;
    }

    public float getElapsedTime() {
        return Math.clamp(elapsedTime,0,Float.MAX_VALUE);
    }

    public List<Integer> getHitList() {
        return hitList;
    }

    public void setHitList(List<Integer> hitList) {
        this.hitList = hitList;
    }

    public void setElapsedTime(float elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public void syncElapsedTimeToServer(){
        if (owner.level().isClientSide){
            if (EpicFightCapabilities.getEntityPatch(owner, LivingEntityPatch.class) != null) {
                LivingEntityPatch<?> livingEntityPatch = EpicFightCapabilities.getEntityPatch(owner, LivingEntityPatch.class);
                float elapsedTime = livingEntityPatch.getAnimator().getPlayerFor(null).getElapsedTime();
                PacketDistributor.sendToServer(new SPSyncElapsedTime(owner.getId(),elapsedTime));
            }
        }
    }

    public void syncHitJointToClient(List<Integer> hitList){
        CompoundTag tag = new CompoundTag();
        tag.putIntArray("hit",hitList);
        if (owner.level() instanceof ServerLevel serverLevel){
            PacketDistributor.sendToAllPlayers(new CPSyncHitJointList(owner.getId(),tag));
        }

    }

    public LivingEntity getOwner() {
        return owner;
    }

    public HashMap<Joint, AvalonEntityOBBCollider> getColliderMap() {
        return map;
    }



}
