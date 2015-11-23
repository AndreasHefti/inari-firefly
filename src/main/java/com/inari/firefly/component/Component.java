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

import java.util.Set;

import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;

/** This is the top level definition of all attributed Components in the firefly application.
 * 
 *  Defines a Component with Attributes. The Component has a Set of AttributeKey's that are supported by the Component
 *  and methods the set the Attributes of the Component from an AttributeMap or put all the current Attributes from 
 *  a Component to an AttributeMap.
 *  
 *  Within this Component support all the Attributes of an Component can easily be exposed to or manipulated by other 
 *  systems within a generic but simple way.
 *  
 * @author andreashefti
 *
 */
public interface Component {
    
    /** This is used as a special Component Attribute key for abstract Component types that can have different implementations
     *  for the same Component type. In this case normally only one ComponentBuilder exists for the abstract type of component and
     *  needs a instance type to instantiate the component. This INSTANCE_TYPE_NAME defines the name of the class to instance by
     *  creation within an Attribute.
     *  
     *  Use this if you have an abstract Component implementation, where your component-type and-instance type are different, to
     *  give the ComonentBuilder the instance-type to be able to create an instance form from the type on building
     */
    public static final AttributeKey<String> INSTANCE_TYPE_NAME = new AttributeKey<String>( "instanceTypeName", String.class, Component.class );
    
    Class<? extends Component> componentType();
    
    Set<AttributeKey<?>> attributeKeys();
    
    void fromAttributes( AttributeMap attributes );
    
    void toAttributes( AttributeMap attributes );
    
    // TODO add method to set/get single attribute?
    

    public static final class ComponentKey {
        
        private final Class<?> type;
        private final int id;
        
        
        public ComponentKey( Class<?> type, int id ) {
            super();
            this.type = type;
            this.id = id;
        }

        public Class<?> getType() {
            return type;
        }

        public int getId() {
            return id;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + id;
            result = prime * result + ( ( type == null ) ? 0 : type.hashCode() );
            return result;
        }

        @Override
        public boolean equals( Object obj ) {
            if ( this == obj )
                return true;
            if ( obj == null )
                return false;
            if ( getClass() != obj.getClass() )
                return false;
            ComponentKey other = (ComponentKey) obj;
            if ( id != other.id )
                return false;
            if ( type != other.type ) 
                return false;
            return true;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append( type.getSimpleName() );
            builder.append( "(" );
            builder.append( id );
            builder.append( ")" );
            return builder.toString();
        }

    }
}
