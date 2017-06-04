package com.inari.firefly.graphics.particle;

import com.inari.commons.graphics.RGBColor;
import com.inari.firefly.graphics.BlendMode;
import com.inari.firefly.graphics.rendering.SpriteRenderable;
import com.inari.firefly.system.external.TransformData;

public class Particle implements SpriteRenderable, TransformData {
    
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
     */
    public final float[] data;
    
    public Particle( int spriteId, RGBColor tintColor, BlendMode blendMode, float x, float y ) {
        this.spriteId = spriteId;
        this.tintColor = tintColor;
        this.blendMode = blendMode;
        this.data = new float[] { x, y, 1f, 1f, 0f, 0f, 0f, 0f, 0f };
    }
    
    public Particle( int spriteId, RGBColor tintColor, BlendMode blendMode, float x, float y, float sx, float sy ) {
        this.spriteId = spriteId;
        this.tintColor = tintColor;
        this.blendMode = blendMode;
        this.data = new float[] { x, y, sx, sy, 0f, 0f, 0f, 0f, 0f };
    }
    
    public Particle( int spriteId, RGBColor tintColor, BlendMode blendMode, float x, float y, float sx, float sy, float px, float py, float r ) {
        this.spriteId = spriteId;
        this.tintColor = tintColor;
        this.blendMode = blendMode;
        this.data = new float[] { x, y, sx, sy, px, py, r, 0f, 0f };
    }

    public Particle( int spriteId, RGBColor tintColor, BlendMode blendMode, float x, float y, float sx, float sy, float px, float py, float r, float vx, float vy ) {
        this.spriteId = spriteId;
        this.tintColor = tintColor;
        this.blendMode = blendMode;
        this.data = new float[] { x, y, sx, sy, px, py, r, vx, vy };
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
        return data[ 2 ] != 1f || data[ 3 ] != 1f;
    }
    
    public final void move() {
        data[ 0 ] += data[ 7 ];
        data[ 1 ] += data[ 9 ];
    }

}
