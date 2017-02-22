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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.inari.commons.geom.PositionF;
import com.inari.commons.lang.indexed.IIndexedTypeKey;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.entity.EntityComponent;
import com.inari.firefly.graphics.view.Layer;
import com.inari.firefly.graphics.view.View;
import com.inari.firefly.physics.animation.EntityFloatAnimationAdapter;
import com.inari.firefly.physics.animation.FloatAnimation;
import com.inari.firefly.physics.animation.AttributeAnimationAdapter.AttributeAnimationAdapterKey;
import com.inari.firefly.system.FFContext;

public final class ETransform extends EntityComponent {
    
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
    public static final AttributeKey<Integer> PARENT_ID = AttributeKey.createInt( "parentId", ETransform.class );
    private static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        VIEW_ID,
        LAYER_ID, 
        POSITION_X,
        POSITION_Y,
        PIVOT_POSITION_X,
        PIVOT_POSITION_Y,
        SCALE_X,
        SCALE_Y,
        ROTATION,
        PARENT_ID
    };

    private int viewId, layerId;
    private final PositionF position;
    private final PositionF pivotPosition;
    private float scalex, scaley;
    private float rotation;
    private int parentId;
    
    ETransform() {
        super( TYPE_KEY );
        position = new PositionF();
        pivotPosition = new PositionF();
        resetAttributes();
    }

    @Override
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
        parentId = -1;
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

    public final float getPivotx() {
        return pivotPosition.x;
    }

    public final void setPivotx( float pivotx ) {
        pivotPosition.x = pivotx;
    }

    public final float getPivoty() {
        return pivotPosition.y;
    }

    public final void setPivoty( float pivoty ) {
        pivotPosition.y = pivoty;
    }

    public final void move( final float dx, final float dy ) {
        position.x += dx;
        position.y += dy;
    }

    public final float getScalex() {
        return scalex;
    }

    public final void setScalex( float scalex ) {
        this.scalex = scalex;
    }

    public final float getScaley() {
        return scaley;
    }

    public final void setScaley( float scaley ) {
        this.scaley = scaley;
    }

    public final float getRotation() {
        return rotation;
    }

    public final void setRotation( float rotation ) {
        this.rotation = rotation;
    }

    public final int getParentId() {
        return parentId;
    }

    public final void setParentId( int parentId ) {
        this.parentId = parentId;
    }

    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        return new HashSet<AttributeKey<?>>( Arrays.asList( ATTRIBUTE_KEYS ) );
    }

    @Override
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
        parentId = attributes.getValue( PARENT_ID, parentId );
    }

    @Override
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
        attributes.put( PARENT_ID, parentId );
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
        @Override public final IIndexedTypeKey indexedTypeKey() { return TYPE_KEY; }
        @Override
        public final void apply( int entityId, final FloatAnimation animation, final FFContext context ) {
            final ETransform transform = context.getEntityComponent( entityId, ETransform.TYPE_KEY );
            transform.setXpos( animation.getValue( entityId, transform.getXpos() ) );
        }
    }
    
    private static final class YAxisAnimationAdapter implements EntityFloatAnimationAdapter {
        public static final AttributeAnimationAdapterKey<YAxisAnimationAdapter> TYPE_KEY = AttributeAnimationAdapterKey.create( new YAxisAnimationAdapter() );
        @Override public final IIndexedTypeKey indexedTypeKey() { return TYPE_KEY; }
        @Override
        public final void apply( int entityId, final FloatAnimation animation, final FFContext context ) {
            final ETransform transform = context.getEntityComponent( entityId, ETransform.TYPE_KEY );
            transform.setYpos( animation.getValue( entityId, transform.getYpos() ) );
        }
    }
    
    private static final class XAxisPivotAnimationAdapter implements EntityFloatAnimationAdapter {
        public static final AttributeAnimationAdapterKey<XAxisPivotAnimationAdapter> TYPE_KEY = AttributeAnimationAdapterKey.create( new XAxisPivotAnimationAdapter() );
        @Override public final IIndexedTypeKey indexedTypeKey() { return TYPE_KEY; }
        @Override
        public final void apply( int entityId, final FloatAnimation animation, final FFContext context ) {
            final ETransform transform = context.getEntityComponent( entityId, ETransform.TYPE_KEY );
            transform.setPivotx( animation.getValue( entityId, transform.getPivotx() ) );
        }
    }
    
    public static final class YAxisPivotAnimationAdapter implements EntityFloatAnimationAdapter {
        public static final AttributeAnimationAdapterKey<YAxisPivotAnimationAdapter> TYPE_KEY = AttributeAnimationAdapterKey.create( new YAxisPivotAnimationAdapter() );
        @Override public final IIndexedTypeKey indexedTypeKey() { return TYPE_KEY; }
        @Override
        public final void apply( int entityId, final FloatAnimation animation, final FFContext context ) {
            final ETransform transform = context.getEntityComponent( entityId, ETransform.TYPE_KEY );
            transform.setPivoty( animation.getValue( entityId, transform.getPivoty() ) );
        }
    }
    
    private static final class XScaleAnimationAdapter implements EntityFloatAnimationAdapter {
        public static final AttributeAnimationAdapterKey<XScaleAnimationAdapter> TYPE_KEY = AttributeAnimationAdapterKey.create( new XScaleAnimationAdapter() );
        @Override public final IIndexedTypeKey indexedTypeKey() { return TYPE_KEY; }
        @Override
        public final void apply( int entityId, final FloatAnimation animation, final FFContext context ) {
            final ETransform transform = context.getEntityComponent( entityId, ETransform.TYPE_KEY );
            transform.setScalex( animation.getValue( entityId, transform.getScalex() ) );
        }
    }
    
    public static final class YScaleAnimationAdapter implements EntityFloatAnimationAdapter {
        public static final AttributeAnimationAdapterKey<YScaleAnimationAdapter> TYPE_KEY = AttributeAnimationAdapterKey.create( new YScaleAnimationAdapter() );
        @Override public final IIndexedTypeKey indexedTypeKey() { return TYPE_KEY; }
        @Override
        public final void apply( int entityId, final FloatAnimation animation, final FFContext context ) {
            final ETransform transform = context.getEntityComponent( entityId, ETransform.TYPE_KEY );
            transform.setScaley( animation.getValue( entityId, transform.getScaley() ) );
        }
    }
    
    private static final class RotationAnimationAdapter implements EntityFloatAnimationAdapter {
        public static final AttributeAnimationAdapterKey<RotationAnimationAdapter> TYPE_KEY = AttributeAnimationAdapterKey.create( new RotationAnimationAdapter() );
        @Override public final IIndexedTypeKey indexedTypeKey() { return TYPE_KEY; }
        @Override
        public final void apply( int entityId, final FloatAnimation animation, final FFContext context ) {
            final ETransform transform = context.getEntityComponent( entityId, ETransform.TYPE_KEY );
            transform.setRotation( animation.getValue( entityId, transform.getRotation() ) );
        }
    }

}
