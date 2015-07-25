package com.inari.firefly.renderer;

import static com.inari.firefly.renderer.BlendMode.GL11BlendConstants.GL_DST_ALPHA;
import static com.inari.firefly.renderer.BlendMode.GL11BlendConstants.GL_DST_COLOR;
import static com.inari.firefly.renderer.BlendMode.GL11BlendConstants.GL_ONE;
import static com.inari.firefly.renderer.BlendMode.GL11BlendConstants.GL_ONE_MINUS_DST_ALPHA;
import static com.inari.firefly.renderer.BlendMode.GL11BlendConstants.GL_ONE_MINUS_SRC_ALPHA;
import static com.inari.firefly.renderer.BlendMode.GL11BlendConstants.GL_SRC_ALPHA;
import static com.inari.firefly.renderer.BlendMode.GL11BlendConstants.GL_ZERO;

public enum BlendMode {
    /** No blending. Disables blending */
    NONE( -1, -1 ),
    /** Normal alpha blending. GL11: GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA */
    NORMAL_ALPHA( GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA ),
    /** Additive blending ( without alpha ). GL11: GL_ONE, GL_ONE ) */
    ADDITIVE( GL_ONE, GL_ONE ),
    /** Additive blending ( with alpha ). GL11: GL_SRC_ALPHA, GL_ONE */
    ADDITIVE_ALPHA( GL_SRC_ALPHA, GL_ONE ),
    /** Multiplied blending. GL11: GL_DST_COLOR, GL_ZERO */
    MULT( GL_DST_COLOR, GL_DST_COLOR ),
    /** Clears the destination. GL11: GL_ZERO, GL_ZERO */
    CLEAR( GL_ZERO, GL_ZERO ),
    /** The source overlaps the destination. GL11: GL_ONE, GL_ZERO */
    SRC( GL_ONE, GL_ZERO ),
    /** Only the destination. GL11: GL_ZERO, GL_ONE */
    DEST( GL_ZERO, GL_ZERO ),
    SRC_OVER_DEST( GL_ONE, GL_ONE_MINUS_SRC_ALPHA ),
    DEST_OVER_SRC( GL_ONE_MINUS_DST_ALPHA, GL_ONE ),
    SRC_IN_DEST( GL_DST_ALPHA, GL_ZERO ),
    DEST_IN_SRC( GL_ONE, GL_SRC_ALPHA ),
    SRC_OUT_DEST( GL_ONE_MINUS_DST_ALPHA, GL_ZERO ),
    DEST_OUT_SRC( GL_ZERO, GL_ONE_MINUS_SRC_ALPHA ),
    SRC_ATOP_DEST( GL_DST_ALPHA, GL_ONE_MINUS_SRC_ALPHA ),
    DEST_ATOP_SRC( GL_ONE_MINUS_DST_ALPHA, GL_DST_ALPHA ),
    SRC_XOR_DEST( GL_ONE_MINUS_DST_ALPHA, GL_ONE_MINUS_SRC_ALPHA );

    
    public int gl11SourceConst;
    public int gl11DestConst;
    
    private BlendMode( int gl11SourceConst, int gl11DestConst ) {
        this.gl11DestConst = gl11DestConst;
        this.gl11DestConst = gl11DestConst;
    }
    
    public interface GL11BlendConstants {
        int GL_ZERO = 0x0;
        int GL_ONE = 0x1;
        int GL_SRC_COLOR = 0x300;
        int GL_ONE_MINUS_SRC_COLOR = 0x301;
        int GL_SRC_ALPHA = 0x302;
        int GL_ONE_MINUS_SRC_ALPHA = 0x303;
        int GL_DST_ALPHA = 0x304;
        int GL_ONE_MINUS_DST_ALPHA = 0x305;
        int GL_DST_COLOR = 0x306;
        int GL_ONE_MINUS_DST_COLOR = 0x307;
        int GL_SRC_ALPHA_SATURATE = 0x308;
        int GL_CONSTANT_COLOR = 0x8001;
        int GL_ONE_MINUS_CONSTANT_COLOR = 0x8002;
        int GL_CONSTANT_ALPHA = 0x8003;
        int GL_ONE_MINUS_CONSTANT_ALPHA = 0x8004;
    }
}
