package com.inari.firefly.entity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.inari.commons.geom.Vector2f;
import com.inari.commons.lang.indexed.Indexer;
import com.inari.firefly.component.AttributeKey;
import com.inari.firefly.component.AttributeMap;

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
    
    private float xpos = 0;
    private float ypos = 0;
    
    private float xscale = 1;
    private float yscale = 1;
    
    private float rotationXPos = 0;
    private float rotationYPos = 0;
    private float rotation = 0;
    
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
    public final void fromAttributeMap( AttributeMap attributes ) {
        xpos = attributes.getValue( XPOSITION, xpos );
        ypos = attributes.getValue( YPOSITION, ypos );
        xscale = attributes.getValue( XSCALE, xscale );
        yscale = attributes.getValue( YSCALE, yscale );
        rotationXPos = attributes.getValue( ROTATION_XPOSITION, rotationXPos );
        rotationYPos = attributes.getValue( ROTATION_YPOSITION, rotationYPos );
        rotation = attributes.getValue( ROTATION, rotation );
    }

    @Override
    public final void toAttributeMap( AttributeMap attributes ) {
        attributes.put( XPOSITION, xpos );
        attributes.put( YPOSITION, ypos );
        attributes.put( XSCALE, xscale );
        attributes.put( YSCALE, yscale );
        attributes.put( ROTATION_XPOSITION, rotationXPos );
        attributes.put( ROTATION_YPOSITION, rotationYPos );
        attributes.put( ROTATION, rotation );
    }

    @Override
    public final String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "CTransform [xpos=" );
        builder.append( xpos );
        builder.append( ", ypos=" );
        builder.append( ypos );
        builder.append( ", xscale=" );
        builder.append( xscale );
        builder.append( ", yscale=" );
        builder.append( yscale );
        builder.append( ", rotationXPos=" );
        builder.append( rotationXPos );
        builder.append( ", rotationYPos=" );
        builder.append( rotationYPos );
        builder.append( ", rotation=" );
        builder.append( rotation );
        builder.append( "]" );
        return builder.toString();
    }

    public static final void addPosition( ETransform source, ETransform dest ) {
        dest.xpos = dest.xpos + source.xpos;
        dest.ypos = dest.ypos + source.ypos;
    }
    
    public static final void copyFrom( ETransform source, ETransform dest ) {
        dest.xpos = source.xpos;
        dest.ypos = source.ypos;
        dest.xscale = source.xscale;
        dest.yscale = source.yscale;
        dest.rotationXPos = source.rotationXPos;
        dest.rotationYPos = source.rotationYPos;
        dest.rotation = source.rotation;
    }

}
