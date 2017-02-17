/*******************************************************************************
 * Copyright (c) 2015 - 2016, Andreas Hefti, inarisoft@yahoo.de 
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
package com.inari.firefly.graphics.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.component.build.ComponentCreationException;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.component.ComponentSystem;
import com.inari.firefly.system.component.SystemBuilderAdapter;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.component.SystemComponentBuilder;

public final class ViewSystem extends ComponentSystem<ViewSystem> {

    public static final FFSystemTypeKey<ViewSystem> SYSTEM_KEY = FFSystemTypeKey.create( ViewSystem.class );
    
    private static final SystemComponentKey<?>[] SUPPORTED_COMPONENT_TYPES = new SystemComponentKey[] {
        View.TYPE_KEY,
        Layer.TYPE_KEY
    };

    public static final int BASE_VIEW_ID = 0;
    private static final int INITAL_SIZE = 10;
    
    private final DynArray<View> views;
    private final List<View> orderedViewports;
    private final DynArray<List<Layer>> oderedLayersOfView;
    
    private boolean hasActiveViewports = false;

    ViewSystem() {
        super( SYSTEM_KEY );
        views = new DynArray<View>( INITAL_SIZE );
        oderedLayersOfView = new DynArray<List<Layer>>( INITAL_SIZE );
        orderedViewports = new ArrayList<View>( INITAL_SIZE );
    }
    
    @Override
    public void init( FFContext context ) {
        super.init( context );

        // create the base view that is the screen
        Rectangle screenBounds = new Rectangle(
            0, 0,
            context.getScreenWidth(),
            context.getScreenHeight()
        );
        ViewBuilder viewBuilder = new ViewBuilder();
        viewBuilder.buildBaseView( screenBounds );
    }
    
    @Override
    public final void dispose( FFContext context ) {
        clear();
    }

    public final boolean hasView( int viewId ) {
        return views.contains( viewId );
    }
    
    public final View getView( int viewId ) {
        if ( !views.contains( viewId ) ) {
            return null;
        }
        return views.get( viewId );
    }
    
    public final int getViewId( String viewName ) {
        for ( View view : views ) {
            if ( viewName.equals( view.getName() ) ) {
                return view.index();
            }
        }
        
        return -1;
    }
    
    public final View getView( String viewName ) {
        return getView( getViewId( viewName ) );
    };
    
    
    
    public final Collection<View> getAllViewports() {
        return Collections.unmodifiableCollection( orderedViewports );
    }
    
    public final boolean hasViewports() {
        return !orderedViewports.isEmpty();
    }
    
    
    
    public final boolean hasActiveViewports() {
        return hasActiveViewports;
    }
    
    public final List<View> getViewports() {
        return orderedViewports;
    }
    
    public final void activateView( int viewId ) {
        if ( viewId == BASE_VIEW_ID ) {
            throw new IllegalArgumentException( "The baseView (Screen) has no activation" );
        }
        View view = views.get( viewId );
        if ( view != null && !view.isActive() ) {
            view.active = true;
            hasActiveViewports = true;
            context.notify( ViewEvent.create( ViewEvent.Type.VIEW_ACTIVATED, view ) );
        }
    }

    public final void deactivateView( int viewId ) {
        if ( viewId == BASE_VIEW_ID ) {
            throw new IllegalArgumentException( "The baseView (Screen) has no activation" );
        }
        View view = views.get( viewId );
        if ( view != null && view.isActive() ) {
            view.active = false;
            hasActiveViewports = false;
            for ( View _view : orderedViewports ) {
                if ( _view.active ) {
                    hasActiveViewports = true;
                    break;
                }
            }
            context.notify( ViewEvent.create( ViewEvent.Type.VIEW_DISPOSED, view ) );
        }
    }
    
    public final void moveViewUp( int viewId ) {
        if ( viewId == BASE_VIEW_ID ) {
            throw new IllegalArgumentException( "The baseView (Screen) cannot change its order" );
        }
        
        View view = views.get( viewId );
        int index = orderedViewports.indexOf( view );
        if ( index < orderedViewports.size() - 1 ) {
            orderedViewports.remove( index );
            index++;
            orderedViewports.add( index, view );
            reorder();
        }
    }

    public final void moveViewDown( int viewId ) {
        if ( viewId == BASE_VIEW_ID ) {
            throw new IllegalArgumentException( "The baseView (Screen) cannot change its order" );
        }
        
        View view = views.get( viewId );
        int index = orderedViewports.indexOf( view );
        if ( index >= 1 ) {
            orderedViewports.remove( index );
            index--;
            orderedViewports.add( index, view );
            reorder();
        }
    }
    
    public final void deleteView( int viewId ) {
        View view = views.remove( viewId );
        if ( view != null ) {
            view.active = false;
            orderedViewports.remove( view );
            disableLayering( viewId );
            context.notify( ViewEvent.create( ViewEvent.Type.VIEW_DELETED, view ) );
            view.dispose();
        }
    }
    
    public final void deleteView( String viewName ) {
        deleteView( getViewId( viewName ) );
    }
    
    public final void clear() {
        for ( View view : views ) {
            disableLayering( view.index() );
            deleteView( view.index() );
        }
        views.clear();
        orderedViewports.clear();
        oderedLayersOfView.clear();
    }

    
    public final boolean hasLayer( int viewId, int index ) {
        if ( !oderedLayersOfView.contains( viewId ) ) {
            return false;
        }
        List<Layer> layers = oderedLayersOfView.get( viewId );
        return index >= 0 && index < layers.size() && layers.get( index ) != null;
    }
    
    public final Layer getLayer( int layerId ) {
        for ( List<Layer> layersOfView : oderedLayersOfView ) {
            for ( Layer layer : layersOfView ) {
                if ( layer.index() == layerId ) {
                    return layer;
                }
            }
        }
        
        return null;
    }
    
    public final int getLayerId( String layerName ) {
        for ( List<Layer> layersOfView : oderedLayersOfView ) {
            for ( Layer layer : layersOfView ) {
                if ( layerName.equals( layer.getName() ) ) {
                    return layer.index();
                }
            }
        }
        
        return -1;
    }
    
    public final List<Layer> getLayersOfView( int viewId ) {
        if ( !oderedLayersOfView.contains( viewId ) ) {
            return Collections.emptyList();
        }
        
        return oderedLayersOfView.get( viewId );
    }
    
    public final void activateLayer( int layerId ) {
        if ( layerId < 0 ) {
            return;
        }
        
        getLayer( layerId ).active = true;
    }
    
    public final void activateLayer( String layerName ) {
        activateLayer( getLayerId( layerName ) );
    }
    
    public final void deactivateLayer( String layerName ) {
        deactivateLayer( getLayerId( layerName ) );
    }
    
    public final void deactivateLayer( int layerId ) {
        if ( layerId < 0 ) {
            return;
        }
        
        getLayer( layerId ).active = false;
    }

    public final boolean moveLayerUp( int viewId, int index ) {
        if ( index < 1 ) {
            return false;
        }
        
        if ( !isLayeringEnabled( viewId ) ) {
            return false;
        }
        
        List<Layer> layersOfView = oderedLayersOfView.get( viewId );
        if ( layersOfView == null || layersOfView.size() <= index ) {
            return false;
        }
        
        Layer layer = layersOfView.remove( index );
        index--;
        layersOfView.add( index, layer );
        return true;
    }
    
    public final boolean moveLayerDown( int viewId, int index ) {
        if ( index < 0 ) {
            return false;
        }
        
        if ( !isLayeringEnabled( viewId ) ) {
            return false;
        }
        
        List<Layer> layersOfView = oderedLayersOfView.get( viewId );
        if ( layersOfView == null || index >= layersOfView.size() - 1 ) {
            return false;
        }
        
        Layer layer = layersOfView.remove( index );
        index++;
        layersOfView.add( index, layer );
        return true;
    }
    
    public final void deleteLayers( int viewId ) {
        if ( !isLayeringEnabled( viewId ) ) {
            return;
        }
        
        List<Layer> layers = oderedLayersOfView.get( viewId );
        if ( layers != null ) {
            for ( Layer layer : layers ) {
                layer.dispose();
            }
            layers.clear();
        }
    }
    
    public final void deleteLayer( int layerId ) {
        Layer layer = getLayer( layerId );
        if ( layer == null ) {
            return;
        }
        
        List<Layer> layers = oderedLayersOfView.get( layer.getViewId() );
        layers.remove( layer );
        layer.dispose();
    }
    
    public final boolean isLayeringEnabled( int viewId ) {
        if ( !views.contains( viewId ) ) {
            return false;
        }
        return getView( viewId ).isLayeringEnabled();
    }
    
    public final void enableLayering( int viewId ) {
        if ( !views.contains( viewId ) ) {
            return;
        }
        
        views.get( viewId ).setLayeringEnabled( true );
    }
    
    public final void disableLayering( int viewId ) {
        if ( !isLayeringEnabled( viewId ) ) {
            return;
        }
        
        View view = getView( viewId );
        deleteLayers( viewId );
        view.setLayeringEnabled( false );
    }

    public final SystemComponentBuilder getViewBuilder() {
        return new ViewBuilder();
    }
    
    public final SystemComponentBuilder getLayerBuilder() {
        return new LayerBuilder();
    }

    @Override
    public final SystemComponentKey<?>[] supportedComponentTypes() {
        return SUPPORTED_COMPONENT_TYPES;
    }

    @Override
    public final SystemBuilderAdapter<?>[] getSupportedBuilderAdapter() {
        return new SystemBuilderAdapter<?>[] {
            new ViewBuilderAdapter(),
            new LayerBuilderAdapter()
        };
    }
    
    private void reorder() {
        int order = 0;
        for ( View viewport : orderedViewports ) {
            viewport.order = order;
            order++;
        }
    }
    
    
    private final class ViewBuilder extends SystemComponentBuilder {

        private ViewBuilder() {
            super( context );
        }
        
        @Override
        public final SystemComponentKey<View> systemComponentKey() {
            return View.TYPE_KEY;
        }

        @Override
        public final int doBuild( int componentId, Class<?> subType, boolean activate ) {
            View view = createSystemComponent( componentId, subType, context );
            
            views.set( view.index(), view );
            if ( componentId != BASE_VIEW_ID ) {
                view.order = orderedViewports.size();
                orderedViewports.add( view );
            }
            context.notify( ViewEvent.create( ViewEvent.Type.VIEW_CREATED, view ) );
            
            if ( activate ) {
                activateView( view.index() );
            }
            return view.index();
        }
        
        void buildBaseView( Rectangle screenBounds ) {
            set( View.NAME, "BASE_VIEW" );
            set( View.BOUNDS, screenBounds );
            
            build( BASE_VIEW_ID );
            View view = getView( BASE_VIEW_ID );
            view.active = true;
            view.order = -1;
        }
    }
    
    private final class LayerBuilder extends SystemComponentBuilder {
        
        private LayerBuilder() {
            super( context );
        }

        @Override
        public final SystemComponentKey<Layer> systemComponentKey() {
            return Layer.TYPE_KEY;
        }

        @Override
        public int doBuild( int componentId, Class<?> subType, boolean activate ) {
            Layer layer = createSystemComponent( componentId, subType, context );
            checkName( layer );
            
            int viewId = layer.getViewId();
            if ( !hasView( viewId ) ) {
                throw new ComponentCreationException( "The View with id: " + viewId + ". dont exists." );
            }
            View view = getView( viewId );
            if ( !view.isLayeringEnabled() ) {
                throw new ComponentCreationException( "Layering is not enabled for view with id: " + viewId + ". Enable Layering for View first" );
            }
            
            List<Layer> viewLayers;
            if ( !oderedLayersOfView.contains( viewId ) ) {
                viewLayers = new ArrayList<Layer>( INITAL_SIZE );
                oderedLayersOfView.set( viewId, viewLayers );
            } else {
                viewLayers = oderedLayersOfView.get( viewId );
            }
            
            int layerId = layer.index();
            viewLayers.add( layer );
            
            if ( activate ) {
                activateLayer( layerId );
            }
            
            return layerId;
        }
    }

    private final class ViewBuilderAdapter extends SystemBuilderAdapter<View> {
        private ViewBuilderAdapter() {
            super( ViewSystem.this, View.TYPE_KEY );
        }
        @Override
        public final View get( int id ) {
            return views.get( id );
        }
        @Override
        public final Iterator<View> getAll() {
            return views.iterator();
        }
        @Override
        public final void delete( int id ) {
            deleteView( id );
        }
        @Override
        public int getId( String name ) {
            return getViewId( name );
        }
        @Override
        public void activate( int id ) {
            activateView( id );
        }
        @Override
        public void deactivate( int id ) {
            deactivateView( id );
        }
        @Override
        public final SystemComponentBuilder createComponentBuilder( Class<? extends View> componentType ) {
            return new ViewBuilder();
        }
    }
    
    private final class LayerBuilderAdapter extends SystemBuilderAdapter<Layer> {
        private LayerBuilderAdapter() {
            super( ViewSystem.this, Layer.TYPE_KEY );
        }
        @Override
        public final Layer get( int id ) {
            return getLayer( id );
        }
        @Override
        public final Iterator<Layer> getAll() {
            Collection<Layer> allLayers = new ArrayList<Layer>();
            for ( List<Layer> layersOfView : oderedLayersOfView ) {
                if ( layersOfView == null || layersOfView.isEmpty() ) {
                    continue;
                }
                
                allLayers.addAll( layersOfView );
            }
            return allLayers.iterator();
        }
        @Override
        public final void delete( int id ) {
            deleteLayer( id );
        }
        @Override
        public final int getId( String name ) {
            return getLayerId( name );
        }
        @Override
        public final void activate( int id ) {
            activateLayer( id );
        }
        @Override
        public final void deactivate( int id ) {
            deactivateLayer( id );
        }
        @Override
        public final SystemComponentBuilder createComponentBuilder( Class<? extends Layer> componentType ) {
            return new LayerBuilder();
        }
    }

}
