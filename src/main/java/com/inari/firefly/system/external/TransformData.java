package com.inari.firefly.system.external;

public interface TransformData {
    
    float getXOffset();
    float getYOffset();
    float getScaleX();
    float getScaleY();
    float getPivotX();
    float getPivotY();
    float getRotation();
    boolean hasRotation();
    boolean hasScale();
}
