package com.inari.firefly;

import com.inari.firefly.system.external.FFTimer;

public class TestTimer extends FFTimer {

    @Override
    public void tick() {
        lastUpdateTime++;
        time++;
        timeElapsed++;
    }

    public void setTime( long time ) {
        lastUpdateTime = time;
        this.time = time;
        timeElapsed = time;
    }
    
    

}
