package com.inari.firefly.graphics.rendering;

import java.util.Comparator;

import com.inari.commons.lang.aspect.Aspects;
import com.inari.commons.lang.indexed.IndexedTypeSet;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.entity.EGroup;
import com.inari.firefly.entity.EntityComponent;
import com.inari.firefly.graphics.ETransform;
import com.inari.firefly.graphics.sprite.ESprite;
import com.inari.firefly.system.RenderEvent;

public final class SpriteGroupRenderer extends Renderer {
    
    public static final RenderingChain.RendererKey CHAIN_KEY = new RenderingChain.RendererKey( "NestedSpriteRenderer", SpriteGroupRenderer.class );
    public static final Aspects MATCHING_ASPECTS = EntityComponent.ASPECT_GROUP.createAspects( 
        ETransform.TYPE_KEY, 
        ESprite.TYPE_KEY,
        EGroup.TYPE_KEY
    );

    public SpriteGroupRenderer( int index ) {
        super( index );
        setName( CHAIN_KEY.name );
    }
    
    @Override
    public final boolean match( Aspects aspects ) {
        return aspects.include( MATCHING_ASPECTS );
    }

    @Override
    protected final void accepted( int entityId, final Aspects aspects, final DynArray<IndexedTypeSet> renderablesOfView ) {
        renderablesOfView.sort( RENDERABLE_COMPARATOR );
    }

    @Override
    public final void render( RenderEvent event ) {
        final DynArray<IndexedTypeSet> spritesToRender = getEntites( event.getViewId(), event.getLayerId(), false );
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
            final EGroup group = components.get( EGroup.TYPE_KEY );
            
            transformCollector.set( transform );
            collectTransformData( group.getParentId(), transformCollector );
            graphics.renderSprite( sprite, transformCollector );
        }
    }
    
    private void collectTransformData( final int parentId, final TransformDataCollector transformCollector ) {
        if ( parentId < 0 ) {
            return;
        }
        
        final ETransform parentTransform = entitySystem.getComponent( parentId, ETransform.TYPE_KEY );
        final EGroup group = entitySystem.getComponent( parentId, EGroup.TYPE_KEY );
        if ( parentTransform != null ) {
            transformCollector.add( parentTransform );
            if ( group != null ) {
                collectTransformData( group.getParentId(), transformCollector );
            }
        }
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
            EGroup gr1 = its1.get( EGroup.TYPE_KEY );
            EGroup gr2 = its2.get( EGroup.TYPE_KEY );
            int o1 = gr1.getPositionZ();
            int o2 = gr2.getPositionZ();
            if ( o1 == o2 ) {
                return 0;
            }
            
            return ( o1 < o2 )? 1 : -1;
        }
    };

}
