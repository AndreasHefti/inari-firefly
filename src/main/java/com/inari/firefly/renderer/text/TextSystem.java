package com.inari.firefly.renderer.text;

import java.util.Iterator;

import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.AspectBitSet;
import com.inari.commons.lang.indexed.IndexedTypeSet;
import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.asset.AssetNameKey;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.entity.event.EntityActivationEvent;
import com.inari.firefly.entity.event.EntityActivationListener;
import com.inari.firefly.renderer.TextureAsset;
import com.inari.firefly.renderer.sprite.SpriteAsset;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFInitException;
import com.inari.firefly.system.RenderEvent;
import com.inari.firefly.system.RenderEventListener;
import com.inari.firefly.system.component.ComponentSystem;
import com.inari.firefly.system.component.SystemBuilderAdapter;
import com.inari.firefly.system.component.SystemComponent.SystemComponentKey;
import com.inari.firefly.system.component.SystemComponentBuilder;

public class TextSystem 
    extends 
        ComponentSystem<TextSystem>
    implements 
        EntityActivationListener,
        RenderEventListener {
    
    public static final FFSystemTypeKey<TextSystem> SYSTEM_KEY = FFSystemTypeKey.create( TextSystem.class );
    
    private static final SystemComponentKey[] SUPPORTED_COMPONENT_TYPES = new SystemComponentKey[] {
        Font.TYPE_KEY
    };

    private EntitySystem entitySystem;
    private AssetSystem assetSystem;
    private TextRenderer renderer;

    private final DynArray<Font> fonts;
    private final DynArray<DynArray<DynArray<IndexedTypeSet>>> textPerViewAndLayer;
    
    TextSystem() {
        super( SYSTEM_KEY );
        fonts = new DynArray<Font>();
        textPerViewAndLayer = new DynArray<DynArray<DynArray<IndexedTypeSet>>>();
    }
    
    @Override
    public final void init( FFContext context ) throws FFInitException {
        entitySystem = context.getSystem( EntitySystem.SYSTEM_KEY );
        assetSystem = context.getSystem( AssetSystem.SYSTEM_KEY );
        
        renderer = new TextRenderer( this );
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
    
    public final Font getFont( int fontId ) {
        return fonts.get( fontId );
    }
    
    public final int getFontId( String name ) {
        for ( Font font : fonts ) {
            if ( name.equals( font.getName() ) ) {
                return font.getId();
            }
        }
        
        return -1;
    }
    
    public final void loadFont( int fontId ) {
        Font font = fonts.get( fontId );
        String name = font.getName();
        assetSystem.loadAsset( new AssetNameKey( name, name ) );
        assetSystem.loadAssets( name );
    }
    
    public final void disposeFont( int fontId ) {
        Font font = fonts.get( fontId );
        String name = font.getName();
        assetSystem.disposeAssets( name );
        assetSystem.disposeAsset( new AssetNameKey( name, name ) );
    }
    
    public final void deleteFont( int fontId ) {
        Font font = fonts.remove( fontId );
        if ( font != null ) {
            font.dispose();
        }
    }
    
    public final void clear() {
        for ( Font font : fonts ) {
            font.dispose();
        }
        fonts.clear();
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
    
    public final FontBuilder getFontBuilder() {
        return new FontBuilder();
    }

    @Override
    public final SystemComponentKey[] supportedComponentTypes() {
        return SUPPORTED_COMPONENT_TYPES;
    }

    @Override
    public final SystemBuilderAdapter<?>[] getSupportedBuilderAdapter() {
        return new SystemBuilderAdapter<?>[] {
            new FontBuilderHelper( this )
        };
    }

    public final class FontBuilder extends SystemComponentBuilder {

        protected FontBuilder() {}
        
        @Override
        public final SystemComponentKey systemComponentKey() {
            return Font.TYPE_KEY;
        }

        @Override
        public final int doBuild( int componentId, Class<?> subType, boolean activate ) {
            Font font = new Font( componentId );
            font.fromAttributes( attributes );
            
            checkName( font );
            

            Rectangle textureRegion = new Rectangle( 0, 0, font.getCharWidth(), font.getCharHeight() );
            char[][] charTextureMap = font.getCharTextureMap();
            int charWidth = font.getCharWidth();
            int charHeight = font.getCharHeight();
            String fontName = font.getName();
            
            int fontTextureId = assetSystem.getAssetBuilder()
                .set( TextureAsset.NAME, fontName )
                .set( TextureAsset.ASSET_GROUP, fontName )
                .set( TextureAsset.RESOURCE_NAME, font.getFontTextureResourceName() )
                .set( TextureAsset.TEXTURE_WIDTH, charTextureMap[ 0 ].length * font.getCharWidth() )
                .set( TextureAsset.TEXTURE_HEIGHT, charTextureMap.length * font.getCharHeight() )
            .build( TextureAsset.class );
            
            for ( int y = 0; y < charTextureMap.length; y++ ) {
                for ( int x = 0; x < charTextureMap[ y ].length; x++ ) {
                    textureRegion.x = x * charWidth;
                    textureRegion.y = y * charHeight;
                    
                    int charSpriteAssetId = assetSystem.getAssetBuilder()
                        .set( SpriteAsset.TEXTURE_ID, fontTextureId )
                        .set( SpriteAsset.TEXTURE_REGION, textureRegion )
                        .set( SpriteAsset.ASSET_GROUP, fontName )
                        .set( SpriteAsset.NAME, fontName + "_" + x + "_"+ y )
                    .build(  SpriteAsset.class  );
                    
                    font.setCharSpriteMapping( charTextureMap[ y ][ x ], charSpriteAssetId );
                }
            }
 
            fonts.set( font.index(), font );
            
            if ( activate ) {
                loadFont( font.index() );
            }
            
            return font.getId();
        }
    }
    
    private final class FontBuilderHelper extends SystemBuilderAdapter<Font> {
        public FontBuilderHelper( TextSystem system ) {
            super( system, new FontBuilder() );
        }
        @Override
        public final SystemComponentKey componentTypeKey() {
            return Font.TYPE_KEY;
        }
        @Override
        public final Font get( int id, Class<? extends Font> subtype ) {
            return fonts.get( id );
        }
        @Override
        public final void delete( int id, Class<? extends Font> subtype ) {
            deleteFont( id );
        }
        @Override
        public final Iterator<Font> getAll() {
            return fonts.iterator();
        }
    }

}
