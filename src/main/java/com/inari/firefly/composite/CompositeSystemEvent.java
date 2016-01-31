package com.inari.firefly.composite;

import com.inari.commons.event.Event;

public final class CompositeSystemEvent extends Event<CompositeSystem> {
    
    public static final EventTypeKey TYPE_KEY = createTypeKey( CompositeSystemEvent.class );
    
    public enum Type {
        LOAD,
        DISPOSE
    }
    
    final Type type;
    final int compositeId;
    final String compositeName;

    public CompositeSystemEvent( Type type, String compositeName ) {
        super( TYPE_KEY );
        this.type = type;
        this.compositeId = -1;
        this.compositeName = compositeName;
    }
    
    public CompositeSystemEvent( Type type, int compositeId ) {
        super( TYPE_KEY );
        this.type = type;
        this.compositeId = compositeId;
        this.compositeName = null;
    }

    @Override
    public final void notify( CompositeSystem listener ) {
        switch ( type ) {
            case LOAD: {
                if ( compositeId >= 0 ) { 
                    listener.loadComposite( compositeId ); 
                } else {
                    listener.loadComposite( compositeName ); 
                }
            }
            case DISPOSE: {
                if ( compositeId >= 0 ) { 
                    listener.disposeComposite( compositeId ); 
                } else {
                    listener.disposeComposite( compositeName ); 
                }
            }
        }
    }

}
