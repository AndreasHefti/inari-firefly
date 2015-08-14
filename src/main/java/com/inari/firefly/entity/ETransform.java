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
package com.inari.firefly.entity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.inari.commons.geom.Vector2f;
import com.inari.commons.lang.indexed.Indexer;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;

public final class ETransform extends EntityComponent {
    
    public static final int COMPONENT_TYPE = Indexer.getIndexForType( ETransform.class, EntityComponent.class );
    
    public static final AttributeKey<Float> XPOSITION = new AttributeKey<Float>( "xpos", Float.class, ETransform.class );
    public static final AttributeKey<Float> YPOSITION = new AttributeKey<Float>( "ypos", Float.class, ETransform.class );
    public static final AttributeKey<Float> XSCALE = new AttributeKey<Float>( "xscale", Float.class, ETransform.class );
    public static final AttributeKey<Float> YSCALE = new AttributeKey<Float>( "yscale", Float.class, ETransform.class );
    public static final AttributeKey<Float> ROTATION_XPOSITION = new AttributeKey<Float>( "rotationXPos", Float.class, ETransform.class );
    public static final AttributeKey<Float> ROTATION_YPOSITION = new AttributeKey<Float>( "rotationYPos", Float.class, ETransform.class );
    public static final AttributeKey<Float> ROTATION = new AttributeKey<Float>( "rotation", Float.class, ETransform.class );
    public static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        XPOSITION, 
        YPOSITION,
        XSCALE,
        YSCALE,
        ROTATION_XPOSITION,
        ROTATION_YPOSITION,
        ROTATION
    };
    
    private float xpos, ypos;
    private float xscale, yscale;
    private float rotationXPos, rotationYPos;
    private float rotation;
    
    public ETransform() {
        super();
        xpos = 0;
        ypos = 0;
        xscale = 1;
        yscale = 1;
        rotationXPos = 0;
        rotationYPos = 0;
        rotation = 0;
    }
    
    @Override
    public final Class<ETransform> getComponentType() {
        return ETransform.class;
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
    
    public final void move( Vector2f moveVector ) {
        xpos += moveVector.dx;
        ypos += moveVector.dy;
    }
    
    public final boolean hasScale() {
        return ( xscale != 1 || yscale != 1 );
    }

    public final float getXscale() {
        return xscale;
    }

    public final void setXscale( float xscale ) {
        this.xscale = xscale;
    }

    public final float getYscale() {
        return yscale;
    }

    public final void setYscale( float yscale ) {
        this.yscale = yscale;
    }
    
    public final boolean hasRotation() {
        return rotation != 0;
    }

    public final float getRotationXPos() {
        return rotationXPos;
    }

    public final void setRotationXPos( float rotationXPos ) {
        this.rotationXPos = rotationXPos;
    }

    public final float getRotationYPos() {
        return rotationYPos;
    }

    public final void setRotationYPos( float rotationYPos ) {
        this.rotationYPos = rotationYPos;
    }

    public final float getRotation() {
        return rotation;
    }

    public final void setRotation( float rotation ) {
        this.rotation = rotation;
    }

    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        return new HashSet<AttributeKey<?>>( Arrays.asList( ATTRIBUTE_KEYS ) );
    }

    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        xpos = attributes.getValue( XPOSITION, xpos );
        ypos = attributes.getValue( YPOSITION, ypos );
        xscale = attributes.getValue( XSCALE, xscale );
        yscale = attributes.getValue( YSCALE, yscale );
        rotationXPos = attributes.getValue( ROTATION_XPOSITION, rotationXPos );
        rotationYPos = attributes.getValue( ROTATION_YPOSITION, rotationYPos );
        rotation = attributes.getValue( ROTATION, rotation );
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        attributes.put( XPOSITION, xpos );
        attributes.put( YPOSITION, ypos );
        attributes.put( XSCALE, xscale );
        attributes.put( YSCALE, yscale );
        attributes.put( ROTATION_XPOSITION, rotationXPos );
        attributes.put( ROTATION_YPOSITION, rotationYPos );
        attributes.put( ROTATION, rotation );
    }
}
