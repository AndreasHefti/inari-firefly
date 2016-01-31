package com.inari.firefly.composite;

import java.util.Iterator;

import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.FFInitException;
import com.inari.firefly.component.Component;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.component.ComponentSystem;
import com.inari.firefly.system.component.SystemBuilderAdapter;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.component.SystemComponentBuilder;

public final class CompositeSystem extends ComponentSystem<CompositeSystem> {
    
    public static final FFSystemTypeKey<CompositeSystem> SYSTEM_KEY = FFSystemTypeKey.create( CompositeSystem.class );
    
    private static final SystemComponentKey<?>[] SUPPORTED_COMPONENT_TYPES = new SystemComponentKey[] {
       Composite.TYPE_KEY
    };
    
    private DynArray<Composite> composites;

    protected CompositeSystem() {
        super( SYSTEM_KEY );
        composites = new DynArray<Composite>();
    }
 
    @Override
    public final void init( FFContext context ) throws FFInitException {
        super.init( context );
        
        context.registerListener( CompositeSystemEvent.class, this );
    }

    @Override
    public final void dispose( FFContext context ) {
        context.disposeListener( CompositeSystemEvent.class, this );
        
        clear();
    }
    
    public final int getCompositeId( final String name ) {
        for ( Composite composite : composites ) {
            if ( name.equals( composite ) ) {
                return composite.getId();
            }
        }
        
        return -1;
    }

    public final Composite getComposite( final String name ) {
        for ( Composite composite : composites ) {
            if ( name.equals( composite ) ) {
                return composite;
            }
        }
        
        return null;
    }
    
    public final Composite getComposite( final int compositeId ) {
        if ( !composites.contains( compositeId ) ) {
            return null;
        }
        
        return composites.get( compositeId );
    }
    
    public final void loadComposite( String name ) {
        Composite composite = getComposite( name );
        if ( composite != null ) {
            composite.load( context );
        }
    }
    
    public final void loadComposite( int compositeId ) {
        if ( !composites.contains( compositeId ) ) {
            return;
        }
        
        composites.get( compositeId ).load( context );
    }
    
    public final void disposeComposite( String name ) {
        Composite composite = getComposite( name );
        if ( composite != null ) {
            composite.dispose( context );
        }
    }
    
    public final void disposeComposite( int compositeId ) {
        if ( !composites.contains( compositeId ) ) {
            return;
        }
        
        composites.get( compositeId ).dispose( context );
    }

    public final void deleteComposite( final int compositeId ) {
        if ( !composites.contains( compositeId ) ) {
            return;
        }
        
        Composite removed = composites.remove( compositeId );
        dispose( removed );
    }

    private final void dispose( final Composite removed ) {
        removed.dispose( context );
        removed.dispose();
    }

    public final void deleteComposite( final String name ) {
        deleteComposite( getCompositeId( name ) );
    }
    
    @Override
    public final void clear() {
        for ( Composite composite : composites ) {
            dispose( composite );
        }
        
        composites.clear();
    }

    public final CompositeBuilder getCompositeBuilder() {
        return new CompositeBuilder();
    }

    @Override
    public final SystemComponentKey<?>[] supportedComponentTypes() {
        return SUPPORTED_COMPONENT_TYPES;
    }
    
    
    @Override
    public final SystemBuilderAdapter<?>[] getSupportedBuilderAdapter() {
        return new SystemBuilderAdapter<?>[] {
            new CompositeBuilderAdapter( this )
        };
    }
    
    public final class CompositeBuilder extends SystemComponentBuilder {
        
        @Override
        public final SystemComponentKey<Composite> systemComponentKey() {
            return Composite.TYPE_KEY;
        }

        @Override
        public final int doBuild( int componentId, Class<?> type, boolean activate ) {
            attributes.put( Component.INSTANCE_TYPE_NAME, type.getName() );
            Composite result = getInstance( context, componentId );
            result.fromAttributes( attributes );
            composites.set( result.index(), result );
            
            postInit( result, context );
            
            if ( activate ) {
                result.load( context );
            }
            
            return result.getId();
        }
    }

    private final class CompositeBuilderAdapter extends SystemBuilderAdapter<Composite> {
        public CompositeBuilderAdapter( CompositeSystem system ) {
            super( system, new CompositeBuilder() );
        }
        @Override
        public final SystemComponentKey<Composite> componentTypeKey() {
            return Composite.TYPE_KEY;
        }
        @Override
        public final Composite getComponent( int id ) {
            return composites.get( id );
        }
        @Override
        public final Iterator<Composite> getAll() {
            return composites.iterator();
        }
        @Override
        public final void deleteComponent( int id ) {
            deleteComposite( id );
        }
        @Override
        public final void deleteComponent( String name ) {
            deleteComposite( name );
        }
        @Override
        public final Composite getComponent( String name ) {
            return getComposite( name );
        }
    }

}
