package com.inari.firefly.graphics.rendering;

import com.inari.commons.lang.aspect.Aspects;
import com.inari.firefly.system.RenderEvent;
import com.inari.firefly.system.component.SystemComponent;

public abstract class Renderer extends SystemComponent {

    public static final SystemComponentKey<Renderer> TYPE_KEY = SystemComponentKey.create( Renderer.class );
    
    
    protected Renderer( int index ) {
        super( index );
    }
    
    public abstract boolean match( final Aspects aspects );
    
    public abstract boolean accept( int entityId, final Aspects aspects );
    
    public abstract boolean dispose( int entityId, final Aspects aspects );
    
    public abstract void render( RenderEvent event );

}
