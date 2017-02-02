package com.inari.firefly.system.utils;

import com.inari.firefly.system.FFContext;

public interface Triggerer {
    
    void trigger( FFContext context, int componentId );

}
