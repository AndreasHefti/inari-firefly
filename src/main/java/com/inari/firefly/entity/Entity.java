package com.inari.firefly.entity;

import com.inari.commons.lang.aspect.Aspect;
import com.inari.commons.lang.indexed.BaseIndexedObject;
import com.inari.commons.lang.indexed.IndexedTypeSet;

public final class Entity extends BaseIndexedObject {
    
    private final IEntitySystem provider;

    Entity( int entityId, IEntitySystem provider ) {
        super( entityId );
        this.provider = provider;
    }

    @Override
    public final Class<Entity> getIndexedObjectType() {
        return Entity.class;
    }
    
    public final int getId() {
        return indexedId;
    }

    public final Aspect getAspect() {
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
        return provider.isActive( indexedId() );
    }
    
    public final boolean setActive( boolean activate ) {
        if ( activate && !isActive() ) {
            provider.activate( indexedId() );
            return isActive();
        } else if ( !activate && isActive() ) {
            provider.deactivate( indexedId() );
            return !isActive();
        }
        
        return false;
    }
    
    public final void restore() {
        provider.restore( indexedId() );
    } 

    @Override
    public final String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "Entity [indexedId=" ).append( indexedId() ).append( ", isActive=" ).append( isActive() ).append( "]" );
        if ( isActive() ) {
            builder.append( "\n  Components [index=" ).append( provider.getComponents( indexedId() )  ).append( "]" );
        }
        return builder.toString();
    }

}
