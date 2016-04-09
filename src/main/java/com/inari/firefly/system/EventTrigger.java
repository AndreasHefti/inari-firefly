package com.inari.firefly.system;

import com.inari.commons.config.StringConfigurable;

public abstract class EventTrigger implements StringConfigurable {
    
    protected FFContext context;
    protected final Condition condition;
    protected int componentId;
    
    protected EventTrigger( Condition condition ) {
        this.condition = condition;
    }
    
    public void register( FFContext context, int componentId ) {
        this.context = context;
        this.componentId = componentId;
    }
    
    public abstract void dispose( FFContext context );

}
