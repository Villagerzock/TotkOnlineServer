package net.villagerzock;

import net.villagerzock.math.Quaternion;
import net.villagerzock.math.Vector3;

public interface GameObject {
    Vector3 getPosition();
    Vector3 getVelocity();
    Quaternion getRotation();
    Vector3 getScale();
    void setPosition(Vector3 vector3);
    void setRotation(Quaternion quaternion);
    void setScale(Vector3 vector3);
}
