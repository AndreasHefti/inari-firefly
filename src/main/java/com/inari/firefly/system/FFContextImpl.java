/*******************************************************************************
 * Copyright (c) 2015, Andreas Hefti, inarisoft@yahoo.de 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/ 
package com.inari.firefly.system;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.inari.commons.lang.TypedKey;
import com.inari.firefly.Disposable;
import com.inari.firefly.component.ComponentSystem;
import com.inari.firefly.component.ComponentSystem.BuildType;
import com.inari.firefly.component.attr.Attributes;

public class FFContextImpl implements FFContext {

    private final Map<TypedKey<?>, Object> properties =  new LinkedHashMap<TypedKey<?>, Object>();
    private final Map<TypedKey<?>, Object> systemComponents = new LinkedHashMap<TypedKey<?>, Object>();
    private final Map<TypedKey<? extends ComponentSystem>, Set<Class<?>>> componentTypes = new LinkedHashMap<TypedKey<? extends ComponentSystem>, Set<Class<?>>>();
    
    public FFContextImpl( InitMap initMap ) {
        create( initMap );
        init( false );
    }
    
    public FFContextImpl( InitMap initMap, boolean skipCheck ) {
        create( initMap );
        init( skipCheck );
    }

    @Override
    public <T> T getComponent( TypedKey<T> key ) {
        return key.type().cast( systemComponents.get( key ) );
    }

    @Override
    public <T> T getProperty( TypedKey<T> key ) {
        return key.type().cast( properties.get( key ) );
    }

    public final void dispose() {
        for ( Object component : systemComponents.values() ) {
            if ( component instanceof Disposable ) {
                ( (Disposable) component ).dispose( this );
            }
        }
        
        systemComponents.clear();
    }
    
    @Override
    public final void fromAttributes( Attributes attributes, BuildType buildType ) {
        for ( TypedKey<? extends ComponentSystem> key : componentTypes.keySet() ) {
            getComponent( key ).fromAttributes( attributes, buildType );
        }
    }

    @Override
    public final void toAttributes( Attributes attributes ) {
        for ( TypedKey<? extends ComponentSystem> key : componentTypes.keySet() ) {
            getComponent( key ).toAttributes( attributes );
        }
    }
    
    @Override
    public final Map<TypedKey<? extends ComponentSystem>, Set<Class<?>>> getComponentTypes() {
        return new HashMap<TypedKey<? extends ComponentSystem>, Set<Class<?>>>( componentTypes );
    }

    @SuppressWarnings( "unchecked" )
    private void create( InitMap componentsToCreate ) {
        for( Map.Entry<TypedKey<?>, Class<?>> componentToCreate : componentsToCreate ) {

            TypedKey<?> key = componentToCreate.getKey();
            Class<?> type = componentToCreate.getValue();
            
            try {
                Constructor<?> defaultConstructor = type.getDeclaredConstructor();
                defaultConstructor.setAccessible( true );
                Object component = defaultConstructor.newInstance();
                if ( component instanceof ComponentSystem ) {
                    componentTypes.put( 
                        (TypedKey<? extends ComponentSystem>) key, 
                        ( (ComponentSystem) component ).supportedComponentTypes() 
                    );
                }
                systemComponents.put( key, component );
            } catch ( Exception e ) {
                throw new FFInitException( "Failed to create instance for component: " + type, e );
            }
            
        }
    }

    private void init( boolean skipCheck ) {
        
        for ( Object component : systemComponents.values() ) {
            if ( component instanceof FFContextInitiable ) {
                ( (FFContextInitiable) component ).init( this );
                continue;
            }
        }

        initDefaultProperties();
        
        if ( skipCheck ) {
            return;
        }
//        checkCompleteness();
    }

    private void initDefaultProperties() {
        properties.put( Properties.ENTITY_MAP_CAPACITY, 1000 );
        properties.put( Properties.ENTITY_COMPONENT_SET_CAPACITY, 20 );
    }

//    private void checkCompleteness() {
//        for ( Field field : FFContext.System.class.getFields() ) {
//            if ( Modifier.isStatic( field.getModifiers() ) && field.getType() == TypedKey.class ) {
//                try {
//                    Object key = field.getComponent( null );
//                    if ( !systemComponents.containsKey( key ) ) {
//                        throw new FFInitException( "Missing Component after init: " + key );
//                    }
//                } catch ( Exception e ) {
//                    throw new FFInitException( "Unknown exception while checkCompleteness: " + field, e );
//                }
//            }
//        }
//    }
    
    public static final class InitMap implements Iterable<Map.Entry<TypedKey<?>, Class<?>>> {
        
        private LinkedHashMap<TypedKey<?>, Class<?>> internalMap = new LinkedHashMap<TypedKey<?>, Class<?>>();

        public <T> InitMap put( TypedKey<T> key, Class<? extends T> type ) {
            if ( internalMap.containsKey( key ) ) {
                throw new FFInitException( "There is already a component for key: " + key + " registered to the context" );
            }
            if ( !key.type().isAssignableFrom( type ) ) {
                throw new FFInitException( "Invalid Component type mapping on FFContext init key: " + key + " within classType: " + type );
            }
            
            internalMap.put( key, type );
            
            return this;
        }

        @Override
        public Iterator<Entry<TypedKey<?>, Class<?>>> iterator() {
            return internalMap.entrySet().iterator();
        }
    }

}
