package com.inari.firefly.physics.animation;

public class TestIntAnimation extends IntAnimation {
    
    private int value = 0;

    protected TestIntAnimation( int id ) {
        super( id );
    }

    @Override
    public int getInitValue() {
        return 0;
    }

    @Override
    public int getValue( int component, int currentValue ) {
        return value;
    }

    @Override
    public void update() {
        value++;
    }

}
