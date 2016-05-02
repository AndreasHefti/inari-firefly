package com.inari.firefly.system;

import com.inari.commons.lang.indexed.IndexedType;
import com.inari.commons.lang.indexed.IndexedTypeKey;
import com.inari.commons.lang.indexed.Indexer;
import com.inari.firefly.system.utils.FFContextInitiable;

public interface FFSystem extends IndexedType, FFContextInitiable {
    
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
        
        @SuppressWarnings( "unchecked" )
        public static final <T extends FFSystem> FFSystemTypeKey<T> create( Class<T> type ) {
            return Indexer.createIndexedTypeKey( FFSystemTypeKey.class, type );
        }

    }

}
