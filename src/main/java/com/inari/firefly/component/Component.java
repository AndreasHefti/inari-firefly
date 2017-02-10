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
 *  systems or services within a generic but simple way.
 *  Potential services may be: 
 *   - A Component Builder framework that builds Components within the general component attribute interface
 *   - A persistence service that gets all attribute values of a living component system, serialize the values and store it somehow to load and rebuild the system and components later
 *   - A IDE that manages the components, shows the attributes as input fields in an inspector
 *  
 *  A Component can have be type- and/or object indexed. Therefore a ComponentKey is defined that combines this two key
 *  components into one key that identifies a Component instance
 *  
 * @author andreashefti
 *
 */
public interface Component {
    
    /** Use this to get the ComponentId of this Component that identifies the Component by type and instance */
    ComponentId componentId();
    
    /** Use this to get a Set of all AttributeKey's supported by this Component instance. 
     * 
     *  For all this AttributeKey's can be set attribute values within a AttributeMap to the Component 
     *  or get from the Component instance to a AttributeMap.
     *  
     *  This defines the external component information interface for external services. A external service can use this to verify 
     *  which component attributes are available/supported by a specified component instance.
     *  
     * @return a Set of all AttributeKey's supported by this Component instance 
     */
    Set<AttributeKey<?>> attributeKeys();
    
    /** Use this to set values for the Components attributes within a AttributeMap.
     *  All attributes that are defines within the map are set to the Component instance.
     *  Component attributes that are not in the map or attributes of other Component types then this type are ignored.
     *  
     * @param attributes AttributeMap contains all attribute values to set to the Component instance
     */
    void fromAttributes( AttributeMap attributes );
    
    /** Use this to get all Component attribute values form this Component instance within a AttributeMap
     * 
     * @param attributes AttributeMap where all the attribute values are put into
     */
    void toAttributes( AttributeMap attributes );

}
