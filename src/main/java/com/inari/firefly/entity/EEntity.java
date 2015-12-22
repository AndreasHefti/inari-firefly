package com.inari.firefly.entity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.inari.commons.lang.list.IntBag;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;

public class EEntity extends EntityComponent {
    
    public static final EntityComponentTypeKey<EEntity> TYPE_KEY = EntityComponentTypeKey.create( EEntity.class );
    
    public static final AttributeKey<String> ENTITY_NAME = new AttributeKey<String>( "entityName", String.class, EEntity.class );
    public static final AttributeKey<IntBag> CONTROLLER_IDS = new AttributeKey<IntBag>( "controllerIds", IntBag.class, EEntity.class );
    public static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        ENTITY_NAME,
        CONTROLLER_IDS
    };
    
    private String entityName;
    private IntBag controllerIds;
    
    public EEntity() {
        super( TYPE_KEY );
        resetAttributes();
    }

    @Override
    public final void resetAttributes() {
        controllerIds = null;
        entityName = null;
    }

    public final String getEntityName() {
        return entityName;
    }

    public final void setEntityName( String entityName ) {
        this.entityName = entityName;
    }

    public final IntBag getControllerIds() {
        return controllerIds;
    }

    public final void setControllerIds( IntBag controllerIds ) {
        this.controllerIds = controllerIds;
        if ( controllerIds != null ) {
            controllerIds.trim();
        }
    }
    
    public final boolean controlledBy( int controllerId ) {
        if ( controllerIds == null ) {
            return false;
        }
        
        return controllerIds.contains( controllerId );
    }

    @Override
    public final Class<EEntity> componentType() {
        return EEntity.class;
    }

    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        return new HashSet<AttributeKey<?>>( Arrays.asList( ATTRIBUTE_KEYS ) );
    }

    @Override
    public void fromAttributes( AttributeMap attributes ) {
        entityName = attributes.getValue( ENTITY_NAME, entityName );
        setControllerIds( attributes.getValue( CONTROLLER_IDS, controllerIds ) );
    }

    @Override
    public void toAttributes( AttributeMap attributes ) {
        attributes.put( ENTITY_NAME, entityName );
        attributes.put( CONTROLLER_IDS, controllerIds );
    }
    
}
