package com.inari.firefly.system.utils;

import com.inari.firefly.system.FFContext;

public interface Initiable {
    
    Disposable init( FFContext context );

}
