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

import com.inari.commons.lang.aspect.IndexedAspect;
import com.inari.commons.lang.indexed.BaseIndexedObject;
import com.inari.commons.lang.indexed.IndexedTypeSet;

public final class Entity extends BaseIndexedObject {
    
    private final EntitySystem provider;

    Entity( int entityId, EntitySystem provider ) {
        super( entityId );
        this.provider = provider;
    }

    @Override
    public final Class<Entity> indexedObjectType() {
        return Entity.class;
    }
    
    public final int getId() {
        return indexedId;
    }

    public final IndexedAspect getAspect() {
        return provider.getEntityAspect( indexedId );
    }
    
    public final <T extends EntityComponent> T getComponent( Class<T> componentType ) {
        return provider.getComponent( indexedId, componentType );
    }
    
    public final <T extends EntityComponent> T getComponent( int componentTypeId ) {
        return provider.getComponent( indexedId, componentTypeId );
    }

    public final IndexedTypeSet getComponents() {
        return provider.getComponents( indexedId );
    }

    public final boolean isActive() {
        if ( indexedId < 0 ) {
            return false;
        }
        return provider.isActive( index() );
    }
    
    public final boolean setActive( boolean activate ) {
        if ( activate && !isActive() ) {
            provider.activate( index() );
            return isActive();
        } else if ( !activate && isActive() ) {
            provider.deactivate( index() );
            return !isActive();
        }
        
        return false;
    }

    @Override
    public final void dispose() {
        if ( indexedId >= 0 ) {
            provider.delete( index() );
        }
        super.dispose();
    }

    final void setId( int id ) {
        indexedId = id;
    }

    @Override
    public final String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "Entity [indexedId=" ).append( index() ).append( ", isActive=" ).append( isActive() ).append( "]" );
        if ( isActive() ) {
            builder.append( "\n  Components [index=" ).append( provider.getComponents( index() )  ).append( "]" );
        }
        return builder.toString();
    }

}
