package com.inari.firefly.graphics.tile;

import com.inari.commons.lang.indexed.IIndexedTypeKey;
import com.inari.firefly.graphics.BaseRenderer;
import com.inari.firefly.system.FFContext;

public abstract class TileGridRenderer extends BaseRenderer {
    
    public static final SystemComponentKey<TileGridRenderer> TYPE_KEY = SystemComponentKey.create( TileGridRenderer.class );
    
    protected final TileGridSystem tileGridSystem;

    protected TileGridRenderer( int id, FFContext context ) {
        super( id, context );
        
        tileGridSystem = context.getSystem( TileGridSystem.SYSTEM_KEY );
    }

    @Override
    public final IIndexedTypeKey indexedTypeKey() {
        return TYPE_KEY;
    }

}
