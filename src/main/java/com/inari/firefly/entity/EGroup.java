package com.inari.firefly.entity;

import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;

public final class EGroup extends EntityComponent {
    
    public static final EntityComponentTypeKey<EGroup> TYPE_KEY = EntityComponentTypeKey.create( EGroup.class );
    
    public static final AttributeKey<Integer> PARENT_ID = AttributeKey.createInt( "parentId", EGroup.class );
    public static final AttributeKey<Integer> POSITION_Z = AttributeKey.createInt( "positionZ", EGroup.class );
    public static final Set<AttributeKey<?>> ATTRIBUTE_KEYS = JavaUtils.<AttributeKey<?>>unmodifiableSet( 
        PARENT_ID,
        POSITION_Z
    );
    
    private int parentId;
    private int positionZ;

    public EGroup() {
        super( TYPE_KEY );
        resetAttributes();
    }
    
    public final void resetAttributes() {
        parentId = -1;
        positionZ = 0;
    }

    public final Set<AttributeKey<?>> attributeKeys() {
        return ATTRIBUTE_KEYS;
    }

    public final int getParentId() {
        return parentId;
    }

    public final void setParentId( int parentId ) {
        this.parentId = parentId;
    }

    public final int getPositionZ() {
        return positionZ;
    }

    public final void setPositionZ( int positionZ ) {
        this.positionZ = positionZ;
    }

    public final void fromAttributes( AttributeMap attributes ) {
        parentId = attributes.getValue( PARENT_ID, parentId );
        positionZ = attributes.getValue( POSITION_Z, positionZ );
    }

    public final void toAttributes( AttributeMap attributes ) {
        attributes.put( PARENT_ID, parentId );
        attributes.put( POSITION_Z, positionZ );
    }

}
