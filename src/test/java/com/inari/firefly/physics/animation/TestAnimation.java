package com.inari.firefly.physics.animation;

public class TestAnimation extends FloatAnimation {

    protected TestAnimation( int id ) {
        super( id );
    }
    
    @Override
    public final float getInitValue() {
        return -1;
    }

    @Override
    public float getValue( int componentId, float currentValue ) {
        finished = true;
        return currentValue;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer( "Animation{" );
        sb.append( "startTime=" ).append( getStartTime() );
        sb.append( ", looping=" ).append( isLooping() );
        sb.append( ", finished=" ).append( finished );
        sb.append( '}' );
        return sb.toString();
    }

    @Override
    public void update() {
        // TODO Auto-generated method stub
    }
}
