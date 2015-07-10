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
package com.inari.firefly.component;

import com.inari.commons.lang.indexed.IndexedObject;
import com.inari.firefly.component.ComponentSystem.BuildType;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.component.attr.Attributes;
import com.inari.firefly.component.attr.ComponentAttributeMap;
import com.inari.firefly.component.attr.ComponentKey;
import com.inari.firefly.component.build.ComponentBuilder;

public abstract class ComponentBuilderHelper<C extends Component> {
    
    public abstract C get( int id );
    public abstract void delete( int id );

    public void buildComponents(
        Class<C> type,
        BuildType buildType,
        ComponentBuilder<C> builder,
        Attributes attrs
    ) {
        for ( AttributeMap attributes : attrs.getAllOfType( type ) ) {
            int componentId = attributes.getComponentKey().getId();
            if ( buildType == BuildType.MERGE_ATTRIBUTES ) {
                C component = get( componentId );
                if ( component != null ) {
                    component.toAttributes( builder.getAttributes() );
                }
            } else if ( buildType == BuildType.OVERWRITE ) {
                delete( componentId );
            }
            builder
                .setAttributes( attributes )
                .buildAndNext()
                .clear();
        }
    }
    
    public final static <C extends Component> void toAttributes( Attributes attributes, Class<? extends C> componentType, Iterable<C> components ) {
        for ( C component : components ) {
            toAttributes( attributes, componentType, component );
        }
    }
    public final static <C extends Component> void toAttributes( Attributes attributes, Class<? extends C> componentType, C component ) {
        AttributeMap attrs = new ComponentAttributeMap();
        int id = ( component instanceof IndexedObject )? ( (IndexedObject) component ).index() : -1;
        attrs.setComponentKey( new ComponentKey( componentType, id ) );
        component.toAttributes( attrs );
        attributes.add( attrs );
    }
}
