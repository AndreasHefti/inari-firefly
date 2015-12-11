package com.inari.firefly.entity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;

public class EEntity extends EntityComponent {
    
    public static final EntityComponentTypeKey TYPE_KEY = createTypeKey( EEntity.class );
    
    public static final AttributeKey<String> ENTITY_NAME = new AttributeKey<String>( "entityName", String.class, EEntity.class );
    public static final AttributeKey<int[]> CONTROLLER_IDS = new AttributeKey<int[]>( "controllerIds", int[].class, EEntity.class );
    public static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        ENTITY_NAME,
        CONTROLLER_IDS
    };
    
    private String entityName;
    private int[] controllerIds;
    
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

    public final int[] getControllerIds() {
        return controllerIds;
    }

    public final void setControllerIds( int[] controllerIds ) {
        this.controllerIds = controllerIds;
    }
    
    public final boolean controlledBy( int controllerId ) {
        if ( controllerIds == null ) {
            return false;
        }
        
        for ( int i = 0; i < controllerIds.length; i++ ) {
            if ( controllerIds[ i ] == controllerId ) {
                return true;
            } 
        }
        
        return false;
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
        controllerIds = attributes.getValue( CONTROLLER_IDS, controllerIds );
    }

    @Override
    public void toAttributes( AttributeMap attributes ) {
        attributes.put( ENTITY_NAME, entityName );
        attributes.put( CONTROLLER_IDS, controllerIds );
    }
    
}
