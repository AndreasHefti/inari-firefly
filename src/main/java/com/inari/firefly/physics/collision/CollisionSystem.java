package com.inari.firefly.physics.collision;

import java.util.ArrayDeque;
import java.util.Set;

import com.inari.commons.GeomUtils;
import com.inari.commons.JavaUtils;
import com.inari.commons.geom.BitMask;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.IntIterator;
import com.inari.commons.lang.Named;
import com.inari.commons.lang.aspect.Aspect;
import com.inari.commons.lang.aspect.AspectGroup;
import com.inari.commons.lang.aspect.Aspects;
import com.inari.commons.lang.indexed.Indexed;
import com.inari.commons.lang.list.IntBag;
import com.inari.firefly.entity.EntityActivationEvent;
import com.inari.firefly.entity.EntityActivationListener;
import com.inari.firefly.graphics.ETransform;
import com.inari.firefly.graphics.tile.ETile;
import com.inari.firefly.graphics.tile.TileGrid;
import com.inari.firefly.graphics.tile.TileGrid.TileGridIterator;
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
import com.inari.firefly.system.component.SystemComponentMap;
import com.inari.firefly.system.component.SystemComponentViewLayerMap;

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
    private static final Set<SystemComponentKey<?>> SUPPORTED_COMPONENT_TYPES = JavaUtils.<SystemComponentKey<?>>unmodifiableSet( 
        ContactPool.TYPE_KEY,
        CollisionResolver.TYPE_KEY
    );
    
    private static final ArrayDeque<Contact> CONTACT_POOL = new ArrayDeque<Contact>();
    
    private final SystemComponentViewLayerMap<ContactPool> contactPools;
    private final SystemComponentMap<CollisionResolver> collisionResolvers;
    
    private TileGridSystem tileGridSystem;
    private final Rectangle checkPivot = new Rectangle( 0, 0, 0, 0 );
    
    
    
    CollisionSystem() {
        super( SYSTEM_KEY );
        contactPools = new SystemComponentViewLayerMap<>( this, ContactPool.TYPE_KEY, 10, 10 ); 
        collisionResolvers = new SystemComponentMap<>( this, CollisionResolver.TYPE_KEY, 20, 10 );
    }

    @Override
    public final void init( FFContext context ) {
        super.init( context );
        
        context.registerListener( EntityActivationEvent.TYPE_KEY, this );
        context.registerListener( ViewEvent.TYPE_KEY, this );
        context.registerListener( MoveEvent.TYPE_KEY, this );
        
        tileGridSystem = context.getSystem( TileGridSystem.SYSTEM_KEY );
    }
    
    public final Set<SystemComponentKey<?>> supportedComponentTypes() {
        return SUPPORTED_COMPONENT_TYPES;
    }

    public final Set<SystemBuilderAdapter<?>> getSupportedBuilderAdapter() {
        return JavaUtils.<SystemBuilderAdapter<?>>unmodifiableSet( 
            contactPools.getBuilderAdapter(),
            collisionResolvers.getBuilderAdapter()
        );
    }

    public final void onViewEvent( ViewEvent event ) {
        if ( event.isOfType( Type.VIEW_DELETED ) ) {
            contactPools.deleteAll( event.getView().index() );
            return;
        }
    }
    
    public final boolean match( Aspects aspects ) {
        return aspects.contains( ECollision.TYPE_KEY ) && 
               aspects.contains( ETransform.TYPE_KEY ) && 
               !aspects.contains( ETile.TYPE_KEY );
    }
    
    public final void entityActivated( int entityId, final Aspects aspects ) {
        final ContactPool pool = contactPools.get( context.getEntityComponent( entityId, ETransform.TYPE_KEY ) );
        if ( pool != null ) {
            pool.add( entityId );
        }
    }

    public final void entityDeactivated( int entityId, final Aspects aspects ) {
        final ContactPool pool = contactPools.get( context.getEntityComponent( entityId, ETransform.TYPE_KEY ) );
        if ( pool != null ) {
            pool.remove( entityId );
        }
    }
    
    public final void onMoveEvent( final MoveEvent event ) {
        final IntBag movedEntityIds = event.movedEntityIds();
        final int nullValue = movedEntityIds.getNullValue();
        
        for ( int i = 0; i < movedEntityIds.length(); i++ ) {
            final int entityId = movedEntityIds.get( i );
            if ( entityId == nullValue || !context.getEntityComponentAspects( entityId ).contains( ECollision.TYPE_KEY ) ) {
                continue;
            }
            
            final ECollision collision = context.getEntityComponent( entityId, ECollision.TYPE_KEY );
            final ContactScan contactScan = collision.getContactScan();
            if ( contactScan == null ) {
                continue;
            }
            
            final ETransform transform = context.getEntityComponent( entityId, ETransform.TYPE_KEY );
            final int collisionResolverId = collision.getCollisionResolverId();
            
            scanContacts( entityId, collision );
            
            if ( collisionResolverId >= 0 ) {
                collisionResolvers.get( collisionResolverId ).resolve( entityId );
            }
            
            if ( contactScan.hasAnyContact() ) {
                ContactEvent.notify( context, entityId );
            }
            
            // update the contact pool if there is one for the moved entity
            final ContactPool contactPool = contactPools.get( transform );
            if ( contactPool != null ) {
                contactPool.update( entityId );
            }
        }
    }
    
    public final void updateContacts( int entityId ) {
        final ECollision collision = context.getEntityComponent( entityId, ECollision.TYPE_KEY );
        scanContacts( entityId, collision );
    }
    
    public final void updateContacts( Indexed indexed ) {
        updateContacts( indexed.index() );
    }
    
    public final void updateContacts( String entityName ) {
        updateContacts( context.getEntityId( entityName ) );
    }
    
    public final void updateContacts( Named entityName ) {
        updateContacts( context.getEntityId( entityName ) );
    }
    
    public final void updateContacts( int entityId, final String constraintName ) {
        final ECollision collision = context.getEntityComponent( entityId, ECollision.TYPE_KEY );
        ContactConstraint constraint = collision.getContactScan().getContactContstraint( constraintName );
        if ( constraint == null ) {
            return;
        }

        updateContacts( entityId, constraint );
    }
    
    public final void updateContacts( Indexed indexed, final String constraintName ) {
        updateContacts( indexed.index(), constraintName );
    }
    
    public final void updateContacts( String entityName, final String constraintName ) {
        updateContacts( context.getEntityId( entityName ), constraintName );
    }
    
    public final void updateContacts( Named entityName, final String constraintName ) {
        updateContacts( context.getEntityId( entityName ), constraintName );
    }

    public final void updateContacts( int entityId, ContactConstraint constraint ) {
        final ETransform transform = context.getEntityComponent( entityId, ETransform.TYPE_KEY );
        final EMovement movement = context.getEntityComponent( entityId, EMovement.TYPE_KEY );
        final int viewId = transform.getViewId();
        int layerId = constraint.layerId;
        if ( layerId < 0 ) {
            layerId = transform.getLayerId();
        }
        
        constraint.clear();
        update( 
            constraint,
            transform.getXpos(),
            transform.getYpos(),
            movement.getVelocityX(),
            movement.getVelocityY()
        );
        
        scanTileContacts( entityId, viewId, layerId, constraint );
        scanSpriteContacts( entityId, viewId, layerId, constraint );
    }
    
    public final void updateContacts( Indexed indexed, ContactConstraint constraint ) {
        updateContacts( indexed.index(), constraint );
    }
    
    public final void updateContacts( String entityName, ContactConstraint constraint ) {
        updateContacts( context.getEntityId( entityName ), constraint );
    }
    
    public final void updateContacts( Named entityName, ContactConstraint constraint ) {
        updateContacts( context.getEntityId( entityName ), constraint );
    }

    public final void clearSystem() {
        contactPools.clear();
        collisionResolvers.clear();
    }
    
    public final void dispose( FFContext context ) {
        clearSystem();
        context.disposeListener( EntityActivationEvent.TYPE_KEY, this );
        context.disposeListener( ViewEvent.TYPE_KEY, this );
        context.disposeListener( MoveEvent.TYPE_KEY, this );
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
        update( 
            contactScan,
            transform.getXpos(),
            transform.getYpos(),
            movement.getVelocityX(),
            movement.getVelocityY()
        );
        
        final int viewId = transform.getViewId();
        for ( int i = 0; i < contactScan.constraints.capacity(); i++ ) {
            ContactConstraint constraint = contactScan.constraints.get( i );
            if ( constraint == null ) {
                continue;
            }
            
            int layerId = constraint.layerId;
            if ( layerId < 0 ) {
                layerId = transform.getLayerId();
            }
            
            scanTileContacts( entityId, viewId, layerId, constraint );
            scanSpriteContacts( entityId, viewId, layerId, constraint );
        }
    }

    
    private void scanSpriteContacts( final int entityId, final int viewId, final int layerId, final ContactConstraint constraint ) {
        final ContactPool pool = contactPools.get( viewId, layerId );
        if ( pool == null ) {
            return;
        }
        
        final IntIterator entityIterator = pool.get( constraint.worldBounds );
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
        
        TileGridIterator tileGridIterator = tileGrid.getTileGridIterator( constraint.worldBounds );
        while ( tileGridIterator.hasNext() ) {
            final int entityId2 = tileGridIterator.next();
            if ( entityId == entityId2 ) {
                continue;
            }
            
            scanContact( constraint, entityId2, tileGridIterator.getWorldXPos(), tileGridIterator.getWorldYPos() );
        }
    }
    
    private void scanContact( final ContactConstraint constraint, final int entityId, final float xpos, final float ypos ) {
        if ( entityId < 0 || !context.getEntityComponentAspects( entityId ).contains( ECollision.TYPE_KEY ) ) {
            return;
        }
        
        final ECollision collision = context.getEntityComponent( entityId, ECollision.TYPE_KEY );
        if ( !match( constraint, collision ) ) {
            return;
        }
        
        final Rectangle collisionBounds = collision.getCollisionBounds();
        final Contact contact = createContact(
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
            disposeContact( contact );
            return;
        }
        
        // normalize the intersection to origin of coordinate system
        intersectionBounds.x = intersectionBounds.x - constraintWorldBounds.x;
        intersectionBounds.y = intersectionBounds.y - constraintWorldBounds.y;
        
        final BitMask bitmask2 = collision.getCollisionMask();
        if ( bitmask2 == null ) {
            addContact( constraint, contact );
            return;
        }
        
        checkPivot.x = constraintWorldBounds.x - contactWorldBounds.x;
        checkPivot.y = constraintWorldBounds.y - contactWorldBounds.y;
        checkPivot.width = constraintWorldBounds.width;
        checkPivot.height = constraintWorldBounds.height;

        if ( bitmask2 != null && BitMask.createIntersectionMask( checkPivot, bitmask2, intersectionMask, true ) ) {
            addContact( constraint, contact );
            return;
        }
        
        disposeContact( contact );
    }
    
    
    private final void update( final ContactScan contactScan, float x, float y, float vx, float vy ) {
        for ( int i = 0; i < contactScan.constraints.capacity(); i++ ) {
            ContactConstraint constraint = contactScan.constraints.get( i );
            if ( constraint == null ) {
                continue;
            }
            
            update( constraint, x, y, vx, vy );
        }
    }
    
    private final void update( final ContactConstraint constraint, float x, float y, float vx, float vy ) {
        constraint.worldBounds.x = ( ( vx > 0 )? (int) Math.ceil( x ) : (int) Math.floor( x ) ) + constraint.contactScanBounds.x;
        constraint.worldBounds.y = ( ( vy > 0 )? (int) Math.ceil( y ) : (int) Math.floor( y ) ) + constraint.contactScanBounds.y;
        constraint.worldBounds.width = constraint.contactScanBounds.width;
        constraint.worldBounds.height = constraint.contactScanBounds.height;
        constraint.intersectionMask.reset( 0, 0, constraint.contactScanBounds.width, constraint.contactScanBounds.height );
    }
    
    private final boolean match( final ContactConstraint constraint, final ECollision collision ) {
        if ( !constraint.filtering ) {
            return true;
        } else {
            final Aspect materialType = collision.getMaterialType();
            return ( materialType != null && constraint.materialTypeFilter.contains( materialType ) );
        }
    }
    
    private final boolean addContact( final ContactConstraint constraint, final Contact contact ) {
        if ( contact == null ) { 
            return false;
        }

        if ( !GeomUtils.intersect( contact.intersectionBounds(), constraint.normalizedContactScanBounds ) ) {
            return false;
        }

        BitMask intersectionMask = contact.intersectionMask();
        if ( intersectionMask != null && !intersectionMask.isEmpty() ) {
            constraint.intersectionMask.or( intersectionMask );
        } else {
            Rectangle intersectionBounds = contact.intersectionBounds();
            constraint.intersectionMask.setRegion( intersectionBounds, true );
        }
        
        if ( contact.contactType != null ) {
            constraint.contactTypes.set( contact.contactType );
        }
        if ( contact.materialType != null ) {
            constraint.materialTypes.set( contact.materialType );
        }
        
        constraint.contacts.add( contact );
        return true;
    }
    
    
    
    
    
    final static void disposeContact( final Contact contact ) {
        contact.entityId = -1;
        contact.intersectionMask.clearMask();
        contact.worldBounds.clear();
        contact.contactType = null;
        contact.materialType = null;
        contact.intersectionBounds.clear();
        CONTACT_POOL.add( contact );
    }
    
    final static Contact createContact( int entityId ) {
        Contact contact = CONTACT_POOL.getFirst();
        if ( contact == null ) {
            contact = new Contact();
        }
        
        contact.entityId = entityId;
        return contact;
    }

    final static Contact createContact( int entityId, Aspect materialType, Aspect contactType, int x, int y, int width, int height ) {
        Contact contact = ( !CONTACT_POOL.isEmpty() )? 
            CONTACT_POOL.pollFirst() :
                new Contact();
        
        contact.entityId = entityId;
        contact.contactType = contactType;
        contact.materialType = materialType;
        contact.worldBounds.x = x;
        contact.worldBounds.y = y;
        contact.worldBounds.width = width;
        contact.worldBounds.height = height;
        return contact;
    }

}
