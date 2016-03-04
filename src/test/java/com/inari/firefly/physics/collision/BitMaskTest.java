package com.inari.firefly.physics.collision;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.BitSet;

import org.junit.Test;

import com.inari.commons.StringUtils;
import com.inari.commons.geom.Position;
import com.inari.commons.geom.Rectangle;
import com.inari.firefly.FFTest;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.component.attr.ComponentAttributeMap;

public class BitMaskTest extends FFTest {
    
    
    public static final String PP_REGION_1 = 
            "0000000000" +
            "0000000000" +
            "0000000000" +
            "0000000000" +
            "0000000000" +
            "1111111111" +
            "1111111111" +
            "1111111111" +
            "1111111111" +
            "1111111111";
    
    public static final String PP_REGION_2 = 
            "0000000001" +
            "0000000011" +
            "0000000111" +
            "0000001111" +
            "0000011111" +
            "0000111111" +
            "0001111111" +
            "0011111111" +
            "0111111111" +
            "1111111111";
    
    @Test
    public void testCreation() {
        AttributeMap attrs = new ComponentAttributeMap();
        attrs.put( BitMask.WIDTH, 10 );
        attrs.put( BitMask.HEIGHT, 10 );
        BitMask pp1 = new BitMask( 1 );
        pp1.fromAttributes( attrs );
        
        attrs.put( BitMask.BITS, StringUtils.bitsetFromString( PP_REGION_1 ) );
        BitMask pp2 = new BitMask( 2 );
        pp2.fromAttributes( attrs );
        
        attrs.put( BitMask.BITS, StringUtils.bitsetFromString( PP_REGION_2 ) );
        BitMask pp3 = new BitMask( 3 );
        pp3.fromAttributes( attrs );
        
        assertEquals( 
            "BitMask [region=[x=0,y=0,width=10,height=10] bits=\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000", 
            pp1.toString() 
        );
        assertEquals( 
            "BitMask [region=[x=0,y=0,width=10,height=10] bits=\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "1111111111\n" + 
            "1111111111\n" + 
            "1111111111\n" + 
            "1111111111\n" + 
            "1111111111", 
            pp2.toString() 
        );
        assertEquals( 
            "BitMask [region=[x=0,y=0,width=10,height=10] bits=\n" + 
            "0000000001\n" + 
            "0000000011\n" + 
            "0000000111\n" + 
            "0000001111\n" + 
            "0000011111\n" + 
            "0000111111\n" + 
            "0001111111\n" + 
            "0011111111\n" + 
            "0111111111\n" + 
            "1111111111", 
            pp3.toString() 
        );
        
        pp1.dispose();
        pp2.dispose();
        pp3.dispose();
    }
    
    @Test
    public void testSetPixel() {
        AttributeMap attrs = new ComponentAttributeMap();
        attrs.put( BitMask.WIDTH, 10 );
        attrs.put( BitMask.HEIGHT, 10 );
        BitMask pp = new BitMask( 1 );
        pp.fromAttributes( attrs );
        
        assertEquals( 
            "BitMask [region=[x=0,y=0,width=10,height=10] bits=\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000", 
            pp.toString() 
        );
        
        pp.setBit( 3, 4 );
        assertEquals( 
            "BitMask [region=[x=0,y=0,width=10,height=10] bits=\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0001000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000", 
            pp.toString() 
        );
        pp.setBits( new Position( 0,0 ), new Position( 1,0 ), new Position( 2,0 ) );
        assertEquals( 
            "BitMask [region=[x=0,y=0,width=10,height=10] bits=\n" + 
            "1110000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0001000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000", 
            pp.toString() 
        );
        pp.setPixelRegion( new Rectangle( 5, 5, 4, 4 ) );
        assertEquals( 
            "BitMask [region=[x=0,y=0,width=10,height=10] bits=\n" + 
            "1110000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0001000000\n" + 
            "0000011110\n" + 
            "0000011110\n" + 
            "0000011110\n" + 
            "0000011110\n" + 
            "0000000000", 
            pp.toString() 
        );
        pp.setPixelRegion( new Rectangle( 5, 0, 40, 2 ) );
        assertEquals( 
            "BitMask [region=[x=0,y=0,width=10,height=10] bits=\n" + 
            "1110011111\n" + 
            "0000011111\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0001000000\n" + 
            "0000011110\n" + 
            "0000011110\n" + 
            "0000011110\n" + 
            "0000011110\n" + 
            "0000000000", 
            pp.toString() 
        );
        
        pp.clearBits();
        assertEquals( 
            "BitMask [region=[x=0,y=0,width=10,height=10] bits=\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000", 
            pp.toString() 
        );
        
        pp.dispose();
    }
    
