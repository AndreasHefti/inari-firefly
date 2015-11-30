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
package com.inari.firefly.system.component;

import java.util.Iterator;

import com.inari.commons.lang.indexed.IndexedObject;
import com.inari.firefly.component.Component;
import com.inari.firefly.component.Component.ComponentKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.component.attr.Attributes;
import com.inari.firefly.component.attr.ComponentAttributeMap;
import com.inari.firefly.system.component.ComponentSystem.BuildType;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;

public abstract class SystemBuilderAdapter<C extends Component> {
    
    protected final ComponentSystem<?> system;
    protected final SystemComponentBuilder componentBuilder;

    public SystemBuilderAdapter( ComponentSystem<?> system, SystemComponentBuilder componentBuilder ) {
        this.system = system;
        this.componentBuilder = componentBuilder;
    }

    public final ComponentSystem<?> getSystem() {
        return system;
    }

    public final SystemComponentBuilder getComponentBuilder() {
        return componentBuilder;
    }

    public void fromAttributes( Attributes attributes, BuildType buildType ) {
        if ( buildType == BuildType.CLEAR_OLD ) {
            system.clear();
        }
        @SuppressWarnings( "unchecked" )
        Class<C> indexedType = (Class<C>) componentTypeKey().indexedType;
        for ( Class<? extends C> subType : attributes.getAllSubTypes( indexedType ) ) {
            
            buildComponents( subType, buildType, attributes );
        }
    }
    
    public void toAttributes( Attributes attributes ) {
        Iterator<C> all = getAll();
        while ( all.hasNext() ) {
            C component = all.next();
            AttributeMap attrs = new ComponentAttributeMap();
            int id = ( component instanceof IndexedObject )? ( (IndexedObject) component ).index() : -1;
            attrs.setComponentKey( new ComponentKey( component.componentType(), id ) );
            component.toAttributes( attrs );
            attributes.add( attrs );
        }
    }

    public abstract SystemComponentKey componentTypeKey();
    
    public abstract C get( int id, Class<? extends C> subtype );
    public abstract void delete( int id, Class<? extends C> subtype );
    public abstract Iterator<C> getAll();

    public void buildComponents(
        Class<? extends C> subType,
        BuildType buildType,
        Attributes attrs
    ) {
        for ( AttributeMap attributes : attrs.getAllOfType( subType ) ) {
            int componentId = attributes.getComponentKey().getId();
            if ( buildType == BuildType.MERGE_ATTRIBUTES ) {
                C component = get( componentId, subType );
                if ( component != null ) {
                    component.toAttributes( componentBuilder.getAttributes() );
                }
            } else if ( buildType == BuildType.OVERWRITE ) {
                delete( componentId, subType );
            }
            componentBuilder
                .setAttributes( attributes )
                .buildAndNext( subType )
                .clear();
        }
    }

}
