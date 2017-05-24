package com.inari.firefly.graphics.rendering;

import java.util.Set;

import com.inari.commons.lang.aspect.Aspects;
import com.inari.commons.lang.list.DynArray;
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

    public static final String[] DEFAULT_RRENDERER_ORDER = new String[] {
        "DefaultShapeRenderer",
        "GroupedSpriteRenderer",
        "MultiplePositionSpriteRenderer",
        "SimpleSpriteRenderer",
        "FastTileRenderer",
        "DefaultTextRenderer"
    };
    
    public static final FFSystemTypeKey<RenderingSystem> SYSTEM_KEY = FFSystemTypeKey.create( RenderingSystem.class );
    
    // TODO
    private boolean allowMultipleAcceptance;
    
    private DynArray<Renderer> ordering = DynArray.create( Renderer.class, 20 );
    private DynArray<Renderer> renderer = DynArray.create( Renderer.class, 20 );

    RenderingSystem( FFSystemTypeKey<RenderingSystem> systemKey ) {
        super( systemKey );
    }

    @Override
    public final boolean match( final Aspects aspects ) {
        return true;
    }
    
    

    @Override
    public final void entityActivated( final int entityId, final Aspects aspects ) {
        for ( int i = 0; i < ordering.capacity(); i++ ) {
            Renderer renderer = ordering.get( i );
            if ( renderer == null ) {
                continue;
            }
            
            if ( !renderer.match( aspects ) ) {
                continue;
            }
            
            if ( renderer.accept( entityId, aspects ) && !allowMultipleAcceptance ) {
                return;
            }
        }
    }

    @Override
    public final void entityDeactivated( final int entityId, final Aspects aspects ) {
        for ( int i = 0; i < ordering.capacity(); i++ ) {
            Renderer renderer = ordering.get( i );
            if ( renderer == null ) {
                continue;
            }
            
            if ( !renderer.match( aspects ) ) {
                continue;
            }
            
            renderer.dispose( entityId, aspects );
        }
    }

    @Override
    public final void render( final RenderEvent event ) {
        for ( int i = 0; i < ordering.capacity(); i++ ) {
            Renderer renderer = ordering.get( i );
            if ( renderer == null ) {
                continue;
            }
            
            renderer.render( event );
        }
    }
    
    public final void reorder( final String[] rendererNames ) {
        DynArray<Renderer> _renderer = DynArray.create( Renderer.class, renderer.size() );
        ordering.clear();
        for ( String name : rendererNames ) {
            for ( Renderer r : _renderer ) {
                if ( name.equals( r.getName() ) ) {
                    _renderer.remove( r );
                }
                ordering.add( r );
            }
        }
        
        ordering.addAll( _renderer );
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
