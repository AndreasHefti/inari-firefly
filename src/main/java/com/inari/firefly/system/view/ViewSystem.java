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
package com.inari.firefly.system.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.IntIterator;
import com.inari.commons.lang.list.DynArray;
import com.inari.commons.lang.list.IntBag;
import com.inari.firefly.component.build.ComponentCreationException;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.component.ComponentSystem;
import com.inari.firefly.system.component.SystemBuilderAdapter;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.component.SystemComponentBuilder;
import com.inari.firefly.system.view.event.ViewEvent;

public final class ViewSystem extends ComponentSystem<ViewSystem> {

    public static final FFSystemTypeKey<ViewSystem> SYSTEM_KEY = FFSystemTypeKey.create( ViewSystem.class );
    
    private static final SystemComponentKey<?>[] SUPPORTED_COMPONENT_TYPES = new SystemComponentKey[] {
        View.TYPE_KEY,
        Layer.TYPE_KEY,
    };

    public static final int BASE_VIEW_ID = 0;
    private static final int INITAL_SIZE = 20;
    
    private final DynArray<View> views;
    private final DynArray<Layer> layers;
    private final DynArray<IntBag> layersOfView;
    
    private final List<View> orderedViewports;
    private final List<View> activeViewports;
    
    ViewSystem() {
        super( SYSTEM_KEY );
        views = new DynArray<View>( INITAL_SIZE );
        layers = new DynArray<Layer>( INITAL_SIZE );
        orderedViewports = new ArrayList<View>( INITAL_SIZE );
        activeViewports = new ArrayList<View>( INITAL_SIZE );
        layersOfView = new DynArray<IntBag>( INITAL_SIZE );
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
        ViewBuilder viewBuilder = getViewBuilder();
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
        return views.get( viewId );
    }
    
    public final int getViewId( String viewName ) {
        for ( View view : views ) {
            if ( viewName.equals( view.getName() ) ) {
                return view.getId();
            }
        }
        
        return -1;
    }
    
    public final View getView( String viewName ) {
        return getView( getViewId( viewName ) );
    };
    
    public final Iterator<View> activeViewportIterator() {
        return activeViewports.iterator();
    }
    
    public final Collection<View> getActiveViewports() {
        return Collections.unmodifiableCollection( activeViewports );
    }
    
    public final Collection<View> getAllViewports() {
        return Collections.unmodifiableCollection( orderedViewports );
    }
    
    public final boolean hasViewports() {
        return !orderedViewports.isEmpty();
    }
    
    public final boolean hasActiveViewports() {
        return !activeViewports.isEmpty();
    }
    
    public final void activateView( int viewId ) {
        if ( viewId == BASE_VIEW_ID ) {
            throw new IllegalArgumentException( "The baseView (Screen) has no activation" );
        }
        View view = views.get( viewId );
        if ( view != null && !view.isActive() ) {
            view.active = true;
            refreshActiveViewports();
            context.notify( new ViewEvent( view, ViewEvent.Type.VIEW_ACTIVATED ) );
        }
    }

    public final void deactivateView( int viewId ) {
        if ( viewId == BASE_VIEW_ID ) {
            throw new IllegalArgumentException( "The baseView (Screen) has no activation" );
        }
        View view = views.get( viewId );
        if ( view != null && view.isActive() ) {
            view.active = false;
            refreshActiveViewports();
            context.notify( new ViewEvent( view, ViewEvent.Type.VIEW_DISPOSED ) );
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
            refreshActiveViewports();
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
            refreshActiveViewports();
        }
    }
    
