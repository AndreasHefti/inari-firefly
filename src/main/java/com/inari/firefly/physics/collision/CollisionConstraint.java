package com.inari.firefly.physics.collision;

import com.inari.commons.lang.indexed.IndexedTypeKey;
import com.inari.firefly.system.component.SystemComponent;

public abstract class CollisionConstraint extends SystemComponent {
    
    public static final SystemComponentKey<CollisionConstraint> TYPE_KEY = SystemComponentKey.create( CollisionConstraint.class );

    protected CollisionConstraint( int id ) {
        super( id );
    }
    
    @Override
    public final IndexedTypeKey indexedTypeKey() {
        return TYPE_KEY;
    }
    
    public abstract void checkCollisions( int entityId, boolean update );

}
