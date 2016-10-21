package com.inari.firefly.component;

import com.inari.commons.lang.IntIterator;
import com.inari.commons.lang.indexed.IndexedTypeKey;
import com.inari.commons.lang.list.IntBag;

public class ComponentGroup {
    
    private final IndexedTypeKey componentTypeKey;
    private final IntBag componentIds = new IntBag( 10, -1 );

    public ComponentGroup( IndexedTypeKey componentTypeKey ) {
        this.componentTypeKey = componentTypeKey;
    }

    public final IndexedTypeKey getComponentTypeKey() {
        return componentTypeKey;
    }
    
    public final IntIterator getComponentIds() {
        return componentIds.iterator();
    }
    
    public final ComponentGroup add( int componentId ) {
        componentIds.add( componentId );
        return this;
    }
    
    public final ComponentGroup remove( int componentId ) {
        componentIds.remove( componentId );
        return this;
    }

}
