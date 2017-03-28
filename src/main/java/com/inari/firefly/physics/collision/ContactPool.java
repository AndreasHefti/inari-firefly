package com.inari.firefly.physics.collision;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.IntIterator;
import com.inari.commons.lang.indexed.IIndexedTypeKey;
import com.inari.firefly.FFInitException;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.graphics.view.Layer;
import com.inari.firefly.graphics.view.View;
import com.inari.firefly.system.component.SystemComponent;

public abstract class ContactPool extends SystemComponent{
    
    public static final SystemComponentKey<ContactPool> TYPE_KEY = SystemComponentKey.create( ContactPool.class );
    
    public static final AttributeKey<String> VIEW_NAME = AttributeKey.createString( "viewName", ContactPool.class );
    public static final AttributeKey<Integer> VIEW_ID = AttributeKey.createInt( "viewId", ContactPool.class );
    public static final AttributeKey<String> LAYER_NAME = AttributeKey.createString( "layerName", ContactPool.class );
    public static final AttributeKey<Integer> LAYER_ID = AttributeKey.createInt( "layerId", ContactPool.class );
    
    private static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        VIEW_ID,
        LAYER_ID,
    };
    
    protected int viewId;
    protected int layerId;
    
    protected EntitySystem entitySystem;

    protected ContactPool( int index ) {
        super( index );
    }
    
    @Override
    public final void init() throws FFInitException {
        super.init();
        
        this.entitySystem = context.getSystem( EntitySystem.SYSTEM_KEY );
    }

    @Override
    public final IIndexedTypeKey indexedTypeKey() {
        return TYPE_KEY;
    };

    public final int getViewId() {
        return viewId;
    }

    public final void setViewId( int viewId ) {
        this.viewId = viewId;
    }

    public final int getLayerId() {
        return layerId;
    }

    public final void setLayerId( int layerId ) {
        this.layerId = layerId;
    }
    
    @Override
    public Set<AttributeKey<?>> attributeKeys() {
        Set<AttributeKey<?>> attributeKeys = super.attributeKeys();
        attributeKeys.addAll( new HashSet<AttributeKey<?>>( Arrays.asList( ATTRIBUTE_KEYS ) ) );
        return attributeKeys;
    }
    
    @Override
    public void fromAttributes( AttributeMap attributes ) {
        super.fromAttributes( attributes );
        
        viewId = attributes.getIdForName( VIEW_NAME, VIEW_ID, View.TYPE_KEY, viewId );
        layerId = attributes.getIdForName( LAYER_NAME, LAYER_ID, Layer.TYPE_KEY, layerId );
    }

    @Override
    public void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        
        attributes.put( VIEW_ID, viewId );
        attributes.put( LAYER_ID, layerId );
    } 
    
    abstract void add( int entityId );
    
    abstract void remove( int entityId );
    
    abstract void update( int entityId );
    
    abstract IntIterator get( Rectangle region );
    
    abstract void clear();

}
