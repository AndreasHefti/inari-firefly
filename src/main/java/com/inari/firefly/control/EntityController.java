package com.inari.firefly.control;

import com.inari.firefly.component.NamedIndexedComponent;

public abstract class EntityController extends NamedIndexedComponent {
    
    protected EntityController( int id ) {
        super( id );
    }

    @Override
    public final Class<EntityController> getIndexedObjectType() {
        return EntityController.class;
    }
    
    @Override
    public final Class<EntityController> getComponentType() {
        return EntityController.class;
    }
    
    public abstract ComponentControllerType getComponentControllerType();

    public abstract void update( long time, int entityId );

}
