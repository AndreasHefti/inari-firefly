package com.inari.firefly.component;

import java.util.Set;

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
    
    Class<? extends Component> getComponentType();
    
    Set<AttributeKey<?>> attributeKeys();
    
    void fromAttributeMap( AttributeMap attributes );
    
    void toAttributeMap( AttributeMap attributes );

}
