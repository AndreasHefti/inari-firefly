package com.inari.firefly.sprite.tile;

import com.inari.commons.geom.Vector2i;
import com.inari.commons.graphics.RGBColor;
import com.inari.firefly.component.AttributeKey;
import com.inari.firefly.component.AttributeMap;
import com.inari.firefly.entity.EntityComponent;
import com.inari.firefly.sprite.ESprite;
import com.inari.firefly.sprite.SpriteRenderable;
import com.inari.firefly.system.ViewAwareComponent;
import com.inari.firefly.system.LayeredComponent;

public abstract class ETile extends EntityComponent implements SpriteRenderable, LayeredComponent, ViewAwareComponent {
    
    public static final AttributeKey<Integer> SPRITE_ID = new AttributeKey<Integer>( "spriteId", Integer.class, ETile.class );
    public static final AttributeKey<Integer> VIEW_ID = new AttributeKey<Integer>( "viewId", Integer.class, ESprite.class );
    public static final AttributeKey<Integer> LAYER_ID = new AttributeKey<Integer>( "layerId", Integer.class, ESprite.class );
    public static final AttributeKey<Integer> X_OFFSET = new AttributeKey<Integer>( "xOffset", Integer.class, ETile.class );
    public static final AttributeKey<Integer> Y_OFFSET = new AttributeKey<Integer>( "yOffset", Integer.class, ETile.class );
    public static final AttributeKey<RGBColor> RENDER_COLOR = new AttributeKey<RGBColor>( "renderColor", RGBColor.class, ESprite.class );
    
    private int spriteId;
    private int viewId;
    private int layerId;

    private final Vector2i offset;
    private final RGBColor renderColor;

    protected ETile() {
        spriteId = -1;
        viewId = 0;
        layerId = 0;
        offset = new Vector2i( 0, 0 );
        renderColor = new RGBColor( 1, 1, 1, 0 );
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

    public final boolean hasOffset() {
        return ( offset.dx != 0 || offset.dy != 0 );
    }

    public final Vector2i getOffset() {
        return offset;
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
    public void fromAttributeMap( AttributeMap attributes ) {
        spriteId = attributes.getValue( SPRITE_ID, spriteId );
        viewId = attributes.getValue( VIEW_ID, viewId );
        layerId = attributes.getValue( LAYER_ID, layerId );
        offset.dx = attributes.getValue( X_OFFSET, offset.dx );
        offset.dy = attributes.getValue( Y_OFFSET, offset.dy );
        setRenderColor( attributes.getValue( RENDER_COLOR, this.renderColor ) );
    }

    @Override
    public void toAttributeMap( AttributeMap attributes ) {
        attributes.put( SPRITE_ID, spriteId );
        attributes.put( VIEW_ID, viewId );
        attributes.put( LAYER_ID, layerId );
        attributes.put( X_OFFSET, offset.dx );
        attributes.put( Y_OFFSET, offset.dy );
        attributes.put( RENDER_COLOR, renderColor );
    }
    
    protected final ETile copy( ETile copy ) {
        copy.spriteId = spriteId;
        copy.viewId = viewId;
        copy.layerId = layerId;
        copy.offset.dx = offset.dx;
        copy.offset.dy = offset.dy;
        copy.renderColor.r = renderColor.r;
        copy.renderColor.g = renderColor.g;
        copy.renderColor.b = renderColor.b;
        copy.renderColor.a = renderColor.a;
        return copy;
    }

}
