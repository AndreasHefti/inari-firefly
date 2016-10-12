package com.inari.firefly.graphics.view;

import java.util.Arrays;
import java.util.Set;

import com.inari.commons.lang.indexed.IndexedTypeKey;
import com.inari.commons.lang.list.DynArray;
import com.inari.commons.lang.list.IntBag;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.system.component.SystemComponent;

@Deprecated // create and use a general ComponentGroup type for this that is not a component by itself
public final class LayerGroup extends SystemComponent {
    
    public static final SystemComponentKey<LayerGroup> TYPE_KEY = SystemComponentKey.create( LayerGroup.class );
    
    public static final AttributeKey<DynArray<String>> LAYER_NAMES = AttributeKey.createForDynArray( "layerNames", LayerGroup.class );
    public static final AttributeKey<IntBag> LAYER_IDS = new AttributeKey<IntBag>( "layerIds", IntBag.class, LayerGroup.class );
    public static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        LAYER_IDS
    };
    
    private final IntBag layerIds;
    
    LayerGroup( int index ) {
        super( index );
        layerIds = new IntBag();
    }
    
    @Override
    public final IndexedTypeKey indexedTypeKey() {
        return TYPE_KEY;
    }

    public final IntBag getLayerIds() {
        return layerIds;
    }

    public final void setLayerIds( IntBag layerIds ) {
        this.layerIds.clear();
        this.layerIds.addAll( layerIds );
    }
    
    public final void removeLayer( int layerId ) {
        layerIds.remove( layerId );
    }
    
    public final boolean isEmpty() {
        return layerIds.isEmpty();
    }

    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        Set<AttributeKey<?>> attributeKeys = super.attributeKeys();
        attributeKeys.addAll( Arrays.asList( ATTRIBUTE_KEYS ) );
        return attributeKeys;
    }

    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        super.fromAttributes( attributes );
        
        setLayerIds( attributes.getIdsForNames( LAYER_NAMES, LAYER_IDS, Layer.TYPE_KEY, layerIds ) );
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        
        attributes.put( LAYER_IDS, layerIds );
    }

}
