package com.inari.firefly.control.maintenance;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.inari.firefly.component.Component;
import com.inari.firefly.component.ComponentId;

public abstract class NamedComponentsInit extends Init {
    
    protected Map<String, ComponentId> ids;

    protected NamedComponentsInit( int index ) {
        super( index );
        ids = new HashMap<String, ComponentId>();
    }
    
    public final Map<String, ComponentId> getComponentIds() {
        return Collections.unmodifiableMap( ids );
    }
    
    public final ComponentId getComponentId( String name ) {
        return ids.get( name );
    }
    
    public final int getId( String name ) {
        final ComponentId id = getComponentId( name );
        if ( id == null ) {
            return -1;
        }
        
        return id.indexId;
    }
    
    public final <C extends Component> C getComponent( String name ) {
        final ComponentId id = getComponentId( name );
        if ( id == null ) {
            return null;
        }
        
        return context.getComponent( id );
    }

    protected void cleanup() {
        for ( ComponentId id : ids.values() ) {
            context.deleteComponent( id );
        }
        
        ids.clear();
    }
    
    

}
