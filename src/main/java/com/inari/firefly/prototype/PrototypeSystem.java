package com.inari.firefly.prototype;

import java.util.Iterator;

import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.component.ComponentId;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.component.ComponentSystem;
import com.inari.firefly.system.component.SystemBuilderAdapter;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.component.SystemComponentBuilder;

public class PrototypeSystem extends ComponentSystem<PrototypeSystem> {
    
    public static final FFSystemTypeKey<PrototypeSystem> SYSTEM_KEY = FFSystemTypeKey.create( PrototypeSystem.class );
    
    private static final SystemComponentKey<?>[] SUPPORTED_COMPONENT_TYPES = new SystemComponentKey[] {
        Prototype.TYPE_KEY,
    };

    private final DynArray<Prototype> prototypes;
    
    PrototypeSystem() {
        super( SYSTEM_KEY );
        prototypes = DynArray.create( Prototype.class, 20, 10 );
    }

    @Override
    public final void init( FFContext context ) {
        super.init( context );
    }
    
    @Override
    public final void dispose( FFContext context ) {
        clear();
    }
    
    public final Prototype getPrototype( int prototypeId ) {
        if ( !prototypes.contains( prototypeId ) ) {
            return null;
        }
        
        return prototypes.get( prototypeId );
    }
    
    public final Prototype getPrototype( String prototypeName ) {
        for ( Prototype prototype : prototypes ) {
            if ( prototypeName.equals( prototype.getName() ) ) {
                return prototype;
            }
        }
        
        return null;
    }
    
    public final <T extends Prototype> T getPrototypeAs( int prototypeId, Class<T> subType ) {
        Prototype prototype = getPrototype( prototypeId );
        if ( prototype == null ) {
            return null;
        }
        
        return subType.cast( prototype );
    }

    public final void clear() {
        for ( Prototype prototype : prototypes ) {
            disposeSystemComponent( prototype );
        }
        
        prototypes.clear();
    }
    
    public final int getPrototypeId( String prototypeName ) {
        for ( Prototype prototype : prototypes ) {
            if ( prototypeName.equals( prototype.getName() ) ) {
                return prototype.index();
            }
        }
        
        return -1;
    }
    
    public final void deletePrototype( int prototypeId ) {
        Prototype prototype = prototypes.remove( prototypeId );

        if ( prototype != null ) {
            disposeSystemComponent( prototype );
        }
    }

    public final DynArray<ComponentId> createOne( int prototypeId, AttributeMap attributes ) {
        if ( prototypes.contains( prototypeId ) ) {
            return null;
        }
        
        return getPrototype( prototypeId ).createOne( attributes );
    }

    public final SystemComponentBuilder getPrototypeBuilder( Class<? extends Prototype> componentType ) {
        if ( componentType == null ) {
            throw new IllegalArgumentException( "componentType is needed for SystemComponentBuilder for component: " + Prototype.TYPE_KEY.name() );
        }
        return new PrototypeBuilder( componentType );
    }

    @Override
    public final SystemComponentKey<?>[] supportedComponentTypes() {
        return SUPPORTED_COMPONENT_TYPES;
    }

    @Override
    public final SystemBuilderAdapter<?>[] getSupportedBuilderAdapter() {
        return new SystemBuilderAdapter<?>[] {
            new PrototypeBuilderAdapter()
        };
    }

    private final class PrototypeBuilder extends SystemComponentBuilder {
        
        private PrototypeBuilder( Class<? extends Prototype> componentType ) {
            super( context, componentType );
        }

        @Override
        public final SystemComponentKey<Prototype> systemComponentKey() {
            return Prototype.TYPE_KEY;
        }
        
        @Override
        public final int doBuild( int componentId, Class<?> prototypeType, boolean activate ) {
            Prototype prototype = createSystemComponent( componentId, prototypeType, context );
            prototypes.set( prototype.index(), prototype );
            
            if ( activate ) {
                prototype.load( context );
            }
            
            return prototype.index();
        }
    }
    
    private final class PrototypeBuilderAdapter extends SystemBuilderAdapter<Prototype> {
        private PrototypeBuilderAdapter() {
            super( PrototypeSystem.this, Prototype.TYPE_KEY );
        }
        @Override
        public final Prototype get( int id ) {
            return prototypes.get( id );
        }
        @Override
        public final Iterator<Prototype> getAll() {
            return prototypes.iterator();
        }
        @Override
        public final void delete( int id ) {
            deletePrototype( id );
        }
        @Override
        public final int getId( String name ) {
            return getPrototypeId( name );
        }
        @Override
        public final void activate( int id ) {
            throw new UnsupportedOperationException( componentTypeKey() + " is not activable" );
        }
        @Override
        public final void deactivate( int id ) {
            throw new UnsupportedOperationException( componentTypeKey() + " is not activable" );
        }
        @Override
        public final SystemComponentBuilder createComponentBuilder( Class<? extends Prototype> componentType ) {
            return getPrototypeBuilder( componentType );
        }
    }

}
