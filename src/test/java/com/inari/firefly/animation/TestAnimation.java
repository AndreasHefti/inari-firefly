package com.inari.firefly.animation;

public class TestAnimation extends FloatAnimation {

    protected TestAnimation( int id ) {
        super( id );
    }

    @Override
    public float getValue( long time, int componentId, float currentValue ) {
        finished = true;
        active = false;
        return currentValue;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer( "Animation{" );
        sb.append( "startTime=" ).append( getStartTime() );
        sb.append( ", looping=" ).append( isLooping() );
        sb.append( ", active=" ).append( active );
        sb.append( ", finished=" ).append( finished );
        sb.append( '}' );
        return sb.toString();
    }
}
