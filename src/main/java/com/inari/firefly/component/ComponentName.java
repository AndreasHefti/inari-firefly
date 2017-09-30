package com.inari.firefly.component;

import com.inari.commons.lang.indexed.IIndexedTypeKey;
import com.inari.firefly.system.component.IComponentName;

public class ComponentName implements IComponentName {
    
    public final IIndexedTypeKey typeKey;
    public final String name;
    
    public ComponentName( IIndexedTypeKey typeKey, String name ) {
        this.typeKey = typeKey;
        this.name = name;
    }

    public final String name() {
        return name;
    }

    public final IIndexedTypeKey typeKey() {
        return typeKey;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
        result = prime * result + ( ( typeKey == null ) ? 0 : typeKey.hashCode() );
        return result;
    }

    public boolean equals( Object obj ) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        ComponentName other = (ComponentName) obj;
        if ( name == null ) {
            if ( other.name != null )
                return false;
        } else if ( !name.equals( other.name ) )
            return false;
        if ( typeKey == null ) {
            if ( other.typeKey != null )
                return false;
        } else if ( !typeKey.equals( other.typeKey ) )
            return false;
        return true;
    }

}
