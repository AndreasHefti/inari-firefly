package com.inari.firefly.physics.collision;

import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.commons.geom.BitMask;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.Aspect;
import com.inari.commons.lang.list.DynArray;
import com.inari.commons.lang.list.IntBag;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.entity.EntityComponent;

public final class ECollision extends EntityComponent {
    
    public static final EntityComponentTypeKey<ECollision> TYPE_KEY = EntityComponentTypeKey.create( ECollision.class );
    
    public static final AttributeKey<Rectangle> COLLISION_BOUNDS = AttributeKey.createRectangle( "collisionBounds", ECollision.class );
    public static final AttributeKey<BitMask> COLLISION_MASK = AttributeKey.create( "collisionMask", BitMask.class, ECollision.class );
    public static final AttributeKey<String> COLLISION_RESOLVER_NAME = AttributeKey.createString( "collisionResolverName", ECollision.class );
    public static final AttributeKey<Integer> COLLISION_RESOLVER_ID = AttributeKey.createInt( "collisionResolverId", ECollision.class );
    public static final AttributeKey<Aspect> MATERIAL_TYPE = AttributeKey.createAspect( "materialType", ECollision.class );
    public static final AttributeKey<Aspect> CONTACT_TYPE = AttributeKey.createAspect( "contactType", ECollision.class );
    public static final AttributeKey<IntBag> CONTACT_CONSTRAINT_IDS = AttributeKey.createIntBag( "constraintIds", ECollision.class );
    public static final AttributeKey<DynArray<String>> CONTACT_CONSTRAINT_NAMES = AttributeKey.createDynArray( "constraintNames", ECollision.class, String.class );
    public static final Set<AttributeKey<?>> ATTRIBUTE_KEYS = JavaUtils.<AttributeKey<?>>unmodifiableSet(
        COLLISION_BOUNDS,
        COLLISION_MASK,
        COLLISION_RESOLVER_ID,
        MATERIAL_TYPE,
        CONTACT_TYPE,
        CONTACT_CONSTRAINT_IDS,
        CONTACT_CONSTRAINT_NAMES
    );
    
    final Rectangle collisionBounds;
    BitMask collisionMask;
    int collisionResolverId;
    Aspect materialType;
    Aspect contactType;
    
    final ContactScan contactScan;

    ECollision() {
        super( TYPE_KEY );
        collisionBounds = new Rectangle();
        contactScan = new ContactScan();
        resetAttributes();
    }
    
    @Override
    public final void resetAttributes() {
        collisionMask = null;
        collisionResolverId = -1;
        contactType = null;
        materialType = null;
        contactScan.clear();
    }

    public final Rectangle getCollisionBounds() {
        return collisionBounds;
    }

    public final void setCollisionBounds( Rectangle collisionBounds ) {
        if ( collisionBounds == null ) {
            this.collisionBounds.x = 0;
            this.collisionBounds.y = 0;
            this.collisionBounds.width = 0;
            this.collisionBounds.height = 0;
            return;
        }
        this.collisionBounds.setFrom( collisionBounds );
    }

    public final BitMask getCollisionMask() {
        return collisionMask;
    }

    public final void setCollisionMask( BitMask collisionMask ) {
        this.collisionMask = collisionMask;
    }

    public final int getCollisionResolverId() {
        return collisionResolverId;
    }

    public final void setCollisionResolverId( int collisionResolverId ) {
        this.collisionResolverId = collisionResolverId;
    }

    public final ContactScan getContactScan() {
        return contactScan;
    }

    public final Aspect getMaterialType() {
        return materialType;
    }

    public final void setMaterialType( Aspect materialType ) {
        this.materialType = materialType;
    }

    public final Aspect getContactType() {
        return contactType;
    }

    public final void setContactType( Aspect contactType ) {
        this.contactType = contactType;
    }
    
    public final ECollision addContactConstraint( int contactConstraintId ) {
        contactScan.contacts.set( contactConstraintId, new Contacts( contactConstraintId ) );
        return this;
    }
    
    public final ECollision removeContactConstraint( int contactConstraintId ) {
        Contacts removed = contactScan.contacts.remove( contactConstraintId );
        if ( removed != null ) {
            removed.clear();
        }
        return this;
    }
    
    public final Contacts getContacts( int contactConstraintId ) {
        return contactScan.getContacts( contactConstraintId );
    }
    
    public final ECollision clearContactConstraints() {
        for ( int i = 0; i < contactScan.contacts.capacity(); i++ ) {
            Contacts contacts = contactScan.contacts.get( i );
            if ( contacts == null ) {
                continue;
            }
            
            contacts.clear();
        }
        
        contactScan.contacts.clear();
        return this;
    }

    public final Set<AttributeKey<?>> attributeKeys() {
        return ATTRIBUTE_KEYS;
    }

    public final void fromAttributes( AttributeMap attributes ) {
        setCollisionBounds( attributes.getValue( COLLISION_BOUNDS, collisionBounds ) );
        collisionMask = attributes.getValue( COLLISION_MASK, collisionMask );
        collisionResolverId = attributes.getIdForName( COLLISION_RESOLVER_NAME, COLLISION_RESOLVER_ID, CollisionResolver.TYPE_KEY, collisionResolverId );
        materialType = attributes.getValue( MATERIAL_TYPE, materialType );
        contactType = attributes.getValue( CONTACT_TYPE, contactType );

        clearContactConstraints();
        final IntBag constraintIds = attributes.getIdsForNames( CONTACT_CONSTRAINT_NAMES, CONTACT_CONSTRAINT_IDS, ContactConstraint.TYPE_KEY, null );
        if ( constraintIds != null ) {
            for ( int i = 0; i < constraintIds.length(); i++ ) {
                if ( constraintIds.isEmpty( i ) ) {
                    continue;
                }
                addContactConstraint( constraintIds.get( i ) );
            }
        }
    }

    public final void toAttributes( AttributeMap attributes ) {
        attributes.put( COLLISION_BOUNDS, collisionBounds );
        attributes.put( COLLISION_MASK, collisionMask );
        attributes.put( COLLISION_RESOLVER_ID, collisionResolverId );
        attributes.put( MATERIAL_TYPE, materialType );
        attributes.put( CONTACT_TYPE, contactType );
        
        IntBag constraintIds = new IntBag( contactScan.contacts.size() );
        for ( int i = 0; i < contactScan.contacts.capacity(); i++ ) {
            Contacts contacts = contactScan.contacts.get( i );
            if ( contacts == null ) {
                continue;
            }
            
            constraintIds.add( i );
        }
        attributes.put( CONTACT_CONSTRAINT_IDS, constraintIds );
    }

}
