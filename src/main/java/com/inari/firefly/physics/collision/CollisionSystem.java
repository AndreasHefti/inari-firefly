package com.inari.firefly.physics.collision;

import java.util.Iterator;

import com.inari.commons.GeomUtils;
import com.inari.commons.geom.BitMask;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.IntIterator;
import com.inari.commons.lang.aspect.AspectGroup;
import com.inari.commons.lang.aspect.Aspects;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.FFInitException;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntityActivationEvent;
import com.inari.firefly.entity.EntityActivationListener;
import com.inari.firefly.graphics.tile.ETile;
import com.inari.firefly.graphics.tile.TileGrid;
import com.inari.firefly.graphics.tile.TileGrid.TileIterator;
import com.inari.firefly.graphics.tile.TileGridSystem;
import com.inari.firefly.graphics.view.ViewEvent;
import com.inari.firefly.graphics.view.ViewEvent.Type;
import com.inari.firefly.graphics.view.ViewEventListener;
import com.inari.firefly.physics.movement.EMovement;
import com.inari.firefly.physics.movement.MoveEvent;
import com.inari.firefly.physics.movement.MoveEventListener;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.component.ComponentSystem;
import com.inari.firefly.system.component.SystemBuilderAdapter;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.component.SystemComponentBuilder;

