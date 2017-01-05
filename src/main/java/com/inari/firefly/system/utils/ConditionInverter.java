package com.inari.firefly.system.utils;

import com.inari.firefly.system.FFContext;

public final class ConditionInverter extends ConditionDecorator {

    protected ConditionInverter( Condition decoratedCondition ) {
        super( decoratedCondition );
    }

    @Override
    public final boolean check( FFContext context ) {
        return !decoratedCondition.check( context );
    }

}
