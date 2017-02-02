package com.inari.firefly.system.utils;

import com.inari.firefly.system.FFContext;

public abstract class Trigger {
    
    protected FFContext context;
    protected final Condition condition;
    protected int componentId;
    protected Triggerer triggerer;
    
    protected Trigger( Condition condition ) {
        this.condition = condition;
    }
    
    public void register( FFContext context, int componentId, Triggerer triggerer ) {
        this.context = context;
        this.componentId = componentId;
        this.triggerer = triggerer;
    }
    
    public abstract void dispose( FFContext context );

}
