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
package com.inari.firefly.renderer.sprite;

import java.util.Comparator;

import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.lang.TypedKey;
import com.inari.commons.lang.aspect.IndexedAspect;
import com.inari.commons.lang.indexed.IndexedTypeSet;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.entity.event.EntityActivationEvent;
import com.inari.firefly.entity.event.EntityActivationListener;
import com.inari.firefly.renderer.SpriteRenderable;
import com.inari.firefly.renderer.tile.ETile;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFContextInitiable;

public final class SpriteViewSystem implements FFContextInitiable, EntityActivationListener {
    
    public static final TypedKey<SpriteViewSystem> CONTEXT_KEY = TypedKey.create( "FF_SPRITE_VIEW_SYSTEM", SpriteViewSystem.class ); 
    
    private EntitySystem entitySystem;

    private final DynArray<DynArray<DynArray<IndexedTypeSet>>> spritesPerViewAndLayer;
    
    SpriteViewSystem() {
        spritesPerViewAndLayer = new DynArray<DynArray<DynArray<IndexedTypeSet>>>();
    }
    
    @Override
    public void init( FFContext context ) {
        entitySystem = context.getComponent( EntitySystem.CONTEXT_KEY );

        IEventDispatcher eventDispatcher = context.getComponent( FFContext.EVENT_DISPATCHER );
        eventDispatcher.register( EntityActivationEvent.class, this );
    }
    
    @Override
    public final void dispose( FFContext context ) {
        IEventDispatcher eventDispatcher = context.getComponent( FFContext.EVENT_DISPATCHER );
        eventDispatcher.unregister( EntityActivationEvent.class, this );
        
        spritesPerViewAndLayer.clear();
    }
    
    @Override
    public final boolean match( IndexedAspect aspect ) {
        return aspect.contains( ESprite.COMPONENT_TYPE ) && !aspect.contains( ETile.COMPONENT_TYPE );
    }
    
    @Override
    public final void onEntityActivationEvent( EntityActivationEvent event ) {
        IndexedTypeSet components = entitySystem.getComponents( event.entityId );
        ETransform transform = components.get( ETransform.COMPONENT_TYPE );
        int viewId = transform.getViewId();
        int layerId = transform.getLayerId();
        switch ( event.eventType ) {
            case ENTITY_ACTIVATED: {
                DynArray<IndexedTypeSet> renderablesOfView = getSprites( viewId, layerId, true );
                renderablesOfView.add( components );
                renderablesOfView.sort( RENDERABLE_COMPARATOR );
                break;
            }
            case ENTITY_DEACTIVATED: {
                DynArray<IndexedTypeSet> renderablesOfView = getSprites( viewId, layerId, false );
                renderablesOfView.remove( components );
            }
        }
    }
    
    public final DynArray<IndexedTypeSet> getSprites( int viewId, int layerId ) {
        return getSprites( viewId, layerId, false );
    }
 
    private final DynArray<IndexedTypeSet> getSprites( int viewId, int layerId, boolean createNew ) {
        DynArray<DynArray<IndexedTypeSet>> spritePerLayer = null;
        if ( spritesPerViewAndLayer.contains( viewId ) ) { 
            spritePerLayer = spritesPerViewAndLayer.get( viewId );
        } else if ( createNew ) {
            spritePerLayer = new DynArray<DynArray<IndexedTypeSet>>();
            spritesPerViewAndLayer.set( viewId, spritePerLayer );
        }
        
        if ( spritePerLayer == null ) {
            return null;
        }
        
        DynArray<IndexedTypeSet> spritesOfLayer = null;
        if ( spritePerLayer.contains( layerId ) ) { 
            spritesOfLayer = spritePerLayer.get( layerId );
        } else if ( createNew ) {
            spritesOfLayer = new DynArray<IndexedTypeSet>();
            spritePerLayer.set( layerId, spritesOfLayer );
        }
        
        return spritesOfLayer;
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
            int o1 = sr1.getOrdering();
            int o2 = sr2.getOrdering();
            if ( o1 == o2 ) {
                return 0;
            }
            
            return ( o1 < o2 )? 1 : -1;
        }
    };
}
