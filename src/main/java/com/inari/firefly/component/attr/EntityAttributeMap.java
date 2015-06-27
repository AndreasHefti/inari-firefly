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
package com.inari.firefly.component.attr;

import java.util.HashSet;
import java.util.Set;

import com.inari.firefly.component.Component;
import com.inari.firefly.entity.Entity;

public class EntityAttributeMap extends ComponentAttributeMap {
    
    @Override
    public final void setComponentKey( ComponentKey typeKey ) {
        if ( typeKey.getType() != Entity.class ) {
            throw new IllegalArgumentException( "The ComponentKey has not the expected type: " + Entity.class.getName() );
        }
        super.setComponentKey( typeKey );
    }

    public Set<Class<? extends Component>> getEntityComponentTypes() {
        Set<Class<? extends Component>> componentTypes = new HashSet<Class<? extends Component>>();
        for ( AttributeKey<?> attrKey : attributes.keySet() ) {
            componentTypes.add( attrKey.componentType );
        }
        return componentTypes;
    }

}
