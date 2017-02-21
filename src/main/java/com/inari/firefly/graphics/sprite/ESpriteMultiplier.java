package com.inari.firefly.graphics.sprite;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.inari.commons.geom.PositionF;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.entity.EntityComponent;

public final class ESpriteMultiplier extends EntityComponent {
    
    public static final EntityComponentTypeKey<ESpriteMultiplier> TYPE_KEY = EntityComponentTypeKey.create( ESpriteMultiplier.class );
    
    public static final AttributeKey<DynArray<PositionF>> MULTI_POSITIONS = AttributeKey.createDynArray( "positions", ESpriteMultiplier.class );
    private static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        MULTI_POSITIONS
    };
    
    private DynArray<PositionF> positions;
    
    public ESpriteMultiplier() {
        super( TYPE_KEY );
        positions = new DynArray<PositionF>( 10, 10 );
    }

    @Override
    public final void resetAttributes() {
        positions.clear();
    }

    public final DynArray<PositionF> getPositions() {
        return positions;
    }
    
    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        return new HashSet<AttributeKey<?>>( Arrays.asList( ATTRIBUTE_KEYS ) );
    }
    
    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        positions.clear();
        if ( attributes.contains( MULTI_POSITIONS ) ) {
            positions.addAll( attributes.getValue( MULTI_POSITIONS ) );
        }
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        attributes.put( MULTI_POSITIONS, positions );
    }

}
