package com.merlin204.avalon.api.collider;


import org.joml.Quaternionf;
import org.joml.Vector3f;

//有向包围碰撞箱OBB的接口
public interface IAvalonOBBCollier {
    //获取碰撞箱的轴半长
    Vector3f getHalfExtents();

    //设置碰撞箱的轴半长
    void setHalfExtents(Vector3f halfExtents);

    //获取碰撞箱的中心点
    Vector3f getCenter();

    //设置碰撞箱的中心点
    void setCenter(Vector3f center);

    // 获取碰撞箱的旋转
    Quaternionf getRotation();

    // 设置碰撞箱的旋转
    void setRotation(Quaternionf rotation);

    // 获取碰撞箱的顶点集
    Vector3f[] getVertices();

    // 获取碰撞箱的轴向
    Vector3f[] getAxes();

}
