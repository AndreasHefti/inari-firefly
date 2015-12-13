package com.inari.firefly.system.component;

public interface ComponentSystemAdapter<C extends SystemComponent> {
    
    C getComponent( int id );
    <CS extends C> CS getComponent( int id, Class<CS> subType );
    C getComponent( String name );
    <CS extends C> CS getComponent( String name, Class<CS> subType );
    void deleteComponent( int id );
    void deleteComponent( int id, Class<? extends C> subType );
    void deleteComponent( String name );

}
