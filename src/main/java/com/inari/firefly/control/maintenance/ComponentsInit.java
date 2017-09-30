package com.inari.firefly.control.maintenance;

import java.util.Iterator;

import com.inari.commons.lang.indexed.Indexed;
import com.inari.commons.lang.indexed.IndexedTypeKey;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.component.Component;
import com.inari.firefly.component.ComponentId;

public abstract class ComponentsInit extends Init {
    
    protected DynArray<ComponentId> ids;

    protected ComponentsInit( int index ) {
        super( index );
        ids = DynArray.create( ComponentId.class );
    }
    
    public final Iterator<ComponentId> getAllIds() {
        return ids.iterator();
    }

    public final ComponentId getComponentId( final Indexed index ) {
        return getComponentId( index.index() );
    }
    
    public final ComponentId getComponentId( int index ) {
        if ( !ids.contains( index ) ) {
            return null;
        }
        
        return ids.get( index );
    }
    
    public final <C extends Component> C getComponent( final Indexed index ) {
        return getComponent( index.index() );
    }

    public final <C extends Component> C getComponent( int index ) {
        final ComponentId id = getComponentId( index );
        if ( id == null ) {
            return null;
        }
        
        return context.getComponent( id );
    }
    
    protected ComponentsInit set( final Indexed index, IndexedTypeKey typeKey, int componentId ) {
        set( index.index(), typeKey, componentId );
        return this;
    }
    
    protected ComponentsInit set( int index, IndexedTypeKey typeKey, int componentId ) {
        ids.set( index, new ComponentId( typeKey, componentId ) );
        return this;
    }

    protected void cleanup() {
        for ( ComponentId id : ids ) {
            context.deleteComponent( id );
        }
        
        ids.clear();
    }
    

}
