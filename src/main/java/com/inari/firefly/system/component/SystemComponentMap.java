package com.inari.firefly.system.component;

import java.util.Iterator;

import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.FFInitException;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;

public class SystemComponentMap<C extends SystemComponent> {

    static final Activation UNSUPPORTED_ACTIVATION = new Activation() {
        @Override public final void activate( int id ) { throw new UnsupportedOperationException(); }
        @Override public void deactivate( int id ) { throw new UnsupportedOperationException(); }
    };
    
    @SuppressWarnings( "rawtypes" )
    static final BuilderAdapter DUMMY_BUILDER_ADAPTER = new BuilderAdapter() {
        @Override public final void finishBuild( final SystemComponent component ) {}
        @Override public final void finishDeletion( final SystemComponent component ) {}
    };
    
    public final SystemComponentKey<C> componentKey;
    public final DynArray<C> map;
    
    private final Activation activationAdapter;
    private final BuilderAdapter<C> builderAdapter;

    protected SystemComponentMap( SystemComponentKey<C> componentKey, Activation activationAdapter, BuilderAdapter<C> builderAdapter, int cap ) {
        this.componentKey = componentKey;
        map = DynArray.create( componentKey.<C>type(), cap );
        this.activationAdapter = activationAdapter;
        this.builderAdapter = builderAdapter;
    }
    
    public void set( int index, C value ) {
        map.set( index, value );
    }

    public final C get( int id ) {
        return map.get( id );
    }
    
    public final C get( String name ) {
        return get( getId( name ) );
    }

    public final <SC extends C> SC getAs( int id, Class<SC> subType ) {
        C component = get( id );
        if ( component == null ) {
            return null;
        }
        
        return subType.cast( component );
    }
    
    public final <SC extends C> SC getAs( String name, Class<SC> subType ) {
        return subType.cast( getId( name ) );
    }
    
    public int getId( String name ) {
        for ( int i = 0; i < map.capacity(); i++ ) {
            final C component = map.get( i );
            if ( component == null ) {
                continue;
            }
            
            return component.index();
        }
        
        throw new FFInitException( "No Component with name: " + name + " found" );
    }
    
    public final Iterator<C> getAll() {
        return map.iterator();
    }

    public final void activate( int id ) {
        activationAdapter.activate( id );
    }
    
    public final void activate( String name ) {
        activationAdapter.activate( getId( name ) );
    }

    public final void deactivate( int id ) {
        activationAdapter.deactivate( id );
    }
    
    public final void deactivate( String name ) {
        activationAdapter.deactivate( getId( name ) );
    }
    
    public C remove( int id ) {
        if ( !map.contains( id ) ) {
            throw new FFInitException( "Invalid id/index: " + id );
        }
        
        return map.remove( id );
    }
    
    public final void delete( int id ) {
        if ( !map.contains( id ) ) {
            throw new FFInitException( "Invalid id/index: " + id );
        }
        
        C removed = remove( id );
        builderAdapter.finishDeletion( removed );
        removed.dispose();
    }
    
    public final void delete( String name ) {
        delete( getId( name ) );
    }
    
    public void clear() {
        for ( int i = 0; i < map.capacity(); i++ ) {
            final C component = map.get( i );
            if ( component == null ) {
                continue;
            }
            
            deactivate( i );
            delete( i );
        }
        
        map.clear();
    }
    
    
    
    public final SystemComponentBuilder getBuilder( final FFContext context ) {
        final Class<C> type = componentKey.type();
        return new ComponentBuilder( context, type );
    }
    
    public final SystemComponentBuilder getBuilder( final FFContext context, Class<? extends C> subType ) {
        return new ComponentBuilder( context, subType );
    }
    
