package com.inari.firefly.system.external;

import com.inari.firefly.component.attr.AttributeKey;

public interface TextureData {

    String getResourceName();
    
    <A> A getDynamicAttribute( AttributeKey<A> key );
    
    void setTextureWidth( int width );
    
    void setTextureHeight( int height );
    
}
