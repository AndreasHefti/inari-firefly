package com.inari.firefly.system.utils;

import com.inari.firefly.system.FFContext;

public interface Condition {
    
    public abstract boolean check( final FFContext context );

}
