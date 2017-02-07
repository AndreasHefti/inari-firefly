package com.inari.firefly.graphics.view;

import com.inari.firefly.control.Controller;

public abstract class ViewController extends Controller implements ViewEventListener {
    
    protected ViewSystem viewSystem;
    
    protected ViewController( int id ) {
        super( id );
    }

    @Override
    protected void init() {
        super.init();
        
        viewSystem = context.getSystem( ViewSystem.SYSTEM_KEY );
        context.registerListener( ViewEvent.TYPE_KEY, this );
    }

    @Override
    public final void dispose() {
        context.disposeListener( ViewEvent.TYPE_KEY, this );
        
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
    public final void update() {
        if ( componentIds.size() <= 0 ) {
            return;
        }
        
        for ( int i = 0; i < componentIds.length(); i++ ) {
            if ( componentIds.isEmpty( i ) ) {
                continue;
            }
            int viewId = componentIds.get( i );
            update( viewSystem.getView( viewId ) );
        }
    }
    
    
    public abstract void update( final View view );

}
