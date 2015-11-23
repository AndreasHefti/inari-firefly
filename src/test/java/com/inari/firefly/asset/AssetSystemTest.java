package com.inari.firefly.asset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.lang.indexed.Indexer;
import com.inari.firefly.FireFlyMock;
import com.inari.firefly.asset.AssetSystem.AssetBuilder;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.component.attr.Attributes;
import com.inari.firefly.system.FFContext;

public class AssetSystemTest {
    
    @Before
    public void clear() {
        Indexer.clear();
    }
    
    @Test
    public void testCreation() {
        FFContext ffContext = new FireFlyMock().getContext();
        ffContext.getSystem( AssetSystem.CONTEXT_KEY );
        
        Attributes attrs = new Attributes();
        ffContext.toAttributes( attrs, Asset.TYPE_KEY );
        
        assertEquals( 
            "", 
            attrs.toString()
        );
    }
    
//    @Test
//    public void testIllegalCalls() {
//        FFContext ffContext = getTestFFContext();
//        AssetSystem service = new AssetSystem();
//        service.init( ffContext );
//        
//        try {
//            service.loadAsset( new AssetNameKey( "group", "name" ) );
//            fail( "Expect IllegalArgumentException here" );
//        } catch ( IllegalArgumentException iae ) {
//            assertEquals( "No Asset found for: AssetKey [group=group, name=name]", iae.getMessage() );
//        }
//        
//        try {
//            service.loadAssets( "group" );
//            fail( "Expect IllegalArgumentException here" );
//        } catch ( IllegalArgumentException iae ) {
//            assertEquals( "No group found: group", iae.getMessage() );
//        }
//        
//        try {
//            service.disposeAsset( new AssetNameKey( "group", "name" ) );
//            fail( "Expect IllegalArgumentException here" );
//        } catch ( IllegalArgumentException iae ) {
//            assertEquals( "No Asset found for: AssetKey [group=group, name=name]", iae.getMessage() );
//        }
//        
//        try {
//            service.disposeAssets( "group" );
//            fail( "Expect IllegalArgumentException here" );
//        } catch ( IllegalArgumentException iae ) {
//            assertEquals( "No group found: group", iae.getMessage() );
//        }
//        
//        try {
//            service.deleteAsset( new AssetNameKey( "group", "name" ) );
//            fail( "Expect IllegalArgumentException here" );
//        } catch ( IllegalArgumentException iae ) {
//            assertEquals( "No Asset found for: AssetKey [group=group, name=name]", iae.getMessage() );
//        }
//        
//        try {
//            service.deleteAssets( "group" );
//            fail( "Expect IllegalArgumentException here" );
//        } catch ( IllegalArgumentException iae ) {
//            assertEquals( "No group found: group", iae.getMessage() );
//        }
//    }

    
    
    @Test
    public void testBuildAsset() {
        FFContext ffContext = new FireFlyMock().getContext();
        AssetSystem service = ffContext.getSystem( AssetSystem.CONTEXT_KEY );
        
        service.init( ffContext );
        Attributes attrs = new Attributes();
        
        AssetBuilder assetBuilder = service.getAssetBuilder();
        assetBuilder
            .set( TestAsset.NAME, "asset1" )
            .set( TestAsset.ASSET_GROUP,"group1" )
            .build( 0, TestAsset.class );
        
        ffContext.toAttributes( attrs, Asset.TYPE_KEY );
        assertEquals( 
            "TestAsset(0)::name:String=asset1, group:String=group1", 
            attrs.toString() 
        );
        
        attrs.clear();
        assetBuilder
            .set( TestAsset.NAME, "asset2" )
            .set( TestAsset.ASSET_GROUP,"group1" )
            .build( 1, TestAsset.class );
        assetBuilder
            .set( TestAsset.NAME, "asset3" )
            .set( TestAsset.ASSET_GROUP,"group1" )
            .build( 2, TestAsset.class );
        assetBuilder
            .set( TestAsset.NAME, "asset4" )
            .set( TestAsset.ASSET_GROUP,"group2" )
            .build( 3, TestAsset.class );
        assetBuilder
            .set( TestAsset.NAME, "asset5" )
            .set( TestAsset.ASSET_GROUP,"group3" )
            .build( 4, TestAsset.class );
        
        ffContext.toAttributes( attrs, Asset.TYPE_KEY );
        assertEquals( 
            "TestAsset(0)::name:String=asset1, group:String=group1 " +
            "TestAsset(1)::name:String=asset2, group:String=group1 " +
            "TestAsset(2)::name:String=asset3, group:String=group1 " +
            "TestAsset(3)::name:String=asset4, group:String=group2 " +
            "TestAsset(4)::name:String=asset5, group:String=group3", 
            attrs.toString() 
        );
        attrs.clear();
    }
    
