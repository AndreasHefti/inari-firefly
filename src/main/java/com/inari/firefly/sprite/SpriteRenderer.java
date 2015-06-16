package com.inari.firefly.sprite;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.lang.IntIterator;
import com.inari.commons.lang.aspect.Aspect;
import com.inari.commons.lang.list.DynArray;
import com.inari.commons.lang.list.IntBag;
import com.inari.firefly.Disposable;
import com.inari.firefly.FFContext;
import com.inari.firefly.entity.EParentEntity;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.IEntitySystem;
import com.inari.firefly.entity.event.AspectedEntityActivationListener;
import com.inari.firefly.entity.event.EntityActivationEvent;
import com.inari.firefly.system.ILowerSystemFacade;
import com.inari.firefly.system.event.RenderEvent;
import com.inari.firefly.system.event.RenderEventListener;
import com.inari.firefly.system.event.ViewEvent;
import com.inari.firefly.system.event.ViewEventListener;

public final class SpriteRenderer implements AspectedEntityActivationListener, RenderEventListener, ViewEventListener, Disposable {

    private final IEntitySystem entityProvider;
    private final ILowerSystemFacade renderer;
    
    private boolean useHirarchicalEntityRendering = false;
    
    private final DynArray<BitSet> entriesPerView;
    
    // TODO find better solution for nestedEntityPostRendering if it is slow or buggy
    private final IntBag needsPostRendering = new IntBag( 20, -1, 10 );
    private final Map<Integer, DynArray<ChildEntityRenderInfo>> nestedEntityPostRendering = new HashMap<Integer, DynArray<ChildEntityRenderInfo>>();

    public SpriteRenderer( FFContext context ) {
        entityProvider = context.get( FFContext.System.ENTITY_SYSTEM );
        renderer = context.get( FFContext.System.LOWER_SYSTEM_FACADE );
        entriesPerView = new DynArray<BitSet>();
        
        IEventDispatcher eventDispatcher = context.get( FFContext.System.EVENT_DISPATCHER );
        eventDispatcher.register( EntityActivationEvent.class, this );
        eventDispatcher.register( RenderEvent.class, this );
        eventDispatcher.register( ViewEvent.class, this );
    }
    
    @Override
    public void dispose( FFContext context ) {
        IEventDispatcher eventDispatcher = context.get( FFContext.System.EVENT_DISPATCHER );
        eventDispatcher.unregister( EntityActivationEvent.class, this );
        eventDispatcher.unregister( RenderEvent.class, this );
        eventDispatcher.unregister( ViewEvent.class, this );
        
        entriesPerView.clear();
        needsPostRendering.clear();
        nestedEntityPostRendering.clear();
    }

    public boolean isUseHirarchicalEntityRendering() {
        return useHirarchicalEntityRendering;
    }

    public void setUseHirarchicalEntityRendering( boolean useHirarchicalEntityRendering ) {
        this.useHirarchicalEntityRendering = useHirarchicalEntityRendering;
    }

    @Override
    public final void onEntityActivationEvent( EntityActivationEvent event ) {
        ESprite cSprite = entityProvider.getComponent( event.entityId, ESprite.COMPONENT_TYPE );
        int viewId = cSprite.getViewId();
        
        switch ( event.type ) {
            case ENTITY_ACTIVATED: {
                entriesPerView.get( viewId ).set( event.entityId );
                break;
            }
            case ENTITY_DEACTIVATED: {
                entriesPerView.get( viewId ).clear( event.entityId );
                nestedEntityPostRendering.remove( event.entityId );
            }
        }
    }

    @Override
    public final boolean match( Aspect aspect ) {
        return aspect.contains( ESprite.COMPONENT_TYPE );
    }

    @Override
    public final void render( RenderEvent event ) {
        int currentLayer = event.getLayerId();
        boolean withLayerCheck = currentLayer >= 0;
        BitSet entitiesOfView = entriesPerView.get( event.getViewId() );
        
        if ( useHirarchicalEntityRendering ) {
            renderWithHirarchicalEntities( currentLayer, entitiesOfView );
            return;
        }
        
        if ( !withLayerCheck ) {
            simpleRenderNoLayerCheck( entitiesOfView );
            return;
        }
        
        simpleRenderLayerCheck( currentLayer, entitiesOfView );
    }

    @Override
    public final void onViewEvent( ViewEvent event ) {
        switch ( event.type ) {
            case VIEW_CREATED: {
                entriesPerView.set( event.view.indexedId(), new BitSet() );
                break;
            }
            case VIEW_DELETED: {
                entriesPerView.remove( event.view.indexedId() );
                break;
            }
            default: {}
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "EntityRendererSystem [entriesPerView=" )
               .append( entriesPerView ).append( "]" );
        return builder.toString();
    }
    
