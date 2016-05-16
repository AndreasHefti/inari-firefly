package com.inari.firefly.physics.collision;

import java.util.BitSet;

import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.Disposable;
import com.inari.commons.lang.aspect.Aspect;

public interface Contact extends Disposable {
    
    int movingEntityId();
    int contactEntityId();
    Rectangle movingWorldBounds();
    Rectangle contactWorldBounds();
    
    Rectangle intersectionBounds();
    BitSet intersectionMask();
    Aspect contactType();
    boolean isSolid();
    
    boolean valid();

}
