package com.inari.firefly.system;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.FFContext;
import com.inari.firefly.component.AttributeKey;
import com.inari.firefly.component.build.BaseComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilderFactory;
import com.inari.firefly.component.build.ComponentCreationException;
import com.inari.firefly.system.event.RenderEvent;
import com.inari.firefly.system.event.ViewEvent;

public final class ViewSystem implements FFSystem, ComponentBuilderFactory {
    
    public static final int BASE_VIEW_ID = 0;

    private IEventDispatcher eventDispatcher;
    
    private final DynArray<View> views;
    private final DynArray<List<Layer>> layersOfView;
    
    
    ViewSystem() {
        views = new DynArray<View>();
        layersOfView = new DynArray<List<Layer>>();
    }
    
    @Override
    public void init( FFContext context ) {
        this.eventDispatcher = context.get( FFContext.System.EVENT_DISPATCHER );

        // create the base view that is the screen
        ILowerSystemFacade lowerSystemFacade = context.get( FFContext.System.LOWER_SYSTEM_FACADE );
        Rectangle screenBounds = new Rectangle(
            0, 0,
            lowerSystemFacade.getScreenWidth(),
            lowerSystemFacade.getScreenHeight()
        );
        getViewBuilder()
            .setAttribute( View.NAME, "BASE_VIEW" )
            .setAttribute( View.ACTIVE, true )
            .setAttribute( View.ORDER, -1 )
            .setAttribute( View.BOUNDS, screenBounds )
            .build( BASE_VIEW_ID )
            .setBase( true );
    }
    
    @Override
    public final void dispose( FFContext context ) {
        clear();
    }

    public final boolean hasView( int viewId ) {
        return views.contains( viewId );
    }
    
    public final View getView( int viewId ) {
        return views.get( viewId );
    }
    
    public final void activateView( int viewId ) {
        View view = views.get( viewId );
        if ( view != null && !view.isActive() ) {
            view.setActive( true );
            eventDispatcher.notify( new ViewEvent( view, ViewEvent.Type.VIEW_ACTIVATED ) );
        }
    }

    public final void deactivateView( int viewId ) {
        View view = views.get( viewId );
        if ( view != null && view.isActive() ) {
            view.setActive( false );
            eventDispatcher.notify( new ViewEvent( view, ViewEvent.Type.VIEW_DISABLED ) );
        }
    }
    
    public final void deleteView( int viewId ) {
        View view = views.remove( viewId );
        if ( view != null ) {
            disableLayering( viewId );
            eventDispatcher.notify( new ViewEvent( view, ViewEvent.Type.VIEW_DELETED ) );
        }
    }
    
    public final void clear() {
        for( View view : views ) {
            disableLayering( view.indexedId() );
            deleteView( view.indexedId() );
        }
        views.clear();
        layersOfView.clear();
    }
    
    public final boolean hasLayer( int viewId, int layerId ) {
        return getLayer( viewId, layerId ) != null;
    }
    
    public final Layer getLayer( int viewId, int layerId ) {
        if ( !hasView( viewId ) || !isLayeringEnabled( viewId ) ) {
            return null;
        }
        
        Collection<Layer> layers = layersOfView.get( viewId );
        for ( Layer layer : layers ) {
            if ( layerId == layer.indexedId() ) {
                return layer;
            }
        }
        
        return null;
    }
    
    public final boolean moveLayerUp( int viewId, int index ) {
        if ( index < 1 ) {
            return false;
        }
        
        checkViewExists( viewId );
        List<Layer> layers = layersOfView.get( viewId );
        if ( layers.size() <= index ) {
            return false;
        }
        
        Layer removed = layers.remove( index );
        layers.add( --index, removed );
        return true;
    }
    
    public final boolean moveLayerDown( int viewId, int index ) {
        if ( index < 0 ) {
            return false;
        }
        
        checkViewExists( viewId );
        List<Layer> layers = layersOfView.get( viewId );
        if ( index >= layers.size() - 1 ) {
            return false;
        }
        
        Layer removed = layers.remove( index );
        layers.add( ++index, removed );
        return true;
    }