    private final void simpleRenderLayerCheck( int currentLayer, BitSet entitiesOfView ) {
        for ( int entityId = entitiesOfView.nextSetBit( 0 ); entityId >= 0; entityId = entitiesOfView.nextSetBit( entityId + 1 ) ) {
            ESprite sprite = entityProvider.getComponent( entityId, ESprite.COMPONENT_TYPE );
            // if the sprite is not in current layout; continue
            if ( sprite.getLayerId() != currentLayer ) {
                continue;
            }
            
            ETransform transform = entityProvider.getComponent( entityId, ETransform.COMPONENT_TYPE );
            renderer.renderSprite( sprite, transform );
        }
    }

    private final void simpleRenderNoLayerCheck( BitSet entitiesOfView ) {
        for ( int entityId = entitiesOfView.nextSetBit( 0 ); entityId >= 0; entityId = entitiesOfView.nextSetBit( entityId + 1 ) ) {
            ESprite sprite = entityProvider.getComponent( entityId, ESprite.COMPONENT_TYPE );
            ETransform transform = entityProvider.getComponent( entityId, ETransform.COMPONENT_TYPE );
            renderer.renderSprite( sprite, transform );
        }
    }
    
    private void renderWithHirarchicalEntities( int currentLayer, BitSet entitiesOfView ) {
        // first render all none child entities and create post rendering data for child entities
        for ( int entityId = entitiesOfView.nextSetBit( 0 ); entityId >= 0; entityId = entitiesOfView.nextSetBit( entityId + 1 ) ) {
            ESprite sprite = entityProvider.getComponent( entityId, ESprite.COMPONENT_TYPE );
            // if the sprite is not in current layout; continue
            if ( sprite.getLayerId() != currentLayer ) {
                continue;
            }
            
            Aspect entityAspect = entityProvider.getEntityAspect( entityId );
            // if the sprite is a child of another Entity, create ChildEntityRenderInfo for post processing and continue
            if ( entityAspect.contains( EParentEntity.COMPONENT_TYPE ) ) {
                createChildEntityRenderInfo( entityId, sprite );
                continue;
            }
            
            ETransform transform = entityProvider.getComponent( entityId, ETransform.COMPONENT_TYPE );
            renderer.renderSprite( sprite, transform );
        }
        
        // if we have some values in needsPostRendering, we have to do the post rendering
        if ( !needsPostRendering.isEmpty() ) {
            IntIterator iterator = needsPostRendering.iterator();
            while( iterator.hasNext() ) {
                DynArray<ChildEntityRenderInfo> childData = nestedEntityPostRendering.get( iterator.next() );
                for ( ChildEntityRenderInfo info :  childData ) {
                    renderer.renderSprite( info.sprite, info.transform );
                }
            }
            needsPostRendering.clear();
        }
    }
    
    private void createChildEntityRenderInfo( int entityId, ESprite sprite ) {
        Integer entityIdKey = Integer.valueOf( entityId );
        EParentEntity parent = entityProvider.getComponent( entityId, EParentEntity.COMPONENT_TYPE );
        
        DynArray<ChildEntityRenderInfo> childData = nestedEntityPostRendering.get( entityIdKey );
        if ( childData == null ) {
            childData = new DynArray<ChildEntityRenderInfo>( 10 );
            nestedEntityPostRendering.put( entityIdKey, childData );
        }
        
        int ordering = parent.getOrdering();
        ChildEntityRenderInfo childEntityRenderInfo = childData.get( ordering );
        if ( childEntityRenderInfo == null ) {
            childEntityRenderInfo = new ChildEntityRenderInfo( sprite );
            childData.set( ordering, childEntityRenderInfo );
        }
        
        childEntityRenderInfo.setForEntity( entityId );
        needsPostRendering.add( entityId );
    }

    private final class ChildEntityRenderInfo {
        
        final ESprite sprite;
        final ETransform transform = new ETransform();
        
        ChildEntityRenderInfo( ESprite sprite ) {
            this.sprite = sprite;
        }
        
        public void setForEntity( int entityId ) {
            
            EParentEntity parent = entityProvider.getComponent( entityId, EParentEntity.COMPONENT_TYPE );
            ETransform sourceTransform = entityProvider.getComponent( entityId, ETransform.COMPONENT_TYPE );
            
            ETransform.copyFrom( sourceTransform, transform );
            calcNestedTransform( parent );
        }
        
        private void calcNestedTransform( EParentEntity parent ) {
            int parentEntityId = parent.getParentEntityId();
            ETransform parentTransform = entityProvider.getComponent( parentEntityId, ETransform.COMPONENT_TYPE );
            ETransform.addPosition( parentTransform, transform );
            
            Aspect aspect = entityProvider.getEntityAspect( parentEntityId );
            if ( aspect.contains( EParentEntity.COMPONENT_TYPE ) ) {
                calcNestedTransform( 
                    entityProvider.<EParentEntity>getComponent( parentEntityId, EParentEntity.COMPONENT_TYPE ) 
                );
            }
        }
    }

}
