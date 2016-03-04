package com.inari.firefly.physics.collision;

import com.inari.commons.lang.indexed.IndexedTypeKey;
import com.inari.firefly.system.component.SystemComponent;

public abstract class CollisionResolver extends SystemComponent {
    
    public static final SystemComponentKey<CollisionResolver> TYPE_KEY = SystemComponentKey.create( CollisionResolver.class );

    protected CollisionResolver( int id ) {
        super( id );
    }
    
    @Override
    public final IndexedTypeKey indexedTypeKey() {
        return TYPE_KEY;
    }
    
    public abstract void resolve( Collisions collisions );

}
