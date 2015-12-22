package com.inari.firefly.component;

import com.inari.commons.lang.TypedKey;
import com.inari.commons.lang.indexed.IIndexedTypeKey;

public abstract class ContextComponent implements Component {
    
    public final ComponentKey componentKey;
    
    protected ContextComponent() {
        final Class<? extends ContextComponent> componentType = this.getClass();
        IIndexedTypeKey typeKey = new IIndexedTypeKey() {
            @SuppressWarnings( "unchecked" )
            @Override
            public Class<? extends ContextComponent> type() {
                return componentType;
            }
            @Override
            public int typeIndex() {
                return -1;
            }
        };
        componentKey = new ComponentKey( typeKey, -1 );
    }
    
    @Override
    public final ComponentKey componentKey() {
        return componentKey;
    }
    
    public final Class<? extends ContextComponent> componentType() {
        return this.getClass();
    }

    public abstract TypedKey<? extends ContextComponent> contextKey();

}
