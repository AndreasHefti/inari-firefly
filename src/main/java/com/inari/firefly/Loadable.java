package com.inari.firefly;

import com.inari.firefly.system.FFContext;

public interface Loadable {
    
    Disposable load( FFContext context );

}
