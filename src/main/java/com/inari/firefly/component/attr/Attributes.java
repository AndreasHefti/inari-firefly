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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class Attributes implements Iterable<AttributeMap> {
    
    private final Map<ComponentKey, AttributeMap> attributes = new LinkedHashMap<ComponentKey, AttributeMap>();

    @Override
    public final Iterator<AttributeMap> iterator() {
        return attributes.values().iterator();
    }
    
    public final AttributeMap get( ComponentKey key ) {
        return attributes.get( key );
    }
    
    public final void add( AttributeMap attributes ) {
        if ( attributes.getComponentKey() == null ) {
            throw new IllegalArgumentException( "The attributes has no ComponentKey" );
        }
        this.attributes.put( attributes.getComponentKey(), attributes );
    }
    
    public Collection<AttributeMap> getAllOfType( Class<?> componentType ) {
        Collection<AttributeMap> result = new ArrayList<AttributeMap>();
        for ( AttributeMap attrs : attributes.values() ) {
            if ( componentType == attrs.getComponentKey().getType() ) {
                result.add( attrs );
            }
        }
        return result;
    }
    
    @SuppressWarnings( "unchecked" )
    public <C> Set<Class<? extends C>> getAllSubTypes( Class<C> componentType ) {
        Set<Class<? extends C>> result = new HashSet<Class<? extends C>>();
        for ( ComponentKey componentKey : attributes.keySet() ) {
            if ( componentType.isAssignableFrom( componentKey.getType() ) ) {
                result.add( (Class<? extends C>) componentKey.getType() );
            }
        }
        return result;
    }
    
    public final void clear() {
        attributes.clear();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for ( Map.Entry<ComponentKey, AttributeMap> entry : attributes.entrySet() ) {
            builder.append( entry.getKey() );
            builder.append( "::" );
            builder.append( entry.getValue() );
            builder.append( " " );
        }
        return builder.toString();
    }

    
}
