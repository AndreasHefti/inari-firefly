package com.inari.firefly.system.component;

import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.graphics.view.ViewAndLayerAware;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;

public class SystemComponentViewLayerMap<C extends SystemComponent & ViewAndLayerAware> extends SystemComponentMap<C> {
    
    final DynArray<DynArray<C>> componentOfViewsPerLayer;
    
    @SuppressWarnings( "unchecked" )
    public SystemComponentViewLayerMap( 
        ComponentSystem<?> system, 
        SystemComponentKey<C> componentKey
    ) {
        this( system, componentKey, (BuilderListener<C>) VOID_BUILDER_LSTENER, 20, 10 );
    }
    
    @SuppressWarnings( "unchecked" )
    public SystemComponentViewLayerMap( 
        ComponentSystem<?> system, 
        SystemComponentKey<C> componentKey, 
        int cap, int grow 
    ) {
        this( system, componentKey, (BuilderListener<C>) VOID_BUILDER_LSTENER, cap, grow );
    }
    
    public SystemComponentViewLayerMap( 
        ComponentSystem<?> system, 
        SystemComponentKey<C> componentKey, 
        BuilderListener<C> builderAdapter
    ) {
        this( system, componentKey, builderAdapter, 20, 10 );
    }
    
    public SystemComponentViewLayerMap( 
        ComponentSystem<?> system, 
        SystemComponentKey<C> componentKey, 
        BuilderListener<C> builderAdapter, 
        int cap, int grow  
    ) {
        super( system, componentKey, builderAdapter, cap, grow );
        componentOfViewsPerLayer = DynArray.createTyped( DynArray.class, cap, grow );
    }
    
    @Override
    public final void set( int index, C component ) {
        super.set( index, component );
        
        int viewId = component.getViewId();
        if ( !componentOfViewsPerLayer.contains( viewId ) ) {
            componentOfViewsPerLayer.set( viewId, DynArray.create( componentKey.<C>type(), 10, 5 ) );
        }
        
        DynArray<C> perLayer = componentOfViewsPerLayer.get( viewId );
        perLayer.set( component.getLayerId(), component );
    }
    
    public final C get( ViewAndLayerAware viewAndLayerAwareComponent ) {
        return get( 
            viewAndLayerAwareComponent.getViewId(), 
            viewAndLayerAwareComponent.getLayerId() 
        ); 
    }
    
    public final C get( int viewId, int layerId ) {
        if ( !componentOfViewsPerLayer.contains( viewId ) ) {
            return null;
        }

        return componentOfViewsPerLayer
            .get( viewId )
            .get( layerId );
    }

    @Override
    public final C remove( int id ) {
        C removed = super.remove( id );
        if ( removed != null ) {
            componentOfViewsPerLayer
                .get( removed.getViewId() )
                .remove( removed.getLayerId() );
        }
        
        return removed;
    }
    
    public final void delete( int viewId, int layerId ) {
        if ( !componentOfViewsPerLayer.contains( viewId ) ) {
            return;
        }
        DynArray<C> perLayer = componentOfViewsPerLayer.get( viewId );
        if ( !perLayer.contains( layerId ) ) {
            return;
        }
        
        delete( perLayer.get( layerId ).index() );
    }
    
    public final void deleteAll( int viewId ) {
        if ( componentOfViewsPerLayer.contains( viewId ) ) {
            final DynArray<C> toDelete = componentOfViewsPerLayer.get( viewId );
            for ( C c : toDelete ) {
                delete( c.index() );
            }
        }
    }
    
    @Override
    public final void clear() {
        super.clear();
        componentOfViewsPerLayer.clear();
    }

}
