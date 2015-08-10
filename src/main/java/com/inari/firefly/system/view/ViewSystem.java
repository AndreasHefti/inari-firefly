/*******************************************************************************
 * Copyright (c) 2015, Andreas Hefti, inarisoft@yahoo.de 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/ 
package com.inari.firefly.system.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.component.ComponentBuilderHelper;
import com.inari.firefly.component.ComponentSystem;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.Attributes;
import com.inari.firefly.component.build.BaseComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilder;
import com.inari.firefly.component.build.ComponentBuilderFactory;
import com.inari.firefly.component.build.ComponentCreationException;
import com.inari.firefly.system.FFComponent;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.ILowerSystemFacade;
import com.inari.firefly.system.view.event.ViewEvent;

public final class ViewSystem implements FFComponent, ComponentSystem, ComponentBuilderFactory {
    
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
        eventDispatcher = context.getComponent( FFContext.EVENT_DISPATCHER );

        // create the base view that is the screen
        ILowerSystemFacade lowerSystemFacade = context.getComponent( FFContext.LOWER_SYSTEM_FACADE );
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

    public final int viewArrayLength() {
        return views.capacity();
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
            eventDispatcher.notify( new ViewEvent( view, ViewEvent.Type.VIEW_DISPOSED ) );
        }
    }
    
    public final void deleteView( int viewId ) {
        View view = views.remove( viewId );
        if ( view != null ) {
            disableLayering( viewId );
            eventDispatcher.notify( new ViewEvent( view, ViewEvent.Type.VIEW_DELETED ) );
            view.dispose();
        }
    }
    
    public final void clear() {
        for( View view : views ) {
            disableLayering( view.index() );
            deleteView( view.index() );
        }
        views.clear();
        layersOfView.clear();
    }
    
    public final boolean hasLayer( int viewId, int layerId ) {
        return getLayer( viewId, layerId ) != null;
    }
    
    public final Layer getLayer( int layerId ) {
        for ( List<Layer> layers : layersOfView ) {
            for ( Layer layer : layers ) {
                if ( layer.index() == layerId ) {
                    return layer;
                }
            }
        }
        
        return null;
    }
    
    public final Layer getLayer( int viewId, int layerId ) {
        if ( !hasView( viewId ) || !isLayeringEnabled( viewId ) ) {
            return null;
        }
        
        Collection<Layer> layers = layersOfView.get( viewId );
        for ( Layer layer : layers ) {
            if ( layerId == layer.index() ) {
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
            for ( Layer layer : layers ) {
                layer.dispose();
            }
            layers.clear();
        }
    }
    
    public final void deleteLayer( int layerId ) {
        for ( List<Layer> layers : layersOfView ) {
            Iterator<Layer> layerIt = layers.iterator();
            while ( layerIt.hasNext() ) {
                Layer layer = layerIt.next();
                if ( layer.index() == layerId ) {
                    layerIt.remove();
                    layer.dispose();
                }
            }
        }
    }
    
    public final void deleteLayer( int viewId, int layerId ) {
        List<Layer> layers = layersOfView.get( viewId );
        if ( layers.size() == 1 ) {
            throw new IllegalArgumentException( "There is only this layer left int the Layer list. If there should be no Layering for that view, please disable Layering instead." );
        }
        
        if ( layers != null ) {
            Layer layer = layers.get( layerId );
            if ( layer != null ) {
                layers.remove( layerId );
                layer.dispose();
            }
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
                return super.setAttribute( key, value.index() );
            }

            @Override
            public Layer build( int componentId ) {
                Layer layer = new Layer( componentId );
                layer.fromAttributes( attributes );
                checkName( layer );
                
                int viewId = layer.getViewId();
                View view = getView( viewId );
                if ( view == null ) {
                    throw new ComponentCreationException( "The View with id: " + viewId + ". dont exists." );
                }
                if ( !view.isLayeringEnabled() ) {
                    throw new ComponentCreationException( "Layering is not enabled for view with id: " + viewId + ". Enable Layering for View first" );
                }
                
                List<Layer> layers;
                if ( !layersOfView.contains( viewId ) ) {
                    layers = new ArrayList<Layer>();
                    layersOfView.set( viewId, layers );
                } else {
                    layers = layersOfView.get( viewId );
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
                view.fromAttributes( attributes );
                checkName( view );
                views.set( view.index(), view );
                eventDispatcher.notify( new ViewEvent( view, ViewEvent.Type.VIEW_CREATED ) );
                return view;
            }
        };
    }

    private void checkViewExists( int viewId ) {
        if ( !hasView( viewId ) ) {
            throw new IllegalArgumentException( "View with id: " + viewId + " doesn't exist." );
        }
    }
    
    private static final Set<Class<?>> SUPPORTED_COMPONENT_TYPES = new HashSet<Class<?>>();
    @Override
    public final Set<Class<?>> supportedComponentTypes() {
        if ( SUPPORTED_COMPONENT_TYPES.isEmpty() ) {
            SUPPORTED_COMPONENT_TYPES.add( View.class );
            SUPPORTED_COMPONENT_TYPES.add( Layer.class );
        }
        return SUPPORTED_COMPONENT_TYPES;
    }

    @Override
    public void fromAttributes( Attributes attributes ) {
        fromAttributes( attributes, BuildType.CLEAR_OLD );
    }

    @Override
    public final void fromAttributes( Attributes attributes, BuildType buildType ) {
        if ( buildType == BuildType.CLEAR_OLD ) {
            clear();
        }
        
        new ComponentBuilderHelper<View>() {
            @Override
            public View get( int id ) {
                return views.get( id );
            }
            @Override
            public void delete( int id ) {
                deleteView( id );
            }
        }.buildComponents( View.class, buildType, getViewBuilder(), attributes );
        
        new ComponentBuilderHelper<Layer>() {
            @Override
            public Layer get( int id ) {
                return getLayer( id );
            }
            @Override
            public void delete( int id ) {
                deleteLayer( id );
            }
        }.buildComponents( Layer.class, buildType, getLayerBuilder(), attributes );
    }

    @Override
    public final void toAttributes( Attributes attributes ) {
        ComponentBuilderHelper.toAttributes( attributes, View.class, views );
        for ( List<Layer> layers : layersOfView ) {
            ComponentBuilderHelper.toAttributes( attributes, Layer.class, layers );
        }
    }

}
