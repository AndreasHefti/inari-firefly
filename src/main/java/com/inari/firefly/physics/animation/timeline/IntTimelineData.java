package com.inari.firefly.physics.animation.timeline;

import com.inari.commons.StringUtils;
import com.inari.commons.config.StringConfigurable;

public final class IntTimelineData implements StringConfigurable {
    
    int value;
    long time;
    
    public IntTimelineData() {
    }

    public IntTimelineData( int value, long time ) {
        this.value = value;
        this.time = time;
    }

    public final int getValue() {
        return value;
    }

    public final void setValue( int value ) {
        this.value = value;
    }

    public final long getTime() {
        return time;
    }

    public final void setTime( long time ) {
        this.time = time;
    }

    @Override
    public final void fromConfigString( String stringValue ) {
        String[] stringValues = StringUtils.splitToArray( stringValue, StringUtils.VALUE_SEPARATOR_STRING );
        this.value = Integer.parseInt( stringValues[ 0 ] );
        this.time = Long.parseLong( stringValues[ 1 ] );
    }

    @Override
    public final String toConfigString() {
        StringBuilder builder = new StringBuilder();
        builder.append( value ).append( StringUtils.VALUE_SEPARATOR ).append( time );
        return builder.toString();
    }

}
