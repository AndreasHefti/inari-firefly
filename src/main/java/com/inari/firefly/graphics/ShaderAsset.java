package com.inari.firefly.graphics;

import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.firefly.asset.Asset;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.component.SystemComponentType;
import com.inari.firefly.system.utils.Disposable;

public final class ShaderAsset extends Asset {

    public static final SystemComponentType COMPONENT_TYPE = new SystemComponentType( Asset.TYPE_KEY, ShaderAsset.class );
    public static final AttributeKey<String> VERTEX_SHADER_RESOURCE_NAME = AttributeKey.createString( "vertexShaderResourceName", ShaderAsset.class );
    public static final AttributeKey<String> VERTEX_SHADER_PROGRAM = AttributeKey.createString( "vertexShaderProgram", ShaderAsset.class );
    public static final AttributeKey<String> FRAGMENT_SHADER_RESOURCE_NAME = AttributeKey.createString( "fragmentShaderResourceName", ShaderAsset.class );
    public static final AttributeKey<String> FRAGMENT_SHADER_PROGRAM = AttributeKey.createString( "fragmentShaderProgram", ShaderAsset.class );
    public static final Set<AttributeKey<?>> ATTRIBUTE_KEYS = JavaUtils.<AttributeKey<?>>unmodifiableSet(
        VERTEX_SHADER_RESOURCE_NAME,
        VERTEX_SHADER_PROGRAM,
        FRAGMENT_SHADER_RESOURCE_NAME,
        FRAGMENT_SHADER_PROGRAM
    );

    private String vertexShaderResourceName;
    private String vertexShaderProgram;
    private String fragmentShaderResourceName;
    private String fragmentShaderProgram;
    
    private int shaderId = -1;

    ShaderAsset( int assetId ) {
        super( assetId );
        
        vertexShaderResourceName = null;
        vertexShaderProgram = null;
        fragmentShaderResourceName = null;
        fragmentShaderProgram = null;
    }
    
    @Override
    public final int getInstanceId( int index ) {
        return shaderId;
    }
    
    public final int getShaderId() {
        return shaderId;
    }

    public final String getVertexShaderResourceName() {
        return vertexShaderResourceName;
    }

    public final void setVertexShaderResourceName( String vertexShaderResourceName ) {
        this.vertexShaderResourceName = vertexShaderResourceName;
    }

    public final String getVertexShaderProgram() {
        return vertexShaderProgram;
    }

    public final void setVertexShaderProgram( String vertexShaderProgram ) {
        this.vertexShaderProgram = vertexShaderProgram;
    }

    public final String getFragmentShaderResourceName() {
        return fragmentShaderResourceName;
    }

    public final void setFragmentShaderResourceName( String fragmentShaderResourceName ) {
        this.fragmentShaderResourceName = fragmentShaderResourceName;
    }

    public final String getFragmentShaderProgram() {
        return fragmentShaderProgram;
    }

    public final void setFragmentShaderProgram( String fragmentShaderProgram ) {
        this.fragmentShaderProgram = fragmentShaderProgram;
    }

    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        return JavaUtils.unmodifiableSet( super.attributeKeys(), ATTRIBUTE_KEYS );
    }

    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        super.fromAttributes( attributes );
        vertexShaderResourceName = attributes.getValue( VERTEX_SHADER_RESOURCE_NAME, vertexShaderResourceName );
        vertexShaderProgram = attributes.getValue( VERTEX_SHADER_PROGRAM, vertexShaderProgram );
        fragmentShaderResourceName = attributes.getValue( FRAGMENT_SHADER_RESOURCE_NAME, fragmentShaderResourceName );
        fragmentShaderProgram = attributes.getValue( FRAGMENT_SHADER_PROGRAM, fragmentShaderProgram );
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        if ( vertexShaderResourceName != null ) {
            attributes.put( VERTEX_SHADER_RESOURCE_NAME, vertexShaderResourceName );
        }
        if ( vertexShaderProgram != null ) {
            attributes.put( VERTEX_SHADER_PROGRAM, vertexShaderProgram );
        }
        if ( fragmentShaderResourceName != null ) {
            attributes.put( FRAGMENT_SHADER_RESOURCE_NAME, fragmentShaderResourceName );
        }
        if ( fragmentShaderProgram != null ) {
            attributes.put( FRAGMENT_SHADER_PROGRAM, fragmentShaderProgram );
        }
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
