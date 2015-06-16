package com.inari.firefly.sprite;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.inari.commons.graphics.RGBColor;
import com.inari.commons.lang.indexed.IndexProvider;
import com.inari.firefly.component.AttributeKey;
import com.inari.firefly.component.AttributeMap;
import com.inari.firefly.entity.EntityComponent;
import com.inari.firefly.system.LayeredComponent;
import com.inari.firefly.system.ViewAwareComponent;

public final class ESprite extends EntityComponent implements SpriteRenderable, LayeredComponent, ViewAwareComponent {
    
    public static final int COMPONENT_TYPE = IndexProvider.getIndexForType( ESprite.class, EntityComponent.class );

    public static final AttributeKey<Integer> SPRITE_ID = new AttributeKey<Integer>( "spriteId", Integer.class, ESprite.class );
    public static final AttributeKey<Integer> VIEW_ID = new AttributeKey<Integer>( "viewId", Integer.class, ESprite.class );
    public static final AttributeKey<Integer> LAYER_ID = new AttributeKey<Integer>( "layerId", Integer.class, ESprite.class );
    public static final AttributeKey<RGBColor> RENDER_COLOR = new AttributeKey<RGBColor>( "renderColor", RGBColor.class, ESprite.class );
    public static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        SPRITE_ID,
        VIEW_ID,
        LAYER_ID, 
        RENDER_COLOR 
    };

    private int spriteId;
    private int viewId;
    private int layerId;
    private final RGBColor renderColor;
    
    ESprite() {
        spriteId = -1;
        viewId = 0;
        layerId = 0;
        renderColor = new RGBColor( 1, 1, 1, 0 );
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
    public final RGBColor getRenderColor() {
        return renderColor;
    }

    public final void setRenderColor( RGBColor renderColor ) {
        this.renderColor.r = renderColor.r;
        this.renderColor.g = renderColor.g;
        this.renderColor.b = renderColor.b;
        this.renderColor.a = renderColor.a;
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
        setRenderColor( attributes.getValue( RENDER_COLOR, this.renderColor ) );
    }

    @Override
    public final void toAttributeMap( AttributeMap attributes ) {
        attributes.put( SPRITE_ID, spriteId );
        attributes.put( VIEW_ID, viewId );
        attributes.put( LAYER_ID, layerId );
        attributes.put( RENDER_COLOR, renderColor );
    }

    @Override
    public final String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "CSprite [spriteId=" );
        builder.append( spriteId );
        builder.append( ", viewId=" );
        builder.append( viewId );
        builder.append( ", layerId=" );
        builder.append( layerId );
        builder.append( ", renderColor=" );
        builder.append( renderColor );
        builder.append( "]" );
        return builder.toString();
    }

}
