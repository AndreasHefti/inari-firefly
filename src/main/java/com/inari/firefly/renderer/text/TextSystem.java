package com.inari.firefly.renderer.text;

import com.inari.commons.lang.aspect.AspectBitSet;
import com.inari.commons.lang.indexed.IIndexedTypeKey;
import com.inari.commons.lang.indexed.IndexedTypeSet;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.FFInitException;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.entity.event.EntityActivationEvent;
import com.inari.firefly.entity.event.EntityActivationListener;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFSystem;
import com.inari.firefly.system.RenderEvent;
import com.inari.firefly.system.RenderEventListener;

public class TextSystem 
    implements 
        FFSystem,
        EntityActivationListener,
        RenderEventListener {
    
    public static final FFSystemTypeKey<TextSystem> SYSTEM_KEY = FFSystemTypeKey.create( TextSystem.class );

    private EntitySystem entitySystem;
    private TextRenderer renderer;

    private final DynArray<DynArray<DynArray<IndexedTypeSet>>> textPerViewAndLayer;
    
    TextSystem() {
        textPerViewAndLayer = new DynArray<DynArray<DynArray<IndexedTypeSet>>>();
    }
    
    @Override
    public IIndexedTypeKey indexedTypeKey() {
        return SYSTEM_KEY;
    }

    @Override
    public FFSystemTypeKey<?> systemTypeKey() {
        return SYSTEM_KEY;
    }
    
    @Override
    public final void init( FFContext context ) throws FFInitException {
        entitySystem = context.getSystem( EntitySystem.SYSTEM_KEY );
        
        renderer = new TextRenderer();
        renderer.init( context );
        
        context.registerListener( EntityActivationEvent.class, this );
        context.registerListener( RenderEvent.class, this );
    }

    @Override
    public final void dispose( FFContext context ) {
        context.disposeListener( EntityActivationEvent.class, this );
        context.disposeListener( RenderEvent.class, this );
        
        renderer.dispose( context );
        textPerViewAndLayer.clear();
        clear();
    }
    
    public final boolean hasTexts( int viewId ) {
        return textPerViewAndLayer.contains( viewId );
    }
    
    public final void clear() {
        textPerViewAndLayer.clear();
    }

    @Override
    public final boolean match( AspectBitSet aspect ) {
        return aspect.contains( EText.TYPE_KEY );
    }

    @Override
    public void onEntityActivationEvent( EntityActivationEvent event ) {
        IndexedTypeSet components = entitySystem.getComponents( event.entityId );
        ETransform transform = components.get( ETransform.TYPE_KEY );
        int viewId = transform.getViewId();
        int layerId = transform.getLayerId();
        switch ( event.eventType ) {
            case ENTITY_ACTIVATED: {
                DynArray<IndexedTypeSet> renderablesOfView = getTexts( viewId, layerId, true );
                renderablesOfView.add( components );
                break;
            }
            case ENTITY_DEACTIVATED: {
                DynArray<IndexedTypeSet> renderablesOfView = getTexts( viewId, layerId, false );
                renderablesOfView.remove( components );
            }
        }
    }
    
    @Override
    public final void render( RenderEvent event ) {
        renderer.render( event.getViewId(), event.getLayerId() );
    }
    
    public final DynArray<IndexedTypeSet> getTexts( int viewId, int layerId ) {
        return getTexts( viewId, layerId, false );
    }
 
    private final DynArray<IndexedTypeSet> getTexts( int viewId, int layerId, boolean createNew ) {
        DynArray<DynArray<IndexedTypeSet>> textPerLayer = null;
        if ( textPerViewAndLayer.contains( viewId ) ) { 
            textPerLayer = textPerViewAndLayer.get( viewId );
        } else if ( createNew ) {
            textPerLayer = new DynArray<DynArray<IndexedTypeSet>>();
            textPerViewAndLayer.set( viewId, textPerLayer );
        }
        
        if ( textPerLayer == null ) {
            return null;
        }
        
        DynArray<IndexedTypeSet> textOfLayer = null;
        if ( textPerLayer.contains( layerId ) ) { 
            textOfLayer = textPerLayer.get( layerId );
        } else if ( createNew ) {
            textOfLayer = new DynArray<IndexedTypeSet>();
            textPerLayer.set( layerId, textOfLayer );
        }
        
        return textOfLayer;
    }

}
