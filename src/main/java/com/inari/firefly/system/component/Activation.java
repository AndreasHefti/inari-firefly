package com.inari.firefly.system.component;

public interface Activation {
    void activate( int id );
    void deactivate( int id ); 
    boolean isActive( int id );
}
