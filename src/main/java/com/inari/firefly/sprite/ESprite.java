package com.inari.firefly.sprite;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.inari.commons.graphics.RGBColor;
import com.inari.commons.lang.indexed.Indexer;
import com.inari.firefly.component.AttributeKey;
import com.inari.firefly.component.AttributeMap;
import com.inari.firefly.entity.EntityComponent;
import com.inari.firefly.movement.EMovement;

public final class ESprite extends EntityComponent implements SpriteRenderable {
    
    public static final int COMPONENT_TYPE = Indexer.getIndexForType( ESprite.class, EntityComponent.class );

    public static final AttributeKey<Integer> SPRITE_ID = new AttributeKey<Integer>( "spriteId", Integer.class, ESprite.class );
    public static final AttributeKey<Integer> VIEW_ID = new AttributeKey<Integer>( "viewId", Integer.class, ESprite.class );
    public static final AttributeKey<Integer> LAYER_ID = new AttributeKey<Integer>( "layerId", Integer.class, ESprite.class );
    public static final AttributeKey<Integer> ORDERING = new AttributeKey<Integer>( "ordering", Integer.class, ESprite.class );
    public static final AttributeKey<RGBColor> TINT_COLOR = new AttributeKey<RGBColor>( "tintColor", RGBColor.class, ESprite.class );
    public static final AttributeKey<Integer> CONTROLLER_ID = new AttributeKey<Integer>( "controllerId", Integer.class, EMovement.class );
    public static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        SPRITE_ID,
        VIEW_ID,
        LAYER_ID, 
        TINT_COLOR,
        CONTROLLER_ID
    };

    private int spriteId;
    private int viewId;
    private int layerId;
    private int ordering;
    private final RGBColor tintColor;
    private int controllerId = -1;
    
    ESprite() {
        spriteId = -1;
        viewId = 0;
        layerId = 0;
        tintColor = new RGBColor( 1, 1, 1, 0 );
    }
    
    @Override
    public final Class<ESprite> getComponentType() {
        return ESprite.class;
    }

    @Override
    public final int getSpriteId() {
        return spriteId;
    }

    public final void setSpriteId( int spriteId ) {
        this.spriteId = spriteId;
    }
    
    @Override
    public final int getViewId() {
        return viewId;
    }

    public final void setViewId( int viewId ) {
        this.viewId = viewId;
    }

    @Override
    public final int getLayerId() {
        return layerId;
    }

    public final void setLayerId( int layerId ) {
        this.layerId = layerId;
    }
    
    @Override
    public final int getOrdering() {
        return ordering;
    }

    public final void setOrdering( int ordering ) {
        this.ordering = ordering;
    }

    @Override
    public final RGBColor getTintColor() {
        return tintColor;
    }

    public final void setTintColor( RGBColor tintColor ) {
        this.tintColor.r = tintColor.r;
        this.tintColor.g = tintColor.g;
        this.tintColor.b = tintColor.b;
        this.tintColor.a = tintColor.a;
    }

    public final int getControllerId() {
        return controllerId;
    }

    public final void setControllerId( int controllerId ) {
        this.controllerId = controllerId;
    }

    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        return new HashSet<AttributeKey<?>>( Arrays.asList( ATTRIBUTE_KEYS ) );
    }

    @Override
    public final void fromAttributeMap( AttributeMap attributes ) {
        spriteId = attributes.getValue( SPRITE_ID, spriteId );
        viewId = attributes.getValue( VIEW_ID, viewId );
        layerId = attributes.getValue( LAYER_ID, layerId );
        ordering = attributes.getValue( ORDERING, ordering );
        setTintColor( attributes.getValue( TINT_COLOR, tintColor ) );
        controllerId = attributes.getValue( CONTROLLER_ID, controllerId );
    }

    @Override
    public final void toAttributeMap( AttributeMap attributes ) {
        attributes.put( SPRITE_ID, spriteId );
        attributes.put( VIEW_ID, viewId );
        attributes.put( LAYER_ID, layerId );
        attributes.put( ORDERING, ordering );
        attributes.put( TINT_COLOR, tintColor );
        attributes.put( CONTROLLER_ID, controllerId );
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "ESprite [spriteId=" );
        builder.append( spriteId );
        builder.append( ", viewId=" );
        builder.append( viewId );
        builder.append( ", layerId=" );
        builder.append( layerId );
        builder.append( ", ordering=" );
        builder.append( ordering );
        builder.append( ", tintColor=" );
        builder.append( tintColor );
        builder.append( ", controllerId=" );
        builder.append( controllerId );
        builder.append( "]" );
        return builder.toString();
    }

}
