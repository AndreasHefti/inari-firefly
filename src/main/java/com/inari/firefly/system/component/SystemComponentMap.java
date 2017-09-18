package com.inari.firefly.system.component;

import java.util.Iterator;

import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.FFInitException;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.utils.Disposable;

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
    
    private final ComponentSystem<?> system;
    private final Activation activationAdapter;
    private final BuilderAdapter<C> builderAdapter;

    protected SystemComponentMap( 
        ComponentSystem<?> system, 
        SystemComponentKey<C> componentKey, 
        Activation activationAdapter, 
        BuilderAdapter<C> builderAdapter, 
        int cap, int grow 
    ) {
        this.system = system;
        this.componentKey = componentKey;
        map = DynArray.create( componentKey.<C>type(), cap, grow );
        this.activationAdapter = activationAdapter;
        this.builderAdapter = builderAdapter;
    }
    
    public void set( int index, C value ) {
        map.set( index, value );
    }

    public final C get( int id ) {
        if ( !map.contains( id ) ) {
            return null;
        }
        
        return map.get( id );
    }
    
    public final C get( String name ) {
        return map.get( getId( name ) );
    }

    public final <SC extends C> SC getAs( int id, Class<SC> subType ) {
        final C c = get( id );
        if ( c == null ) {
            return null;
        }
        
        return subType.cast( c );
    }
    
    public final <SC extends C> SC getAs( String name, Class<SC> subType ) {
        int id = getId( name );
        if ( id < 0 ) {
            return null;
        }
        
        return getAs( id, subType );
    }
    
    public int getId( String name ) {
        if ( name == null ) {
            return -1;
        }
        
        for ( int i = 0; i < map.capacity(); i++ ) {
            final C component = map.get( i );
            if ( component == null ) {
                continue;
            }
            
            if ( name.equals( component.getName() ) ) {
                return component.index();
            }
        }
        
        return -1;
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
        if ( removed instanceof Disposable ) {
            ( (Disposable) removed ).dispose( system.context );
        }
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
    
    
    
    public final SystemComponentBuilder getBuilder() {
        final Class<C> type = componentKey.type();
        return new ComponentBuilder( type );
    }
    
    public final SystemComponentBuilder getBuilder( Class<? extends C> subType ) {
        return new ComponentBuilder( subType );
    }
    
    private SystemBuilderAdapter<C> componentBuilderAdapter = null;
    public final SystemBuilderAdapter<C>  getBuilderAdapter() {
        if ( componentBuilderAdapter == null ) {
            componentBuilderAdapter = new ComponentBuilderAdapter();
        }
        
        return componentBuilderAdapter;
    }

    
    @SuppressWarnings( "unchecked" )
    public static <C extends  SystemComponent> SystemComponentMap<C> create( 
        ComponentSystem<?> system, 
        SystemComponentKey<C> componentKey 
    ) {
        return new SystemComponentMap<C>( system, componentKey, UNSUPPORTED_ACTIVATION, DUMMY_BUILDER_ADAPTER, 20, 10 );
    }
    
    @SuppressWarnings( "unchecked" )
    public static <C extends  SystemComponent> SystemComponentMap<C> create( 
        ComponentSystem<?> system, 
        SystemComponentKey<C> componentKey, 
        Activation activationAdapter 
    ) {
        return new SystemComponentMap<C>( system, componentKey, activationAdapter, DUMMY_BUILDER_ADAPTER, 20, 10 );
    }
    
    public static <C extends  SystemComponent> SystemComponentMap<C> create( 
        ComponentSystem<?> system, 
        SystemComponentKey<C> componentKey, 
        Activation activationAdapter, 
        BuilderAdapter<C> builderAdapter 
    ) {
        return new SystemComponentMap<C>( system, componentKey, activationAdapter, builderAdapter, 20, 10 );
    }
    
    @SuppressWarnings( "unchecked" )
    public static <C extends  SystemComponent> SystemComponentMap<C> create( 
        ComponentSystem<?> system, 
        SystemComponentKey<C> componentKey, 
        int cap, int grow 
    ) {
        return new SystemComponentMap<C>( system, componentKey, UNSUPPORTED_ACTIVATION, DUMMY_BUILDER_ADAPTER, cap, grow );
    }

    
    @SuppressWarnings( "unchecked" )
    public static <C extends  SystemComponent> SystemComponentMap<C> create( 
        ComponentSystem<?> system, 
        SystemComponentKey<C> componentKey, 
        Activation activationAdapter, 
        int cap, int grow 
    ) {
        return new SystemComponentMap<C>( system, componentKey, activationAdapter, DUMMY_BUILDER_ADAPTER, cap, grow );
    }
    
    public static <C extends  SystemComponent> SystemComponentMap<C> create( 
        ComponentSystem<?> system, 
        SystemComponentKey<C> componentKey, 
        Activation activationAdapter, 
        BuilderAdapter<C> builderAdapter, 
        int cap, int grow 
    ) {
        return new SystemComponentMap<C>( system, componentKey, activationAdapter, builderAdapter, cap, grow );
    }

    
    
    public interface BuilderAdapter<C extends SystemComponent> {
        void finishBuild( final C component );
        void finishDeletion( final C component );
    }

    private final class ComponentBuilder extends SystemComponentBuilder {

        ComponentBuilder( Class<? extends C> subType ) {
            super( system.context, subType );
        }
        
        @Override
        public final SystemComponentKey<?> systemComponentKey() {
            return componentKey;
        }

        @Override
        public final int doBuild( int componentId, Class<?> componentType, boolean activate ) {
            final C component = createSystemComponent( componentId, componentType, system.context );
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
        public ComponentBuilderAdapter() {
            super( system, componentKey );
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
            return SystemComponentMap.this.getBuilder( componentType );
        }
    }

}
