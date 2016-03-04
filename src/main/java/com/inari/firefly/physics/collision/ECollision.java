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
    public static final AttributeKey<Integer> BIT_MASK_ID = new AttributeKey<Integer>( "bitmaskId", Integer.class, ECollision.class );
    public static final AttributeKey<Integer> COLLISION_CONSTRAINT_ID = new AttributeKey<Integer>( "collisionConstraintId", Integer.class, ECollision.class );
    public static final AttributeKey<Integer> COLLISION_RESOLVER_ID = new AttributeKey<Integer>( "collisionResolverId", Integer.class, ECollision.class );
    public static final AttributeKey<IntBag> COLLISION_LAYERS_IDS = new AttributeKey<IntBag>( "collisionLayersIds", IntBag.class, ECollision.class );
    
    private static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        OUTER_BOUNDING,
        BOUNDING,
        BIT_MASK_ID,
        COLLISION_CONSTRAINT_ID,
        COLLISION_RESOLVER_ID,
        COLLISION_LAYERS_IDS
    };
    
    Rectangle outerBounding;
    Rectangle bounding;
    int bitmaskId;
    int collisionConstraintId;
    int collisionResolverId;
    IntBag collisionLayersIds;

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
        collisionLayersIds = null;
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

    public final IntBag getCollisionResolverIds() {
        return collisionLayersIds;
    }

    public final void setCollisionResolverIds( IntBag collisionLayersIds ) {
        this.collisionLayersIds = collisionLayersIds;
    }

    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        return new HashSet<AttributeKey<?>>( Arrays.asList( ATTRIBUTE_KEYS ) );
    }

    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        outerBounding = attributes.getValue( OUTER_BOUNDING, outerBounding );
        collisionConstraintId = attributes.getValue( COLLISION_CONSTRAINT_ID, collisionConstraintId );
        collisionResolverId = attributes.getValue( COLLISION_RESOLVER_ID, collisionResolverId );
        bitmaskId = attributes.getValue( BIT_MASK_ID, bitmaskId );
        bounding = attributes.getValue( BOUNDING, bounding );
        collisionLayersIds = attributes.getValue( COLLISION_LAYERS_IDS, collisionLayersIds );
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        attributes.put( BIT_MASK_ID, bitmaskId );
        attributes.put( BOUNDING, bounding );
        attributes.put( COLLISION_RESOLVER_ID, collisionResolverId );
        attributes.put( OUTER_BOUNDING, outerBounding );
        attributes.put( COLLISION_CONSTRAINT_ID, collisionConstraintId );
        attributes.put( COLLISION_LAYERS_IDS, collisionLayersIds );
    }

}
