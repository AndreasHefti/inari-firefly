package com.inari.firefly.system.external;

import com.inari.commons.lang.functional.IntFunction;

public interface TextureData {

    String getResourceName();
    
    boolean isMipmap();
    
    IntFunction getColorConverter();
    
    void setTextureWidth( int width );
    
    void setTextureHeight( int height );
    
}