public final class CollisionSystem 
    extends 
        ComponentSystem<CollisionSystem> 
    implements 
        EntityActivationListener, 
        ViewEventListener,
        MoveEventListener {
    
    public static final FFSystemTypeKey<CollisionSystem> SYSTEM_KEY = FFSystemTypeKey.create( CollisionSystem.class );
    public static final AspectGroup MATERIAL_ASPECT_GROUP = new AspectGroup( "MATERIAL_ASPECT_GROUP" );
    public static final AspectGroup CONTACT_ASPECT_GROUP = new AspectGroup( "CONTACT_ASPECT_GROUP" );

    private static final SystemComponentKey<?>[] SUPPORTED_COMPONENT_TYPES = new SystemComponentKey[] {
        CollisionQuadTree.TYPE_KEY,
        CollisionResolver.TYPE_KEY
    };
    
    private final DynArray<CollisionQuadTree> quadTrees;
    private final DynArray<DynArray<CollisionQuadTree>> quadTreesPerViewAndLayer;
    private final DynArray<CollisionResolver> collisionResolvers;
    
    private TileGridSystem tileGridSystem;
    private final Rectangle checkPivot = new Rectangle( 0, 0, 0, 0 );
    
    private final ContactEvent contactEvent = new ContactEvent();

    CollisionSystem() {
        super( SYSTEM_KEY );
        quadTrees = new DynArray<CollisionQuadTree>();
        quadTreesPerViewAndLayer = new DynArray<DynArray<CollisionQuadTree>>();
        collisionResolvers = new DynArray<CollisionResolver>();
    }
    
    @Override
    public final boolean match( Aspects aspects ) {
        return aspects.contains( ECollision.TYPE_KEY ) && !aspects.contains( ETile.TYPE_KEY );
    }
    
    @Override
    public final void init( FFContext context ) {
        super.init( context );
        
        context.registerListener( EntityActivationEvent.TYPE_KEY, this );
        context.registerListener( ViewEvent.TYPE_KEY, this );
        context.registerListener( MoveEvent.TYPE_KEY, this );
        
        tileGridSystem = context.getSystem( TileGridSystem.SYSTEM_KEY );
    }

    @Override
    public final void dispose( FFContext context ) {
        clear();
        context.disposeListener( EntityActivationEvent.TYPE_KEY, this );
        context.disposeListener( ViewEvent.TYPE_KEY, this );
        context.disposeListener( MoveEvent.TYPE_KEY, this );
    }
    
    @Override
    public final void onViewEvent( ViewEvent event ) {
        if ( event.isOfType( Type.VIEW_DELETED ) ) {
            quadTreesPerViewAndLayer.remove( event.getView().index() );
            return;
        }
    }
    
    public final void entityActivated( int entityId, final Aspects aspects ) {
        CollisionQuadTree quadTree = getCollisionQuadTreeForEntity( entityId );
        if ( quadTree != null ) {
            quadTree.add( entityId );
        }
    }

    public final void entityDeactivated( int entityId, final Aspects aspects ) {
        CollisionQuadTree quadTree = getCollisionQuadTreeForEntity( entityId );
        if ( quadTree != null ) {
            quadTree.remove( entityId );
        }
    }
    
    @Override
    public final void onMoveEvent( final MoveEvent event ) {
        final IntIterator movedEntiyIterator = event.movedEntityIds();
        while ( movedEntiyIterator.hasNext() ) {
            final int entityId = movedEntiyIterator.next();
            final ECollision collision = context.getEntityComponent( entityId, ECollision.TYPE_KEY );
            final ContactScan contactScan = collision.getContactScan();
            final int collisionResolverId = collision.getCollisionResolverId();
            
            scanContacts( entityId, collision );
            
            if ( collisionResolverId >= 0 ) {
                collisionResolvers.get( collisionResolverId ).resolve( entityId );
            }
            
            if ( contactScan.hasAnyContact() ) {
                contactEvent.entityId = entityId;
                context.notify( contactEvent );
            }
        }
    }
    
    public final void updateContacts( int entityId ) {
        final ECollision collision = context.getEntityComponent( entityId, ECollision.TYPE_KEY );
        scanContacts( entityId, collision );
    }

    private void scanContacts( int entityId, ECollision collision ) {
        final Aspects aspects = context.getEntityComponentAspects( entityId );
        if ( !aspects.contains( ECollision.TYPE_KEY ) ) {
            return;
        }
        
        final ETransform transform = context.getEntityComponent( entityId, ETransform.TYPE_KEY );
        final EMovement movement = context.getEntityComponent( entityId, EMovement.TYPE_KEY );
        final ContactScan contactScan = collision.getContactScan();
        
        contactScan.clearContacts();
        contactScan.update( 
            transform.getXpos(),
            transform.getYpos(),
            movement.getVelocityX(),
            movement.getVelocityY()
        );
        
        final int viewId = transform.getViewId();
        for ( ContactConstraint constraint : contactScan ) {
            int layerId = constraint.layerId;
            if ( layerId < 0 ) {
                layerId = transform.getLayerId();
            }
            
            scanTileContacts( entityId, viewId, layerId, constraint );
            scanSpriteContacts( entityId, viewId, layerId, constraint );
        }
    }

    
    private void scanSpriteContacts( final int entityId, final int viewId, final int layerId, final ContactConstraint constraint ) {
        final CollisionQuadTree quadTree = getCollisionQuadTree( viewId, layerId );
        if ( quadTree == null ) {
            return;
        }
        
        IntIterator entityIterator = quadTree.get( constraint.worldBounds );
        if ( entityIterator == null || !entityIterator.hasNext() ) {
            return;
        }
        
        while ( entityIterator.hasNext() ) {
            final int entityId2 = entityIterator.next();
            if ( entityId == entityId2 ) {
                continue;
            }
            
            final ETransform transform = context.getEntityComponent( entityId2, ETransform.TYPE_KEY );
            scanContact( constraint, entityId2, transform.getXpos(), transform.getYpos() );
        }
    }

    private final void scanTileContacts( final int entityId, final int viewId, final int layerId, final ContactConstraint constraint ) {
        TileGrid tileGrid = tileGridSystem.getTileGrid( viewId, layerId );
        if ( tileGrid == null ) {
            return;
        }
        
        TileIterator tileIterator = tileGrid.iterator( constraint.worldBounds );
        if ( tileIterator == null || !tileIterator.hasNext() ) {
            return;
        }
        
        while ( tileIterator.hasNext() ) {
            final int entityId2 = tileIterator.next();
            if ( entityId == entityId2 ) {
                continue;
            }
            
            scanContact( constraint, entityId2, tileIterator.getWorldXPos(), tileIterator.getWorldYPos() );
        }
    }
    
    private void scanContact( final ContactConstraint constraint, final int entityId, final float xpos, final float ypos ) {
        if ( entityId < 0 || !context.getEntityComponentAspects( entityId ).contains( ECollision.TYPE_KEY ) ) {
            return;
        }
        
        final ECollision collision = context.getEntityComponent( entityId, ECollision.TYPE_KEY );
        if ( !constraint.match( collision ) ) {
            return;
        }
        
        final Rectangle collisionBounds = collision.getCollisionBounds();
        final Contact contact = Contact.createContact(
            entityId,
            collision.getMaterialType(),
            collision.getContactType(),
            (int) Math.floor( xpos ) + collisionBounds.x,
            (int) Math.floor( ypos ) + collisionBounds.y,
            collisionBounds.width,
            collisionBounds.height
        );
        
        final Rectangle constraintWorldBounds = constraint.worldBounds;
        final Rectangle contactWorldBounds = contact.worldBounds();
        final Rectangle intersectionBounds = contact.intersectionBounds();
        final BitMask intersectionMask = contact.intersectionMask();
        
        GeomUtils.intersection( 
            constraintWorldBounds, 
            contactWorldBounds, 
            intersectionBounds 
        );
        
        if ( intersectionBounds.area() <= 0 ) {
            contact.dispose();
            return;
        }
        
        // normalize the intersection to origin of coordinate system
        intersectionBounds.x = intersectionBounds.x - constraintWorldBounds.x;
        intersectionBounds.y = intersectionBounds.y - constraintWorldBounds.y;
        
        final BitMask bitmask2 = collision.getCollisionMask();
        if ( bitmask2 == null ) {
            constraint.addContact( contact );
            return;
        }
        
        checkPivot.x = constraintWorldBounds.x - contactWorldBounds.x;
        checkPivot.y = constraintWorldBounds.y - contactWorldBounds.y;
        checkPivot.width = constraintWorldBounds.width;
        checkPivot.height = constraintWorldBounds.height;

        if ( bitmask2 != null && BitMask.createIntersectionMask( checkPivot, bitmask2, intersectionMask, true ) ) {
            constraint.addContact( contact );
            return;
        }
        
        contact.dispose();
    }
    
    public final CollisionQuadTree getCollisionQuadTree( int id ) {
        return quadTrees.get( id );
    }
    
    public final CollisionQuadTree getCollisionQuadTree( String name ) {
        for ( CollisionQuadTree quadTree : quadTrees ) {
            if ( name.equals( quadTree.getName() ) ) {
                return quadTree;
            }
        }
        return null;
    }
    
    public final CollisionQuadTree getCollisionQuadTree( int viewId, int layerId ) {
        if ( !quadTreesPerViewAndLayer.contains( viewId ) ) {
            return null;
        }
        
        final DynArray<CollisionQuadTree> ofLayer = quadTreesPerViewAndLayer.get( viewId );
        if ( !ofLayer.contains( layerId ) ) {
            return null;
        }
        
        return ofLayer.get( layerId );
    }
    
    public final CollisionQuadTree getCollisionQuadTreeForEntity( int entityId ) {
        final ETransform transform = context.getEntityComponent( entityId, ETransform.TYPE_KEY );
        int viewId = transform.getViewId();
        int layerId = transform.getLayerId();
        if ( !quadTreesPerViewAndLayer.contains( viewId ) ) {
            return null;
        }
        
        DynArray<CollisionQuadTree> quadTreesPerView = quadTreesPerViewAndLayer.get( viewId );
        if ( !quadTreesPerView.contains( layerId ) ) {
            return null;
        }
        
        CollisionQuadTree quadTree = quadTreesPerView.get( layerId );
        if ( quadTree == null ) {
            return null;
        }
        
        return quadTree;
    }
    
    public final int getCollisionQuadTreeId( String name ) {
        for ( CollisionQuadTree quadTree : quadTrees ) {
            if ( name.equals( quadTree.getName() ) ) {
                return quadTree.index();
            }
        }
        return -1;
    }
    
    public final void deleteCollisionQuadTree( int id ) {
        CollisionQuadTree quadTree = getCollisionQuadTree( id );
        if ( quadTree == null ) {
            return;
        }
        disposeQuadTree( quadTrees.remove( quadTree.getLayerId() ) );
        quadTreesPerViewAndLayer.get( quadTree.getViewId() ).remove( quadTree.getLayerId() );
    }
    
    public final void deleteCollisionQuadTree( String name ) {
        CollisionQuadTree quadTree = getCollisionQuadTree( name );
        if ( quadTree == null ) {
            return;
        }
        disposeQuadTree( quadTrees.remove( quadTree.getLayerId() ) );
        quadTreesPerViewAndLayer.get( quadTree.getViewId() ).remove( quadTree.getLayerId() );
    }
    
    private final void disposeQuadTree( CollisionQuadTree quadTree ) {
        if ( quadTree != null ) {
            quadTree.dispose();
        }
    }
    
    public final CollisionResolver getCollisionResolver( int id ) {
        if ( collisionResolvers.contains( id ) ) {
            return collisionResolvers.get( id );
        }
        
        return null;
    }

    public final CollisionResolver getCollisionResolver( String name ) {
        for ( CollisionResolver cr : collisionResolvers ) {
            if ( name.equals( cr.getName() ) ) {
                return cr;
            }
        }
        
        return null;
    }
    
    public final int getCollisionResolverId( String name ) {
        for ( CollisionResolver cr : collisionResolvers ) {
            if ( name.equals( cr.getName() ) ) {
                return cr.index();
            }
        }
        
        return -1;
    }

    public final void deleteCollisionResolver( String name ) {
        CollisionResolver cr = getCollisionResolver( name );
        if ( cr == null ) {
            return;
        }
        
        deleteCollisionResolver( cr.index() );
    }

    public final void deleteCollisionResolver( int id ) {
        disposeCollisionConstraint( collisionResolvers.remove( id ) );
    }

    private void disposeCollisionConstraint( CollisionResolver cr ) {
        if ( cr != null ) {
            cr.dispose();
        }
    }


    @Override
    public final SystemComponentKey<?>[] supportedComponentTypes() {
        return SUPPORTED_COMPONENT_TYPES;
    }

    @Override
    public final SystemBuilderAdapter<?>[] getSupportedBuilderAdapter() {
        return new SystemBuilderAdapter<?>[] {
            new CollisionQuadTreeBuilderAdapter(),
            new CollisionResolverBuilderAdapter()
        };
    }
    
    public final SystemComponentBuilder getCollisionQuadTreeBuilder() {
        return new CollisionQuadTreeBuilder();
    }

    public final SystemComponentBuilder getCollisionResolverBuilder( Class<? extends CollisionResolver> componentType ) {
        if ( componentType == null ) {
            throw new IllegalArgumentException( "componentType is needed for SystemComponentBuilder for component: " + CollisionResolver.TYPE_KEY.name() );
        }
        return new CollisionResolverBuilder( componentType );
    }

    @Override
    public final void clear() {
        for ( CollisionQuadTree quadTree : quadTrees ) {
            disposeQuadTree( quadTree );
        }
        for ( CollisionResolver cr : collisionResolvers ) {
            disposeCollisionConstraint( cr );
        }
        
        quadTrees.clear();
        quadTreesPerViewAndLayer.clear();
        collisionResolvers.clear();
    }

    private final class CollisionQuadTreeBuilder extends SystemComponentBuilder {
        
        private CollisionQuadTreeBuilder() {
            super( context );
        }
        
        @Override
        public final SystemComponentKey<CollisionQuadTree> systemComponentKey() {
            return CollisionQuadTree.TYPE_KEY;
        }

        public final int doBuild( int componentId, Class<?> componentType, boolean activate ) {
            CollisionQuadTree quadTree = createSystemComponent( componentId, componentType, context );
            
            int viewId = quadTree.getViewId();
            int layerId = quadTree.getLayerId();
            
            if ( viewId < 0 ) {
                throw new FFInitException( "ViewId is mandatory for CollisionQuadTree" );
            }
            
            if ( layerId < 0 ) {
                throw new FFInitException( "LayerId is mandatory for CollisionQuadTree" );
            }
            
            if ( quadTree.getWorldArea() == null ) {
                throw new FFInitException( "WorldArea is mandatory for CollisionQuadTree" );
            }
            
            if ( !quadTreesPerViewAndLayer.contains( viewId ) ) {
                quadTreesPerViewAndLayer.set( viewId, new DynArray<CollisionQuadTree>() );
            }
            
            quadTrees.set( quadTree.index(), quadTree );
            quadTreesPerViewAndLayer
                .get( viewId )
                .set( layerId, quadTree );
            
            return quadTree.index();
        }
    }
    
    private final class CollisionResolverBuilder extends SystemComponentBuilder {
        
        private CollisionResolverBuilder( Class<? extends CollisionResolver> componentType ) {
            super( context, componentType );
        }
        
        @Override
        public final SystemComponentKey<CollisionResolver> systemComponentKey() {
            return CollisionResolver.TYPE_KEY;
        }

        public final int doBuild( int componentId, Class<?> componentType, boolean activate ) {
            CollisionResolver cr = createSystemComponent( componentId, componentType, context );
            collisionResolvers.set( cr.index(), cr );
            return cr.index();
        }
    }

    private final class CollisionQuadTreeBuilderAdapter extends SystemBuilderAdapter<CollisionQuadTree> {
        private CollisionQuadTreeBuilderAdapter() {
            super( CollisionSystem.this, CollisionQuadTree.TYPE_KEY );
        }
        @Override
        public final CollisionQuadTree get( int id ) {
            return getCollisionQuadTree( id );
        }
        @Override
        public final Iterator<CollisionQuadTree> getAll() {
            return quadTrees.iterator();
        }
        @Override
        public final void delete( int id ) {
            deleteCollisionQuadTree( id );
        }
        @Override
        public final int getId( String name ) {
            return getCollisionQuadTreeId( name );
        }
        @Override
        public final void activate( int id ) {
            throw new UnsupportedOperationException( componentTypeKey() + " is not activable" );
        }
        @Override
        public final void deactivate( int id ) {
            throw new UnsupportedOperationException( componentTypeKey() + " is not activable" );
        }
        @Override
        public final SystemComponentBuilder createComponentBuilder( Class<? extends CollisionQuadTree> componentType ) {
            return new CollisionQuadTreeBuilder();
        }

    }

    private final class CollisionResolverBuilderAdapter extends SystemBuilderAdapter<CollisionResolver> {
        private CollisionResolverBuilderAdapter() {
            super( CollisionSystem.this, CollisionResolver.TYPE_KEY );
        }
        @Override
        public final CollisionResolver get( int id ) {
            return getCollisionResolver( id );
        }
        @Override
        public final Iterator<CollisionResolver> getAll() {
            return collisionResolvers.iterator();
        }
        @Override
        public final void delete( int id ) {
            deleteCollisionResolver( id );
        }
        @Override
        public final int getId( String name ) {
            return getCollisionResolverId( name );
        }
        @Override
        public final void activate( int id ) {
            throw new UnsupportedOperationException( componentTypeKey() + " is not activable" );
        }
        @Override
        public final void deactivate( int id ) {
            throw new UnsupportedOperationException( componentTypeKey() + " is not activable" );
        }
        @Override
        public final SystemComponentBuilder createComponentBuilder( Class<? extends CollisionResolver> componentType ) {
            return getCollisionResolverBuilder( componentType );
        }
    }

}
