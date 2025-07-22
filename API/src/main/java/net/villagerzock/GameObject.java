package net.villagerzock;

import net.villagerzock.math.Quaternion;
import net.villagerzock.math.Vector3;

public interface GameObject {
    Vector3 getPosition();
    Quaternion getRotation();
    Vector3 getScale();
}
