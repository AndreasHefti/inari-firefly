package com.inari.firefly.graphics.tile;

import com.inari.commons.lang.indexed.IIndexedTypeKey;
import com.inari.firefly.graphics.BaseRenderer;

@Deprecated // will soon be replaced by RenderingSystem
public abstract class TileGridRenderer extends BaseRenderer {
    
    public static final SystemComponentKey<TileGridRenderer> TYPE_KEY = SystemComponentKey.create( TileGridRenderer.class );
    
    protected TileGridSystem tileGridSystem;

    protected TileGridRenderer( int id ) {
        super( id );
    }

    @Override
    public void init() {
        super.init();
        
        tileGridSystem = context.getSystem( TileGridSystem.SYSTEM_KEY );
    }

    @Override
    public final IIndexedTypeKey indexedTypeKey() {
        return TYPE_KEY;
    }

}
