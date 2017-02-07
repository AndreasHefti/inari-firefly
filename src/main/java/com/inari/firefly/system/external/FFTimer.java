package com.inari.firefly.system.external;

import java.util.HashMap;
import java.util.Map;

public abstract class FFTimer {
    
    protected long lastUpdateTime, time, timeElapsed;
    
    private final Map<Float, UpdateScheduler> updateSchedulers;
    
    protected FFTimer() {
        lastUpdateTime = 0;
        time = 0;
        timeElapsed = 0;
        updateSchedulers = new HashMap<Float, UpdateScheduler>();
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

    public abstract void tick();
    
    public final UpdateScheduler createUpdateScheduler( float resolution ) {
        UpdateScheduler updateScheduler = updateSchedulers.get( resolution );
        if ( updateScheduler == null ) {
            updateScheduler = new UpdateScheduler( resolution );
            updateSchedulers.put( resolution, updateScheduler );
        }
        
        return updateScheduler;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "FFTimer [lastUpdateTime=" );
        builder.append( lastUpdateTime );
        builder.append( ", time=" );
        builder.append( time );
        builder.append( ", timeElapsed=" );
        builder.append( timeElapsed );
        builder.append( "]" );
        return builder.toString();
    }

    public final class UpdateScheduler {
        
        private final float resolution;
        private final long delayMillis;
        private long lastUpdate = -1;
        private long tick = 0;
        
        private UpdateScheduler( float resolution ) {
            this.resolution = resolution;
            delayMillis = (long) ( 1000 / resolution );
        }
        
        public final float getResolution() {
            return resolution;
        }

        public final long getTick() {
            return tick;
        }

        public final boolean needsUpdate() {
            if ( lastUpdate < 0 ) {
                lastUpdate = lastUpdateTime;
                return true;
            }
            
            if ( lastUpdateTime - lastUpdate >= delayMillis ) {
                lastUpdate = lastUpdateTime;
                tick++;
                return true;
            }
            
            return false;
        }
        
        public final void reset() {
            lastUpdate = -1;
            tick = 0;
        }

    }

}
