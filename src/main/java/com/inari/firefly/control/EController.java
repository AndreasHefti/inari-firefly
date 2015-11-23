package com.inari.firefly.control;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.inari.firefly.component.Component;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.entity.EntityComponent;

public class EController extends EntityComponent {
    
    public static final EntityComponentTypeKey TYPE_KEY = createTypeKey( EController.class );
    
    public static final AttributeKey<int[]> CONTROLLER_IDS = new AttributeKey<int[]>( "controllerIds", int[].class, EController.class );
    public static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        CONTROLLER_IDS
    };
    
    private int[] controllerIds;
    
    public EController() {
        super( TYPE_KEY );
        resetAttributes();
    }

    @Override
    public final void resetAttributes() {
        controllerIds = null;
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
    public final Class<? extends Component> componentType() {
        return EController.class;
    }

    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        return new HashSet<AttributeKey<?>>( Arrays.asList( ATTRIBUTE_KEYS ) );
    }

    @Override
    public void fromAttributes( AttributeMap attributes ) {
        controllerIds = attributes.getValue( CONTROLLER_IDS, controllerIds );
    }

    @Override
    public void toAttributes( AttributeMap attributes ) {
        attributes.put( CONTROLLER_IDS, controllerIds );
    }

}
