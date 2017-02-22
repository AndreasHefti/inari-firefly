package com.inari.firefly.physics.animation;

import com.inari.commons.lang.aspect.AspectGroup;
import com.inari.commons.lang.indexed.IndexedType;
import com.inari.commons.lang.indexed.IndexedTypeKey;
import com.inari.commons.lang.indexed.Indexer;
import com.inari.firefly.system.FFContext;

public interface AttributeAnimationAdapter<A extends Animation> extends IndexedType {

    
    void apply( int componentId, final A animation, final FFContext context );
    
    
    
    AspectGroup ASPECT_TYPE = new AspectGroup( "AttributeAnimationAdapterKey" );
    class AttributeAnimationAdapterKey<C extends AttributeAnimationAdapter<?>> extends IndexedTypeKey {
        
        private AttributeAnimationAdapter<Animation> instance;

        AttributeAnimationAdapterKey( Class<C> indexedType ) {
            super( indexedType );
        }

        @SuppressWarnings( "rawtypes" )
        @Override
        public final Class<AttributeAnimationAdapter> baseType() {
            return AttributeAnimationAdapter.class;
        }
        
        @Override
        public final String toString() {
            return "AttributeAnimationAdapterKey:" + type().getSimpleName();
        }
        
        @Override
        public final AspectGroup aspectGroup() {
            return ASPECT_TYPE;
        }
        
        public final AnimationMapping createAnimationMapping( String animationName ) {
            return new AnimationMapping( animationName, this );
        }
        
        public final AnimationMapping createAnimationMapping( int animationId ) {
            return new AnimationMapping( animationId, this );
        }
        
        public final AttributeAnimationAdapter<Animation> getAdapterInstance() {
            return instance;
        }
        
        @SuppressWarnings( "unchecked" )
        public static final <C extends AttributeAnimationAdapter<? extends Animation>> AttributeAnimationAdapterKey<C> create( C instance ) {
            AttributeAnimationAdapterKey<C> key = Indexer.createIndexedTypeKey( AttributeAnimationAdapterKey.class, instance.getClass() );
            key.instance = (AttributeAnimationAdapter<Animation>) instance;
            return key;
        }
    }

}
