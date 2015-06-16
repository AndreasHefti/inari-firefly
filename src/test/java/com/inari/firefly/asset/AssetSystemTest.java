package com.inari.firefly.asset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.inari.commons.event.TestAspectedEvent;
import com.inari.commons.event.AspectedEventListener;
import com.inari.commons.event.Event;
import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.event.MatchedEvent;
import com.inari.commons.event.MatchedEventListener;
import com.inari.commons.lang.TypedKey;
import com.inari.firefly.FFContext;
import com.inari.firefly.asset.event.AssetEvent;
import com.inari.firefly.component.AttributeKey;
import com.inari.firefly.component.AttributeMap;
import com.inari.firefly.component.build.ComponentBuilder;

public class AssetSystemTest {
    
    @Test
    public void testCreation() {
        AssetSystem service = getDefaultService();
        
        assertEquals( 
            "AssetSystem [\n" + 
            "]", 
            service.toString() 
        );
    }
    
    @Test
    public void testIllegalCalls() {
        AssetSystem service = getDefaultService();
        
        try {
            service.loadAsset( "group", "name" );
            fail( "Expect IllegalArgumentException here" );
        } catch ( IllegalArgumentException iae ) {
            assertEquals( "No Asset found for group: group name: name", iae.getMessage() );
        }
        
        try {
            service.loadAssets( "group" );
            fail( "Expect IllegalArgumentException here" );
        } catch ( IllegalArgumentException iae ) {
            assertEquals( "No group found: group", iae.getMessage() );
        }
        
        try {
            service.disposeAsset( "group", "name" );
            fail( "Expect IllegalArgumentException here" );
        } catch ( IllegalArgumentException iae ) {
            assertEquals( "No Asset found for group: group name: name", iae.getMessage() );
        }
        
        try {
            service.disposeAssets( "group" );
            fail( "Expect IllegalArgumentException here" );
        } catch ( IllegalArgumentException iae ) {
            assertEquals( "No group found: group", iae.getMessage() );
        }
        
        try {
            service.deleteAsset( "group", "name" );
            fail( "Expect IllegalArgumentException here" );
        } catch ( IllegalArgumentException iae ) {
            assertEquals( "No Asset found for group: group name: name", iae.getMessage() );
        }
        
        try {
            service.deleteAssets( "group" );
            fail( "Expect IllegalArgumentException here" );
        } catch ( IllegalArgumentException iae ) {
            assertEquals( "No group found: group", iae.getMessage() );
        }
    }

    
    
    @Test
    public void testBuildAsset() {
        AssetSystem service = getDefaultService();
        
        ComponentBuilder<TestAsset> assetBuilder = service.getAssetBuilder( TestAsset.class );
        assetBuilder
            .setAttribute( TestAsset.NAME, "asset1" )
            .setAttribute( TestAsset.ASSET_GROUP,"group1" )
            .build( 0 );
        
        assertEquals( 
            "AssetSystem [\n" + 
            "  TestAsset [loaded=false, name=asset1, group=group1, id()=0]\n" + 
            "]", 
            service.toString() 
        );
        
        assetBuilder
            .setAttribute( TestAsset.NAME, "asset2" )
            .setAttribute( TestAsset.ASSET_GROUP,"group1" )
            .build( 1 );
        assetBuilder
            .setAttribute( TestAsset.NAME, "asset3" )
            .setAttribute( TestAsset.ASSET_GROUP,"group1" )
            .build( 2 );
        assetBuilder
            .setAttribute( TestAsset.NAME, "asset4" )
            .setAttribute( TestAsset.ASSET_GROUP,"group2" )
            .build( 3 );
        assetBuilder
            .setAttribute( TestAsset.NAME, "asset5" )
            .setAttribute( TestAsset.ASSET_GROUP,"group3" )
            .build( 4 );
        
        assertEquals( 
            "AssetSystem [\n" + 
            "  TestAsset [loaded=false, name=asset1, group=group1, id()=0]\n" + 
            "  TestAsset [loaded=false, name=asset2, group=group1, id()=1]\n" + 
            "  TestAsset [loaded=false, name=asset3, group=group1, id()=2]\n" + 
            "  TestAsset [loaded=false, name=asset4, group=group2, id()=3]\n" + 
            "  TestAsset [loaded=false, name=asset5, group=group3, id()=4]\n" + 
            "]", 
            service.toString() 
        );
    }
    
