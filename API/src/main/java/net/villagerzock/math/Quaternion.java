package net.villagerzock.math;

public class Quaternion {
    public float x;
    public float y;
    public float z;
    public float w;


    public Quaternion(){
        x = 0;
        y = 0;
        z = 0;
        w = 0;
    }

    public Quaternion(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public float yaw(){
        return (float)Math.toDegrees(Math.atan2(2 * (w * y + z * x),
                1 - 2 * (y * y + z * z)));
    }
}
