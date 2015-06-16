package com.inari.firefly.entity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.inari.commons.lang.indexed.Indexer;
import com.inari.firefly.component.AttributeKey;
import com.inari.firefly.component.AttributeMap;

public class EParentEntity extends EntityComponent {
    
    public static final int COMPONENT_TYPE = Indexer.getIndexForType( EParentEntity.class, EntityComponent.class );
    
    public static final AttributeKey<Integer> PARENT_ENTITY_ID = new AttributeKey<Integer>( "parentEntityId", Integer.class, EParentEntity.class );
    public static final AttributeKey<Integer> ORDERING = new AttributeKey<Integer>( "ordering", Integer.class, EParentEntity.class );
    public static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        PARENT_ENTITY_ID, 
        ORDERING
    };
    
    private int parentEntityId = -1;
    private int ordering = -1;

    @Override
    public final Class<EParentEntity> getComponentType() {
        return EParentEntity.class;
    }

    public int getParentEntityId() {
        return parentEntityId;
    }

    public void setParentEntityId( int parentEntityId ) {
        this.parentEntityId = parentEntityId;
    }
    
    public int getOrdering() {
        return ordering;
    }

    public void setOrdering( int ordering ) {
        this.ordering = ordering;
    }

    @Override
    public Set<AttributeKey<?>> attributeKeys() {
        return new HashSet<AttributeKey<?>>( Arrays.asList( ATTRIBUTE_KEYS ) );
    }

    @Override
    public void fromAttributeMap( AttributeMap attributes ) {
        parentEntityId = attributes.getValue( PARENT_ENTITY_ID, parentEntityId );
        ordering = attributes.getValue( ORDERING, ordering );
    }

    @Override
    public void toAttributeMap( AttributeMap attributes ) {
        attributes.put( PARENT_ENTITY_ID, parentEntityId );
        attributes.put( ORDERING, ordering );
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "CParentEntity [parentEntityId=" );
        builder.append( parentEntityId );
        builder.append( ", ordering=" );
        builder.append( ordering );
        builder.append( ", indexedType()=" );
        builder.append( indexedType() );
        builder.append( ", index()=" );
        builder.append( index() );
        builder.append( "]" );
        return builder.toString();
    }

}
