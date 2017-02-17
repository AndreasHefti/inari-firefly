package com.inari.firefly.graphics.view;

public interface ActiveViewportProvider {
    
    View getNextActiveView( int index );
    
    Layer getNextActiveLayer( int viewId, int index );

}
