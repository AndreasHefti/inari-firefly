package com.inari.firefly.graphics.text;

import com.inari.commons.graphics.RGBColor;
import com.inari.commons.lang.indexed.IndexedTypeKey;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.graphics.BaseRenderer;
import com.inari.firefly.graphics.BlendMode;
import com.inari.firefly.graphics.SpriteRenderable;

@Deprecated // will soon be replaced by RenderingSystem
public abstract class TextRenderer extends BaseRenderer {
    
    public static final SystemComponentKey<TextRenderer> TYPE_KEY = SystemComponentKey.create( TextRenderer.class );
    
    protected TextSystem textSystem;
    protected AssetSystem assetSystem;
    
    TextRenderer( int id ) {
        super( id );
    }

    @Override
    public void init() {
        super.init();
        
        textSystem = context.getSystem( TextSystem.SYSTEM_KEY );
        assetSystem = context.getSystem( AssetSystem.SYSTEM_KEY );
    }

    @Override
    public final IndexedTypeKey indexedTypeKey() {
        return TYPE_KEY;
    }
    
    final TextRenderable textRenderable = new TextRenderable();
    final class TextRenderable implements SpriteRenderable {
        
        int spriteId;
        RGBColor tintColor; 
        BlendMode blendMode;
        int shaderId;

        @Override public final int getSpriteId() { return spriteId; }
        @Override public final RGBColor getTintColor() { return tintColor; }
        @Override public final BlendMode getBlendMode() { return blendMode; }
        @Override public final int getShaderId() { return shaderId; }
    }

}
