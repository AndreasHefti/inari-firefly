package com.inari.firefly;

import com.inari.firefly.system.FFTimer;

public class TestTimer extends FFTimer {

    @Override
    public void tick() {
        lastUpdateTime++;
        time++;
        timeElapsed++;
        update++;
    }

    public void setTime( long time ) {
        lastUpdateTime = time;
        this.time = time;
        timeElapsed = time;
        update = time;
    }
    
    

}
