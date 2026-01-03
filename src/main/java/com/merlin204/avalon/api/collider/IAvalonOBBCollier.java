package com.merlin204.avalon.api.collider;


import org.joml.Quaternionf;
import org.joml.Vector3f;

//有向包围碰撞箱OBB的接口
public interface IAvalonOBBCollier {
    //获取碰撞箱的轴半长
    Vector3f getHalfExtents();

    //获取碰撞箱的中心点
    Vector3f getCenter();

    // 获取碰撞箱的轴向
    Vector3f[] getAxes();

}
