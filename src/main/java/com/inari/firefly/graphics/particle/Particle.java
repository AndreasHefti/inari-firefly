package com.inari.firefly.graphics.particle;

import java.util.Arrays;

import com.inari.commons.graphics.RGBColor;
import com.inari.firefly.FFInitException;
import com.inari.firefly.graphics.BlendMode;
import com.inari.firefly.graphics.rendering.SpriteRenderable;
import com.inari.firefly.system.external.TransformData;

public final class Particle implements SpriteRenderable, TransformData {
    
    public int spriteId;
    public RGBColor tintColor;
    public BlendMode blendMode;
    
    /** data contains on index:
     *  0 = xpos
     *  1 = ypos
     *  2 = xscale
     *  3 = yscale
     *  4 = xpivot
     *  5 = ypivot
     *  6 = rotation
     *  7 = xvelocity
     *  8 = yvelocity
     *  9...x = individual controller defined
     */
    public final float[] data;

    public Particle( int spriteId, RGBColor tintColor, BlendMode blendMode, float... data ) {
        if ( data == null || data.length < 7) {
            throw new FFInitException( "Invalid particle data: " + Arrays.toString( data ) );
        }
        
        this.spriteId = spriteId;
        this.tintColor = tintColor;
        this.blendMode = blendMode;
        this.data = data;
    }

    @Override
    public final int getSpriteId() {
        return spriteId;
    }

    @Override
    public final RGBColor getTintColor() {
        return tintColor;
    }

    @Override
    public final BlendMode getBlendMode() {
        return blendMode;
    }

    @Override
    public final int getShaderId() {
        return -1;
    }

    @Override
    public final float getXOffset() {
        return data[ 0 ];
    }

    @Override
    public final float getYOffset() {
        return data[ 1 ];
    }

    @Override
    public final float getScaleX() {
        return data[ 2 ];
    }

    @Override
    public float getScaleY() {
        return data[ 3 ];
    }

    @Override
    public final float getPivotX() {
        return data[ 4 ];
    }

    @Override
    public final float getPivotY() {
        return data[ 5 ];
    }

    @Override
    public float getRotation() {
        return data[ 6 ];
    }

    @Override
    public final boolean hasRotation() {
        return data[ 6 ] != 0f;
    }

    @Override
    public final boolean hasScale() {
        return data[ 1 ] != 1f || data[ 2 ] != 1f;
    }

}
