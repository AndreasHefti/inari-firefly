package com.inari.firefly.graphics;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.inari.firefly.Disposable;
import com.inari.firefly.asset.Asset;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.system.FFContext;

public final class ShaderAsset extends Asset {

    public static final AttributeKey<Boolean> RESOURCE_BASED = new AttributeKey<Boolean>( "resourceBased", Boolean.class, ShaderAsset.class );
    public static final AttributeKey<String> RESOURCE_NAME = new AttributeKey<String>( "resourceName", String.class, ShaderAsset.class );
    public static final AttributeKey<String> SHADER_PROGRAM = new AttributeKey<String>( "shaderProgram", String.class, ShaderAsset.class );
    private static final Set<AttributeKey<?>> ATTRIBUTE_KEYS = new HashSet<AttributeKey<?>>( Arrays.<AttributeKey<?>>asList( new AttributeKey[] {
        RESOURCE_BASED,
        RESOURCE_NAME,
        SHADER_PROGRAM
    } ) );

    private boolean resourceBased;
    private String resourceName;
    private String shaderProgram;
    
    private int shaderId = -1;

    ShaderAsset( int assetId ) {
        super( assetId );
        resourceBased = true;
    }
    
    @Override
    public final int getInstanceId() {
        return shaderId;
    }
    
    public final int getShaderId() {
        return shaderId;
    }

    public final boolean isResourceBased() {
        return resourceBased;
    }

    public final void setResourceBased( boolean resourceBased ) {
        this.resourceBased = resourceBased;
    }

    public final String getResourceName() {
        return resourceName;
    }

    public final void setResourceName( String resourceName ) {
        this.resourceName = resourceName;
    }

    public final String getShaderProgram() {
        return shaderProgram;
    }

    public final void setShaderProgram( String shaderProgram ) {
        this.shaderProgram = shaderProgram;
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
        resourceBased = attributes.getValue( RESOURCE_BASED, resourceBased );
        resourceName = attributes.getValue( RESOURCE_NAME, resourceName );
        shaderProgram = attributes.getValue( SHADER_PROGRAM, shaderProgram );
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        attributes.put( RESOURCE_BASED, resourceBased );
        attributes.put( RESOURCE_NAME, resourceName );
        attributes.put( SHADER_PROGRAM, shaderProgram );
    }

    @Override
    public final Disposable load( FFContext context ) {
        if ( loaded ) {
            return this;
        }

        shaderId = context.getGraphics().createShader( this );
        return this;
    }

    @Override
    public void dispose( FFContext context ) {
        if ( !loaded ) {
            return;
        }
        
        context.getGraphics().disposeShader( shaderId );
        shaderId = -1;
    }
}
