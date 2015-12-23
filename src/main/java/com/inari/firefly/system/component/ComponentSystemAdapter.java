package com.inari.firefly.system.component;

public interface ComponentSystemAdapter<C extends SystemComponent> {
    
    C getComponent( int id );
    C getComponent( String name );
    void deleteComponent( int id );
    void deleteComponent( String name );

}
