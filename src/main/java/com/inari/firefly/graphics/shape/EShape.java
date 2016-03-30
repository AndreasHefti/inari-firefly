package com.inari.firefly.graphics.shape;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.inari.commons.graphics.RGBColor;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.entity.EntityComponent;
import com.inari.firefly.graphics.BlendMode;

public class EShape extends EntityComponent {
    
    public enum Type {
        POINT,
        LINE,
        POLI_LINE,
        POLIGON,
        RECTANGLE,
        SQUARE,
        CIRCLE,
        CONE,
        ARC,
        CURVE,
        TRIANGLE
    }
    
    public static final EntityComponentTypeKey<EShape> TYPE_KEY = EntityComponentTypeKey.create( EShape.class );

    public static final AttributeKey<Type> SHAPE_TYPE = new AttributeKey<Type>( "shapeType", Type.class, EShape.class );
    public static final AttributeKey<float[]> VERTICES = new AttributeKey<float[]>( "vertices", float[].class, EShape.class );
    public static final AttributeKey<DynArray<RGBColor>> COLORS = AttributeKey.createForDynArray( "colors", EShape.class );
    public static final AttributeKey<Integer> SEGMENTS = new AttributeKey<Integer>( "segments", Integer.class, EShape.class );
    public static final AttributeKey<Boolean> FILL = new AttributeKey<Boolean>( "fill", Boolean.class, EShape.class );
    public static final AttributeKey<BlendMode> BLEND_MODE = new AttributeKey<BlendMode>( "blendMode", BlendMode.class, EShape.class );

    private static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        SHAPE_TYPE,
        VERTICES,
        COLORS,
        SEGMENTS,
        FILL,
        BLEND_MODE
    };
    
    private Type shapeType;
    private float[] vertices;
    private final DynArray<RGBColor> colors = new DynArray<RGBColor>();
    private int segments;
    private boolean fill;
    private BlendMode blendMode;

    public EShape() {
        super( TYPE_KEY );
        resetAttributes();
    }
    
    @Override
    public final void resetAttributes() {
        shapeType = null;
        vertices = null;
        colors.clear();
        segments = 0;
        fill = false;
        blendMode = BlendMode.NONE;
    }

    public final Type getShapeType() {
        return shapeType;
    }

    public final void setShapeType( Type shapeType ) {
        this.shapeType = shapeType;
    }

    public final float[] getVertices() {
        return vertices;
    }

    public final void setVertices( float[] vertices ) {
        this.vertices = vertices;
    }

    public final DynArray<RGBColor> getColors() {
        return colors;
    }

    public final int getSegments() {
        return segments;
    }

    public final void setSegments( int segments ) {
        this.segments = segments;
    }

    public final boolean isFill() {
        return fill;
    }

    public final void setFill( boolean fill ) {
        this.fill = fill;
    }

    public final BlendMode getBlendMode() {
        return blendMode;
    }

    public final void setBlendMode( BlendMode blendMode ) {
        this.blendMode = blendMode;
    }

    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        return new HashSet<AttributeKey<?>>( Arrays.asList( ATTRIBUTE_KEYS ) );
    }

    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        shapeType = attributes.getValue( SHAPE_TYPE, shapeType );
        vertices = attributes.getValue( VERTICES, vertices );
        if ( attributes.contains( COLORS ) ) {
            colors.clear();
            colors.addAll( attributes.getValue( COLORS, colors ) );
        }
        segments = attributes.getValue( SEGMENTS, segments );
        fill = attributes.getValue( FILL, fill );
        blendMode = attributes.getValue( BLEND_MODE, blendMode );
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        attributes.put( SHAPE_TYPE, shapeType );
        attributes.put( VERTICES, vertices );
        attributes.put( COLORS, colors );
        attributes.put( SEGMENTS, segments );
        attributes.put( FILL, fill );
        attributes.put( BLEND_MODE, blendMode );
    }

}
