/*******************************************************************************
 * Copyright (c) 2015 - 2016, Andreas Hefti, inarisoft@yahoo.de 
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
package com.inari.firefly.graphics;

import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.commons.geom.PositionF;
import com.inari.commons.lang.indexed.IIndexedTypeKey;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.entity.EntityComponent;
import com.inari.firefly.graphics.view.Layer;
import com.inari.firefly.graphics.view.View;
import com.inari.firefly.graphics.view.ViewAndLayerAware;
import com.inari.firefly.physics.animation.AttributeAnimationAdapter.AttributeAnimationAdapterKey;
import com.inari.firefly.physics.animation.EntityFloatAnimationAdapter;
import com.inari.firefly.physics.animation.FloatAnimation;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.external.TransformData;

public final class ETransform extends EntityComponent implements TransformData, ViewAndLayerAware {
    
    public static final EntityComponentTypeKey<ETransform> TYPE_KEY = EntityComponentTypeKey.create( ETransform.class );

    public static final AttributeKey<String> VIEW_NAME = AttributeKey.createString( "viewName", ETransform.class );
    public static final AttributeKey<Integer> VIEW_ID = AttributeKey.createInt( "viewId", ETransform.class );
    public static final AttributeKey<String> LAYER_NAME = AttributeKey.createString( "layerName", ETransform.class );
    public static final AttributeKey<Integer> LAYER_ID = AttributeKey.createInt( "layerId", ETransform.class );
    public static final AttributeKey<PositionF> POSITION = AttributeKey.createPositionF( "position", ETransform.class );
    public static final AttributeKey<Float> POSITION_X = AttributeKey.createFloat( "position_x", ETransform.class );
    public static final AttributeKey<Float> POSITION_Y = AttributeKey.createFloat( "position_y", ETransform.class );
    public static final AttributeKey<PositionF> PIVOT_POSITION = AttributeKey.createPositionF( "pivotPosition", ETransform.class );
    public static final AttributeKey<Float> PIVOT_POSITION_X = AttributeKey.createFloat( "pivotPosition_x", ETransform.class );
    public static final AttributeKey<Float> PIVOT_POSITION_Y = AttributeKey.createFloat( "pivotPosition_y", ETransform.class );
    public static final AttributeKey<Float> SCALE_X = AttributeKey.createFloat( "scalex", ETransform.class );
    public static final AttributeKey<Float> SCALE_Y = AttributeKey.createFloat( "scaley", ETransform.class );
    public static final AttributeKey<Float> ROTATION = AttributeKey.createFloat( "rotation", ETransform.class );
    public static final Set<AttributeKey<?>> ATTRIBUTE_KEYS = JavaUtils.<AttributeKey<?>>unmodifiableSet( 
        VIEW_ID,
        LAYER_ID, 
        POSITION_X,
        POSITION_Y,
        PIVOT_POSITION_X,
        PIVOT_POSITION_Y,
        SCALE_X,
        SCALE_Y,
        ROTATION
    );

    private int viewId, layerId;
    private final PositionF position;
    private final PositionF pivotPosition;
    private float scalex, scaley;
    private float rotation;
    
    ETransform() {
        super( TYPE_KEY );
        position = new PositionF();
        pivotPosition = new PositionF();
        resetAttributes();
    }

    public final void resetAttributes() {
        viewId = 0;
        layerId = 0;
        position.x = 0;
        position.y = 0;
        pivotPosition.x = 0;
        pivotPosition.y = 0;
        scalex = 1;
        scaley = 1;
        rotation = 0;
    }
    
    public final int getViewId() {
        return viewId;
    }

    public final void setViewId( int viewId ) {
        this.viewId = viewId;
    }

    public final int getLayerId() {
        return layerId;
    }

    public final void setLayerId( int layerId ) {
        this.layerId = layerId;
    }

    public final float getXpos() {
        return position.x;
    }

    public final void setXpos( float xpos ) {
        position.x = xpos;
    }

    public final float getYpos() {
        return position.y;
    }

    public final void setYpos( float ypos ) {
        position.y = ypos;
    }
    
    public final void setPosition( float xpos, float ypos ) {
        position.x = xpos;
        position.y = ypos;
    }

    public final float getPivotX() {
        return pivotPosition.x;
    }

    public final void setPivotX( float pivotx ) {
        pivotPosition.x = pivotx;
    }

    public final float getPivotY() {
        return pivotPosition.y;
    }

    public final void setPivotY( float pivoty ) {
        pivotPosition.y = pivoty;
    }
    
    public final void setPivot( float x, float y ) {
        pivotPosition.x = x;
        pivotPosition.y = y;
    }

    public final void move( final float dx, final float dy ) {
        position.x += dx;
        position.y += dy;
    }

    public final float getScaleX() {
        return scalex;
    }

    public final void setScaleX( float scalex ) {
        this.scalex = scalex;
    }

    public final float getScaleY() {
        return scaley;
    }

    public final void setScaleY( float scaley ) {
        this.scaley = scaley;
    }
    
    public final void setScale( float x, float y ) {
        scalex = x;
        scaley = y;
    }

    public final float getRotation() {
        return rotation;
    }

    public final void setRotation( float rotation ) {
        this.rotation = rotation;
    }
    
    public final float getXOffset() {
        return position.x;
    }

    public final float getYOffset() {
        return position.y;
    }

    public final boolean hasRotation() {
        return rotation != 0.0;
    }

    public final boolean hasScale() {
        return scalex != 1.0 || scaley != 1.0;
    }

    public final Set<AttributeKey<?>> attributeKeys() {
        return ATTRIBUTE_KEYS;
    }

    public final void fromAttributes( AttributeMap attributes ) {
        viewId = attributes.getIdForName( VIEW_NAME, VIEW_ID, View.TYPE_KEY, viewId );
        layerId = attributes.getIdForName( LAYER_NAME, LAYER_ID, Layer.TYPE_KEY, layerId );
        if ( attributes.contains( POSITION ) ) {
            PositionF pos = attributes.getValue( POSITION );
            position.x = pos.x;
            position.y = pos.y;
        } else {
            position.x = attributes.getValue( POSITION_X, position.x );
            position.y = attributes.getValue( POSITION_Y, position.y );
        }
        if ( attributes.contains( PIVOT_POSITION ) ) {
            PositionF pos = attributes.getValue( PIVOT_POSITION );
            pivotPosition.x = pos.x;
            pivotPosition.y = pos.y;
        } else {
            pivotPosition.x = attributes.getValue( PIVOT_POSITION_X, pivotPosition.x );
            pivotPosition.y = attributes.getValue( PIVOT_POSITION_Y, pivotPosition.y );
        }
        scalex = attributes.getValue( SCALE_X, scalex );
        scaley = attributes.getValue( SCALE_Y, scaley );
        
        rotation = attributes.getValue( ROTATION, rotation );
    }

    public final void toAttributes( AttributeMap attributes ) {
        attributes.put( VIEW_ID, viewId );
        attributes.put( LAYER_ID, layerId );
        attributes.put( POSITION_X, position.x );
        attributes.put( POSITION_Y, position.y );
        attributes.put( PIVOT_POSITION_X, pivotPosition.x );
        attributes.put( PIVOT_POSITION_Y, pivotPosition.y );
        attributes.put( SCALE_X, scalex );
        attributes.put( SCALE_Y, scalex );
        attributes.put( ROTATION, rotation );
    }
    
    
    public interface AnimationAdapter {
        AttributeAnimationAdapterKey<XAxisAnimationAdapter> POSITION_X = XAxisAnimationAdapter.TYPE_KEY;
        AttributeAnimationAdapterKey<YAxisAnimationAdapter> POSITION_Y = YAxisAnimationAdapter.TYPE_KEY;
        AttributeAnimationAdapterKey<XAxisPivotAnimationAdapter> PIVOT_POSITION_X = XAxisPivotAnimationAdapter.TYPE_KEY;
        AttributeAnimationAdapterKey<YAxisPivotAnimationAdapter> PIVOT_POSITION_Y = YAxisPivotAnimationAdapter.TYPE_KEY;
        AttributeAnimationAdapterKey<XScaleAnimationAdapter> SCALE_X = XScaleAnimationAdapter.TYPE_KEY;
        AttributeAnimationAdapterKey<YScaleAnimationAdapter> SCALE_Y = YScaleAnimationAdapter.TYPE_KEY;
        AttributeAnimationAdapterKey<RotationAnimationAdapter> ROTATION = RotationAnimationAdapter.TYPE_KEY;
    }
    
    private static final class XAxisAnimationAdapter implements EntityFloatAnimationAdapter {
        public static final AttributeAnimationAdapterKey<XAxisAnimationAdapter> TYPE_KEY = AttributeAnimationAdapterKey.create( new XAxisAnimationAdapter() );
        public final IIndexedTypeKey indexedTypeKey() { return TYPE_KEY; }
        public final void apply( int entityId, final FloatAnimation animation, final FFContext context ) {
            final ETransform transform = context.getEntityComponent( entityId, ETransform.TYPE_KEY );
            transform.setXpos( animation.getValue( entityId, transform.getXpos() ) );
        }
    }
    
    private static final class YAxisAnimationAdapter implements EntityFloatAnimationAdapter {
        public static final AttributeAnimationAdapterKey<YAxisAnimationAdapter> TYPE_KEY = AttributeAnimationAdapterKey.create( new YAxisAnimationAdapter() );
        public final IIndexedTypeKey indexedTypeKey() { return TYPE_KEY; }
        public final void apply( int entityId, final FloatAnimation animation, final FFContext context ) {
            final ETransform transform = context.getEntityComponent( entityId, ETransform.TYPE_KEY );
            transform.setYpos( animation.getValue( entityId, transform.getYpos() ) );
        }
    }
    
    private static final class XAxisPivotAnimationAdapter implements EntityFloatAnimationAdapter {
        public static final AttributeAnimationAdapterKey<XAxisPivotAnimationAdapter> TYPE_KEY = AttributeAnimationAdapterKey.create( new XAxisPivotAnimationAdapter() );
        public final IIndexedTypeKey indexedTypeKey() { return TYPE_KEY; }
        public final void apply( int entityId, final FloatAnimation animation, final FFContext context ) {
            final ETransform transform = context.getEntityComponent( entityId, ETransform.TYPE_KEY );
            transform.setPivotX( animation.getValue( entityId, transform.getPivotX() ) );
        }
    }
    
    public static final class YAxisPivotAnimationAdapter implements EntityFloatAnimationAdapter {
        public static final AttributeAnimationAdapterKey<YAxisPivotAnimationAdapter> TYPE_KEY = AttributeAnimationAdapterKey.create( new YAxisPivotAnimationAdapter() );
        @Override public final IIndexedTypeKey indexedTypeKey() { return TYPE_KEY; }
        @Override
        public final void apply( int entityId, final FloatAnimation animation, final FFContext context ) {
            final ETransform transform = context.getEntityComponent( entityId, ETransform.TYPE_KEY );
            transform.setPivotY( animation.getValue( entityId, transform.getPivotY() ) );
        }
    }
    
    private static final class XScaleAnimationAdapter implements EntityFloatAnimationAdapter {
        public static final AttributeAnimationAdapterKey<XScaleAnimationAdapter> TYPE_KEY = AttributeAnimationAdapterKey.create( new XScaleAnimationAdapter() );
        public final IIndexedTypeKey indexedTypeKey() { return TYPE_KEY; }
        public final void apply( int entityId, final FloatAnimation animation, final FFContext context ) {
            final ETransform transform = context.getEntityComponent( entityId, ETransform.TYPE_KEY );
            transform.setScaleX( animation.getValue( entityId, transform.getScaleX() ) );
        }
    }
    
    public static final class YScaleAnimationAdapter implements EntityFloatAnimationAdapter {
        public static final AttributeAnimationAdapterKey<YScaleAnimationAdapter> TYPE_KEY = AttributeAnimationAdapterKey.create( new YScaleAnimationAdapter() );
        public final IIndexedTypeKey indexedTypeKey() { return TYPE_KEY; }
        public final void apply( int entityId, final FloatAnimation animation, final FFContext context ) {
            final ETransform transform = context.getEntityComponent( entityId, ETransform.TYPE_KEY );
            transform.setScaleY( animation.getValue( entityId, transform.getScaleY() ) );
        }
    }
    
    private static final class RotationAnimationAdapter implements EntityFloatAnimationAdapter {
        public static final AttributeAnimationAdapterKey<RotationAnimationAdapter> TYPE_KEY = AttributeAnimationAdapterKey.create( new RotationAnimationAdapter() );
        public final IIndexedTypeKey indexedTypeKey() { return TYPE_KEY; }
        public final void apply( int entityId, final FloatAnimation animation, final FFContext context ) {
            final ETransform transform = context.getEntityComponent( entityId, ETransform.TYPE_KEY );
            transform.setRotation( animation.getValue( entityId, transform.getRotation() ) );
        }
    }
}
