package com.inari.firefly.entity;

import com.inari.commons.lang.indexed.Indexed;
import com.inari.commons.lang.indexed.Indexer;
import com.inari.firefly.component.Component;

public abstract class EntityComponent implements Component, Indexed {
    
    private final int index;
    
    protected EntityComponent() {
        index = Indexer.getIndexForType( this.getClass(), EntityComponent.class );
    }
    
    @Override
    public final Class<EntityComponent> indexedType() {
        return EntityComponent.class;
    }

    @Override
    public final int index() {
        return index;
    }
    
    public int getControllerId() {
        return -1;
    }

}
