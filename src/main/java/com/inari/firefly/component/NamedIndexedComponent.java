package com.inari.firefly.component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.inari.commons.lang.indexed.BaseIndexedObject;
import com.inari.commons.lang.indexed.IndexedObject;

public abstract class NamedIndexedComponent extends BaseIndexedObject implements NamedComponent {

    public static final AttributeKey<String> NAME = new AttributeKey<String>( "name", String.class, NamedIndexedComponent.class );
    public static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] {
        NAME
    };
    
    protected String name;
    
    protected NamedIndexedComponent( int id ) {
        super( id );
    }

    @Override
    public Class<? extends Component> getComponentType() {
        return this.getClass();
    }

    @Override
    public Class<? extends IndexedObject> getIndexedObjectType() {
        return this.getClass();
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final void setName( String name ) {
        this.name = name;
    }

    @Override
    public Set<AttributeKey<?>> attributeKeys() {
        return new HashSet<AttributeKey<?>>( Arrays.asList( ATTRIBUTE_KEYS ) );
    }

    @Override
    public void fromAttributeMap( AttributeMap attributes ) {
        name = attributes.getValue( NAME, name );
    }

    @Override
    public void toAttributeMap( AttributeMap attributes ) {
        attributes.put( NAME, name );
    }

}