    @Test
    public void testIntersectionOfPixel() {
        
        AttributeMap attrs = new ComponentAttributeMap();
        attrs.put( BitMask.WIDTH, 10 );
        attrs.put( BitMask.HEIGHT, 10 );
        BitMask pp1 = new BitMask( 1 );
        pp1.fromAttributes( attrs );
        
        attrs.put( BitMask.BITS, StringUtils.bitsetFromString( PP_REGION_1 ) );
        BitMask pp2 = new BitMask( 2 );
        pp2.fromAttributes( attrs );
        
        attrs.put( BitMask.BITS, StringUtils.bitsetFromString( PP_REGION_2 ) );
        BitMask pp3 = new BitMask( 3 );
        pp3.fromAttributes( attrs );
        
        assertEquals( 
            "BitMask [region=[x=0,y=0,width=10,height=10] bits=\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000", 
            pp1.toString() 
        );
        assertFalse( pp1.intersects( 1, 1 ) );
        assertFalse( pp1.intersects( 5, 6 ) );
        assertFalse( pp1.intersects( 2, 9 ) );
        assertFalse( pp1.intersects( 0, 5 ) );
        assertFalse( pp1.intersects( -1, 1 ) );
        assertFalse( pp1.intersects( 1, 50 ) );
        
        assertEquals( 
            "BitMask [region=[x=0,y=0,width=10,height=10] bits=\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "1111111111\n" + 
            "1111111111\n" + 
            "1111111111\n" + 
            "1111111111\n" + 
            "1111111111", 
            pp2.toString() 
        );
        assertFalse( pp2.intersects( 1, 1 ) );
        assertTrue( pp2.intersects( 5, 6 ) );
        assertTrue( pp2.intersects( 2, 9 ) );
        assertTrue( pp2.intersects( 0, 5 ) );
        assertFalse( pp2.intersects( -1, 1 ) );
        assertFalse( pp2.intersects( 1, 50 ) );
        
        assertEquals( 
            "BitMask [region=[x=0,y=0,width=10,height=10] bits=\n" + 
            "0000000001\n" + 
            "0000000011\n" + 
            "0000000111\n" + 
            "0000001111\n" + 
            "0000011111\n" + 
            "0000111111\n" + 
            "0001111111\n" + 
            "0011111111\n" + 
            "0111111111\n" + 
            "1111111111", 
            pp3.toString() 
        );
        assertFalse( pp3.intersects( 1, 1 ) );
        assertTrue( pp3.intersects( 5, 6 ) );
        assertTrue( pp3.intersects( 2, 9 ) );
        assertFalse( pp3.intersects( 0, 5 ) );
        assertFalse( pp3.intersects( -1, 1 ) );
        assertFalse( pp3.intersects( 1, 50 ) );
        
        pp1.dispose();
        pp2.dispose();
        pp3.dispose();
    }
    
