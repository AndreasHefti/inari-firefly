package com.inari.firefly.physics.animation;

public interface Frame {
    long intervalTime();
    
    interface IntFrame extends Frame {
        int value();
    }
    
    interface FloatFrame extends Frame {
        float value();
    }
    
    interface ValueFrame<T> extends Frame {
        T value();
    }
}
