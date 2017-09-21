package com.inari.firefly.system.component;

import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;

public final class SystemComponentActivationMap<C extends SystemComponent & Activatable> extends SystemComponentMap<C> {

    public final DynArray<C> activeComponents;
    
    @SuppressWarnings( "unchecked" )
    public SystemComponentActivationMap( 
        ComponentSystem<?> system, 
        SystemComponentKey<C> componentKey
    ) {
        this( system, componentKey, (BuilderListener<C>) VOID_BUILDER_LSTENER, 20, 10 );
    }
    
    @SuppressWarnings( "unchecked" )
    public SystemComponentActivationMap( 
        ComponentSystem<?> system, 
        SystemComponentKey<C> componentKey, 
        int cap, int grow 
    ) {
        this( system, componentKey, (BuilderListener<C>) VOID_BUILDER_LSTENER, cap, grow );
    }
    
    public SystemComponentActivationMap( 
        ComponentSystem<?> system, 
        SystemComponentKey<C> componentKey, 
        BuilderListener<C> builderAdapter
    ) {
        this( system, componentKey, builderAdapter, 20, 10 );
    }
    
    public SystemComponentActivationMap( 
        ComponentSystem<?> system, 
        SystemComponentKey<C> componentKey, 
        BuilderListener<C> builderAdapter, 
        int cap, int grow  
    ) {
        super( system, componentKey, builderAdapter, cap, grow );
        activeComponents = DynArray.create( componentKey.<C>type(), cap, grow );
    }

    @Override
    public final void activate( int id ) {
        super.activate( id );
        C component = get( id );
        component.setActive( true );
        activeComponents.add( component );
    }

    @Override
    public final void deactivate( int id ) {
        super.deactivate( id );
        C component = get( id );
        component.setActive( false );
        activeComponents.remove( component );
    }

}
