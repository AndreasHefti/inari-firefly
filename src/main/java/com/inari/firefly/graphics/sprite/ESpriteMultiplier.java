package com.inari.firefly.graphics.sprite;

import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.commons.geom.PositionF;
import com.inari.commons.lang.list.DynArray;
import com.inari.commons.lang.list.DynArrayRO;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.entity.EntityComponent;

public final class ESpriteMultiplier extends EntityComponent {
    
    public static final EntityComponentTypeKey<ESpriteMultiplier> TYPE_KEY = EntityComponentTypeKey.create( ESpriteMultiplier.class );
    
    public static final AttributeKey<DynArray<PositionF>> MULTI_POSITIONS = AttributeKey.createDynArray( "positions", ESpriteMultiplier.class, PositionF.class );
    public static final Set<AttributeKey<?>> ATTRIBUTE_KEYS = JavaUtils.<AttributeKey<?>>unmodifiableSet(
        MULTI_POSITIONS
    );
    
    private final DynArray<PositionF> positions;
    
    public ESpriteMultiplier() {
        super( TYPE_KEY );
        positions = DynArray.create( PositionF.class, 20, 10 );
    }

    public final void resetAttributes() {
        positions.clear();
    }

    public final DynArrayRO<PositionF> getPositions() {
        return positions;
    }
    
    public final Set<AttributeKey<?>> attributeKeys() {
        return ATTRIBUTE_KEYS;
    }
    
    public final void fromAttributes( AttributeMap attributes ) {
        positions.clear();
        if ( attributes.contains( MULTI_POSITIONS ) ) {
            positions.addAll( attributes.getValue( MULTI_POSITIONS ) );
        }
    }

    public final void toAttributes( AttributeMap attributes ) {
        attributes.put( MULTI_POSITIONS, positions );
    }

}