    @Test
    public void testIntersectionOfRectangle() {
        
        AttributeMap attrs = new ComponentAttributeMap();
        attrs.put( BitMask.WIDTH, 10 );
        attrs.put( BitMask.HEIGHT, 10 );
        BitMask pp1 = new BitMask( 1 );
        pp1.fromAttributes( attrs );
        
        attrs.put( BitMask.BITS, StringUtils.bitsetFromString( PP_REGION_1 ) );
        BitMask pp2 = new BitMask( 2 );
        pp2.fromAttributes( attrs );
        
        attrs.put( BitMask.BITS, StringUtils.bitsetFromString( PP_REGION_2 ) );
        BitMask pp3 = new BitMask( 3 );
        pp3.fromAttributes( attrs );
        
        assertEquals( 
            "BitMask [region=[x=0,y=0,width=10,height=10] bits=\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000", 
            pp1.toString() 
        );
        assertFalse( pp1.intersects( new Rectangle( 0, 0, 1, 1 ) ) );
        assertFalse( pp1.intersects( new Rectangle( 5, 5, 1, 1 ) ) );
        assertFalse( pp1.intersects( new Rectangle( 0, 0, 10, 10 ) ) );
        assertFalse( pp1.intersects( new Rectangle( 2, 3, 4, 5 ) ) );
        assertFalse( pp1.intersects( new Rectangle( -3, 0, 50, 4 ) ) );
            
        assertEquals( 
            "BitMask [region=[x=0,y=0,width=10,height=10] bits=\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "1111111111\n" + 
            "1111111111\n" + 
            "1111111111\n" + 
            "1111111111\n" + 
            "1111111111", 
            pp2.toString() 
        );
        assertFalse( pp2.intersects( new Rectangle( 0, 0, 1, 1 ) ) );
        assertTrue( pp2.intersects( new Rectangle( 5, 5, 1, 1 ) ) );
        assertEquals( 
            "1", 
            StringUtils.bitsetToString( pp2.intersectionMask, pp2.intersectionRegion.width, pp2.intersectionRegion.height ) 
        );
        assertTrue( pp2.intersects( new Rectangle( 0, 0, 10, 10 ) ) );
        assertEquals( 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "1111111111\n" + 
            "1111111111\n" + 
            "1111111111\n" + 
            "1111111111\n" + 
            "1111111111", 
            StringUtils.bitsetToString( pp2.intersectionMask, pp2.intersectionRegion.width, pp2.intersectionRegion.height ) 
        );
        assertTrue( pp2.intersects( new Rectangle( 2, 3, 4, 5 ) ) );
        assertEquals( 
            "0000\n" + 
            "0000\n" + 
            "1111\n" + 
            "1111\n" + 
            "1111", 
            StringUtils.bitsetToString( pp2.intersectionMask, pp2.intersectionRegion.width, pp2.intersectionRegion.height ) 
        );
        assertFalse( pp2.intersects( new Rectangle( -3, 0, 50, 4 ) ) );
        
        assertEquals( 
            "BitMask [region=[x=0,y=0,width=10,height=10] bits=\n" + 
            "0000000001\n" + 
            "0000000011\n" + 
            "0000000111\n" + 
            "0000001111\n" + 
            "0000011111\n" + 
            "0000111111\n" + 
            "0001111111\n" + 
            "0011111111\n" + 
            "0111111111\n" + 
            "1111111111", 
            pp3.toString() 
        );
        assertFalse( pp3.intersects( new Rectangle( 0, 0, 1, 1 ) ) );
        assertTrue( pp3.intersects( new Rectangle( 5, 5, 1, 1 ) ) );
        assertEquals( 
            "1", 
            StringUtils.bitsetToString( pp3.intersectionMask, pp3.intersectionRegion.width, pp3.intersectionRegion.height ) 
        );
        assertTrue( pp3.intersects( new Rectangle( 0, 0, 10, 10 ) ) );
        assertEquals( 
            "0000000001\n" + 
            "0000000011\n" + 
            "0000000111\n" + 
            "0000001111\n" + 
            "0000011111\n" + 
            "0000111111\n" + 
            "0001111111\n" + 
            "0011111111\n" + 
            "0111111111\n" + 
            "1111111111", 
            StringUtils.bitsetToString( pp3.intersectionMask, pp3.intersectionRegion.width, pp3.intersectionRegion.height ) 
        );
        assertTrue( pp3.intersects( new Rectangle( 2, 3, 4, 5 ) ) );
        assertEquals( 
            "0000\n" + 
            "0001\n" + 
            "0011\n" + 
            "0111\n" + 
            "1111", 
            StringUtils.bitsetToString( pp3.intersectionMask, pp3.intersectionRegion.width, pp3.intersectionRegion.height ) 
        );
        assertTrue( pp3.intersects( new Rectangle( -3, 0, 50, 4 ) ) );
        assertEquals( 
            "0000000001\n" + 
            "0000000011\n" + 
            "0000000111\n" + 
            "0000001111", 
            StringUtils.bitsetToString( pp3.intersectionMask, pp3.intersectionRegion.width, pp3.intersectionRegion.height ) 
        );
        
        // difference between with or without cornerCheck
        pp1.setPixelRegion( new Rectangle( 5, 5, 5, 2 ) );
        assertEquals( 
            "BitMask [region=[x=0,y=0,width=10,height=10] bits=\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000011111\n" + 
            "0000011111\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000", 
            pp1.toString() 
        );
        
        Rectangle otherRegion = new Rectangle( -10, -10, 17, 17 );
        
        assertFalse( pp1.intersectsRegion( otherRegion, true ) );
        assertTrue( pp1.intersects( otherRegion ) );
        
        pp1.dispose();
        pp2.dispose();
        pp3.dispose();
    }
    
