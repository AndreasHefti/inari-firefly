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
package com.inari.firefly.sprite;

import java.util.Comparator;

import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.geom.Vector2f;
import com.inari.commons.lang.aspect.Aspect;
import com.inari.commons.lang.indexed.IndexedTypeSet;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.FFContext;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.IEntitySystem;
import com.inari.firefly.entity.event.AspectedEntityActivationListener;
import com.inari.firefly.entity.event.EntityActivationEvent;
import com.inari.firefly.sprite.tile.ETile;
import com.inari.firefly.sprite.tile.TileGrid.TileGridIterator;
import com.inari.firefly.sprite.tile.TileGridSystem;
import com.inari.firefly.system.FFSystem;
import com.inari.firefly.system.ILowerSystemFacade;
import com.inari.firefly.system.event.RenderEvent;
import com.inari.firefly.system.event.RenderEventListener;

public final class SpriteRendererSystem implements FFSystem, AspectedEntityActivationListener, RenderEventListener {
    
    private IEventDispatcher eventDispatcher;
    private IEntitySystem entityProvider;
    private TileGridSystem tileGridSystem;
    private ILowerSystemFacade lowerSystemFacade;
    
    private final DynArray<DynArray<IndexedTypeSet>> renderablesPerView;
    
    SpriteRendererSystem() {
        renderablesPerView = new DynArray<DynArray<IndexedTypeSet>>();
    }
    
    @Override
    public void init( FFContext context ) {
        eventDispatcher = context.get( FFContext.System.EVENT_DISPATCHER );
        entityProvider = context.get( FFContext.System.ENTITY_SYSTEM );
        lowerSystemFacade = context.get( FFContext.System.LOWER_SYSTEM_FACADE );
        tileGridSystem = context.get( FFContext.System.TILE_GRID_SYSTEM ); 

        eventDispatcher.register( EntityActivationEvent.class, this );
        eventDispatcher.register( RenderEvent.class, this );
    }
    
    @Override
    public final void dispose( FFContext context ) {
        IEventDispatcher eventDispatcher = context.get( FFContext.System.EVENT_DISPATCHER );
        eventDispatcher.unregister( EntityActivationEvent.class, this );
        eventDispatcher.unregister( RenderEvent.class, this );
        
        renderablesPerView.clear();
    }
    
    @Override
    public final boolean match( Aspect aspect ) {
        return aspect.contains( ESprite.COMPONENT_TYPE );
    }
    
    @Override
    public final void onEntityActivationEvent( EntityActivationEvent event ) {
        IndexedTypeSet components = entityProvider.getComponents( event.entityId );
        ESprite sprite = components.get( ESprite.COMPONENT_TYPE );
        int viewId = sprite.getViewId();
        switch ( event.type ) {
            case ENTITY_ACTIVATED: {
                DynArray<IndexedTypeSet> renderablesOfView = getRenderablesOfView( viewId, true );
                renderablesOfView.add( components );
                renderablesOfView.sort( RENDERABLE_COMPARATOR );
                break;
            }
            case ENTITY_DEACTIVATED: {
                DynArray<IndexedTypeSet> renderablesOfView = getRenderablesOfView( viewId, false );
                renderablesOfView.remove( components );
            }
        }
    }

    @Override
    public final void render( RenderEvent event ) {
        int viewId = event.getViewId();
        int layer = -1;
        DynArray<IndexedTypeSet> renderableOfView = renderablesPerView.get( viewId );
        if ( renderableOfView == null ) {
            return;
        }
        
        for ( int i = 0; i < renderableOfView.capacity(); i++ ) {
            IndexedTypeSet components = renderableOfView.get( i );
            ESprite sprite = components.get( ESprite.COMPONENT_TYPE );
            ETransform transform = components.get( ETransform.COMPONENT_TYPE );
            int layerId = sprite.getLayerId();
            if ( layer != layerId ) {
                layer = layerId;
                renderTileGrid( viewId, layer, event.getClip() );
            }
            lowerSystemFacade.renderSprite( sprite, transform );
        }
    }
    
    private final void renderTileGrid( int viewId, int layerId, Rectangle clip ) {
        TileGridIterator iterator = tileGridSystem.iterator( viewId, layerId, clip );
        if ( iterator == null ) {
            return;
        }
        
        Vector2f actualWorldPosition  = iterator.getWorldPosition();
        while( iterator.hasNext() ) {
            ETile cTile = entityProvider.getComponent( iterator.next(), ETile.COMPONENT_TYPE );
            lowerSystemFacade.renderSprite( cTile, actualWorldPosition.dx, actualWorldPosition.dy );
        }
    }

    private final DynArray<IndexedTypeSet> getRenderablesOfView( int viewId, boolean createNew ) {
        DynArray<IndexedTypeSet> renderableOfView = renderablesPerView.get( viewId );
        if ( renderableOfView == null && createNew ) {
            renderableOfView = new DynArray<IndexedTypeSet>();
            renderablesPerView.set( viewId, renderableOfView );
        }
        return renderableOfView;
    }
    
    private static final Comparator<IndexedTypeSet> RENDERABLE_COMPARATOR = new Comparator<IndexedTypeSet>() {
        
        @Override
        public final int compare( IndexedTypeSet its1, IndexedTypeSet its2 ) {
            if ( its1 == null && its2 == null ) {
                return 0;
            }
            if ( its1 == null ) {
                return 1;
            }
            if ( its2 == null ) {
                return -1;
            }
            SpriteRenderable sr1 = its1.get( ESprite.COMPONENT_TYPE );
            SpriteRenderable sr2 = its2.get( ESprite.COMPONENT_TYPE );
            int l1 = sr1.getLayerId();
            int l2 = sr2.getLayerId();
            int o1 = sr1.getOrdering();
            int o2 = sr2.getOrdering();
            if ( l1 != l2 ) {
                return ( l1 < l2 )? 1 : -1;
            }
            if ( o1 == o2 ) {
                return 0;
            }
            
            return ( o1 < o2 )? 1 : -1;
        }
    };
}
