package com.inari.firefly.graphics.rendering;

import java.util.Set;

import com.inari.commons.lang.aspect.Aspects;
import com.inari.firefly.entity.EntityActivationListener;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.RenderEvent;
import com.inari.firefly.system.RenderEventListener;
import com.inari.firefly.system.component.ComponentSystem;
import com.inari.firefly.system.component.SystemBuilderAdapter;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;

public final class RenderingSystem 
    extends ComponentSystem<RenderingSystem> 
    implements RenderEventListener, EntityActivationListener {
    
    public static final FFSystemTypeKey<RenderingSystem> SYSTEM_KEY = FFSystemTypeKey.create( RenderingSystem.class );
    
    // TODO
    private boolean allowMultipleAcceptance;

    RenderingSystem( FFSystemTypeKey<RenderingSystem> systemKey ) {
        super( systemKey );
    }

    @Override
    public final boolean match( Aspects arg0 ) {
        return true;
    }

    

    @Override
    public final void entityActivated( int entityId, Aspects aspects ) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public final void entityDeactivated( int entityId, Aspects aspects ) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public final void render( RenderEvent event ) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public final Set<SystemComponentKey<?>> supportedComponentTypes() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public final Set<SystemBuilderAdapter<?>> getSupportedBuilderAdapter() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public final void dispose( FFContext context ) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public final void clear() {
        // TODO Auto-generated method stub
        
    }
    
    

}