    private SystemBuilderAdapter<C> componentBuilderAdapter = null;
    public final SystemBuilderAdapter<C>  getBuilderAdapter( final FFContext context, final ComponentSystem<?> system ) {
        if ( componentBuilderAdapter == null ) {
            componentBuilderAdapter = new ComponentBuilderAdapter( context, system );
        }
        
        return componentBuilderAdapter;
    }

    
    @SuppressWarnings( "unchecked" )
    public static <C extends  SystemComponent> SystemComponentMap<C> create( SystemComponentKey<C> componentKey ) {
        return new SystemComponentMap<C>( componentKey, UNSUPPORTED_ACTIVATION, DUMMY_BUILDER_ADAPTER, 20 );
    }
    
    @SuppressWarnings( "unchecked" )
    public static <C extends  SystemComponent> SystemComponentMap<C> create( SystemComponentKey<C> componentKey, Activation activationAdapter ) {
        return new SystemComponentMap<C>( componentKey, activationAdapter, DUMMY_BUILDER_ADAPTER, 20 );
    }
    
    public static <C extends  SystemComponent> SystemComponentMap<C> create( SystemComponentKey<C> componentKey, Activation activationAdapter, BuilderAdapter<C> builderAdapter ) {
        return new SystemComponentMap<C>( componentKey, activationAdapter, builderAdapter, 20 );
    }
    
    @SuppressWarnings( "unchecked" )
    public static <C extends  SystemComponent> SystemComponentMap<C> create( SystemComponentKey<C> componentKey, int cap ) {
        return new SystemComponentMap<C>( componentKey, UNSUPPORTED_ACTIVATION, DUMMY_BUILDER_ADAPTER, cap );
    }
    
    @SuppressWarnings( "unchecked" )
    public static <C extends  SystemComponent> SystemComponentMap<C> create( SystemComponentKey<C> componentKey, Activation activationAdapter, int cap ) {
        return new SystemComponentMap<C>( componentKey, activationAdapter, DUMMY_BUILDER_ADAPTER, cap );
    }
    
    public static <C extends  SystemComponent> SystemComponentMap<C> create( SystemComponentKey<C> componentKey, Activation activationAdapter, BuilderAdapter<C> builderAdapter, int cap ) {
        return new SystemComponentMap<C>( componentKey, activationAdapter, builderAdapter, cap );
    }

    
    
    public interface BuilderAdapter<C extends SystemComponent> {
        void finishBuild( final C component );
        void finishDeletion( final C component );
    }

    private final class ComponentBuilder extends SystemComponentBuilder {

        private final FFContext context;
        ComponentBuilder( final FFContext context, Class<? extends C> subType ) {
            super( context, subType );
            this.context = context;
        }
        
        @Override
        public final SystemComponentKey<?> systemComponentKey() {
            return componentKey;
        }

        @Override
        public final int doBuild( int componentId, Class<?> componentType, boolean activate ) {
            final C component = createSystemComponent( componentId, componentType, context );
            final int index = component.index();

            builderAdapter.finishBuild( component );
            SystemComponentMap.this.set( index, component );
            
            if ( activate ) {
                activationAdapter.activate( index );
            }
            
            return index;
        }
    }
    
    private final class ComponentBuilderAdapter extends SystemBuilderAdapter<C> {
        private final FFContext context;
        public ComponentBuilderAdapter( final FFContext context, final ComponentSystem<?> system ) {
            super( system, componentKey );
            this.context = context;
        }
        
        @Override
        public final C get( int id ) {
            return SystemComponentMap.this.get( id );
        }
        
        @Override
        public final void delete( int id ) {
            SystemComponentMap.this.delete( id );
        }
        
        @Override
        public final Iterator<C> getAll() {
            return SystemComponentMap.this.getAll();
        }
        
        @Override
        public final int getId( String name ) {
            return SystemComponentMap.this.getId( name );
        }
        @Override
        public final void activate( int id ) {
            SystemComponentMap.this.activationAdapter.activate( id );
        }
        @Override
        public final void deactivate( int id ) {
            SystemComponentMap.this.activationAdapter.deactivate( id );
        }

        @Override
        public final SystemComponentBuilder createComponentBuilder( Class<? extends C> componentType ) {
            return SystemComponentMap.this.getBuilder( context, componentType );
        }
    }

}
