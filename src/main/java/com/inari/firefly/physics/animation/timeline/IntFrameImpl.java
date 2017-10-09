package com.inari.firefly.physics.animation.timeline;

import com.inari.firefly.physics.animation.Frame;

public final class IntFrameImpl implements Frame.IntFrame {
    
    private final int value;
    private final long time;
    

    public IntFrameImpl( int value, long time ) {
        this.value = value;
        this.time = time;
    }

    public final int value() {
        return value;
    }

    public final long intervalTime() {
        return time;
    }

//    public final void fromConfigString( String stringValue ) {
//        String[] stringValues = StringUtils.splitToArray( stringValue, StringUtils.VALUE_SEPARATOR_STRING );
//        this.value = Integer.parseInt( stringValues[ 0 ] );
//        this.time = Long.parseLong( stringValues[ 1 ] );
//    }
//
//    public final String toConfigString() {
//        StringBuilder builder = new StringBuilder();
//        builder.append( value ).append( StringUtils.VALUE_SEPARATOR ).append( time );
//        return builder.toString();
//    }

}
