package com.inari.firefly.system.external;

import com.inari.commons.geom.Rectangle;
import com.inari.firefly.component.attr.AttributeKey;

public interface SpriteData {
    
    int getTextureId();
    
    Rectangle getTextureRegion();
    
    <A> A getDynamicAttribute( AttributeKey<A> key );
    
    boolean isHorizontalFlip();
    
    boolean isVerticalFlip();

}
