package com.inari.firefly.control;

import com.inari.commons.lang.list.IntBag;
import com.inari.firefly.Disposable;
import com.inari.firefly.component.NamedIndexedComponent;

public abstract class Controller extends NamedIndexedComponent implements Disposable {
    
    // TODO check if this is a proper init
    protected IntBag componentIds = new IntBag( 10, -1 );
    
    protected Controller( int id ) {
        super( id );
    }

    @Override
    public final Class<Controller> getIndexedObjectType() {
        return Controller.class;
    }
    
    @Override
    public final Class<Controller> getComponentType() {
        return Controller.class;
    }
    
    public final void addComponentId( int componentId ) {
        componentIds.add( componentId );
    }
    
    public final void removeComponentId( int componentId ) {
        componentIds.remove( componentId );
    }
    
    public abstract void update( long time );

}
