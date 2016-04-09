package com.inari.firefly.system;

import com.inari.commons.config.StringConfigurable;

public abstract class Condition implements StringConfigurable {
    
    public abstract boolean check( FFContext context );

    @Override
    public void fromConfigString( String stringValue ) {
    }

    @Override
    public String toConfigString() {
        return "";
    }

}
