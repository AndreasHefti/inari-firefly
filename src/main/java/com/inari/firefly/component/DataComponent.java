package com.inari.firefly.component;

import com.inari.commons.lang.TypedKey;

public abstract class DataComponent implements Component {
    
    public abstract TypedKey<? extends DataComponent> componentKey();

}
