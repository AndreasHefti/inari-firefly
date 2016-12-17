package com.inari.firefly.system.utils;

import com.inari.firefly.system.FFContext;

public final class FalseCondition extends Condition {

    @Override
    public final boolean check( FFContext context ) {
        return false;
    }

}
