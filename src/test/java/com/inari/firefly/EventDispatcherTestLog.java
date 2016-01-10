package com.inari.firefly;

import java.util.ArrayList;
import java.util.Collection;

import com.inari.commons.event.Event;
import com.inari.commons.event.IEventLog;

public class EventDispatcherTestLog implements IEventLog {
    
    private Collection<String> events = new ArrayList<String>();

    @Override
    public final void log( Event<?> event ) {
        events.add( event.toString() );
    }
    
    public final void clearLog() {
        events.clear();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "EventLog [events=" );
        builder.append( events );
        builder.append( "]" );
        return builder.toString();
    }
}