    @Test
    public void testCreateLoadDisposeAndDeleteSingleAsset() {
        FFContext ffContext = new FireFlyMock().getContext();
        AssetSystem service = ffContext.getSystem( AssetSystem.CONTEXT_KEY );
        IEventDispatcher eventDispatcher = ffContext.getEventDispatcher();
        
        service.init( ffContext );
        Attributes attrs = new Attributes();
        
        service
            .getAssetBuilder()
                .set( TestAsset.NAME, "asset1" )
                .set( TestAsset.ASSET_GROUP,"group1" )
            .build( TestAsset.class );
        
        assertEquals( 
            "TestEventDispatcher [events=[ViewEvent [eventType=VIEW_CREATED, view=0], AssetEvent [eventType=ASSET_CREATED, assetType=class com.inari.firefly.asset.AssetSystemTest$TestAsset]]]", 
            eventDispatcher.toString() 
        );
        
        attrs.clear();
        ffContext.toAttributes( attrs, Asset.TYPE_KEY );
        assertEquals( 
            "TestAsset(0)::name:String=asset1, group:String=group1", 
            attrs.toString() 
        );
        
        service.loadAsset( new AssetNameKey( "group1", "asset1" ) );
        assertTrue( service.isAssetLoaded( new AssetNameKey( "group1", "asset1" ) ) );
        attrs.clear();
        ffContext.toAttributes( attrs, Asset.TYPE_KEY );
        assertEquals( 
            "TestAsset(0)::name:String=asset1, group:String=group1", 
            attrs.toString() 
        );
        assertEquals( 
            "TestEventDispatcher [events=["
            + "ViewEvent [eventType=VIEW_CREATED, view=0], "
            + "AssetEvent [eventType=ASSET_CREATED, assetType=class com.inari.firefly.asset.AssetSystemTest$TestAsset], "
            + "AssetEvent [eventType=ASSET_LOADED, assetType=class com.inari.firefly.asset.AssetSystemTest$TestAsset]]]", 
            eventDispatcher.toString() 
        );
        
        service.disposeAsset( new AssetNameKey( "group1", "asset1" ) );
        assertFalse( service.isAssetLoaded( new AssetNameKey( "group1", "asset1" ) ) );
        attrs.clear();
        ffContext.toAttributes( attrs, Asset.TYPE_KEY );
        assertEquals( 
            "TestEventDispatcher [events=[" +
            "ViewEvent [eventType=VIEW_CREATED, view=0], " +
            "AssetEvent [eventType=ASSET_CREATED, assetType=class com.inari.firefly.asset.AssetSystemTest$TestAsset], " +
            "AssetEvent [eventType=ASSET_LOADED, assetType=class com.inari.firefly.asset.AssetSystemTest$TestAsset], " +
            "AssetEvent [eventType=ASSET_DISPOSED, assetType=class com.inari.firefly.asset.AssetSystemTest$TestAsset]]]", 
            eventDispatcher.toString() 
        );
        
        service.deleteAsset( new AssetNameKey( "group1", "asset1" ) );
        attrs.clear();
        ffContext.toAttributes( attrs, Asset.TYPE_KEY );
        assertEquals( 
            "", 
            attrs.toString() 
        );
        assertEquals( 
            "TestEventDispatcher [events=[" +
            "ViewEvent [eventType=VIEW_CREATED, view=0], " +
            "AssetEvent [eventType=ASSET_CREATED, assetType=class com.inari.firefly.asset.AssetSystemTest$TestAsset], " +
            "AssetEvent [eventType=ASSET_LOADED, assetType=class com.inari.firefly.asset.AssetSystemTest$TestAsset], " +
            "AssetEvent [eventType=ASSET_DISPOSED, assetType=class com.inari.firefly.asset.AssetSystemTest$TestAsset], " +
            "AssetEvent [eventType=ASSET_DELETED, assetType=class com.inari.firefly.asset.AssetSystemTest$TestAsset]]]", 
            eventDispatcher.toString() 
        );
    }
    
