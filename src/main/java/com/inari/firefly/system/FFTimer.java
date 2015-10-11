package com.inari.firefly.system;

public abstract class FFTimer {
    
    protected long lastUpdateTime, time, timeElapsed;
    
    protected FFTimer() {
        lastUpdateTime = 0;
        time = 0;
        timeElapsed = 0;
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

    protected abstract void tick();
    
    public final UpdateScheduler createUpdateScheduler( int resolution ) {
        return new UpdateScheduler( resolution );
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
        
        private final int resolution;
        private final long delayMillis;
        private long lastUpdate = -1;
        private long tick = 0;
        
        private UpdateScheduler( int resolution ) {
            this.resolution = resolution;
            delayMillis =  1000 / resolution;
        }
        
        public final int getResolution() {
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
