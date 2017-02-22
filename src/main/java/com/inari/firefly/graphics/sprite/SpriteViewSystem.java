package com.inari.firefly.graphics.sprite;

import java.util.Comparator;

import com.inari.commons.geom.PositionF;
import com.inari.commons.lang.aspect.Aspects;
import com.inari.commons.lang.indexed.IIndexedTypeKey;
import com.inari.commons.lang.indexed.IndexedTypeKey;
import com.inari.commons.lang.indexed.IndexedTypeSet;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.FFInitException;
import com.inari.firefly.entity.EntityActivationEvent;
import com.inari.firefly.entity.EntityActivationListener;
import com.inari.firefly.entity.EntityComponent;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.graphics.BaseRenderer;
import com.inari.firefly.graphics.ETransform;
import com.inari.firefly.graphics.SpriteRenderable;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFSystem;
import com.inari.firefly.system.RenderEvent;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;

public final class SpriteViewSystem 
    implements 
        FFSystem, 
        EntityActivationListener {
    
    public static final FFSystemTypeKey<SpriteViewSystem> SYSTEM_KEY = FFSystemTypeKey.create( SpriteViewSystem.class );
    public static final Aspects MATCHING_ASPECTS = EntityComponent.ASPECT_GROUP.createAspects( 
        ETransform.TYPE_KEY, 
        ESprite.TYPE_KEY 
    );
    
    private static final SystemComponentKey<SpriteRenderer> SPRITE_RENDERER_TYPE_KEY = SystemComponentKey.create( SpriteRenderer.class );

    private EntitySystem entitySystem;
    private final DynArray<DynArray<DynArray<IndexedTypeSet>>> spritesPerViewAndLayer;
    private SpriteRenderer spriteRenderer;
    
    
    SpriteViewSystem() {
        spritesPerViewAndLayer = new DynArray<DynArray<DynArray<IndexedTypeSet>>>();
    }
    
    @Override
    public final IndexedTypeKey indexedTypeKey() {
        return SYSTEM_KEY;
    }

    @Override
    public final FFSystemTypeKey<SpriteViewSystem> systemTypeKey() {
        return SYSTEM_KEY;
    }

    @Override
    public final void init( FFContext context ) throws FFInitException {
        entitySystem = context.getSystem( EntitySystem.SYSTEM_KEY );
        spriteRenderer = new SpriteRenderer( context );
        
        context.registerListener( RenderEvent.TYPE_KEY, spriteRenderer );
        context.registerListener( EntityActivationEvent.TYPE_KEY, this );
    }
    
    @Override
    public final void dispose( FFContext context ) {
        context.disposeListener( RenderEvent.TYPE_KEY, spriteRenderer );
        context.disposeListener( EntityActivationEvent.TYPE_KEY, this );
        
        spriteRenderer.dispose();
    }
    
    @Override
    public final boolean match( Aspects aspects ) {
        return aspects.include( MATCHING_ASPECTS );
    }
    
    public final void entityActivated( int entityId, final Aspects aspects ) {
        final IndexedTypeSet components = entitySystem.getComponents( entityId );
        final ETransform transform = components.get( ETransform.TYPE_KEY );
        final DynArray<IndexedTypeSet> renderablesOfView = getSprites( transform.getViewId(), transform.getLayerId(), true );
        renderablesOfView.add( components );
        renderablesOfView.sort( RENDERABLE_COMPARATOR );
    }
    
    public final void entityDeactivated( int entityId, final Aspects aspects ) {
        final IndexedTypeSet components = entitySystem.getComponents( entityId );
        final ETransform transform = components.get( ETransform.TYPE_KEY );
        final DynArray<IndexedTypeSet> renderablesOfView = getSprites( transform.getViewId(), transform.getLayerId(), false );
        renderablesOfView.remove( components );
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

    private final Comparator<IndexedTypeSet> RENDERABLE_COMPARATOR = new Comparator<IndexedTypeSet>() {
        
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
            SpriteRenderable sr1 = its1.get( ESprite.TYPE_KEY );
            SpriteRenderable sr2 = its2.get( ESprite.TYPE_KEY );
            int o1 = sr1.getOrdering();
            int o2 = sr2.getOrdering();
            if ( o1 == o2 ) {
                return 0;
            }
            
            return ( o1 < o2 )? 1 : -1;
        }
    };
    
    final class SpriteRenderer extends BaseRenderer { 
        
        protected final TransformDataCollector transformCollector = new ExactTransformDataCollector();

        protected SpriteRenderer( FFContext context ) {
            super( 0 );
            
            injectContext( context );
            init();
        }

        @Override
        public final void render( RenderEvent event ) {
            final DynArray<IndexedTypeSet> spritesToRender = getSprites( event.getViewId(), event.getLayerId(), false );
            if ( spritesToRender == null ) {
                return;
            }
            
            for ( int i = 0; i < spritesToRender.capacity(); i++ ) {
                final IndexedTypeSet components = spritesToRender.get( i );
                if ( components == null ) {
                    continue;
                }

                final ESprite sprite = components.get( ESprite.TYPE_KEY );
                final ETransform transform = components.get( ETransform.TYPE_KEY );
                
                if ( components.contains( ESpriteMultiplier.TYPE_KEY.index() ) ) {
                    final ESpriteMultiplier multiplier = components.get( ESpriteMultiplier.TYPE_KEY );
                    final DynArray<PositionF> positions = multiplier.getPositions();
                    
                    for ( int p = 0; p < positions.capacity(); p++ ) {
                        PositionF pos = positions.get( p );
                        if ( pos == null ) {
                            continue;
                        }
                        
                        transformCollector.set( transform, pos.x, pos.y );
                        render( sprite, transform.getParentId(), transformCollector );
                    }
                } else {
                    transformCollector.set( transform );
                    render( sprite, transform.getParentId(), transformCollector );
                }
            }
        }

        @Override
        public final IIndexedTypeKey indexedTypeKey() {
            return SPRITE_RENDERER_TYPE_KEY;
        }
    }

}
