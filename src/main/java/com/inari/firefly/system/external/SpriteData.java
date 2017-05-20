package com.inari.firefly.system.external;

import com.inari.commons.geom.Rectangle;

public interface SpriteData {
    
    int getTextureId();
    
    Rectangle getTextureRegion();
    
    boolean isHorizontalFlip();
    
    boolean isVerticalFlip();

}
