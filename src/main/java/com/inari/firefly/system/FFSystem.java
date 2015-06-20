package com.inari.firefly.system;

import com.inari.firefly.Disposable;
import com.inari.firefly.FFContext;

public interface FFSystem extends Disposable {
    
    void init( FFContext context );

}
