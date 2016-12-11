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
package com.inari.firefly.entity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.inari.commons.geom.PositionF;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.graphics.view.Layer;
import com.inari.firefly.graphics.view.View;

public final class ETransform extends EntityComponent {
    
    public static final EntityComponentTypeKey<ETransform> TYPE_KEY = EntityComponentTypeKey.create( ETransform.class );
    
    public static final AttributeKey<String> VIEW_NAME = new AttributeKey<String>( "viewName", String.class, ETransform.class );
    public static final AttributeKey<Integer> VIEW_ID = new AttributeKey<Integer>( "viewId", Integer.class, ETransform.class );
    public static final AttributeKey<String> LAYER_NAME = new AttributeKey<String>( "layerName", String.class, ETransform.class );
    public static final AttributeKey<Integer> LAYER_ID = new AttributeKey<Integer>( "layerId", Integer.class, ETransform.class );
    public static final AttributeKey<PositionF> POSITION = new AttributeKey<PositionF>( "position", PositionF.class, ETransform.class );
    public static final AttributeKey<PositionF> PIVOT_POSITION = new AttributeKey<PositionF>( "pivotPosition", PositionF.class, ETransform.class );
    public static final AttributeKey<Float> SCALE_X = new AttributeKey<Float>( "scalex", Float.class, ETransform.class );
    public static final AttributeKey<Float> SCALE_Y = new AttributeKey<Float>( "scaley", Float.class, ETransform.class );
    public static final AttributeKey<Float> ROTATION = new AttributeKey<Float>( "rotation", Float.class, ETransform.class );
    public static final AttributeKey<Integer> PARENT_ID = new AttributeKey<Integer>( "parentId", Integer.class, ETransform.class );
    private static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        VIEW_ID,
        LAYER_ID, 
        POSITION,
        PIVOT_POSITION,
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
        }
        if ( attributes.contains( PIVOT_POSITION ) ) {
            PositionF pos = attributes.getValue( PIVOT_POSITION );
            pivotPosition.x = pos.x;
            pivotPosition.y = pos.y;
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
        attributes.put( POSITION, position );
        attributes.put( PIVOT_POSITION, pivotPosition );
        attributes.put( SCALE_X, scalex );
        attributes.put( SCALE_Y, scalex );
        attributes.put( ROTATION, rotation );
        attributes.put( PARENT_ID, parentId );
    }

}