    @Test
    public void testIntersectionWithOther() {
        AttributeMap attrs = new ComponentAttributeMap();
        attrs.put( BitMask.WIDTH, 10 );
        attrs.put( BitMask.HEIGHT, 10 );
        attrs.put( BitMask.BITS, StringUtils.bitsetFromString( PP_REGION_1 ) );
        BitMask pp1 = new BitMask( 2 );
        pp1.fromAttributes( attrs );
        
        attrs.put( BitMask.BITS, StringUtils.bitsetFromString( PP_REGION_2 ) );
        BitMask pp2 = new BitMask( 3 );
        pp2.fromAttributes( attrs );
        
        assertEquals( 
            "BitMask [region=[x=0,y=0,width=10,height=10] bits=\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "1111111111\n" + 
            "1111111111\n" + 
            "1111111111\n" + 
            "1111111111\n" + 
            "1111111111", 
            pp1.toString() 
        );
        assertEquals( 
            "BitMask [region=[x=0,y=0,width=10,height=10] bits=\n" + 
            "0000000001\n" + 
            "0000000011\n" + 
            "0000000111\n" + 
            "0000001111\n" + 
            "0000011111\n" + 
            "0000111111\n" + 
            "0001111111\n" + 
            "0011111111\n" + 
            "0111111111\n" + 
            "1111111111", 
            pp2.toString() 
        );
        
        assertTrue( pp2.intersects( 0, 0, pp1 ) );
        assertEquals( 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000000000\n" + 
            "0000111111\n" + 
            "0001111111\n" + 
            "0011111111\n" + 
            "0111111111\n" + 
            "1111111111", 
            StringUtils.bitsetToString( pp2.intersectionMask, pp2.intersectionRegion.width, pp2.intersectionRegion.height ) 
        );
        assertTrue( pp2.intersects( -1, -1, pp1 ) );
        assertEquals( 
            "000000000\n" + 
            "000000000\n" + 
            "000000000\n" + 
            "000000000\n" + 
            "000001111\n" + 
            "000011111\n" + 
            "000111111\n" + 
            "001111111\n" + 
            "011111111", 
            StringUtils.bitsetToString( pp2.intersectionMask, pp2.intersectionRegion.width, pp2.intersectionRegion.height ) 
        );
        assertTrue( pp2.intersects( -2, -2, pp1 ) );
        assertEquals( 
            "00000000\n" + 
            "00000000\n" + 
            "00000000\n" + 
            "00000011\n" + 
            "00000111\n" + 
            "00001111\n" + 
            "00011111\n" + 
            "00111111", 
            StringUtils.bitsetToString( pp2.intersectionMask, pp2.intersectionRegion.width, pp2.intersectionRegion.height ) 
        );
        assertTrue( pp2.intersects( -3, -3, pp1 ) );
        assertEquals( 
            "0000000\n" + 
            "0000000\n" + 
            "0000000\n" + 
            "0000001\n" + 
            "0000011\n" + 
            "0000111\n" + 
            "0001111", 
            StringUtils.bitsetToString( pp2.intersectionMask, pp2.intersectionRegion.width, pp2.intersectionRegion.height ) 
        );
        assertTrue( pp2.intersects( -4, -4, pp1 ) );
        assertEquals( 
            "000000\n" + 
            "000000\n" + 
            "000000\n" + 
            "000000\n" + 
            "000001\n" + 
            "000011", 
            StringUtils.bitsetToString( pp2.intersectionMask, pp2.intersectionRegion.width, pp2.intersectionRegion.height ) 
        );
        assertFalse( pp2.intersects( -5, -5, pp1 ) );
        assertEquals( 
            "00000\n" + 
            "00000\n" + 
            "00000\n" + 
            "00000\n" + 
            "00000", 
            StringUtils.bitsetToString( pp2.intersectionMask, pp2.intersectionRegion.width, pp2.intersectionRegion.height ) 
        );
        assertFalse( pp2.intersects( -6, -6, pp1 ) );
        
        pp1.dispose();
        pp2.dispose();
    }
    
