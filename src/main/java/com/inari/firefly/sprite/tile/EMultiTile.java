package com.inari.firefly.sprite.tile;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.inari.commons.lang.indexed.Indexer;
import com.inari.firefly.component.AttributeKey;
import com.inari.firefly.component.AttributeMap;
import com.inari.firefly.entity.EntityComponent;

public class EMultiTile extends ETile {
    
    public static final int COMPONENT_TYPE = Indexer.getIndexForType( EMultiTile.class, EntityComponent.class );

    public static final AttributeKey<int[][]> GRID_POSITIONS = new AttributeKey<int[][]>( "gridXPos", int[][].class, ESingleTile.class );
    public static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        SPRITE_ID, 
        VIEW_ID,
        LAYER_ID,
        GRID_POSITIONS,
        X_OFFSET,
        Y_OFFSET,
        RENDER_COLOR
    };

    private int[][] gridPositions;
    
    public EMultiTile() {
        super();
    }
    
    @Override
    public final Class<ESingleTile> getComponentType() {
        return ESingleTile.class;
    }
    
    @Override
    public Set<AttributeKey<?>> attributeKeys() {
        return new HashSet<AttributeKey<?>>( Arrays.asList( ATTRIBUTE_KEYS ) );
    }

    final int[][] getGridPositions() {
        return gridPositions;
    }

    @Override
    public final void fromAttributeMap( AttributeMap attributes ) {
        super.fromAttributeMap( attributes );
        gridPositions = attributes.getValue( GRID_POSITIONS, gridPositions );
    }

    @Override
    public void toAttributeMap( AttributeMap attributes ) {
        super.toAttributeMap( attributes );
        attributes.put( GRID_POSITIONS, gridPositions );
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "CMultiTile [gridPositions=" );
        builder.append( Arrays.toString( gridPositions ) );
        builder.append( ", getSpriteId()=" );
        builder.append( getSpriteId() );
        builder.append( ", getViewId()=" );
        builder.append( getViewId() );
        builder.append( ", getLayerId()=" );
        builder.append( getLayerId() );
        builder.append( ", getOffset()=" );
        builder.append( getOffset() );
        builder.append( ", getRenderColor()=" );
        builder.append( getRenderColor() );
        builder.append( "]" );
        return builder.toString();
    }

}
