package com.inari.firefly.graphics.text;

import java.util.Iterator;

import com.inari.commons.lang.aspect.AspectBitSet;
import com.inari.commons.lang.indexed.IndexedTypeSet;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.FFInitException;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntityActivationEvent;
import com.inari.firefly.entity.EntityActivationListener;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.RenderEvent;
import com.inari.firefly.system.component.ComponentSystem;
import com.inari.firefly.system.component.SystemBuilderAdapter;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.component.SystemComponentBuilder;

public class TextSystem
    extends 
        ComponentSystem<TextSystem>
    implements 
        EntityActivationListener {
    
    public static final String DEFAULT_TEXT_RENDERER_NAME = "DEFAULT_TEXT_RENDERER";
    public static final FFSystemTypeKey<TextSystem> SYSTEM_KEY = FFSystemTypeKey.create( TextSystem.class );
    private static final SystemComponentKey<?>[] SUPPORTED_COMPONENT_TYPES = new SystemComponentKey[] {
        TextRenderer.TYPE_KEY,
    };

    private EntitySystem entitySystem;

    private final DynArray<TextRenderer> renderer;
    private final DynArray<DynArray<DynArray<IndexedTypeSet>>> textPerViewAndLayer;
    
    TextSystem() {
        super( SYSTEM_KEY );
        renderer = new DynArray<TextRenderer>();
        textPerViewAndLayer = new DynArray<DynArray<DynArray<IndexedTypeSet>>>();
    }
    
    @Override
    public final void init( FFContext context ) throws FFInitException {
        super.init( context );
        entitySystem = context.getSystem( EntitySystem.SYSTEM_KEY );
        
        // build and register default text renderer
        getRendererBuilder()
            .set( TextRenderer.NAME, DEFAULT_TEXT_RENDERER_NAME )
            .build( DefaultTextRenderer.class );
        
        context.registerListener( EntityActivationEvent.class, this );
        
    }

    @Override
    public final void dispose( FFContext context ) {
        context.disposeListener( EntityActivationEvent.class, this );
        
        for ( TextRenderer r : renderer ) {
            r.dispose();
        }
        renderer.clear();
        
        textPerViewAndLayer.clear();
        clear();
    }
    
    public final TextRenderer getRenderer( int id ) {
        if ( renderer.contains( id ) ) {
            return renderer.get( id );
        }
        
        return null;
    }

    public final int getRendererId( String name ) {
        for ( TextRenderer r : renderer ) {
            if ( name.equals( r.getName() ) ) {
                return r.getId();
            }
        }
        
        return -1;
    }

    public final void deleteRenderer( int id ) {
        TextRenderer removed = renderer.remove( id );
        if ( removed != null ) {
            context.disposeListener( RenderEvent.class, removed );
            removed.dispose();
        }
    }
    
    public final TextRendererBuilder getRendererBuilder() {
        return new TextRendererBuilder();
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
        
        DynArray<IndexedTypeSet> textsOfLayer = null;
        if ( textPerLayer.contains( layerId ) ) { 
            textsOfLayer = textPerLayer.get( layerId );
        } else if ( createNew ) {
            textsOfLayer = new DynArray<IndexedTypeSet>();
            textPerLayer.set( layerId, textsOfLayer );
        }
        
        return textsOfLayer;
    }

    @Override
    public final SystemComponentKey<?>[] supportedComponentTypes() {
        return SUPPORTED_COMPONENT_TYPES;
    }

    @Override
    public final SystemBuilderAdapter<?>[] getSupportedBuilderAdapter() {
        return new SystemBuilderAdapter<?>[] {
            new TextRendererBuilderAdapter( this ),
        };
    }
    
    
    public final class TextRendererBuilder extends SystemComponentBuilder {
        
        private TextRendererBuilder() {
            super( context );
        }
        
        @Override
        public final SystemComponentKey<TextRenderer> systemComponentKey() {
            return TextRenderer.TYPE_KEY;
        }

        @Override
        public int doBuild( int componentId, Class<?> componentType, boolean activate ) {
            TextRenderer component = createSystemComponent( componentId, componentType, context );
            renderer.set( component.index(), component );
            return component.getId();
        }
    }

    private final class TextRendererBuilderAdapter extends SystemBuilderAdapter<TextRenderer> {
        public TextRendererBuilderAdapter( TextSystem system ) {
            super( system, new TextRendererBuilder() );
        }
        @Override
        public final SystemComponentKey<TextRenderer> componentTypeKey() {
            return TextRenderer.TYPE_KEY;
        }
        @Override
        public final TextRenderer getComponent( int id ) {
            return getRenderer( id );
        }
        @Override
        public final void deleteComponent( int id ) {
            deleteRenderer( id );
        }
        @Override
        public final Iterator<TextRenderer> getAll() {
            return renderer.iterator();
        }
        
        @Override
        public final void deleteComponent( String name ) {
            deleteRenderer( getRendererId( name ) );
        }
        @Override
        public final TextRenderer getComponent( String name ) {
            return getRenderer( getRendererId( name ) );
        }
    }

}
