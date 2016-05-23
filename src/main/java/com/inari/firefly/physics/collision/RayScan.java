package com.inari.firefly.physics.collision;

import java.util.BitSet;

import com.inari.commons.geom.Rectangle;

public class RayScan {
    
    public final Rectangle bounds;
    BitMask bitMask;
    
    public final BitSet scanBits;
    
    public RayScan( int rayWidth, boolean horizontal ) {
        if ( horizontal ) {
            bounds = new Rectangle( 0, 0, rayWidth, 1 );
        } else {
            bounds = new Rectangle( 0, 0, 1, rayWidth );
        }
        scanBits = new BitSet( rayWidth );
    }

    public final BitMask getBitMask() {
        return bitMask;
    }

    public final void setBitMask( BitMask bitMask ) {
        this.bitMask = bitMask;
    }

    public final Rectangle getBounds() {
        return bounds;
    }

    public final BitSet getScanBits() {
        return scanBits;
    }

    public final void clear() {
        scanBits.clear();
        bitMask = null;
    }

    public final boolean isEmpty() {
        return scanBits.isEmpty();
    }
    
    public final BitSet addScan( int xOffset, int yOffset ) {
        final int x = bounds.x - xOffset;
        final int y = bounds.y - yOffset;
        if ( bitMask != null ) {
            for ( int i = Math.abs( y ); i < bounds.height; i++ ) {
                scanBits.set( i, bitMask.getBit( x, y + i ) );
            }
        } else {
            for ( int i = Math.abs( y ); i < bounds.height; i++ ) {
                scanBits.set( i );
            }
        }
        
        return scanBits;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "RayScan [bounds=" );
        builder.append( bounds );
        builder.append( ", scanBits=" );
        builder.append( scanBits );
        builder.append( ", bitMask=" );
        builder.append( bitMask );
        builder.append( "]" );
        return builder.toString();
    } 

}