    @Test
    public void testCreateLoadDisposeAndDeleteSingleAsset() {
        final TestEventDispatcher ted = new TestEventDispatcher();
        AssetSystem service = new AssetSystem( new FFContext() {
            @SuppressWarnings( "unchecked" )
            @Override
            public <T> T get( TypedKey<T> key ) {
                return (T) ted;
            }
            @Override
            public void dispose() {
            }
        });
        
        service
            .getAssetBuilder( TestAsset.class )
                .setAttribute( TestAsset.NAME, "asset1" )
                .setAttribute( TestAsset.ASSET_GROUP,"group1" )
            .build();
        
        assertEquals( 
            "TestEventDispatcher [events=[ASSET_CREATED]]", 
            ted.toString() 
        );
        
        assertEquals( 
            "AssetSystem [\n" + 
            "  TestAsset [loaded=false, name=asset1, group=group1, id()=5]\n" + 
            "]", 
            service.toString() 
        );
        
        service.loadAsset( "group1", "asset1" );
        assertTrue( service.isAssetLoaded( "group1", "asset1" ) );
        assertEquals( 
            "AssetSystem [\n" + 
            "  TestAsset [loaded=true, name=asset1, group=group1, id()=5]\n" + 
            "]", 
            service.toString() 
        );
        assertEquals( 
            "TestEventDispatcher [events=[ASSET_CREATED, ASSET_LOADED]]", 
            ted.toString() 
        );
        
        service.disposeAsset( "group1", "asset1" );
        assertFalse( service.isAssetLoaded( "group1", "asset1" ) );
        assertEquals( 
            "TestEventDispatcher [events=[ASSET_CREATED, ASSET_LOADED, ASSET_DISPOSED]]", 
            ted.toString() 
        );
        
        service.deleteAsset( "group1", "asset1" );
        assertEquals( 
            "AssetSystem [\n" + 
            "]", 
            service.toString() 
        );
        assertEquals( 
            "TestEventDispatcher [events=[ASSET_CREATED, ASSET_LOADED, ASSET_DISPOSED, ASSET_DELETED]]", 
            ted.toString() 
        );
    }

    
    private AssetSystem getDefaultService() {
        AssetSystem service = new AssetSystem( new FFContext() {
            @SuppressWarnings( "unchecked" )
            @Override
            public <T> T get( TypedKey<T> key ) {
                return (T) new TestEventDispatcher();
            }
            @Override
            public void dispose() {
            }
        });
        return service;
    }
    
    public static class TestAsset extends Asset {
        
        TestAsset( int assetId ) {
            super( assetId );
        } 

        @Override
        public Set<AttributeKey<?>> attributeKeys() {
            return super.attributeKeys( new HashSet<AttributeKey<?>>() );
        }

        @Override
        public void fromAttributeMap( AttributeMap attributes ) {
            super.fromAttributeMap( attributes );
        }

        @Override
        public void toAttributeMap( AttributeMap attributes ) {
            super.toAttributeMap( attributes );
        }

        @Override
        public Class<? extends Asset> getComponentType() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append( "TestAsset [loaded=" );
            builder.append( loaded );
            builder.append( ", name=" );
            builder.append( name );
            builder.append( ", group=" );
            builder.append( group );
            builder.append( ", id()=" );
            builder.append( indexedId() );
            builder.append( "]" );
            return builder.toString();
        }
    }

    private class TestEventDispatcher implements IEventDispatcher {
        
        private List<String> events = new ArrayList<String>();

        @Override
        public <L> void register( Class<? extends Event<L>> eventType, L listener ) {}

        @Override
        public <L> boolean unregister( Class<? extends Event<L>> eventType, L listener ) {
            return false;
        }

        @Override
        public <L> void notify( Event<L> event ) {
            events.add( ( (AssetEvent) event ).getType().toString() );
        }

        @Override
        public <L extends AspectedEventListener> void notify( TestAspectedEvent<L> event ) {
            //events.add( ( (AssetEvent) event ).getActionType().toString() );
        }

        @Override
        public <L extends MatchedEventListener> void notify( MatchedEvent<L> event ) {
            //events.add( event.getClass().getSimpleName() );
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append( "TestEventDispatcher [events=" );
            builder.append( events );
            builder.append( "]" );
            return builder.toString();
        }
    }

}
