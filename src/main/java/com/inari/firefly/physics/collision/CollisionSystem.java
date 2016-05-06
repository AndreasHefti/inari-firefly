package com.inari.firefly.physics.collision;

import java.util.Iterator;

import com.inari.commons.lang.IntIterator;
import com.inari.commons.lang.aspect.Aspects;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.FFInitException;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntityActivationEvent;
import com.inari.firefly.entity.EntityActivationListener;
import com.inari.firefly.graphics.tile.ETile;
import com.inari.firefly.graphics.view.ViewEvent;
import com.inari.firefly.graphics.view.ViewEvent.Type;
import com.inari.firefly.graphics.view.ViewEventListener;
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

    private static final SystemComponentKey<?>[] SUPPORTED_COMPONENT_TYPES = new SystemComponentKey[] {
        BitMask.TYPE_KEY,
        CollisionQuadTree.TYPE_KEY,
        CollisionConstraint.TYPE_KEY
    };
    
    final DynArray<BitMask> bitmasks;
    final DynArray<CollisionQuadTree> quadTrees;
    final DynArray<DynArray<CollisionQuadTree>> quadTreesPerViewAndLayer;
    final DynArray<CollisionConstraint> collisionConstraints;
    final DynArray<CollisionResolver> collisionResolvers;

    CollisionSystem() {
        super( SYSTEM_KEY );
        bitmasks = new DynArray<BitMask>();
        quadTrees = new DynArray<CollisionQuadTree>();
        quadTreesPerViewAndLayer = new DynArray<DynArray<CollisionQuadTree>>();
        collisionConstraints = new DynArray<CollisionConstraint>();
        collisionResolvers = new DynArray<CollisionResolver>();
    }
    
    @Override
    public final boolean match( Aspects aspects ) {
        return aspects.contains( ECollision.TYPE_KEY ) && !aspects.contains( ETile.TYPE_KEY );
    }
    
    @Override
    public final void init( FFContext context ) {
        super.init( context );
        
        context.registerListener( EntityActivationEvent.class, this );
        context.registerListener( ViewEvent.class, this );
        context.registerListener( MoveEvent.class, this );
    }

    @Override
    public final void dispose( FFContext context ) {
        clear();
        context.disposeListener( EntityActivationEvent.class, this );
        context.disposeListener( ViewEvent.class, this );
        context.disposeListener( MoveEvent.class, this );
    }
    
    @Override
    public final void onViewEvent( ViewEvent event ) {
        int viewId = event.view.index();
        if ( event.eventType == Type.VIEW_DELETED ) {
            quadTreesPerViewAndLayer.remove( viewId );
            return;
        }
    }

    @Override
    public final void onEntityActivationEvent( EntityActivationEvent event ) {
        ETransform transform = context.getEntityComponent( event.entityId, ETransform.TYPE_KEY );
        int viewId = transform.getViewId();
        int layerId = transform.getLayerId();
        if ( !quadTreesPerViewAndLayer.contains( viewId ) ) {
            return;
        }
        
        DynArray<CollisionQuadTree> quadTreesPerView = quadTreesPerViewAndLayer.get( viewId );
        if ( !quadTreesPerView.contains( layerId ) ) {
            return;
        }
        CollisionQuadTree quadTree = quadTreesPerView.get( layerId );
        if ( quadTree == null ) {
            return;
        }
        
        switch ( event.eventType ) {
            case ENTITY_ACTIVATED: {
                quadTree.add( event.entityId );
                break;
            }
            case ENTITY_DEACTIVATED: {
                quadTree.remove( event.entityId );
                break;
            }
        }
    }
    
    @Override
    public final void onMoveEvent( final MoveEvent event ) {
        final IntIterator movedEntiyIterator = event.movedEntityIds();
        while ( movedEntiyIterator.hasNext() ) {
            final int entityId = movedEntiyIterator.next();
            final Aspects aspects = context.getEntityAspects( entityId );
            if ( !aspects.contains( ECollision.TYPE_KEY ) ) {
                continue;
            }
            
            ECollision collision = context.getEntityComponent( entityId, ECollision.TYPE_KEY );
            final int collisionConstraintId = collision.getCollisionConstraintId();
            final int collisionResolverId = collision.getCollisionResolverId();
            if ( collisionConstraintId < 0 ) {
                continue;
            }

            Collisions collisions = collisionConstraints
                .get( collisionConstraintId )
                .checkCollisions( entityId );
            
            if ( collisions.size() > 0 ) {
                if ( collisionResolverId >= 0 ) {
                    collisionResolvers.get( collisionResolverId ).resolve( collisions );
                }
            }
        }
    }

    public final BitMask getBitMask( int bitMaskId ) {
        if ( !bitmasks.contains( bitMaskId ) ) {
            return null;
        }
        return bitmasks.get( bitMaskId );
    }
    
    public final int getBitMaskId( String name ) {
        for ( BitMask bitmask : bitmasks ) {
            if ( bitmask.getName().equals( name ) ) {
                return bitmask.index();
            }
        }
        return -1;
    }
    
    public final BitMask getBitMask( String name ) {
        for ( BitMask bitmask : bitmasks ) {
            if ( bitmask.getName().equals( name ) ) {
                return bitmask;
            }
        }
        return null;
    }
    
    public final void deleteBitMask( int bitMaskId ) {
        disposeBitMask( bitmasks.remove( bitMaskId ) );
    }
    
    public final void deleteBitMask( String name ) {
        int bitmaskId = getBitMaskId( name );
        if ( bitmaskId < 0 ) {
            return;
        }
        disposeBitMask( bitmasks.remove( bitmaskId ) );
    }
    
    private final void disposeBitMask( BitMask bitmask ) {
        if ( bitmask != null ) {
            bitmask.dispose();
        }
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
    
    public final CollisionConstraint getCollisionConstraint( int id ) {
        if ( collisionConstraints.contains( id ) ) {
            return collisionConstraints.get( id );
        }
        
        return null;
    }

    public final CollisionConstraint getCollisionConstraint( String name ) {
        for ( CollisionConstraint cc : collisionConstraints ) {
            if ( name.equals( cc.getName() ) ) {
                return cc;
            }
        }
        
        return null;
    }
    
    public final int getCollisionConstraintId( String name ) {
        for ( CollisionConstraint cc : collisionConstraints ) {
            if ( name.equals( cc.getName() ) ) {
                return cc.index();
            }
        }
        
        return -1;
    }

    public final void deleteCollisionConstraint( String name ) {
        disposeCollisionConstraint( collisionConstraints.remove( getCollisionConstraintId( name ) ) );
    }

    public final void deleteCollisionConstraint( int id ) {
        disposeCollisionConstraint( collisionConstraints.remove( id ) );
    }
    
    private void disposeCollisionConstraint( CollisionConstraint cc ) {
        if ( cc != null ) {
            cc.dispose();
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
            new BitMaskBuilderAdapter( this ),
            new CollisionQuadTreeBuilderAdapter( this ),
            new CollisionConstraintBuilderAdapter( this ),
            new CollisionResolverBuilderAdapter( this )
        };
    }
    
    public final BitMaskBuilder getBitMaskBuilder() {
        return new BitMaskBuilder();
    }
    
    public final CollisionQuadTreeBuilder getCollisionQuadTreeBuilder() {
        return new CollisionQuadTreeBuilder();
    }
    
    public final CollisionConstraintBuilder getCollisionConstraintBuilder() {
        return new CollisionConstraintBuilder();
    }
    
    public final CollisionResolverBuilder getCollisionResolverBuilder() {
        return new CollisionResolverBuilder();
    }

    @Override
    public final void clear() {
        for ( BitMask bitmask : bitmasks) {
            disposeBitMask( bitmask );
        }
        for ( CollisionQuadTree quadTree : quadTrees ) {
            disposeQuadTree( quadTree );
        }
        for ( CollisionConstraint cc : collisionConstraints ) {
            disposeCollisionConstraint( cc );
        }
        for ( CollisionResolver cr : collisionResolvers ) {
            disposeCollisionConstraint( cr );
        }
        
        bitmasks.clear();
        quadTrees.clear();
        quadTreesPerViewAndLayer.clear();
        collisionConstraints.clear();
        collisionResolvers.clear();
    }

    public final class BitMaskBuilder extends SystemComponentBuilder {
        
        protected BitMaskBuilder() {
            super( context );
        }
        
        @Override
        public final SystemComponentKey<BitMask> systemComponentKey() {
            return BitMask.TYPE_KEY;
        }

        public int doBuild( int componentId, Class<?> componentType, boolean activate ) {
            BitMask bitmask = new BitMask( componentId );
            bitmask.fromAttributes( attributes );
            
            bitmasks.set( bitmask.index(), bitmask );
            
            return bitmask.index();
        }
    }
    
    public final class CollisionQuadTreeBuilder extends SystemComponentBuilder {
        
        protected CollisionQuadTreeBuilder() {
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
    
    public final class CollisionConstraintBuilder extends SystemComponentBuilder {
        
        protected CollisionConstraintBuilder() {
            super( context );
        }
        
        @Override
        public final SystemComponentKey<CollisionConstraint> systemComponentKey() {
            return CollisionConstraint.TYPE_KEY;
        }

        public final int doBuild( int componentId, Class<?> componentType, boolean activate ) {
            CollisionConstraint cc = createSystemComponent( componentId, componentType, context );
            collisionConstraints.set( cc.index(), cc );
            return cc.index();
        }
    }
    
    public final class CollisionResolverBuilder extends SystemComponentBuilder {
        
        protected CollisionResolverBuilder() {
            super( context );
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

    private final class BitMaskBuilderAdapter extends SystemBuilderAdapter<BitMask> {
        public BitMaskBuilderAdapter( CollisionSystem system ) {
            super( system, new BitMaskBuilder() );
        }
        @Override
        public final SystemComponentKey<BitMask> componentTypeKey() {
            return BitMask.TYPE_KEY;
        }
        @Override
        public final BitMask getComponent( int id ) {
            return bitmasks.get( id );
        }
        @Override
        public final Iterator<BitMask> getAll() {
            return bitmasks.iterator();
        }
        @Override
        public final void deleteComponent( int id ) {
            deleteBitMask( id );
        }
        @Override
        public final void deleteComponent( String name ) {
            deleteBitMask( name );
            
        }
        @Override
        public final BitMask getComponent( String name ) {
            return getBitMask( name );
        }
    }

    private final class CollisionQuadTreeBuilderAdapter extends SystemBuilderAdapter<CollisionQuadTree> {
        public CollisionQuadTreeBuilderAdapter( CollisionSystem system ) {
            super( system, new CollisionQuadTreeBuilder() );
        }
        @Override
        public final SystemComponentKey<CollisionQuadTree> componentTypeKey() {
            return CollisionQuadTree.TYPE_KEY;
        }
        @Override
        public final CollisionQuadTree getComponent( int id ) {
            return getCollisionQuadTree( id );
        }
        @Override
        public final Iterator<CollisionQuadTree> getAll() {
            return quadTrees.iterator();
        }
        @Override
        public final void deleteComponent( int id ) {
            deleteCollisionQuadTree( id );
        }
        @Override
        public final void deleteComponent( String name ) {
            deleteCollisionQuadTree( name );
            
        }
        @Override
        public final CollisionQuadTree getComponent( String name ) {
            return getCollisionQuadTree( name );
        }
    }
    
    private final class CollisionConstraintBuilderAdapter extends SystemBuilderAdapter<CollisionConstraint> {
        public CollisionConstraintBuilderAdapter( CollisionSystem system ) {
            super( system, new CollisionConstraintBuilder() );
        }
        @Override
        public final SystemComponentKey<CollisionConstraint> componentTypeKey() {
            return CollisionConstraint.TYPE_KEY;
        }
        @Override
        public final CollisionConstraint getComponent( int id ) {
            return getCollisionConstraint( id );
        }
        @Override
        public final Iterator<CollisionConstraint> getAll() {
            return collisionConstraints.iterator();
        }
        @Override
        public final void deleteComponent( int id ) {
            deleteCollisionConstraint( id );
        }
        @Override
        public final void deleteComponent( String name ) {
            deleteCollisionConstraint( name );
            
        }
        @Override
        public final CollisionConstraint getComponent( String name ) {
            return getCollisionConstraint( name );
        }
    }

    private final class CollisionResolverBuilderAdapter extends SystemBuilderAdapter<CollisionResolver> {
        public CollisionResolverBuilderAdapter( CollisionSystem system ) {
            super( system, new CollisionResolverBuilder() );
        }
        @Override
        public final SystemComponentKey<CollisionResolver> componentTypeKey() {
            return CollisionResolver.TYPE_KEY;
        }
        @Override
        public final CollisionResolver getComponent( int id ) {
            return getCollisionResolver( id );
        }
        @Override
        public final Iterator<CollisionResolver> getAll() {
            return collisionResolvers.iterator();
        }
        @Override
        public final void deleteComponent( int id ) {
            deleteCollisionResolver( id );
        }
        @Override
        public final void deleteComponent( String name ) {
            deleteCollisionResolver( name );
            
        }
        @Override
        public final CollisionResolver getComponent( String name ) {
            return getCollisionResolver( name );
        }
    }
}
