package com.inari.firefly.entity;

import com.inari.commons.lang.IntIterator;
import com.inari.commons.lang.aspect.Aspect;
import com.inari.commons.lang.functional.Matcher;
import com.inari.commons.lang.indexed.IndexedTypeSet;
import com.inari.firefly.Disposable;
import com.inari.firefly.component.build.ComponentBuilderFactory;
import com.inari.firefly.entity.EntitySystem.EntityBuilder;

public interface IEntitySystem extends ComponentBuilderFactory, Disposable, Iterable<Entity> {
    
    void initEmptyComponents( Class<? extends EntityComponent> type, int number );
    
    void initEmptyEntities( int number );

    EntityBuilder createEntityBuilder();

    boolean isActive( int entity );
    
    void activate( int entity );
    
    void deactivate( int entityId );
    
    void restore( int entityId );
    
    void restoreAll( IntIterator iterator );
    
    public void restoreAll();

    Entity getEntity( int entityId );

    Aspect getEntityAspect( int entityId );

    Iterable<Entity> entities( Aspect aspect );

    Iterable<Entity> entities( Matcher<Entity> matcher );

    <T extends EntityComponent> T getComponent( int entityId, Class<T> componentType );
    
    <T extends EntityComponent> T getComponent( int entityId, int componentId );

    IndexedTypeSet getComponents( int entityId );

}