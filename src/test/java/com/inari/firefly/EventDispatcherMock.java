package com.inari.firefly;

import java.util.ArrayList;
import java.util.Collection;

import com.inari.commons.event.TestAspectedEvent;
import com.inari.commons.event.AspectedEventListener;
import com.inari.commons.event.Event;
import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.event.MatchedEvent;
import com.inari.commons.event.MatchedEventListener;

public class EventDispatcherMock implements IEventDispatcher {
    
    private Collection<Event<?>> events = new ArrayList<Event<?>>();

    @Override
    public <L> void register( Class<? extends Event<L>> eventType, L listener ) {
    }

    @Override
    public <L> boolean unregister( Class<? extends Event<L>> eventType, L listener ) {
        return true;
    }

    @Override
    public <L> void notify( Event<L> event ) {
        events.add( event );
    }

    @Override
    public <L extends AspectedEventListener> void notify( TestAspectedEvent<L> event ) {
        events.add( event );
    }

    @Override
    public <L extends MatchedEventListener> void notify( MatchedEvent<L> event ) {
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
