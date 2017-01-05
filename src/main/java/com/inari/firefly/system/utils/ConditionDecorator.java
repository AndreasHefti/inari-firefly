package com.inari.firefly.system.utils;

public abstract class ConditionDecorator implements Condition {
    
    protected final Condition decoratedCondition;
    
    protected ConditionDecorator( Condition decoratedCondition ) {
        this.decoratedCondition = decoratedCondition;
    }

}