    @Test
    public void testIntersectionPerformance() {
        AttributeMap attrs = new ComponentAttributeMap();
        attrs.put( BitMask.WIDTH, 10 );
        attrs.put( BitMask.HEIGHT, 10 );
        attrs.put( BitMask.BITS, StringUtils.bitsetFromString( PP_REGION_1 ) );
        BitMask pp1 = new BitMask( 2 );
        pp1.fromAttributes( attrs );
        
        attrs.put( BitMask.BITS, StringUtils.bitsetFromString( PP_REGION_2 ) );
        BitMask pp2 = new BitMask( 3 );
        pp2.fromAttributes( attrs );
        
        // 10000 * 10 = 100000 checks seems to be no problem for performance
        for ( int f = 0; f < 10000; f++ ) {
            for ( int i = 0; i < 10; i++ ) {
                pp2.intersects( -1, -1, pp1 );
            }
        }
        
        pp1.dispose();
        pp2.dispose();
    }
    
    
    @Test
    public void testCreateSlashedBitsetNorthEast() {
        
        BitSet bitMask = BitMask.createSlashedBitset( 16, 0, 1, 0, 1 );
        assertEquals( 
            "1111111111111111\n" + 
            "0111111111111111\n" + 
            "0011111111111111\n" + 
            "0001111111111111\n" + 
            "0000111111111111\n" + 
            "0000011111111111\n" + 
            "0000001111111111\n" + 
            "0000000111111111\n" + 
            "0000000011111111\n" + 
            "0000000001111111\n" + 
            "0000000000111111\n" + 
            "0000000000011111\n" + 
            "0000000000001111\n" + 
            "0000000000000111\n" + 
            "0000000000000011\n" + 
            "0000000000000001", 
            StringUtils.bitsetToString( bitMask, 16, 16 ) 
        );
        
        bitMask = BitMask.createSlashedBitset( 16, 1, 2, 0, 1 );
        assertEquals( 
            "1111111111111111\n" + 
            "1111111111111111\n" + 
            "0111111111111111\n" + 
            "0111111111111111\n" + 
            "0011111111111111\n" + 
            "0011111111111111\n" + 
            "0001111111111111\n" + 
            "0001111111111111\n" + 
            "0000111111111111\n" + 
            "0000111111111111\n" + 
            "0000011111111111\n" + 
            "0000011111111111\n" + 
            "0000001111111111\n" + 
            "0000001111111111\n" + 
            "0000000111111111\n" + 
            "0000000111111111", 
            StringUtils.bitsetToString( bitMask, 16, 16 ) 
        );
        
        bitMask = BitMask.createSlashedBitset( 16, -15, 2, 0, 1 );
        assertEquals( 
            "0000000011111111\n" + 
            "0000000011111111\n" + 
            "0000000001111111\n" + 
            "0000000001111111\n" + 
            "0000000000111111\n" + 
            "0000000000111111\n" + 
            "0000000000011111\n" + 
            "0000000000011111\n" + 
            "0000000000001111\n" + 
            "0000000000001111\n" + 
            "0000000000000111\n" + 
            "0000000000000111\n" + 
            "0000000000000011\n" + 
            "0000000000000011\n" + 
            "0000000000000001\n" + 
            "0000000000000001", 
            StringUtils.bitsetToString( bitMask, 16, 16 ) 
        );
        
        bitMask = BitMask.createSlashedBitset( 16, 0, 1, 0, 2 );
        assertEquals( 
            "1111111111111111\n" + 
            "0011111111111111\n" + 
            "0000111111111111\n" + 
            "0000001111111111\n" + 
            "0000000011111111\n" + 
            "0000000000111111\n" + 
            "0000000000001111\n" + 
            "0000000000000011\n" + 
            "0000000000000000\n" + 
            "0000000000000000\n" + 
            "0000000000000000\n" + 
            "0000000000000000\n" + 
            "0000000000000000\n" + 
            "0000000000000000\n" + 
            "0000000000000000\n" + 
            "0000000000000000", 
            StringUtils.bitsetToString( bitMask, 16, 16 ) 
        );
        
        bitMask = BitMask.createSlashedBitset( 16, 16, 1, 0, 2 );
        assertEquals( 
            "1111111111111111\n" + 
            "1111111111111111\n" + 
            "1111111111111111\n" + 
            "1111111111111111\n" + 
            "1111111111111111\n" + 
            "1111111111111111\n" + 
            "1111111111111111\n" + 
            "1111111111111111\n" + 
            "1111111111111111\n" + 
            "0011111111111111\n" + 
            "0000111111111111\n" + 
            "0000001111111111\n" + 
            "0000000011111111\n" + 
            "0000000000111111\n" + 
            "0000000000001111\n" + 
            "0000000000000011", 
            StringUtils.bitsetToString( bitMask, 16, 16 ) 
        );
    }
    
