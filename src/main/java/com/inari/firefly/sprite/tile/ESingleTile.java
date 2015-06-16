package com.inari.firefly.sprite.tile;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.inari.commons.lang.indexed.Indexer;
import com.inari.firefly.component.AttributeKey;
import com.inari.firefly.component.AttributeMap;
import com.inari.firefly.entity.EntityComponent;

public class ESingleTile extends ETile {
    
    public static final int COMPONENT_TYPE = Indexer.getIndexForType( ESingleTile.class, EntityComponent.class );
    
    public static final AttributeKey<Integer> GRID_X_POS = new AttributeKey<Integer>( "gridXPos", Integer.class, ESingleTile.class );
    public static final AttributeKey<Integer> GRID_Y_POS = new AttributeKey<Integer>( "gridYPos", Integer.class, ESingleTile.class );
    public static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        SPRITE_ID, 
        VIEW_ID,
        LAYER_ID,
        GRID_X_POS,
        GRID_Y_POS,
        X_OFFSET,
        Y_OFFSET,
        RENDER_COLOR
    };

    private int gridXPosition;
    private int gridYPosition;
    
    public ESingleTile() {
        super();
        gridXPosition = 0;
        gridYPosition = 0;
    }
    
    @Override
    public final Class<ESingleTile> getComponentType() {
        return ESingleTile.class;
    }
    
    @Override
    public Set<AttributeKey<?>> attributeKeys() {
        return new HashSet<AttributeKey<?>>( Arrays.asList( ATTRIBUTE_KEYS ) );
    }

    public final int getGridXPosition() {
        return gridXPosition;
    }

    public final int getGridYPosition() {
        return gridYPosition;
    }
    
    @Override
    public final void fromAttributeMap( AttributeMap attributes ) {
        super.fromAttributeMap( attributes );
        gridXPosition = attributes.getValue( GRID_X_POS, gridXPosition );
        gridYPosition = attributes.getValue( GRID_Y_POS, gridYPosition );
    }

    @Override
    public void toAttributeMap( AttributeMap attributes ) {
        super.toAttributeMap( attributes );
        attributes.put( GRID_X_POS, gridXPosition );
        attributes.put( GRID_Y_POS, gridYPosition );
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "CSingleTile [gridXPosition=" );
        builder.append( gridXPosition );
        builder.append( ", gridYPosition=" );
        builder.append( gridYPosition );
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
