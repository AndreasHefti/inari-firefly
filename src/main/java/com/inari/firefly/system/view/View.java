/*******************************************************************************
 * Copyright (c) 2015, Andreas Hefti, inarisoft@yahoo.de 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/ 
package com.inari.firefly.system.view;

import java.util.Arrays;
import java.util.Set;

import com.inari.commons.geom.Position;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.graphics.RGBColor;
import com.inari.firefly.component.NamedIndexedComponent;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;

public class View extends NamedIndexedComponent {
    
    public static final AttributeKey<Integer> ORDER = new AttributeKey<Integer>( "order", Integer.class, View.class );
    public static final AttributeKey<Boolean> ACTIVE = new AttributeKey<Boolean>( "active", Boolean.class, View.class );
    public static final AttributeKey<Rectangle> BOUNDS = new AttributeKey<Rectangle>( "bounds", Rectangle.class, View.class );
    public static final AttributeKey<Position> WORLD_POSITION = new AttributeKey<Position>( "worldPosition", Position.class, View.class );
    public static final AttributeKey<RGBColor> CLEAR_COLOR = new AttributeKey<RGBColor>( "clearColor", RGBColor.class, View.class );
    public static final AttributeKey<Boolean> LAYERING_ENABLED = new AttributeKey<Boolean>( "layeringEnabled", Boolean.class, View.class );
    public static final AttributeKey<Float> ZOOM = new AttributeKey<Float>( "zoom", Float.class, View.class );
    public static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] {
        ORDER,
        ACTIVE,
        BOUNDS,
        WORLD_POSITION,
        CLEAR_COLOR,
        LAYERING_ENABLED,
        ZOOM
    };
    
    private boolean isBase = false;
    
    private int order;
    private boolean active = false;
    private boolean layeringEnabled = false;
    private final Rectangle bounds = new Rectangle();
    private final Position worldPosition = new Position( 0, 0 );
    private final RGBColor clearColor = new RGBColor( 0f, 0f, 0f, 1f );
    private float zoom = 1;
    
    View( int viewId ) {
        super( viewId );
    }

    public final int getOrder() {
        return order;
    }

    public final void setOrder( int order ) {
        this.order = order;
    }
    
    public final boolean isActive() {
        return active;
    }

    public final void setActive( boolean active ) {
        this.active = active;
    }

    public final boolean isBase() {
        return isBase;
    }

    final void setBase( boolean isBase ) {
        this.isBase = isBase;
    }

    public final boolean isLayeringEnabled() {
        return layeringEnabled;
    }

    public final void setLayeringEnabled( boolean layeringEnabled ) {
        this.layeringEnabled = layeringEnabled;
    }

    public final void setBounds( Rectangle bounds ) {
        this.bounds.x = bounds.x;
        this.bounds.y = bounds.y;
        this.bounds.width = bounds.width;
        this.bounds.height = bounds.height;
    }

    public final Rectangle getBounds() {
        return bounds;
    }
    
    public final void setWorldPosition( Position worldPosition ) {
        this.worldPosition.x = worldPosition.x;
        this.worldPosition.y = worldPosition.y;
    }

    public final Position getWorldPosition() {
        return worldPosition;
    }
    
    public final void setClearColor( RGBColor clearColor ) {
        this.clearColor.r = clearColor.r;
        this.clearColor.g = clearColor.g;
        this.clearColor.b = clearColor.b;
        this.clearColor.a = clearColor.a;
    }

    public final RGBColor getClearColor() {
        return clearColor;
    }
    
    public float getZoom() {
        return zoom;
    }

    public final void setZoom( float zoom ) {
        this.zoom = zoom;
    }
    
    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        Set<AttributeKey<?>> attributeKeys = super.attributeKeys();
        attributeKeys.addAll( Arrays.asList( ATTRIBUTE_KEYS ) );
        return attributeKeys;
    }

    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        super.fromAttributes( attributes );
        
        order = attributes.getValue( ORDER, order );
        active = attributes.getValue( ACTIVE, active );
        
        Rectangle bounds = attributes.getValue( BOUNDS );
        if ( bounds != null ) {
            setBounds( bounds );
        }
        Position worldPosition = attributes.getValue( WORLD_POSITION );
        if ( worldPosition != null ) {
            setWorldPosition( worldPosition );
        }
        RGBColor clearColor = attributes.getValue( CLEAR_COLOR );
        if ( clearColor != null ) {
            setClearColor( clearColor );
        }
        
        layeringEnabled = attributes.getValue( LAYERING_ENABLED, layeringEnabled );
        zoom = attributes.getValue( ZOOM, zoom );
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        
        attributes.put( ORDER, order );
        attributes.put( ACTIVE, active );
        attributes.put( BOUNDS, new Rectangle( bounds ) );
        attributes.put( WORLD_POSITION, new Position( worldPosition ) );
        attributes.put( CLEAR_COLOR, new RGBColor( clearColor ) );
        attributes.put( LAYERING_ENABLED, layeringEnabled );
        attributes.put( ZOOM, zoom );
    }
}