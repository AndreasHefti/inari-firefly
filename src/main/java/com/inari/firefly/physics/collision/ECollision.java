package com.inari.firefly.physics.collision;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.list.IntBag;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.entity.EntityComponent;

public final class ECollision extends EntityComponent {
    
    public static final EntityComponentTypeKey<ECollision> TYPE_KEY = EntityComponentTypeKey.create( ECollision.class );
    
    public static final AttributeKey<Rectangle> OUTER_BOUNDING = new AttributeKey<Rectangle>( "outerBounding", Rectangle.class, ECollision.class );
    public static final AttributeKey<Rectangle> BOUNDING = new AttributeKey<Rectangle>( "bounding", Rectangle.class, ECollision.class );
    public static final AttributeKey<String> BIT_MASK_NAME = new AttributeKey<String>( "bitmaskName", String.class, ECollision.class );
    public static final AttributeKey<Integer> BIT_MASK_ID = new AttributeKey<Integer>( "bitmaskId", Integer.class, ECollision.class );
    public static final AttributeKey<String> COLLISION_CONSTRAINT_NAME = new AttributeKey<String>( "collisionConstraintName", String.class, ECollision.class );
    public static final AttributeKey<Integer> COLLISION_CONSTRAINT_ID = new AttributeKey<Integer>( "collisionConstraintId", Integer.class, ECollision.class );
    public static final AttributeKey<String> COLLISION_RESOLVER_NAME = new AttributeKey<String>( "collisionResolverName", String.class, ECollision.class );
    public static final AttributeKey<Integer> COLLISION_RESOLVER_ID = new AttributeKey<Integer>( "collisionResolverId", Integer.class, ECollision.class );
    public static final AttributeKey<IntBag> COLLISION_LAYER_IDS = new AttributeKey<IntBag>( "collisionLayersIds", IntBag.class, ECollision.class );
    
    private static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        OUTER_BOUNDING,
        BOUNDING,
        BIT_MASK_ID,
        COLLISION_CONSTRAINT_ID,
        COLLISION_RESOLVER_ID,
        COLLISION_LAYER_IDS
    };
    
    Rectangle outerBounding;
    Rectangle bounding;
    int bitmaskId;
    int collisionConstraintId;
    int collisionResolverId;
    IntBag collisionLayerIds;

    ECollision(  ) {
        super( TYPE_KEY );
        resetAttributes();
    }
    
    @Override
    public final void resetAttributes() {
        outerBounding = null;
        bounding = null;
        bitmaskId = -1;
        collisionConstraintId = -1;
        collisionResolverId = -1;
        collisionLayerIds = null;
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

    public final int getCollisionLayersId() {
        return collisionResolverId;
    }

    public final void setCollisionLayersId( int collisionResolverId ) {
        this.collisionResolverId = collisionResolverId;
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
        this.collisionLayerIds = collisionLayerIds;
    }

    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        return new HashSet<AttributeKey<?>>( Arrays.asList( ATTRIBUTE_KEYS ) );
    }

    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        outerBounding = attributes.getValue( OUTER_BOUNDING, outerBounding );
        collisionConstraintId = attributes.getIdForName( COLLISION_CONSTRAINT_NAME, COLLISION_CONSTRAINT_ID, CollisionConstraint.TYPE_KEY, collisionConstraintId );
        collisionResolverId = attributes.getIdForName( COLLISION_RESOLVER_NAME, COLLISION_RESOLVER_ID, CollisionConstraint.TYPE_KEY, collisionResolverId );
        bitmaskId = attributes.getIdForName( BIT_MASK_NAME, BIT_MASK_ID, BitMask.TYPE_KEY, bitmaskId );
        bounding = attributes.getValue( BOUNDING, bounding );
        collisionLayerIds = attributes.getValue( COLLISION_LAYER_IDS, collisionLayerIds );
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        attributes.put( BIT_MASK_ID, bitmaskId );
        attributes.put( BOUNDING, bounding );
        attributes.put( COLLISION_RESOLVER_ID, collisionResolverId );
        attributes.put( OUTER_BOUNDING, outerBounding );
        attributes.put( COLLISION_CONSTRAINT_ID, collisionConstraintId );
        attributes.put( COLLISION_LAYER_IDS, collisionLayerIds );
    }

}
