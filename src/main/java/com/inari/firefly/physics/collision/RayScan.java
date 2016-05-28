package com.inari.firefly.physics.collision;

import java.util.BitSet;

import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.Aspect;
import com.inari.commons.lang.list.IntBag;

public class RayScan {
    
    public final Rectangle bounds;
    public final BitSet scanBits;
    
    private int solidLength;
    private final IntBag contactTypeLength = new IntBag( 10, -1 );
    
    public RayScan( int rayWidth, boolean horizontal ) {
        if ( horizontal ) {
            bounds = new Rectangle( 0, 0, rayWidth, 1 );
        } else {
            bounds = new Rectangle( 0, 0, 1, rayWidth );
        }
        scanBits = new BitSet( rayWidth );
    }


    public final Rectangle getBounds() {
        return bounds;
    }

    public final BitSet getScanBits() {
        return scanBits;
    }
    
    public final int getSolidContactLength() {
        return solidLength;
    }
    
    public final int getContactTypeLength( Aspect contactType ) {
        return contactTypeLength.get( contactType.index() );
    }
    
    public final void clear() {
        scanBits.clear();
        solidLength = 0;
        contactTypeLength.clear();
    }

    public final boolean isEmpty() {
        return scanBits.isEmpty();
    }
    
    public final BitSet addScan( final int xOffset, final int yOffset, final BitMask bitMask, boolean solid, Aspect contactType ) {
        final int x = bounds.x - xOffset;
        final int y = bounds.y - yOffset;
        int length = 0;
        if ( bitMask != null ) {
            if ( bounds.height > 1 ) {
                for ( int i = Math.abs( y ); i < bounds.height; i++ ) {
                    final boolean bit = bitMask.getBit( x, y + i );
                    if ( bit ) {
                        length++;
                        if ( solid ) {
                            scanBits.set( i );
                        }
                    }
                }
            } else {
                for ( int i = Math.abs( x ); i < bounds.width; i++ ) {
                    final boolean bit = bitMask.getBit( x + i, y );
                    if ( bit ) {
                        length++;
                        if ( solid ) {
                            scanBits.set( i );
                        }
                    }
                }
            }
        } else {
            if ( bounds.height > 1 ) {
                for ( int i = Math.abs( y ); i < bounds.height; i++ ) {
                    length++;
                    if ( solid ) {
                        scanBits.set( i );
                    }
                }
            } else {
                for ( int i = Math.abs( x ); i < bounds.width; i++ ) {
                    length++;
                    if ( solid ) {
                        scanBits.set( i );
                    }
                }
            }
        }

        if ( contactType != null ) {
            int aspectIndex = contactType.index();
            int l =  ( contactTypeLength.contains( aspectIndex ) ) ? contactTypeLength.get( aspectIndex ) : 0;
            l += length;
            contactTypeLength.set( aspectIndex, l );
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
        builder.append( "]" );
        return builder.toString();
    } 

}
