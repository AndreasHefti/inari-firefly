package com.inari.firefly.asset;

import java.util.HashSet;
import java.util.Set;

import com.inari.firefly.component.NamedIndexedComponent;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.component.dynattr.DynamicAttribueMapper;
import com.inari.firefly.component.dynattr.DynamicAttributeMap;
import com.inari.firefly.component.dynattr.DynamicAttributedComponent;

public abstract class Asset extends NamedIndexedComponent implements DynamicAttributedComponent {
    
    public static final AttributeKey<String> ASSET_GROUP = new AttributeKey<String>( "group", String.class, Asset.class );
    
    protected boolean loaded = false;
    protected String group;
    
    private DynamicAttributeMap dynamicAttributeMap = new DynamicAttributeMap();
    
    protected Asset( int assetId ) {
        super( assetId );
    }

    public boolean isLoaded() {
        return loaded;
    }
    @Override
    public final Class<? extends Asset> getIndexedObjectType() {
        return getComponentType();
    }
    
    @Override
    public abstract Class<? extends Asset> getComponentType();

    @Override
    public boolean hasDynamicAttributes() {
        return DynamicAttribueMapper.hasDynamicAttributes( getComponentType() );
    }
    
    @Override
    public final <A> void setDynamicAttribute( AttributeKey<A> key, A value ) {
        dynamicAttributeMap.setDynamicAttribute( key, value, getComponentType() );
    }
    
    @Override
    public final <A> A getDynamicAttribute( AttributeKey<A> key ) {
        return dynamicAttributeMap.getDynamicAttribute( key );
    }

    @Override
    public void fromAttributes( AttributeMap attributes ) {
        super.fromAttributes( attributes );
        group = attributes.getValue( ASSET_GROUP, group );
        
        if ( hasDynamicAttributes() ) {
            dynamicAttributeMap.fromAttributeMap( attributes, this );
        }
    }

    @Override
    public void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        attributes.put( ASSET_GROUP, group );
        
        if ( hasDynamicAttributes() ) {
            dynamicAttributeMap.toAttributeMap( attributes, this );
        }
    }
    
    protected Set<AttributeKey<?>> attributeKeys( Set<AttributeKey<?>> attributeKeys ) {
        return dynamicAttributeMap.attributeKeys( this, new HashSet<AttributeKey<?>>( attributeKeys ) );
    }

    protected int[] dependsOn() {
        return null;
    }
    
    protected void checkNotAlreadyLoaded() {
        if ( loaded ) {
            throw new IllegalStateException( "Asset: " + group + " " + name + " is already loaded and can not be modified" );
        } 
    }

}
