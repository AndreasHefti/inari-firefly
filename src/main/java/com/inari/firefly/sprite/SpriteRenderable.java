package com.inari.firefly.sprite;

import com.inari.commons.graphics.RGBColor;
import com.inari.firefly.system.LayerAware;

public interface SpriteRenderable extends LayerAware {
    
    int getSpriteId();
    
    RGBColor getTintColor();
    
    int getOrdering();

}