    @Test
    public void testCreateSlashSouthWest() {
        
        BitSet bitMask = BitMask.createSlashedBitset( 16, 0, -1, 0, -1 );
        assertEquals( 
            "1000000000000000\n" + 
            "1100000000000000\n" + 
            "1110000000000000\n" + 
            "1111000000000000\n" + 
            "1111100000000000\n" + 
            "1111110000000000\n" + 
            "1111111000000000\n" + 
            "1111111100000000\n" + 
            "1111111110000000\n" + 
            "1111111111000000\n" + 
            "1111111111100000\n" + 
            "1111111111110000\n" + 
            "1111111111111000\n" + 
            "1111111111111100\n" + 
            "1111111111111110\n" + 
            "1111111111111111", 
            StringUtils.bitsetToString( bitMask, 16, 16 ) 
        );
        
        bitMask = BitMask.createSlashedBitset( 16, 0, -2, 0, -1 );
        assertEquals( 
            "1000000000000000\n" + 
            "1000000000000000\n" + 
            "1100000000000000\n" + 
            "1100000000000000\n" + 
            "1110000000000000\n" + 
            "1110000000000000\n" + 
            "1111000000000000\n" + 
            "1111000000000000\n" + 
            "1111100000000000\n" + 
            "1111100000000000\n" + 
            "1111110000000000\n" + 
            "1111110000000000\n" + 
            "1111111000000000\n" + 
            "1111111000000000\n" + 
            "1111111100000000\n" + 
            "1111111100000000", 
            StringUtils.bitsetToString( bitMask, 16, 16 ) 
        );
        
        bitMask = BitMask.createSlashedBitset( 16, 16, -2, 0, -1 );
        assertEquals( 
            "1111111110000000\n" + 
            "1111111110000000\n" + 
            "1111111111000000\n" + 
            "1111111111000000\n" + 
            "1111111111100000\n" + 
            "1111111111100000\n" + 
            "1111111111110000\n" + 
            "1111111111110000\n" + 
            "1111111111111000\n" + 
            "1111111111111000\n" + 
            "1111111111111100\n" + 
            "1111111111111100\n" + 
            "1111111111111110\n" + 
            "1111111111111110\n" + 
            "1111111111111111\n" + 
            "1111111111111111", 
            StringUtils.bitsetToString( bitMask, 16, 16 ) 
        );
        
        bitMask = BitMask.createSlashedBitset( 16, 1, -1, 0, -2 );
        assertEquals( 
            "1100000000000000\n" + 
            "1111000000000000\n" + 
            "1111110000000000\n" + 
            "1111111100000000\n" + 
            "1111111111000000\n" + 
            "1111111111110000\n" + 
            "1111111111111100\n" + 
            "1111111111111111\n" + 
            "1111111111111111\n" + 
            "1111111111111111\n" + 
            "1111111111111111\n" + 
            "1111111111111111\n" + 
            "1111111111111111\n" + 
            "1111111111111111\n" + 
            "1111111111111111\n" + 
            "1111111111111111", 
            StringUtils.bitsetToString( bitMask, 16, 16 ) 
        );
        
        bitMask = BitMask.createSlashedBitset( 16, -15, -1, 0, -2 );
        assertEquals( 
            "0000000000000000\n" + 
            "0000000000000000\n" + 
            "0000000000000000\n" + 
            "0000000000000000\n" + 
            "0000000000000000\n" + 
            "0000000000000000\n" + 
            "0000000000000000\n" + 
            "0000000000000000\n" + 
            "1100000000000000\n" + 
            "1111000000000000\n" + 
            "1111110000000000\n" + 
            "1111111100000000\n" + 
            "1111111111000000\n" + 
            "1111111111110000\n" + 
            "1111111111111100\n" + 
            "1111111111111111", 
            StringUtils.bitsetToString( bitMask, 16, 16 ) 
        );
    }
    
