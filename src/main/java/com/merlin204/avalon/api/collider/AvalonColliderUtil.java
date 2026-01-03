package com.merlin204.avalon.api.collider;

import org.joml.Intersectionf;
import org.joml.Vector3f;

//判断碰撞箱之间是否碰撞的工具方法集(joml伟大无需多言)
public class AvalonColliderUtil {


    /**
     * 判断OBB碰撞箱与OBB碰撞箱是否碰撞
     */
    public static boolean isColliding(IAvalonOBBCollier a,IAvalonOBBCollier b) {
        return Intersectionf.testObOb(a.getCenter(), a.getAxes()[0], a.getAxes()[1], a.getAxes()[2], a.getHalfExtents(), b.getCenter(), b.getAxes()[0], b.getAxes()[1], b.getAxes()[2], b.getHalfExtents());
    }









}
