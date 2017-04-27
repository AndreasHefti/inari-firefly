package com.inari.firefly.system.external;

import java.util.HashMap;
import java.util.Map;

import com.inari.commons.lang.list.DynArray;

public abstract class FFTimer {
    
    protected long lastUpdateTime, time, timeElapsed;
    
    private final Map<Float, UpdateScheduler> updateSchedulers;
    private final DynArray<UpdateScheduler> schedulers;
    
    protected FFTimer() {
        lastUpdateTime = 0;
        time = 0;
        timeElapsed = 0;
        updateSchedulers = new HashMap<Float, UpdateScheduler>();
        schedulers = DynArray.create( UpdateScheduler.class, 20 );
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
    
    public final void updateSchedulers() {
        for ( int i = 0; i < schedulers.capacity(); i++ ) {
            UpdateScheduler updateScheduler = schedulers.get( i );
            if ( updateScheduler == null ) {
                continue;
            } 
            
            updateScheduler.update();
        }
    }

    public abstract void tick();
    
    public final UpdateScheduler createUpdateScheduler( float resolution ) {
        UpdateScheduler updateScheduler = updateSchedulers.get( resolution );
        if ( updateScheduler == null ) {
            updateScheduler = new UpdateScheduler( resolution );
            updateSchedulers.put( resolution, updateScheduler );
            schedulers.add( updateScheduler );
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
        private boolean needsUpdate;
        
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
        
        final void update() {
            if ( lastUpdateTime - lastUpdate >= delayMillis ) {
                lastUpdate = lastUpdateTime;
                tick++;
                needsUpdate = true;
            } else if ( needsUpdate ) {
                needsUpdate = false;
            }
        }

        public final boolean needsUpdate() {
            return needsUpdate;
        }
        
        public final void reset() {
            lastUpdate = -1;
            tick = 0;
        }

    }

}
