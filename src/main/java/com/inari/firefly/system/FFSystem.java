package com.inari.firefly.system;

import com.inari.commons.lang.aspect.AspectGroup;
import com.inari.commons.lang.indexed.IndexedType;
import com.inari.commons.lang.indexed.IndexedTypeKey;
import com.inari.commons.lang.indexed.Indexer;
import com.inari.firefly.system.utils.FFContextInitiable;

public interface FFSystem extends IndexedType, FFContextInitiable {
    
    public final static AspectGroup ASPECT_GROUP = new AspectGroup( "FFSystem" );
    
    FFSystemTypeKey<?> systemTypeKey();

    public static final class FFSystemTypeKey<T extends FFSystem> extends IndexedTypeKey {
        
        public final Class<T> systemType;

        FFSystemTypeKey( Class<T> indexedType ) {
            super( indexedType );
            systemType = indexedType;
        }

        @Override
        public final Class<FFSystem> baseType() {
            return FFSystem.class;
        }
        
        @Override
        public final AspectGroup aspectGroup() {
            return ASPECT_GROUP;
        }
        
        @SuppressWarnings( "unchecked" )
        public static final <T extends FFSystem> FFSystemTypeKey<T> create( Class<T> type ) {
            return Indexer.createIndexedTypeKey( FFSystemTypeKey.class, type );
        }
    }

}
