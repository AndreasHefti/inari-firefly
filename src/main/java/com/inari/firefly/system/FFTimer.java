package com.inari.firefly.system;

public abstract class FFTimer {
    
    protected long lastUpdateTime, time, timeElapsed, update;
    
    protected FFTimer() {
        lastUpdateTime = 0;
        time = 0;
        timeElapsed = 0;
        update = 0;
    }

    public final long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public final long getTime() {
        return time;
    }

    public final long getTimeElapsed() {
        return timeElapsed;
    }

    public final long getUpdate() {
        return update;
    }
    
    protected abstract void tick();

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "FFTimer [lastUpdateTime=" );
        builder.append( lastUpdateTime );
        builder.append( ", time=" );
        builder.append( time );
        builder.append( ", timeElapsed=" );
        builder.append( timeElapsed );
        builder.append( ", update=" );
        builder.append( update );
        builder.append( "]" );
        return builder.toString();
    }

}
