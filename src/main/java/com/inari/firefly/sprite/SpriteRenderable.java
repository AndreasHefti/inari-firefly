package com.inari.firefly.sprite;

import com.inari.commons.graphics.RGBColor;
import com.inari.firefly.system.LayeredComponent;

public interface SpriteRenderable extends LayeredComponent {
    
    int getSpriteId();
    
    RGBColor getTintColor();
    
    int getOrdering();

}
