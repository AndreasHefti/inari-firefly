package com.inari.firefly.system.component;

import java.util.Iterator;

import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.utils.Disposable;

public class SystemComponentMap<C extends SystemComponent> {
    
    public interface BuilderListener<C extends SystemComponent> {
        void notifyBuild( final C component );
        void notifyActivation( int id );
        void notifyDeactivation( int id ); 
        void notifyDeletion( final C component );
    }
    
    public static class BuilderListenerAdapter<C extends SystemComponent> implements BuilderListener<C> {
        public void notifyBuild( C component ) {}
        public void notifyActivation( int id ) {}
        public void notifyDeactivation( int id ) {}
        public void notifyDeletion( C component ) {}
    }

    protected static final BuilderListener<?> VOID_BUILDER_LSTENER = new BuilderListenerAdapter<>();

    public final SystemComponentKey<C> componentKey;
    public final DynArray<C> map;
    
    private final ComponentSystem<?> system;
    private final BuilderListener<C> builderListener;
    
    @SuppressWarnings( "unchecked" )
    public SystemComponentMap( 
        ComponentSystem<?> system, 
        SystemComponentKey<C> componentKey
    ) {
        this( system, componentKey, (BuilderListener<C>) VOID_BUILDER_LSTENER, 20, 10 );
    }
    
    @SuppressWarnings( "unchecked" )
    public SystemComponentMap( 
        ComponentSystem<?> system, 
        SystemComponentKey<C> componentKey, 
        int cap, int grow 
    ) {
        this( system, componentKey, (BuilderListener<C>) VOID_BUILDER_LSTENER, cap, grow );
    }
    
    public SystemComponentMap( 
        ComponentSystem<?> system, 
        SystemComponentKey<C> componentKey, 
        BuilderListener<C> builderListener
    ) {
        this( system, componentKey, builderListener, 20, 10 );
    }

    public SystemComponentMap( 
        ComponentSystem<?> system, 
        SystemComponentKey<C> componentKey, 
        BuilderListener<C> builderListener, 
        int cap, int grow 
    ) {
        this.system = system;
        this.componentKey = componentKey;
        map = DynArray.create( componentKey.<C>type(), cap, grow );
        this.builderListener = builderListener;
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
        return get( getId( name) );
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

    public void activate( int id ) {
        builderListener.notifyActivation( id );
    }
    
    public final void activate( String name ) {
        builderListener.notifyActivation( getId( name ) );
    }

    public void deactivate( int id ) {
        builderListener.notifyDeactivation( id );
    }
    
    public final void deactivate( String name ) {
        builderListener.notifyDeactivation( getId( name ) );
    }
    
    public C remove( int id ) {
        if ( !map.contains( id ) ) {
            return null;
        }
        
        return map.remove( id );
    }
    
    public final void delete( int id ) {
        final C removed = remove( id );
        if ( removed == null ) {
            return;
        }
        
        builderListener.notifyDeletion( removed );
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
    

    private final class ComponentBuilder extends SystemComponentBuilder {

        ComponentBuilder( Class<? extends C> subType ) {
            super( system.context, subType );
        }
        
        public final SystemComponentKey<?> systemComponentKey() {
            return componentKey;
        }

        public final int doBuild( int componentId, Class<?> componentType, boolean activate ) {
            final C component = createSystemComponent( componentId, componentType );
            final int index = component.index();

            builderListener.notifyBuild( component );
            SystemComponentMap.this.set( index, component );
            
            if ( activate ) {
                SystemComponentMap.this.activate( index );
            }
            
            return index;
        }
    }
    
    private final class ComponentBuilderAdapter extends SystemBuilderAdapter<C> {
        public ComponentBuilderAdapter() {
            super( system, componentKey );
        }
        
        public final C get( int id ) {
            return SystemComponentMap.this.get( id );
        }
        
        public final void delete( int id ) {
            SystemComponentMap.this.delete( id );
        }
        
        public final Iterator<C> getAll() {
            return SystemComponentMap.this.getAll();
        }
        
        public final int getId( String name ) {
            return SystemComponentMap.this.getId( name );
        }
        public final void activate( int id ) {
            SystemComponentMap.this.activate( id );
        }
        public final void deactivate( int id ) {
            SystemComponentMap.this.deactivate( id );
        }

        public final SystemComponentBuilder createComponentBuilder( Class<? extends C> componentType ) {
            return SystemComponentMap.this.getBuilder( componentType );
        }
    }

}
