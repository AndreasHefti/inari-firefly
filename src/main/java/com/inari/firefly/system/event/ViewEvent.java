package com.inari.firefly.system.event;

import com.inari.commons.event.Event;
import com.inari.firefly.system.View;

public final class ViewEvent extends Event<ViewEventListener> {
    
    public static enum Type {
        VIEW_CREATED,
        VIEW_ACTIVATED,
        VIEW_DISABLED,
        VIEW_DELETED
    }
    
    public final View view;
    public final Type type;

    public ViewEvent( View view, Type type ) {
        this.type = type;
        this.view = view;
    }

    @Override
    public final void notify( ViewEventListener listener ) {
        listener.onViewEvent( this );
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "ViewEvent [type=" );
        builder.append( type );
        builder.append( ", view=" );
        builder.append( view.indexedId() );
        builder.append( "]" );
        return builder.toString();
    }

    

}
