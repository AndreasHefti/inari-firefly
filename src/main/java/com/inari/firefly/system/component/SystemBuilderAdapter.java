/*******************************************************************************
 * Copyright (c) 2015 - 2016, Andreas Hefti, inarisoft@yahoo.de 
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

import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.component.attr.Attributes;
import com.inari.firefly.component.attr.ComponentAttributeMap;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.component.ComponentSystem.BuildType;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;

public abstract class SystemBuilderAdapter<C extends SystemComponent> implements ComponentSystemAdapter<C> {

    private final ComponentSystem<?> system;
    private final SystemComponentKey<C> componentKey;

    public SystemBuilderAdapter( ComponentSystem<?> system, SystemComponentKey<C> componentKey ) {
        super();
        this.system = system;
        this.componentKey = componentKey;
    }

    public final ComponentSystem<?> getSystem() {
        return system;
    }

    public final SystemComponentKey<C> componentTypeKey() {
        return componentKey;
    }
        
    public void fromAttributes( Attributes attributes, BuildType buildType ) {
        if ( buildType == BuildType.CLEAR_OLD ) {
            getSystem().clearSystem();
        }
        @SuppressWarnings( "unchecked" )
        Class<C> indexedType = (Class<C>) componentTypeKey().indexedType;
        for ( Class<? extends C> subType : attributes.getAllSubTypes( indexedType ) ) {
            
            buildComponents( subType, buildType, attributes );
        }
    }
    
    public void toAttributes( Attributes attributes, FFContext context ) {
        Iterator<C> all = getAll();
        while ( all.hasNext() ) {
            C component = all.next();
            AttributeMap attrs = new ComponentAttributeMap( context );
            attrs.setComponentId( component.componentId() );
            component.toAttributes( attrs );
            attributes.add( attrs );
        }
    }
    
    public void buildComponents(
        Class<? extends C> subType,
        BuildType buildType,
        Attributes attrs
    ) {
        SystemComponentBuilder componentBuilder = createComponentBuilder( subType );
        for ( AttributeMap attributes : attrs.getAllOfType( subType ) ) {
            int componentId = attributes.getComponentId().index();
            if ( buildType == BuildType.MERGE_ATTRIBUTES ) {
                C component = get( componentId );
                if ( component != null ) {
                    component.toAttributes( componentBuilder.getAttributes() );
                }
            } else if ( buildType == BuildType.OVERWRITE ) {
                delete( componentId );
            }
            componentBuilder
                .setAttributes( attributes )
                .buildAndNext()
                .clear();
        }
    }

    public abstract SystemComponentBuilder createComponentBuilder( Class<? extends C> componentType );
    public abstract Iterator<C> getAll();

}
