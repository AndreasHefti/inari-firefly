package com.inari.firefly.system.component;

public interface ComponentSystemAdapter<C extends SystemComponent> extends Activation {
    
    int getId( String name );
    
    C get( int id );
    
    void delete( int id );

}
