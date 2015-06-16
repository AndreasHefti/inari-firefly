package com.inari.firefly;

import java.util.ArrayList;
import java.util.Collection;

import com.inari.commons.event.IAspectedEvent;
import com.inari.commons.event.IAspectedEventListener;
import com.inari.commons.event.IEvent;
import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.event.IMatchedEvent;
import com.inari.commons.event.IMatchedEventListener;

public class EventDispatcherMock implements IEventDispatcher {
    
    private Collection<IEvent<?>> events = new ArrayList<IEvent<?>>();

    @Override
    public <L> void register( Class<? extends IEvent<L>> eventType, L listener ) {
    }

    @Override
    public <L> boolean unregister( Class<? extends IEvent<L>> eventType, L listener ) {
        return true;
    }

    @Override
    public <L> void notify( IEvent<L> event ) {
        events.add( event );
    }

    @Override
    public <L extends IAspectedEventListener> void notify( IAspectedEvent<L> event ) {
        events.add( event );
    }

    @Override
    public <L extends IMatchedEventListener> void notify( IMatchedEvent<L> event ) {
        events.add( event );
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "TestEventDispatcher [events=" );
        builder.append( events );
        builder.append( "]" );
        return builder.toString();
    }

}
