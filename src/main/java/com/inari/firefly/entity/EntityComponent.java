package com.inari.firefly.entity;

import com.inari.commons.lang.indexed.IndexProvider;
import com.inari.commons.lang.indexed.Indexed;
import com.inari.firefly.component.Component;

public abstract class EntityComponent implements Component, Indexed {
    
    private final int index;
    
    protected EntityComponent() {
        index = IndexProvider.getIndexForType( this.getClass(), EntityComponent.class );
    }
    
    @Override
    public final Class<EntityComponent> indexedType() {
        return EntityComponent.class;
    }

    @Override
    public final int index() {
        return index;
    }

}
