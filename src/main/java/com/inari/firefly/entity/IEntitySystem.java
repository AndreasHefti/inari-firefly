/*******************************************************************************
 * Copyright (c) 2015, Andreas Hefti, inarisoft@yahoo.de 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/ 
package com.inari.firefly.entity;

import com.inari.commons.lang.IntIterator;
import com.inari.commons.lang.aspect.Aspect;
import com.inari.commons.lang.functional.Matcher;
import com.inari.commons.lang.indexed.IndexedTypeSet;
import com.inari.firefly.Disposable;
import com.inari.firefly.component.ComponentSystem;
import com.inari.firefly.component.build.ComponentBuilderFactory;
import com.inari.firefly.entity.EntitySystem.EntityBuilder;
import com.inari.firefly.system.FFSystem;

public interface IEntitySystem extends FFSystem, ComponentSystem, ComponentBuilderFactory, Disposable, Iterable<Entity> {
    
    void initEmptyComponents( Class<? extends EntityComponent> type, int number );
    
    void initEmptyEntities( int number );

    EntityBuilder createEntityBuilder();

    boolean isActive( int entity );
    
    void activate( int entity );
    
    void deactivate( int entityId );
    
    void delete( int entityId );
    
    void deleteAll( IntIterator iterator );
    
    public void deleteAll();

    Entity getEntity( int entityId );

    Aspect getEntityAspect( int entityId );

    Iterable<Entity> entities( Aspect aspect );

    Iterable<Entity> entities( Matcher<Entity> matcher );

    <T extends EntityComponent> T getComponent( int entityId, Class<T> componentType );
    
    <T extends EntityComponent> T getComponent( int entityId, int componentId );

    IndexedTypeSet getComponents( int entityId );

}