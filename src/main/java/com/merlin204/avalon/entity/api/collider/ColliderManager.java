package com.merlin204.avalon.entity.api.collider;


import com.merlin204.avalon.network.client.common.CPSyncHitJointList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import yesman.epicfight.api.animation.Joint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ColliderManager {
    private final HashMap<Joint, EntityOBBCollider> map;
    private final LivingEntity owner;
    private List<Integer> hitList = new ArrayList<>();



    public ColliderManager(LivingEntity owner,HashMap<Joint, EntityOBBCollider> map) {
        this.map = map;
        this.owner = owner;
    }

    public List<Integer> getHitList() {
        return hitList;
    }

    public void setHitList(List<Integer> hitList) {
        this.hitList = hitList;
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

    public HashMap<Joint, EntityOBBCollider> getColliderMap() {
        return map;
    }



}