    @Test
    public void testCreateSlashSouthEast() {
        
        BitSet bitMask = BitMask.createSlashedBitset( 16, -15, 1, 0, -1 );
        assertEquals( 
            "0000000000000001\n" + 
            "0000000000000011\n" + 
            "0000000000000111\n" + 
            "0000000000001111\n" + 
            "0000000000011111\n" + 
            "0000000000111111\n" + 
            "0000000001111111\n" + 
            "0000000011111111\n" + 
            "0000000111111111\n" + 
            "0000001111111111\n" + 
            "0000011111111111\n" + 
            "0000111111111111\n" + 
            "0001111111111111\n" + 
            "0011111111111111\n" + 
            "0111111111111111\n" + 
            "1111111111111111", 
            StringUtils.bitsetToString( bitMask, 16, 16 ) 
        );
        
        bitMask = BitMask.createSlashedBitset( 16, -14, 2, 0, -1 );
        assertEquals( 
            "0000000111111111\n" + 
            "0000000111111111\n" + 
            "0000001111111111\n" + 
            "0000001111111111\n" + 
            "0000011111111111\n" + 
            "0000011111111111\n" + 
            "0000111111111111\n" + 
            "0000111111111111\n" + 
            "0001111111111111\n" + 
            "0001111111111111\n" + 
            "0011111111111111\n" + 
            "0011111111111111\n" + 
            "0111111111111111\n" + 
            "0111111111111111\n" + 
            "1111111111111111\n" + 
            "1111111111111111", 
            StringUtils.bitsetToString( bitMask, 16, 16 ) 
        );
        
        bitMask = BitMask.createSlashedBitset( 16, -30, 2, 0, -1 );
        assertEquals( 
            "0000000000000001\n" + 
            "0000000000000001\n" + 
            "0000000000000011\n" + 
            "0000000000000011\n" + 
            "0000000000000111\n" + 
            "0000000000000111\n" + 
            "0000000000001111\n" + 
            "0000000000001111\n" + 
            "0000000000011111\n" + 
            "0000000000011111\n" + 
            "0000000000111111\n" + 
            "0000000000111111\n" + 
            "0000000001111111\n" + 
            "0000000001111111\n" + 
            "0000000011111111\n" + 
            "0000000011111111", 
            StringUtils.bitsetToString( bitMask, 16, 16 ) 
        );
        
        bitMask = BitMask.createSlashedBitset( 16, -14, 1, 0, -2 );
        assertEquals( 
            "0000000000000011\n" + 
            "0000000000001111\n" + 
            "0000000000111111\n" + 
            "0000000011111111\n" + 
            "0000001111111111\n" + 
            "0000111111111111\n" + 
            "0011111111111111\n" + 
            "1111111111111111\n" + 
            "1111111111111111\n" + 
            "1111111111111111\n" + 
            "1111111111111111\n" + 
            "1111111111111111\n" + 
            "1111111111111111\n" + 
            "1111111111111111\n" + 
            "1111111111111111\n" + 
            "1111111111111111", 
            StringUtils.bitsetToString( bitMask, 16, 16 ) 
        );
        
        bitMask = BitMask.createSlashedBitset( 16, -30, 1, 0, -2 );
        assertEquals( 
            "0000000000000000\n" + 
            "0000000000000000\n" + 
            "0000000000000000\n" + 
            "0000000000000000\n" + 
            "0000000000000000\n" + 
            "0000000000000000\n" + 
            "0000000000000000\n" + 
            "0000000000000000\n" + 
            "0000000000000011\n" + 
            "0000000000001111\n" + 
            "0000000000111111\n" + 
            "0000000011111111\n" + 
            "0000001111111111\n" + 
            "0000111111111111\n" + 
            "0011111111111111\n" + 
            "1111111111111111", 
            StringUtils.bitsetToString( bitMask, 16, 16 ) 
        );
    }
    
