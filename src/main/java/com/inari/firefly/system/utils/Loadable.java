package com.inari.firefly.system.utils;

import com.inari.firefly.system.FFContext;

public interface Loadable {
    
    boolean isLoaded();
    
    Disposable load( FFContext context );

}