    @Test
    public void testPreventingOfUseOfIdTwice() {
        FFContext ffContext = new FireFlyMock().getContext();
        AssetSystem service = ffContext.getSystem( AssetSystem.CONTEXT_KEY );
        
        Attributes attrs = new Attributes();
        
        service.getAssetBuilder()
            .set( Asset.NAME, "asset2" )
            .set( Asset.ASSET_GROUP,"group1" )
            .buildAndNext( 1, TestAsset.class );
        
        try {
            service.getAssetBuilder()
                .set( Asset.NAME, "asset2" )
                .set( Asset.ASSET_GROUP,"group3" )
                .buildAndNext( 1, TestAsset.class );
            fail( "Exception expected here" );
        } catch ( Exception e ) {
            e.printStackTrace();
            assertEquals( 
                "Error while constructing: class com.inari.firefly.asset.AssetSystemTest$TestAsset", 
                e.getMessage() 
            );
            assertEquals( 
                "The Object index: 1 is already used by another Object!", 
                e.getCause().getMessage() 
           );
        }
        
        ffContext.toAttributes( attrs, Asset.TYPE_KEY );
        assertEquals( 
            "TestAsset(1)::name:String=asset2, group:String=group1", 
            attrs.toString() 
        );
    }
    
    @Test
    public void testDifferentTypesOnDifferentGroups() {
        FFContext ffContext = new FireFlyMock().getContext();
        AssetSystem service = ffContext.getSystem( AssetSystem.CONTEXT_KEY );
        
        Attributes attrs = new Attributes();
        
        service.getAssetBuilder()
            .set( Asset.NAME, "asset2" )
            .set( Asset.ASSET_GROUP,"group1" )
            .buildAndNext( 1, TestAsset.class )
            .set( Asset.NAME, "asset3" )
            .set( Asset.ASSET_GROUP,"group1" )
            .buildAndNext( 2, TestAsset.class )
            .set( Asset.NAME, "asset4" )
            .set( Asset.ASSET_GROUP,"group2" )
            .buildAndNext( 3, TestAsset.class )
            .set( Asset.NAME, "asset5" )
            .set( Asset.ASSET_GROUP,"group3" )
            .build( 4, TestAsset.class );
        service.getAssetBuilder()
            .set( Asset.NAME, "asset22" )
            .set( Asset.ASSET_GROUP,"group1" )
            .buildAndNext( 1, TestAsset2.class )
            .set( Asset.NAME, "asset23" )
            .set( Asset.ASSET_GROUP,"group1" )
            .buildAndNext( 2, TestAsset2.class )
            .set( Asset.NAME, "asset24" )
            .set( Asset.ASSET_GROUP,"group2" )
            .buildAndNext( 3, TestAsset2.class )
            .set( Asset.NAME, "asset25" )
            .set( Asset.ASSET_GROUP,"group3" )
            .build( 4, TestAsset2.class );
        
        ffContext.toAttributes( attrs, Asset.TYPE_KEY );
        assertEquals( 
            "TestAsset(1)::name:String=asset2, group:String=group1 " +
            "TestAsset(2)::name:String=asset3, group:String=group1 " +
            "TestAsset(3)::name:String=asset4, group:String=group2 " +
            "TestAsset(4)::name:String=asset5, group:String=group3 " +
            "TestAsset2(1)::name:String=asset22, group:String=group1 " +
            "TestAsset2(2)::name:String=asset23, group:String=group1 " +
            "TestAsset2(3)::name:String=asset24, group:String=group2 " +
            "TestAsset2(4)::name:String=asset25, group:String=group3", 
            attrs.toString() 
        );
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
        public void fromAttributes( AttributeMap attributes ) {
            super.fromAttributes( attributes );
        }

        @Override
        public void toAttributes( AttributeMap attributes ) {
            super.toAttributes( attributes );
        }

        @Override
        public Class<TestAsset> componentType() {
            return TestAsset.class;
        }
    }
    
    public static class TestAsset2 extends Asset {
        
        TestAsset2( int assetId ) {
            super( assetId );
        } 

        @Override
        public Set<AttributeKey<?>> attributeKeys() {
            return super.attributeKeys( new HashSet<AttributeKey<?>>() );
        }

        @Override
        public void fromAttributes( AttributeMap attributes ) {
            super.fromAttributes( attributes );
        }

        @Override
        public void toAttributes( AttributeMap attributes ) {
            super.toAttributes( attributes );
        }

        @Override
        public Class<TestAsset2> componentType() {
            return TestAsset2.class;
        }
    }

}