    public final void deleteView( int viewId ) {
        View view = views.remove( viewId );
        if ( view != null ) {
            view.active = false;
            orderedViewports.remove( view );
            activeViewports.remove( view );
            disableLayering( viewId );
            context.notify( new ViewEvent( view, ViewEvent.Type.VIEW_DELETED ) );
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
        layersOfView.clear();
    }
    
    public final boolean hasLayer( int layerId ) {
        return layers.contains( layerId );
    }
    
    public final boolean hasLayer( int viewId, int index ) {
        if ( !layersOfView.contains( viewId ) ) {
            return false;
        }
        IntBag layers = layersOfView.get( viewId );
        return layers.get( index ) != layers.getNullValue();
    }
    
    public final Layer getLayer( int layerId ) {
        if ( layers.contains( layerId ) ) {
            return layers.get( layerId );
        }
        
        return null;
    }
    
    public final int getLayerId( String layerName ) {
        for ( Layer layer : layers  ) {
            if ( layerName.equals( layer.getName() ) ) {
                return layer.getId();
            }
        }
        
        return -1;
    }

    public final boolean moveLayerUp( int viewId, int index ) {
        if ( index < 1 ) {
            return false;
        }
        
        if ( !isLayeringEnabled( viewId ) ) {
            return false;
        }
        
        IntBag layers = layersOfView.get( viewId );
        if ( layers.size() <= index ) {
            return false;
        }
        
        layers.swap( index, --index );
        return true;
    }
    
    public final boolean moveLayerDown( int viewId, int index ) {
        if ( index < 0 ) {
            return false;
        }
        
        if ( !isLayeringEnabled( viewId ) ) {
            return false;
        }
        
        IntBag layers = layersOfView.get( viewId );
        if ( index >= layers.size() - 1 ) {
            return false;
        }
        
        layers.swap( index, ++index );
        return true;
    }

    public final void deleteLayers( int viewId ) {
        if ( !isLayeringEnabled( viewId ) ) {
            return;
        }
        
        IntBag layers = layersOfView.get( viewId );
        if ( layers != null ) {
            IntIterator iterator = layers.iterator();
            while( iterator.hasNext() ) {
                Layer removed = this.layers.remove( iterator.next() );
                removed.dispose();
            }
            layers.clear();
        }
    }
    
    public final void deleteLayer( int layerId ) {
        if ( !layers.contains( layerId ) ) {
            return;
        }
        
        Layer toDelete = layers.get( layerId );
        IntBag layersOfView = this.layersOfView.get( toDelete.getViewId() );
        layersOfView.remove( toDelete.getId() );
        toDelete.dispose();
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
        layersOfView.remove( viewId );
        view.setLayeringEnabled( false );
    }

    public final LayerBuilder getLayerBuilder() {
        return new LayerBuilder();
    }

    public final ViewBuilder getViewBuilder() {
        return new ViewBuilder();
    }

    @Override
    public final SystemComponentKey<?>[] supportedComponentTypes() {
        return SUPPORTED_COMPONENT_TYPES;
    }

    @Override
    public final SystemBuilderAdapter<?>[] getSupportedBuilderAdapter() {
        return new SystemBuilderAdapter<?>[] {
            new ViewBuilderAdapter( this ),
            new LayerBuilderAdapter( this )
        };
    }
    
    private void reorder() {
        int order = 0;
        for ( View viewport : orderedViewports ) {
            viewport.order = order;
            order++;
        }
    }
    
    private void refreshActiveViewports() {
        activeViewports.clear();
        for ( View viewport : orderedViewports ) {
            if ( viewport.active ) {
                activeViewports.add( viewport );
            }
        }
    }
    
    
    public final class ViewBuilder extends SystemComponentBuilder {

        protected ViewBuilder() {}
        
        @Override
        public final SystemComponentKey<View> systemComponentKey() {
            return View.TYPE_KEY;
        }

        @Override
        public final int doBuild( int componentId, Class<?> subType, boolean activate ) {
            View view = new View( componentId );
            view.fromAttributes( attributes );
            
            checkName( view );
            views.set( view.index(), view );
            if ( componentId != BASE_VIEW_ID ) {
                view.order = orderedViewports.size();
                orderedViewports.add( view );
            }
            context.notify( new ViewEvent( view, ViewEvent.Type.VIEW_CREATED ) );
            
            if ( activate ) {
                activateView( view.index() );
            }
            return view.getId();
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
    
    public final class LayerBuilder extends SystemComponentBuilder {

        @Override
        public final SystemComponentKey<Layer> systemComponentKey() {
            return Layer.TYPE_KEY;
        }

        @Override
        public int doBuild( int componentId, Class<?> subType, boolean activate ) {
            Layer layer = new Layer( componentId );
            layer.fromAttributes( attributes );
            checkName( layer );
            
            int viewId = layer.getViewId();
            if ( !hasView( viewId ) ) {
                throw new ComponentCreationException( "The View with id: " + viewId + ". dont exists." );
            }
            View view = getView( viewId );
            if ( !view.isLayeringEnabled() ) {
                throw new ComponentCreationException( "Layering is not enabled for view with id: " + viewId + ". Enable Layering for View first" );
            }
            
            IntBag viewLayers;
            if ( !layersOfView.contains( viewId ) ) {
                viewLayers = new IntBag( INITAL_SIZE );
                layersOfView.set( viewId, viewLayers );
            } else {
                viewLayers = layersOfView.get( viewId );
            }
            
            int layerId = layer.getId();
            viewLayers.add( layerId );
            layers.set( layerId, layer );
            
            return layerId;
        }
    }
    
    private final class ViewBuilderAdapter extends SystemBuilderAdapter<View> {
        public ViewBuilderAdapter( ViewSystem system ) {
            super( system, new ViewBuilder() );
        }
        @Override
        public final SystemComponentKey<View> componentTypeKey() {
            return View.TYPE_KEY;
        }
        @Override
        public final View get( int id, Class<? extends View> subtype ) {
            return views.get( id );
        }
        @Override
        public final Iterator<View> getAll() {
            return views.iterator();
        }
        @Override
        public final void deleteComponent( int id, Class<? extends View> subtype ) {
            deleteView( id );
        }
        @Override
        public final void deleteComponent( String name ) {
            deleteView( name );
            
        }
        @Override
        public final View get( String name, Class<? extends View> subType ) {
            return getView( name );
        }
    }
    
    private final class LayerBuilderAdapter extends SystemBuilderAdapter<Layer> {
        public LayerBuilderAdapter( ViewSystem system ) {
            super( system, new LayerBuilder() );
        }
        @Override
        public final SystemComponentKey<Layer> componentTypeKey() {
            return Layer.TYPE_KEY;
        }
        @Override
        public final Layer get( int id, Class<? extends Layer> subtype ) {
            return getLayer( id );
        }
        @Override
        public final Iterator<Layer> getAll() {
            return layers.iterator();
        }
        @Override
        public final void deleteComponent( int id, Class<? extends Layer> subtype ) {
            deleteLayer( id );
        }
        @Override
        public final void deleteComponent( String name ) {
            deleteLayer( getLayerId( name ) );
            
        }
        @Override
        public final Layer get( String name, Class<? extends Layer> subType ) {
            return getLayer( getLayerId( name ) );
        }
    }

}