    public final void deleteLayers( int viewId ) {
        checkViewExists( viewId );
        if ( !isLayeringEnabled( viewId ) ) {
            return;
        }
        
        Collection<Layer> layers = layersOfView.remove( viewId );
        if ( layers != null ) {
            layers.clear();
        }
    }
    
    public final void deleteLayer( int viewId, int layerId ) {
        Collection<Layer> layers = layersOfView.get( viewId );
        if ( layers.size() == 1 ) {
            throw new IllegalArgumentException( "There is only this layer left int the Layer list. If there should be no Layering for that view, please disable Layering instead." );
        }
        
        if ( layers != null ) {
            layers.remove( layerId );
        }
    }
    
    public final boolean isLayeringEnabled( int viewId ) {
        checkViewExists( viewId );
        return getView( viewId ).isLayeringEnabled();
    }
    
    public final void enableLayering( int viewId ) {
        if ( isLayeringEnabled( viewId ) ) {
            return;
        }

        layersOfView.set( viewId, new ArrayList<Layer>() );
        // build the first Layer and give it a default name
        Layer layer = getLayerBuilder().build();
        layer.setName( "Layer 0" );
    }
    
    public final void disableLayering( int viewId ) {
        
        if ( !isLayeringEnabled( viewId ) ) {
            return;
        }
        
        View view = getView( viewId );
        deleteLayers( viewId );
        layersOfView.remove( viewId );
        view.setLayeringEnabled( false );
    }
    
    public final void renderActiveViews( RenderEvent renderEvent ) {
        for ( View view : views ) {
            if ( !view.isActive() ) {
                continue;
            }
            
            int viewId = view.indexedId();
            renderEvent.setViewId( viewId );
            renderEvent.setClip( view.getBounds() );
            eventDispatcher.notify( renderEvent );
        }
    }
    
    @Override
    @SuppressWarnings( "unchecked" )
    public final <C> ComponentBuilder<C> getComponentBuilder( Class<C> type ) {
        if ( type == View.class ) {
            return (ComponentBuilder<C>) getViewBuilder();
        }
        
        if ( type == Layer.class ) {
            return (ComponentBuilder<C>) getLayerBuilder();
        }
        
        throw new IllegalArgumentException( "Unsupported IComponent type for ViewSystem. Type: " + type );
    }

    public final ComponentBuilder<Layer> getLayerBuilder() {
        return new BaseComponentBuilder<Layer>( this ) {
            

            @SuppressWarnings( "unused" )
            public ComponentBuilder<Layer> setAttribute( AttributeKey<Integer> key, View value ) {
                return super.setAttribute( key, value.indexedId() );
            }

            @Override
            public Layer build( int componentId ) {
                Layer layer = new Layer( componentId );
                layer.fromAttributeMap( attributes );
                int viewId = layer.getViewId();
                View view = getView( viewId );
                if ( view == null ) {
                    throw new ComponentCreationException( "The View with id: " + viewId + ". dont exists." );
                }
                if ( !view.isLayeringEnabled() ) {
                    throw new ComponentCreationException( "Layering is not enabled for view with id: " + viewId + ". Enable Layering for View first" );
                }
                
                List<Layer> layers = layersOfView.get( viewId );
                if ( layers == null ) {
                    layers = new ArrayList<Layer>();
                    layersOfView.set( viewId, layers );
                }
             
                layers.add( layer );
                return layer;
            }
        };
    }

    public final ComponentBuilder<View> getViewBuilder() {
        return new BaseComponentBuilder<View>( this ) {
            @Override
            public View build( int componentId ) {
                View view = new View( componentId );
                view.fromAttributeMap( attributes );
                views.set( view.indexedId(), view );
                eventDispatcher.notify( new ViewEvent( view, ViewEvent.Type.VIEW_CREATED ) );
                return view;
            }
        };
    }
    
    @Override
    public final String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "ViewSystem [views=" );
        builder.append( views );
        builder.append( ", layersOfView=" );
        builder.append( layersOfView );
        builder.append( "]" );
        return builder.toString();
    }

    private void checkViewExists( int viewId ) {
        if ( !hasView( viewId ) ) {
            throw new IllegalArgumentException( "View with id: " + viewId + " doesn't exist." );
        }
    }

}
