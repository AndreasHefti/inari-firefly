package com.inari.firefly.graphics.view;

import com.inari.firefly.control.Controller;
import com.inari.firefly.system.external.FFTimer;

public abstract class ViewController extends Controller implements ViewEventListener {
    
    protected ViewSystem viewSystem;
    
    protected ViewController( int id ) {
        super( id );
    }

    @Override
    public final void init() {
        super.init();
        
        viewSystem = context.getSystem( ViewSystem.SYSTEM_KEY );
        context.registerListener( ViewEvent.class, this );
    }



    @Override
    public final void dispose() {
        context.disposeListener( ViewEvent.class, this );
        
        super.dispose();
    }

    @Override
    public final void onViewEvent( ViewEvent event ) {
        switch ( event.eventType ) {
        case VIEW_ACTIVATED: {
            if ( event.view.controlledBy( index ) ) {
                componentIds.add( event.view.index() );
            }
            break;
        } 
        case VIEW_DISPOSED: {
            componentIds.remove( event.view.index() );
            break;
        }
        default: {}
    }
    }

    @Override
    public final void update( final FFTimer timer ) {
        if ( componentIds.size() <= 0 ) {
            return;
        }
        
        for ( int i = 0; i < componentIds.length(); i++ ) {
            if ( componentIds.isEmpty( i ) ) {
                continue;
            }
            int viewId = componentIds.get( i );
            update( timer, viewSystem.getView( viewId ) );
        }
    }
    
    
    public abstract void update( final FFTimer timer, final View view );

}
