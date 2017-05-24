package com.inari.firefly.graphics.rendering;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.FFInitException;
import com.inari.firefly.component.attr.AttributeMap;

public final class RenderingChain {

    private boolean modifiable = true;
    final Set<Key> keySet = new HashSet<Key>();
    final DynArray<Element> elements = DynArray.create( Element.class, 20 );
    
    public RenderingChain addElement( Key key ) {
        addElement( key, null );
        return this;
    }
    
    public RenderingChain addElement( Key key, AttributeMap attributes ) {
        if ( !modifiable ) {
            throw new FFInitException( "Unmodifiable RenderingChain" );
        }
        if ( keySet.contains( key ) ) {
            throw new FFInitException( "Renderer Type: " + key + " already exists in the RenderingChain" );
        }
        
        keySet.add( key );
        elements.add( new Element( key, attributes ) );
        return this;
    }
    
    public RenderingChain build() {
        modifiable = false;
        return this;
    }
    
    public Set<Key> getChainKeys() {
        return Collections.unmodifiableSet( keySet );
    }

    public final static class Element {
        
        Renderer renderer;
        
        public final Key key;
        public final AttributeMap attributes;
        
        Element( Key key, AttributeMap attributes ) {
            super();
            this.key = key;
            this.attributes = attributes;
        }

        @Override
        public final int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ( ( key == null ) ? 0 : key.hashCode() );
            return result;
        }

        @Override
        public final boolean equals( Object obj ) {
            if ( this == obj )
                return true;
            if ( obj == null )
                return false;
            if ( getClass() != obj.getClass() )
                return false;
            Element other = (Element) obj;
            if ( key == null ) {
                if ( other.key != null )
                    return false;
            } else if ( !key.equals( other.key ) )
                return false;
            return true;
        }
    }
    
    public final static class Key {
        public final String name;
        public final Class<? extends Renderer> rendererType;
        
        public Key( String name, Class<? extends Renderer> rendererType ) {
            super();
            this.name = name;
            this.rendererType = rendererType;
        }

        @Override
        public final String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append( "Key [name=" );
            builder.append( name );
            builder.append( ", rendererType=" );
            builder.append( rendererType );
            builder.append( "]" );
            return builder.toString();
        }

        @Override
        public final int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
            result = prime * result + ( ( rendererType == null ) ? 0 : rendererType.getSimpleName().hashCode() );
            return result;
        }

        @Override
        public final boolean equals( Object obj ) {
            if ( this == obj )
                return true;
            if ( obj == null )
                return false;
            if ( getClass() != obj.getClass() )
                return false;
            Key other = (Key) obj;
            if ( name == null ) {
                if ( other.name != null )
                    return false;
            } else if ( !name.equals( other.name ) )
                return false;
            if ( rendererType == null ) {
                if ( other.rendererType != null )
                    return false;
            } else if ( !rendererType.getSimpleName().equals( other.rendererType.getSimpleName() ) )
                return false;
            return true;
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "RenderingChain [keySet=" );
        builder.append( keySet );
        builder.append( "]" );
        return builder.toString();
    }

}
