package com.inari.firefly.sound;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.inari.firefly.asset.Asset;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;

public class SoundAsset extends Asset {
    
    public static final AttributeKey<String> RESOURCE_NAME = new AttributeKey<String>( "resourceName", String.class, SoundAsset.class );
    private static final Set<AttributeKey<?>> ATTRIBUTE_KEYS = new HashSet<AttributeKey<?>>( Arrays.<AttributeKey<?>>asList( new AttributeKey[] { 
        ASSET_GROUP,
        RESOURCE_NAME
    } ) );
    
    private String resourceName;

    SoundAsset( int id ) {
        super( id );
    }
    
    @Override
    public final Class<SoundAsset> getComponentType() {
        return SoundAsset.class;
    }

    public final String getResourceName() {
        return resourceName;
    }
    
    public final void setResourceName( String resourceName ) {
        checkNotAlreadyLoaded();
        this.resourceName = resourceName;
    }
    
    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        Set<AttributeKey<?>> attributeKeys = super.attributeKeys();
        attributeKeys.addAll( ATTRIBUTE_KEYS );
        return super.attributeKeys( attributeKeys );
    }
    
    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        super.fromAttributes( attributes );
        resourceName = attributes.getValue( RESOURCE_NAME, resourceName );
    }
    
    @Override
    public final void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        attributes.put( RESOURCE_NAME, resourceName );
    }
}
