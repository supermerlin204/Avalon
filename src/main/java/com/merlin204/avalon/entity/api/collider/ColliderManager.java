package com.merlin204.avalon.entity.api.collider;

import com.merlin204.avalon.network.NetworkHandler;
import com.merlin204.avalon.network.server.SyncHitJointPacket;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

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
        NetworkHandler.sendToAllClient(new SyncHitJointPacket(tag,owner.getId()));
    }

    public LivingEntity getOwner() {
        return owner;
    }

    public HashMap<Joint, EntityOBBCollider> getColliderMap() {
        return map;
    }



}
