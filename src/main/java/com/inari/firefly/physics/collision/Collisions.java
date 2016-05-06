package com.inari.firefly.physics.collision;

import java.util.BitSet;
import java.util.Collections;
import java.util.Iterator;

import com.inari.commons.geom.Rectangle;

public interface Collisions extends Iterable<Collisions.CollisionData> {
    
    public static final Collisions EMPTY_COLLISSIONS = new Collisions() {
        @Override public Iterator<CollisionData> iterator() { return Collections.emptyIterator(); }
        @Override public int movingEntityId() { return -1; }
        @Override public Rectangle worldBounds() { return null; }
        @Override public void update() {}
        @Override public int size() { return 0; }
    };
    
    int movingEntityId();
    
    Rectangle worldBounds();

    void update();

    int size();
    
    public interface CollisionData {
        
        int entityId();
        Rectangle worldBounds();
        Rectangle intersectionBounds();
        BitSet intersectionMask();
    }


}