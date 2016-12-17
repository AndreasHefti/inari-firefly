package com.inari.firefly.system.utils;

import com.inari.firefly.system.FFContext;

public final class TimedCondition extends Condition {
    
    private final long timeTo;
    private long startTime = -1;

    public TimedCondition( long timeTo ) {
        super();
        this.timeTo = timeTo;
    }

    @Override
    public final void init( FFContext context ) {
        startTime = context.getTime();
    }

    @Override
    public final void dispose( FFContext context ) {
        startTime = -1;
    }

    @Override
    public final boolean check( FFContext context ) {
        if ( startTime == -1 ) {
            return true;
        }
        
        return !( context.getTime() - startTime < timeTo );
    }

}
