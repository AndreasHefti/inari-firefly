package com.inari.firefly.system.component;

public interface ComponentSystemAdapter<C extends SystemComponent> {
    
    int getId( String name );
    
    C get( int id );
    
    void activate( int id );
    
    void deactivate( int id );
    
    void delete( int id );

}
