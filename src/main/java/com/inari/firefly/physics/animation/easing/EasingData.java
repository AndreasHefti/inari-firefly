package com.inari.firefly.physics.animation.easing;

import com.inari.commons.geom.Easing;
import com.inari.commons.geom.Easing.Type;

public final class EasingData {
    
    final Easing.Type easingType;
    final float startValue;
    final float changeInValue;
    final long duration;
    
    EasingData() {
        easingType = Easing.Type.LINEAR;
        startValue = -1;
        changeInValue = -1;
        duration = -1;
    }
    
    public EasingData( Type easingType, float startValue, float changeInValue, long duration ) {
        this.easingType = easingType;
        this.startValue = startValue;
        this.changeInValue = changeInValue;
        this.duration = duration;
    }

    public final Easing.Type getEasingType() {
        return easingType;
    }

    public final float getStartValue() {
        return startValue;
    }

    public final float getChangeInValue() {
        return changeInValue;
    }

    public final long getDuration() {
        return duration;
    }

}
