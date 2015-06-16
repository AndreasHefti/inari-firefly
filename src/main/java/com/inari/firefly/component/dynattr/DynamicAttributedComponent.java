package com.inari.firefly.component.dynattr;

import com.inari.firefly.component.AttributeKey;
import com.inari.firefly.component.Component;

public interface DynamicAttributedComponent extends Component {
    
    <A> void setDynamicAttribute( AttributeKey<A> key, A value );
    
    <A> A getDynamicAttribute( AttributeKey<A> key );
    
    public boolean hasDynamicAttributes();
    
    @Override
    public Class<? extends DynamicAttributedComponent> getComponentType();
}