    @Test
    public void testCreateSlashNorthWest() {
        
        BitSet bitMask = BitMask.createSlashedBitset( 16, 15, -1, 0, 1 );
        assertEquals( 
            "1111111111111111\n" + 
            "1111111111111110\n" + 
            "1111111111111100\n" + 
            "1111111111111000\n" + 
            "1111111111110000\n" + 
            "1111111111100000\n" + 
            "1111111111000000\n" + 
            "1111111110000000\n" + 
            "1111111100000000\n" + 
            "1111111000000000\n" + 
            "1111110000000000\n" + 
            "1111100000000000\n" + 
            "1111000000000000\n" + 
            "1110000000000000\n" + 
            "1100000000000000\n" + 
            "1000000000000000", 
            StringUtils.bitsetToString( bitMask, 16, 16 ) 
        );
        
        bitMask = BitMask.createSlashedBitset( 16, 15, -2, 0, 1 );
        assertEquals( 
            "1111111100000000\n" + 
            "1111111100000000\n" + 
            "1111111000000000\n" + 
            "1111111000000000\n" + 
            "1111110000000000\n" + 
            "1111110000000000\n" + 
            "1111100000000000\n" + 
            "1111100000000000\n" + 
            "1111000000000000\n" + 
            "1111000000000000\n" + 
            "1110000000000000\n" + 
            "1110000000000000\n" + 
            "1100000000000000\n" + 
            "1100000000000000\n" + 
            "1000000000000000\n" + 
            "1000000000000000", 
            StringUtils.bitsetToString( bitMask, 16, 16 ) 
        );
        
        bitMask = BitMask.createSlashedBitset( 16, 31, -2, 0, 1 );
        assertEquals( 
            "1111111111111111\n" + 
            "1111111111111111\n" + 
            "1111111111111110\n" + 
            "1111111111111110\n" + 
            "1111111111111100\n" + 
            "1111111111111100\n" + 
            "1111111111111000\n" + 
            "1111111111111000\n" + 
            "1111111111110000\n" + 
            "1111111111110000\n" + 
            "1111111111100000\n" + 
            "1111111111100000\n" + 
            "1111111111000000\n" + 
            "1111111111000000\n" + 
            "1111111110000000\n" + 
            "1111111110000000", 
            StringUtils.bitsetToString( bitMask, 16, 16 ) 
        );
        
        bitMask = BitMask.createSlashedBitset( 16, 15, -1, 0, 2 );
        assertEquals( 
            "1111111111111111\n" + 
            "1111111111111100\n" + 
            "1111111111110000\n" + 
            "1111111111000000\n" + 
            "1111111100000000\n" + 
            "1111110000000000\n" + 
            "1111000000000000\n" + 
            "1100000000000000\n" + 
            "0000000000000000\n" + 
            "0000000000000000\n" + 
            "0000000000000000\n" + 
            "0000000000000000\n" + 
            "0000000000000000\n" + 
            "0000000000000000\n" + 
            "0000000000000000\n" + 
            "0000000000000000", 
            StringUtils.bitsetToString( bitMask, 16, 16 ) 
        );
        
        bitMask = BitMask.createSlashedBitset( 16, 31, -1, 0, 2 );
        assertEquals( 
            "1111111111111111\n" + 
            "1111111111111111\n" + 
            "1111111111111111\n" + 
            "1111111111111111\n" + 
            "1111111111111111\n" + 
            "1111111111111111\n" + 
            "1111111111111111\n" + 
            "1111111111111111\n" + 
            "1111111111111111\n" + 
            "1111111111111100\n" + 
            "1111111111110000\n" + 
            "1111111111000000\n" + 
            "1111111100000000\n" + 
            "1111110000000000\n" + 
            "1111000000000000\n" + 
            "1100000000000000", 
            StringUtils.bitsetToString( bitMask, 16, 16 ) 
        );
    }

}
