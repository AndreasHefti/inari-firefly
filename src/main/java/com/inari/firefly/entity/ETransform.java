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

import com.inari.commons.geom.Vector2f;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;

public final class ETransform extends EntityComponent {
    
    public static final EntityComponentTypeKey<ETransform> TYPE_KEY = EntityComponentTypeKey.create( ETransform.class );
    
    public static final AttributeKey<Integer> VIEW_ID = new AttributeKey<Integer>( "viewId", Integer.class, ETransform.class );
    public static final AttributeKey<Integer> LAYER_ID = new AttributeKey<Integer>( "layerId", Integer.class, ETransform.class );
    public static final AttributeKey<Float> XPOSITION = new AttributeKey<Float>( "xpos", Float.class, ETransform.class );
    public static final AttributeKey<Float> YPOSITION = new AttributeKey<Float>( "ypos", Float.class, ETransform.class );
    public static final AttributeKey<Float> PIVOT_X = new AttributeKey<Float>( "pivotx", Float.class, ETransform.class );
    public static final AttributeKey<Float> PIVOT_Y = new AttributeKey<Float>( "pivoty", Float.class, ETransform.class );
    public static final AttributeKey<Float> SCALE_X = new AttributeKey<Float>( "scalex", Float.class, ETransform.class );
    public static final AttributeKey<Float> SCALE_Y = new AttributeKey<Float>( "scaley", Float.class, ETransform.class );
    public static final AttributeKey<Float> ROTATION = new AttributeKey<Float>( "rotation", Float.class, ETransform.class );
    public static final AttributeKey<Integer> PARENT_ID = new AttributeKey<Integer>( "parentId", Integer.class, ETransform.class );
    private static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        VIEW_ID,
        LAYER_ID, 
        XPOSITION, 
        YPOSITION,
        PIVOT_X,
        PIVOT_Y,
        SCALE_X,
        SCALE_Y,
        ROTATION,
        PARENT_ID
    };
    
    private int viewId, layerId;
    private float xpos, ypos;
    private float pivotx, pivoty;
    private float scalex, scaley;
    private float rotation;
    private int parentId;
    
    public ETransform() {
        super( TYPE_KEY );
        resetAttributes();
    }

    @Override
    public final void resetAttributes() {
        viewId = 0;
        layerId = 0;
        xpos = 0;
        ypos = 0;
        pivotx = 0;
        pivoty = 0;
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
        return xpos;
    }

    public final void setXpos( float xpos ) {
        this.xpos = xpos;
    }

    public final float getYpos() {
        return ypos;
    }

    public final void setYpos( float ypos ) {
        this.ypos = ypos;
    }

    public final float getPivotx() {
        return pivotx;
    }

    public final void setPivotx( float pivotx ) {
        this.pivotx = pivotx;
    }

    public final float getPivoty() {
        return pivoty;
    }

    public final void setPivoty( float pivoty ) {
        this.pivoty = pivoty;
    }

    public final void move( Vector2f moveVector, boolean staticPivot ) {
        xpos += moveVector.dx;
        ypos += moveVector.dy;
        if ( !staticPivot ) {
            pivotx += moveVector.dx;
            pivoty += moveVector.dy;
        }
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
        viewId = attributes.getValue( VIEW_ID, viewId );
        layerId = attributes.getValue( LAYER_ID, layerId );
        xpos = attributes.getValue( XPOSITION, xpos );
        ypos = attributes.getValue( YPOSITION, ypos );
        pivotx = attributes.getValue( PIVOT_X, pivotx );
        pivoty = attributes.getValue( PIVOT_Y, pivoty );
        scalex = attributes.getValue( SCALE_X, scalex );
        scaley = attributes.getValue( SCALE_Y, scaley );
        
        rotation = attributes.getValue( ROTATION, rotation );
        parentId = attributes.getValue( PARENT_ID, parentId );
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        attributes.put( VIEW_ID, viewId );
        attributes.put( LAYER_ID, layerId );
        attributes.put( XPOSITION, xpos );
        attributes.put( YPOSITION, ypos );
        attributes.put( PIVOT_X, pivotx );
        attributes.put( PIVOT_Y, pivoty );
        attributes.put( SCALE_X, scalex );
        attributes.put( SCALE_Y, scalex );
        attributes.put( ROTATION, rotation );
        attributes.put( PARENT_ID, parentId );
    }
    
}
