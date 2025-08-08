package net.villagerzock;

import net.villagerzock.math.Quaternion;
import net.villagerzock.math.Vector3;

public class SimpleGameObjectImpl implements GameObject {
    private Vector3 position;
    private Vector3 velocity;
    private Quaternion rotation;
    private Vector3 scale;

    @Override
    public Vector3 getPosition() {
        return position;
    }

    @Override
    public Vector3 getVelocity() {
        return velocity;
    }

    @Override
    public Quaternion getRotation() {
        return rotation;
    }

    @Override
    public Vector3 getScale() {
        return scale;
    }

    @Override
    public void setPosition(Vector3 vector3) {
        position = vector3;
    }

    @Override
    public void setRotation(Quaternion quaternion) {
        rotation = quaternion;
    }

    @Override
    public void setScale(Vector3 vector3) {
        scale = vector3;
    }
}
