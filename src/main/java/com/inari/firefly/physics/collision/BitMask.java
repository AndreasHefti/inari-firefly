package com.inari.firefly.physics.collision;

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

import com.inari.commons.GeomUtils;
import com.inari.commons.StringUtils;
import com.inari.commons.geom.Position;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.indexed.IIndexedTypeKey;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.system.component.SystemComponent;

public final class BitMask extends SystemComponent {
    
    public static final SystemComponentKey<BitMask> TYPE_KEY = SystemComponentKey.create( BitMask.class );

    public static final AttributeKey<Integer> WIDTH = new AttributeKey<Integer>( "width", Integer.class, BitMask.class );
    public static final AttributeKey<Integer> HEIGHT = new AttributeKey<Integer>( "height", Integer.class, BitMask.class );
    public static final AttributeKey<BitSet> BITS = new AttributeKey<BitSet>( "bits", BitSet.class, BitMask.class );
    private static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        WIDTH,
        HEIGHT,
        BITS
    };
    
    final Rectangle region;
    BitSet bits;
    
    final Rectangle intersectionRegion;
    final Rectangle tempRegion;
    final BitSet intersectionMask;

    protected BitMask( int id ) {
        super( id );
        region = new Rectangle();
        bits = new BitSet();
        
        intersectionRegion = new Rectangle( 0, 0, 0, 0 );
        tempRegion = new Rectangle( 0, 0, 0, 0 );
        intersectionMask = new BitSet();
    }
    
    @Override
    public final IIndexedTypeKey indexedTypeKey() {
        return TYPE_KEY;
    }
    
    public final void setWidth( int width ) {
        region.width = width;
    }

    public final int getWidth() {
        return region.width;
    }
    
    public final void setHeight( int height ) {
        region.height = height;
    }

    public final int getHeight() {
        return region.height;
    }
    
    public final BitSet getBits() {
        return bits;
    }

    public final void setBits( BitSet bitmask ) {
        this.bits = bitmask;
    }

    public final void setBit( int x, int y ) {
        checkPosition( x, y );
        bits.set( y * region.width + x );
    }
    
    public final void setBits( Position... points ) {
        for ( Position p : points ) {
            setBit( p.x, p.y );
        }
    }
    
    public final void clearBits() {
        bits.clear();
    }
    
    public final boolean intersects( int x, int y ) {
        if ( x < 0 || y < 0 || x >= region.width || y >= region.height ) {
            return false;
        }
        
        return bits.get( y * region.width + x );
    }
    
    public final boolean intersects( Position... points ) {
        for ( Position p : points ) {
            if ( intersects( p.x, p.y ) ) {
                return true;
            }
        }
        
        return false;
    }
    
    public final boolean intersects( int xOffset, int yOffset, Position... points ) {
        for ( Position p : points ) {
            if ( intersects( p.x + xOffset, p.y + yOffset ) ) {
                return true;
            }
        }
        
        return false;
    }
    
    public final boolean intersects( Rectangle region ) {
        return intersects( 0, 0, region, false );
    }
    
    public final boolean intersects( int xOffset, int yOffset, Rectangle region ) {
        return intersects( xOffset, yOffset, region, false );
    }
    
    public final boolean intersects( int xOffset, int yOffset, Rectangle otherRegion, boolean cornerCheck ) {
        tempRegion.x = otherRegion.x + xOffset;
        tempRegion.y = otherRegion.y + yOffset;
        tempRegion.width = otherRegion.width;
        tempRegion.height = otherRegion.height;
        GeomUtils.intersection( region, tempRegion, intersectionRegion );
        
        if ( cornerCheck ) {
            return intersects( intersectionRegion.x, intersectionRegion.y ) ||
                   intersects( intersectionRegion.x + intersectionRegion.width, intersectionRegion.y ) ||
                   intersects( intersectionRegion.x, intersectionRegion.y + intersectionRegion.height ) ||
                   intersects( intersectionRegion.x + intersectionRegion.width, intersectionRegion.y + intersectionRegion.height );
        } else {
            intersectionMask.clear();
            setRegion( region, intersectionMask, intersectionRegion );
            return bits.intersects( intersectionMask );
        }
    }

    public final boolean intersects( int xOffset, int yOffset, BitMask other ) {
        tempRegion.x = other.region.x + xOffset;
        tempRegion.y = other.region.y + yOffset;
        tempRegion.width = other.region.width;
        tempRegion.height = other.region.height;
        GeomUtils.intersection( region, tempRegion, intersectionRegion );
        intersectionMask.clear();
        
        int otherX = ( xOffset >= 0 )? 0 : -xOffset;
        int otherY = ( yOffset >= 0 )? 0 : -yOffset;
        for ( int y = intersectionRegion.y; y < intersectionRegion.y + intersectionRegion.height; y++ ) {
            for ( int x = intersectionRegion.x; x < intersectionRegion.x + intersectionRegion.width; x++ ) {
                int index = y * region.width + x;
                int otherIndex = otherY * other.region.width + otherX;
                intersectionMask.set( index, other.bits.get( otherIndex ) );
                otherX++;
            }
            otherX = 0;
            otherY++;
        }
        
        return bits.intersects( intersectionMask );
    }

    public final void setPixelRegion( Rectangle region ) {
        Rectangle intersectionRegion = new Rectangle();
        GeomUtils.intersection( region, this.region, intersectionRegion );
        setRegion( this.region, bits, intersectionRegion );
    }
    
    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        Set<AttributeKey<?>> attributeKeys = super.attributeKeys();
        attributeKeys.addAll( new HashSet<AttributeKey<?>>( Arrays.asList( ATTRIBUTE_KEYS ) ) );
        return attributeKeys;
    }
    
    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        super.fromAttributes( attributes );
        
        region.width = attributes.getValue( WIDTH, region.width );
        region.height = attributes.getValue( HEIGHT, region.height );
        bits = attributes.getValue( BITS, bits );
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        
        attributes.put( WIDTH, region.width );
        attributes.put( HEIGHT, region.height );
        attributes.put( BITS, bits );
    } 
    
    @Override
    public final String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "BitMask [region=" ).append( region );
        builder.append( " bits=\n" ).append( StringUtils.bitsetToString( bits, region.width, region.height ) );
        return builder.toString();
    }
    
    private final void checkPosition( int x, int y ) {
        if ( x < 0 || y < 0 || x >= region.width || y >= region.height ) {
            throw new IllegalArgumentException( "position out of bounds: x=" + x + " y=" + y + " width=" + region.width + " height=" + region.height );
        }
    }

    static final void setRegion( Rectangle targetRegion, BitSet bitset, Rectangle subRegion ) {
        for ( int y = subRegion.y; y < subRegion.y + subRegion.height; y++ ) {
            int formIndex =  y * targetRegion.width + subRegion.x;
            int toX = subRegion.x + subRegion.width;
            bitset.set( formIndex, y * targetRegion.width + toX );
        }
    }
    
    public static final BitSet createSlashedBitset( final int squareWidth, final int xoffset, final int xfactor, final int yoffset, final int yfactor ) {
        BitSet result = new BitSet( squareWidth * squareWidth );
        for ( int y = 0; y < squareWidth; y++ ) {
            for ( int x = 0; x < squareWidth; x++ ) {
                result.set( y * squareWidth + x, x * xfactor + xoffset >= y * yfactor + yoffset );
            }
        }
        
        return result;
    }

}
