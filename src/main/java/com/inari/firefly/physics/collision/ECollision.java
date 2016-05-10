package com.inari.firefly.physics.collision;

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.IntIterator;
import com.inari.commons.lang.list.IntBag;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.entity.EntityComponent;

public final class ECollision extends EntityComponent {
    
    public static final EntityComponentTypeKey<ECollision> TYPE_KEY = EntityComponentTypeKey.create( ECollision.class );
    
    public static final AttributeKey<Rectangle> BOUNDING = new AttributeKey<Rectangle>( "bounding", Rectangle.class, ECollision.class );
    public static final AttributeKey<String> BIT_MASK_NAME = new AttributeKey<String>( "bitmaskName", String.class, ECollision.class );
    public static final AttributeKey<Integer> BIT_MASK_ID = new AttributeKey<Integer>( "bitmaskId", Integer.class, ECollision.class );
    public static final AttributeKey<String> COLLISION_CONSTRAINT_NAME = new AttributeKey<String>( "collisionConstraintName", String.class, ECollision.class );
    public static final AttributeKey<Integer> COLLISION_CONSTRAINT_ID = new AttributeKey<Integer>( "collisionConstraintId", Integer.class, ECollision.class );
    public static final AttributeKey<String> COLLISION_RESOLVER_NAME = new AttributeKey<String>( "collisionResolverName", String.class, ECollision.class );
    public static final AttributeKey<Integer> COLLISION_RESOLVER_ID = new AttributeKey<Integer>( "collisionResolverId", Integer.class, ECollision.class );
    public static final AttributeKey<IntBag> COLLISION_LAYER_IDS = new AttributeKey<IntBag>( "collisionLayersIds", IntBag.class, ECollision.class );
    public static final AttributeKey<Boolean> SOLID = new AttributeKey<Boolean>( "solid", Boolean.class, ECollision.class );
    public static final AttributeKey<Integer> CONTACT_TYPE = new AttributeKey<Integer>( "contactType", Integer.class, ECollision.class );
    public static final AttributeKey<IntBag> CONTACTS = new AttributeKey<IntBag>( "contacts", IntBag.class, ECollision.class );
    private static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        BOUNDING,
        BIT_MASK_ID,
        COLLISION_CONSTRAINT_ID,
        COLLISION_RESOLVER_ID,
        COLLISION_LAYER_IDS,
        SOLID,
        CONTACT_TYPE,
        CONTACTS
    };
    
    Rectangle outerBounding;
    Rectangle bounding;
    int bitmaskId;
    int collisionConstraintId;
    int collisionResolverId;
    final IntBag collisionLayerIds;
    boolean solid;
    ContactType contactType;
    final BitSet contacts;

    ECollision() {
        super( TYPE_KEY );
        collisionLayerIds = new IntBag( 5, -1 );
        contacts = new BitSet( 10 );
        resetAttributes();
    }
    
    @Override
    public final void resetAttributes() {
        outerBounding = null;
        bounding = null;
        bitmaskId = -1;
        collisionConstraintId = -1;
        collisionResolverId = -1;
        collisionLayerIds.clear();
        solid = true;
        contactType = null;
        contacts.clear();
    }

    public final int getBitmaskId() {
        return bitmaskId;
    }

    public final void setBitmaskId( int bitmaskId ) {
        this.bitmaskId = bitmaskId;
    }

    public final Rectangle getBounding() {
        return bounding;
    }

    public final void setBounding( Rectangle bounding ) {
        this.bounding = bounding;
    }
 
    public final Rectangle getOuterBounding() {
        return outerBounding;
    }

    public final void setOuterBounding( Rectangle outerBounding ) {
        this.outerBounding = outerBounding;
    }

    public final int getCollisionConstraintId() {
        return collisionConstraintId;
    }

    public final void setCollisionConstraintId( int collisionConstraintId ) {
        this.collisionConstraintId = collisionConstraintId;
    }

    public final int getCollisionResolverId() {
        return collisionResolverId;
    }

    public final void setCollisionResolverId( int collisionResolverId ) {
        this.collisionResolverId = collisionResolverId;
    }

    public final IntBag getCollisionLayerIds() {
        return collisionLayerIds;
    }

    public final void setCollisionLayerIds( IntBag collisionLayerIds ) {
        this.collisionLayerIds.clear();
        this.collisionLayerIds.addAll( collisionLayerIds );
    }
    
    public final boolean isSolid() {
        return solid;
    }

    public final void setSolid( boolean solid ) {
        this.solid = solid;
    }

    public final ContactType getContactType() {
        return contactType;
    }

    public final void setContactType( ContactType contactType ) {
        this.contactType = contactType;
    }

    public final void setContacts( ContactType... contactTypes ) {
        contacts.clear();
        if ( contactTypes != null ) {
            for ( ContactType contactType : contactTypes ) {
                contacts.set( contactType.aspectId() );
            }
        }
    }

    public final void setContact( ContactType contactType ) {
        contacts.set( contactType.aspectId() );
    }

    public final void resetContact( ContactType contactType ) {
        contacts.set( contactType.aspectId(), false );
    }
    
    public final boolean hasContact( ContactType contactType ) {
        return contacts.get( contactType.aspectId() );
    }
    
    public final void clearContacts() {
        contacts.clear();
    }

    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        return new HashSet<AttributeKey<?>>( Arrays.asList( ATTRIBUTE_KEYS ) );
    }

    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        collisionConstraintId = attributes.getIdForName( COLLISION_CONSTRAINT_NAME, COLLISION_CONSTRAINT_ID, CollisionConstraint.TYPE_KEY, collisionConstraintId );
        collisionResolverId = attributes.getIdForName( COLLISION_RESOLVER_NAME, COLLISION_RESOLVER_ID, CollisionResolver.TYPE_KEY, collisionResolverId );
        bitmaskId = attributes.getIdForName( BIT_MASK_NAME, BIT_MASK_ID, BitMask.TYPE_KEY, bitmaskId );
        bounding = attributes.getValue( BOUNDING, bounding );
        if ( attributes.contains( COLLISION_LAYER_IDS ) ) {
            setCollisionLayerIds( attributes.getValue( COLLISION_LAYER_IDS, collisionLayerIds ) );
        }
        solid = attributes.getValue( SOLID, solid );
        if ( attributes.contains( CONTACT_TYPE ) ) {
            contactType = ContactType.byId( attributes.getValue( CONTACT_TYPE ) );
        }
        
        if ( attributes.contains( CONTACTS ) ) {
            IntIterator contactsIterator = attributes.getValue( CONTACTS ).iterator();
            while ( contactsIterator.hasNext() ) {
                contacts.set( contactsIterator.next() );
            }
        }
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        attributes.put( BIT_MASK_ID, bitmaskId );
        attributes.put( BOUNDING, bounding );
        attributes.put( COLLISION_RESOLVER_ID, collisionResolverId );
        attributes.put( COLLISION_CONSTRAINT_ID, collisionConstraintId );
        attributes.put( COLLISION_LAYER_IDS, collisionLayerIds );
        attributes.put( SOLID, solid );
        attributes.put( CONTACT_TYPE, contactType.aspectId() );
        attributes.put( CONTACTS, new IntBag( contacts, -1, 10 ) );
    }

}
