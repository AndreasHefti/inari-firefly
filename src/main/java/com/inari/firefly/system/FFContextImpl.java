package com.inari.firefly.system;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.inari.commons.lang.TypedKey;
import com.inari.firefly.Disposable;
import com.inari.firefly.FFContext;

public class FFContextImpl implements FFContext {

    private final Map<TypedKey<?>, Object> systemComponents = new LinkedHashMap<TypedKey<?>, Object>();
    
    public FFContextImpl( InitMap initMap ) {
        create( initMap );
        init( false );
    }
    
    public FFContextImpl( InitMap initMap, boolean skipCheck ) {
        create( initMap );
        init( skipCheck );
    }

    @Override
    public <T> T get( TypedKey<T> key ) {
        return key.type().cast( systemComponents.get( key ) );
    }
    
    @Override
    public final void dispose() {
        for ( Object component : systemComponents.values() ) {
            if ( component instanceof Disposable ) {
                ( (Disposable) component ).dispose( this );
            }
        }
        
        systemComponents.clear();
    }

    private void create( InitMap componentsToCreate ) {
        for( Map.Entry<TypedKey<?>, Class<?>> componentToCreate : componentsToCreate ) {

            TypedKey<?> key = componentToCreate.getKey();
            Class<?> type = componentToCreate.getValue();
            
            try {
                Object component = type.newInstance();
                systemComponents.put( key, component );
            } catch ( Exception e ) {
                throw new FFInitException( "Failed to create instance for component: " + type, e );
            }
            
        }
    }

    private void init( boolean skipCheck ) {
        
        for ( Object component : systemComponents.values() ) {
            if ( component instanceof FFSystem ) {
                ( (FFSystem) component ).init( this );
                continue;
            }
        }
        
        if ( skipCheck ) {
            return;
        }
        checkCompleteness();
        
//        for( Map.Entry<TypedKey<?>, Class<? extends FFSystem>> componentToInitialise : componentsToInitialise ) {
//
//            TypedKey<?> key = componentToInitialise.getKey();
//            Class<? extends FFSystem> type = componentToInitialise.getValue();
//            
//            Constructor<?> constructor = findSuitableConstructor( type );
//            Object instance = instantiateComponent( type, constructor );
//            
//            if ( instance == null ) {
//                throw new FFInitException( "Failed to create instance for component: " + type + ", Constructor " + constructor + " As no valid signature (emtpy or with IFFContext argument) " );
//            }
//            
//            if ( key == FFContext.System.LOWER_SYSTEM_FACADE && constructor.getParameterCount() == 0 ) {
//                ILowerSystemFacade lowerSystemFacade = (ILowerSystemFacade) instance;
//                IEventDispatcher eventDispatcher = get( FFContext.System.EVENT_DISPATCHER );
//                eventDispatcher.register( ViewEvent.class, lowerSystemFacade );
//                eventDispatcher.register( ViewEvent.class, lowerSystemFacade );
//            }
//
//            systemComponents.put( key, instance );
//        }
//        
        
    }
    
//    private Object instantiateComponent( Class<?> type, Constructor<?> constructor ) {
//        Class<?>[] parameterTypes = constructor.getParameterTypes();
//        Object instance = null;
//        try {
//            if ( parameterTypes.length == 0 ) {
//                instance = constructor.newInstance();
//            } else if ( parameterTypes.length == 1 && parameterTypes[ 0 ] == FFContext.class ) {
//                instance = constructor.newInstance( this );
//            } 
//        } catch ( Exception e ) {
//            throw new FFInitException( "Failed to create instance for component: " + type, e );
//        }
//        return instance;
//    }

//    private Constructor<?> findSuitableConstructor( Class<?> type ) {
//        Constructor<?> constructor = null;
//        try {
//            constructor = type.getConstructor();
//        } catch ( Exception e ) {
//            try {
//                constructor = type.getConstructor( FFContext.class );
//            } catch ( Exception ee ) {}
//        }
//        if ( constructor == null ) {
//            throw new FFInitException( "No suitable Constructor (signature: emtpy or with IFFContext argument) found for type: " + type );
//        }
//        return constructor;
//    }
    
    private void checkCompleteness() {
        for ( Field field : FFContext.System.class.getFields() ) {
            if ( Modifier.isStatic( field.getModifiers() ) && field.getType() == TypedKey.class ) {
                try {
                    Object key = field.get( null );
                    if ( !systemComponents.containsKey( key ) ) {
                        throw new FFInitException( "Missing Component after init: " + key );
                    }
                } catch ( Exception e ) {
                    throw new FFInitException( "Unknown exception while checkCompleteness: " + field, e );
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "FFContextImpl [\n" );
        for ( Map.Entry<TypedKey<?>, Object> entry : systemComponents.entrySet() ) {
            builder.append( "  [" ).append( entry.getKey() ).append( "] : " ).append( entry.getValue() ).append( "\n" );
        }
        builder.append( "]" );
        return builder.toString();
    }
    
    public static final class InitMap implements Iterable<Map.Entry<TypedKey<?>, Class<?>>> {
        
        private LinkedHashMap<TypedKey<?>, Class<?>> internalMap = new LinkedHashMap<TypedKey<?>, Class<?>>();

        public Class<?> put( TypedKey<?> key, Class<?> type ) {
            if ( !key.type().isAssignableFrom( type ) ) {
                throw new FFInitException( "Invalid Component type mapping on FFContext init key: " + key + " within classType: " + type );
            }
            
            return internalMap.put( key, type );
        }

        @Override
        public Iterator<Entry<TypedKey<?>, Class<?>>> iterator() {
            return internalMap.entrySet().iterator();
        }

    }
    
}
