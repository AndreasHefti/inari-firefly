package com.inari.firefly.graphics.view;

import com.inari.commons.event.Event;

public final class ViewChangeEvent extends Event<ViewChangeListener> {
    
    public static final EventTypeKey TYPE_KEY = createTypeKey( ViewChangeEvent.class );
    
    public static enum Type {
        POSITION,
        ORIENTATION,
        SIZE
    }
    
    public View view;
    public Type eventType;

    public ViewChangeEvent( View view, Type eventType ) {
        super( TYPE_KEY );
        this.eventType = eventType;
        this.view = view;
    }

    public final View getView() {
        return view;
    }

    public final void setView( View view ) {
        this.view = view;
    }

    public final Type getEventType() {
        return eventType;
    }

    public final void setEventType( Type eventType ) {
        this.eventType = eventType;
    }

    @Override
    protected final void notify( ViewChangeListener listener ) {
        listener.onViewChange( this );
    }

    @Override
    public final String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "ViewChangeEvent [view=" );
        builder.append( view );
        builder.append( ", eventType=" );
        builder.append( eventType );
        builder.append( "]" );
        return builder.toString();
    }

}
