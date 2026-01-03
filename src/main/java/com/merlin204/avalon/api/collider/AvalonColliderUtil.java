package com.merlin204.avalon.api.collider;

import net.minecraft.world.phys.Vec3;
import org.joml.Intersectionf;
import org.joml.Vector3f;
import yesman.epicfight.api.utils.math.Vec3f;

//判断碰撞箱之间是否碰撞的工具方法集(joml伟大无需多言)
public class AvalonColliderUtil {


    /**
     * 判断OBB碰撞箱与OBB碰撞箱是否碰撞
     */
    public static boolean isColliding(IAvalonOBBCollier a,IAvalonOBBCollier b) {
        return Intersectionf.testObOb(a.getCenter(), a.getAxes()[0], a.getAxes()[1], a.getAxes()[2], a.getHalfExtents(), b.getCenter(), b.getAxes()[0], b.getAxes()[1], b.getAxes()[2], b.getHalfExtents());
    }


    /**
     * 判断射线与OBB碰撞箱是否碰撞
     */
    public static boolean isColliding(Vector3f start, Vector3f end, IAvalonOBBCollier obb) {
        //不知道为什么所有obb的世界坐标xy都是TM反的，所以这里也需要反一下（why yesman! why?）
        start = new Vector3f(-start.x,start.y,-start.z);
        end = new Vector3f(-end.x,end.y,-end.z);
        Vec3 segmentDirection = new Vec3(end.sub(start));
        double segmentLength = segmentDirection.length();
        if (segmentLength < 1e-10) {
            //线段为点的情况
            Vector3f[] axes = obb.getAxes();
            Vector3f halfExtents = obb.getHalfExtents();
            Vector3f relative = start.sub(obb.getCenter());
            for (int i = 0; i < 3; i++) {
                double projection = relative.dot(axes[i]);
                double halfExtent = (i == 0) ? halfExtents.x : (i == 1) ? halfExtents.y : halfExtents.z;
                if (Math.abs(projection) > halfExtent + 1e-6) {
                    return false;
                }
            }
            return true;
        }
        segmentDirection = segmentDirection.scale(1.0 / segmentLength);

        Vector3f[] axes = obb.getAxes();
        Vector3f halfExtents = obb.getHalfExtents();

        Vec3 centerToStart = new Vec3(start.sub(obb.getCenter()));

        double tMin = 0.0;
        double tMax = segmentLength;

        for (int i = 0; i < 3; i++) {
            Vec3 axis = new Vec3(axes[i]);
            double segProj = segmentDirection.dot(axis);
            double centerProj = centerToStart.dot(axis);
            double halfExtent = (i == 0) ? halfExtents.x : (i == 1) ? halfExtents.y : halfExtents.z;
            if (Math.abs(segProj) > 1e-10) {
                double t1 = (-halfExtent - centerProj) / segProj;
                double t2 = (halfExtent - centerProj) / segProj;
                if (t1 > t2) {
                    double temp = t1;
                    t1 = t2;
                    t2 = temp;
                }
                // 更新相交区间
                tMin = Math.max(tMin, t1);
                tMax = Math.min(tMax, t2);
                // 无相交
                if (tMin > tMax) {
                    return false;
                }
                // 相交点在线段范围外
                if (tMax < 0 || tMin > segmentLength) {
                    return false;
                }
            } else {
                // 线段平行于此轴，检查是否在OBB范围内
                if (Math.abs(centerProj) > halfExtent) {
                    return false;
                }
            }
        }
        return tMin <= segmentLength && tMax >= 0;
    }







}
