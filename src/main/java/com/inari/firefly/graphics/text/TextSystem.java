package com.inari.firefly.graphics.text;

import java.util.Iterator;
import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.commons.lang.aspect.Aspects;
import com.inari.commons.lang.indexed.IndexedTypeSet;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.FFInitException;
import com.inari.firefly.entity.EntityActivationEvent;
import com.inari.firefly.entity.EntityActivationListener;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.graphics.ETransform;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.RenderEvent;
import com.inari.firefly.system.component.ComponentSystem;
import com.inari.firefly.system.component.SystemBuilderAdapter;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.component.SystemComponentBuilder;

@Deprecated // will soon be replaced by RenderingSystem
public class TextSystem
    extends 
        ComponentSystem<TextSystem>
    implements 
        EntityActivationListener {
    
    public static final String DEFAULT_TEXT_RENDERER_NAME = "DEFAULT_TEXT_RENDERER";
    public static final FFSystemTypeKey<TextSystem> SYSTEM_KEY = FFSystemTypeKey.create( TextSystem.class );
    private static final Set<SystemComponentKey<?>> SUPPORTED_COMPONENT_TYPES = JavaUtils.<SystemComponentKey<?>>unmodifiableSet( 
        TextRenderer.TYPE_KEY
    );

    private EntitySystem entitySystem;

    private final DynArray<TextRenderer> renderer;
    private final DynArray<DynArray<DynArray<IndexedTypeSet>>> textPerViewAndLayer;
    
    TextSystem() {
        super( SYSTEM_KEY );
        renderer = DynArray.create( TextRenderer.class, 10, 10 );
        textPerViewAndLayer = DynArray.createTyped( DynArray.class, 10, 10 );
    }
    
    @Override
    public final void init( FFContext context ) throws FFInitException {
        super.init( context );
        entitySystem = context.getSystem( EntitySystem.SYSTEM_KEY );
        
        // build and register default text renderer
        getRendererBuilder( DefaultTextRenderer.class )
            .set( TextRenderer.NAME, DEFAULT_TEXT_RENDERER_NAME )
            .build();
        
        context.registerListener( EntityActivationEvent.TYPE_KEY, this );
        
    }

    @Override
    public final void dispose( FFContext context ) {
        context.disposeListener( EntityActivationEvent.TYPE_KEY, this );
        
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
                return r.index();
            }
        }
        
        return -1;
    }

    public final void deleteRenderer( int id ) {
        TextRenderer removed = renderer.remove( id );
        if ( removed != null ) {
            context.disposeListener( RenderEvent.TYPE_KEY, removed );
            removed.dispose();
        }
    }
    
    public final SystemComponentBuilder getRendererBuilder( Class<? extends TextRenderer> componentType ) {
        if ( componentType == null ) {
            throw new IllegalArgumentException( "componentType is needed for SystemComponentBuilder for component: " + TextRenderer.TYPE_KEY.name() );
        }
        return new TextRendererBuilder( componentType );
    }
    
    public final boolean hasTexts( int viewId ) {
        return textPerViewAndLayer.contains( viewId );
    }
    
    public final void clear() {
        textPerViewAndLayer.clear();
    }

    @Override
    public final boolean match( Aspects aspects ) {
        return aspects.contains( EText.TYPE_KEY );
    }
    
    public final void entityActivated( int entityId, final Aspects aspects ) {
        final IndexedTypeSet components = entitySystem.getComponents( entityId );
        final ETransform transform = components.get( ETransform.TYPE_KEY );
        getTexts( transform.getViewId(), transform.getLayerId(), true ).add( components );
    }
    
    public final void entityDeactivated( int entityId, final Aspects aspects ) {
        final IndexedTypeSet components = entitySystem.getComponents( entityId );
        final ETransform transform = components.get( ETransform.TYPE_KEY );
        getTexts( transform.getViewId(), transform.getLayerId(), false ).remove( components );
    }
    
    public final DynArray<IndexedTypeSet> getTexts( int viewId, int layerId ) {
        return getTexts( viewId, layerId, false );
    }
 
    private final DynArray<IndexedTypeSet> getTexts( int viewId, int layerId, boolean createNew ) {
        DynArray<DynArray<IndexedTypeSet>> textPerLayer = null;
        if ( textPerViewAndLayer.contains( viewId ) ) { 
            textPerLayer = textPerViewAndLayer.get( viewId );
        } else if ( createNew ) {
            textPerLayer = DynArray.createTyped( DynArray.class, 10, 10 );
            textPerViewAndLayer.set( viewId, textPerLayer );
        }
        
        if ( textPerLayer == null ) {
            return null;
        }
        
        DynArray<IndexedTypeSet> textsOfLayer = null;
        if ( textPerLayer.contains( layerId ) ) { 
            textsOfLayer = textPerLayer.get( layerId );
        } else if ( createNew ) {
            textsOfLayer = DynArray.create( IndexedTypeSet.class, 40, 10 );
            textPerLayer.set( layerId, textsOfLayer );
        }
        
        return textsOfLayer;
    }

    public final Set<SystemComponentKey<?>> supportedComponentTypes() {
        return SUPPORTED_COMPONENT_TYPES;
    }

    @Override
    public final Set<SystemBuilderAdapter<?>> getSupportedBuilderAdapter() {
        return JavaUtils.<SystemBuilderAdapter<?>>unmodifiableSet( 
            new TextRendererBuilderAdapter()
        );
    }
    
    private final class TextRendererBuilder extends SystemComponentBuilder {
        
        private TextRendererBuilder( Class<? extends TextRenderer> componentType ) {
            super( context, componentType );
        }
        
        @Override
        public final SystemComponentKey<TextRenderer> systemComponentKey() {
            return TextRenderer.TYPE_KEY;
        }

        @Override
        public int doBuild( int componentId, Class<?> componentType, boolean activate ) {
            TextRenderer component = createSystemComponent( componentId, componentType, context );
            renderer.set( component.index(), component );
            return component.index();
        }
    }

    private final class TextRendererBuilderAdapter extends SystemBuilderAdapter<TextRenderer> {
        private TextRendererBuilderAdapter() {
            super( TextSystem.this, TextRenderer.TYPE_KEY );
        }
        @Override
        public final TextRenderer get( int id ) {
            return getRenderer( id );
        }
        @Override
        public final void delete( int id ) {
            deleteRenderer( id );
        }
        @Override
        public final Iterator<TextRenderer> getAll() {
            return renderer.iterator();
        }
        @Override
        public final int getId( String name ) {
            return getRendererId( name );
        }
        @Override
        public final void activate( int id ) {
            throw new UnsupportedOperationException( componentTypeKey() + " is not activable" );
        }
        @Override
        public final void deactivate( int id ) {
            throw new UnsupportedOperationException( componentTypeKey() + " is not activable" );
        }
        @Override
        public final SystemComponentBuilder createComponentBuilder( Class<? extends TextRenderer> componentType ) {
            return getRendererBuilder( componentType );
        }
        
    }

}
