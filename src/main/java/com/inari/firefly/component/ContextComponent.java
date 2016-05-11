package com.inari.firefly.component;

import java.util.Set;

import com.inari.commons.lang.aspect.AspectGroup;
import com.inari.commons.lang.indexed.BaseIndexedObject;
import com.inari.commons.lang.indexed.IIndexedTypeKey;
import com.inari.commons.lang.indexed.IndexedObject;
import com.inari.commons.lang.indexed.IndexedType;
import com.inari.commons.lang.indexed.Indexer;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;

public abstract class ContextComponent extends BaseIndexedObject implements IndexedType, Component {
    
    private final static IIndexedTypeKey TYPE_KEY = new IIndexedTypeKey() {
        
        private AspectGroup aspectGroup = new AspectGroup( "ContextComponent" );
        
        @SuppressWarnings( "unchecked" )
        @Override
        public final Class<? extends ContextComponent> type() {
            return ContextComponent.class;
        }
        
        @Override
        public final Class<?> baseType() {
            return ContextComponent.class;
        }

        @Override
        public final AspectGroup aspectGroup() {
            return aspectGroup;
        }

        @Override
        public final int index() {
            return -1;
        }

        @Override
        public final String name() {
            return "ContextComponent";
        }
    };
    
    public final ComponentId componentId;
    
    protected ContextComponent( String name ) {
        super( -1 );
        setIndex( Indexer.nextObjectIndex( ContextComponent.class ) );
        componentId = new ComponentId( TYPE_KEY, index, name );
    }
    
    @Override
    public final IIndexedTypeKey indexedTypeKey() {
        return componentId.typeKey;
    }
    
    public final Object getName() {
        return componentId.name;
    }
    
    @Override
    public final ComponentId componentId() {
        return componentId;
    }
    
    @Override
    public final Class<? extends IndexedObject> indexedObjectType() {
        return ContextComponent.class;
    }
    
    @Override
    public Set<AttributeKey<?>> attributeKeys() {
        return null;
    }

    @Override
    public void fromAttributes( AttributeMap attributes ) {
    }

    @Override
    public void toAttributes( AttributeMap attributes ) {
    }
    
    

}
