package com.inari.firefly.system;

public final class DefaultFFTimerImpl extends FFTimer {

    @Override
    public final void tick() {
        if ( lastUpdateTime == 0 ) {
            lastUpdateTime = System.currentTimeMillis();
        } else {
            long currentTime = System.currentTimeMillis();
            time += timeElapsed;
            timeElapsed = currentTime - lastUpdateTime;
            lastUpdateTime = currentTime;
            
        }
    }

}
