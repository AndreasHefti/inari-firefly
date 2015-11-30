package com.inari.firefly.system;

import com.inari.commons.lang.indexed.IndexedType;
import com.inari.commons.lang.indexed.IndexedTypeKey;
import com.inari.commons.lang.indexed.Indexer;

public interface FFSystem extends IndexedType, FFContextInitiable {
    
    FFSystemTypeKey<?> systemTypeKey();
    
    public static final class FFSystemTypeKey<T extends FFSystem> extends IndexedTypeKey {
        
        private final Class<T> systemType;

        protected FFSystemTypeKey( Class<T> indexedType ) {
            super( indexedType );
            systemType = indexedType;
        }

        @Override
        protected final Class<FFSystem> baseIndexedType() {
            return FFSystem.class;
        }
        
        public final Class<T> systemType() {
            return systemType;
        }
        
        public final T cast( FFSystem system ) {
            return systemType.cast( system );
        }
        
        @SuppressWarnings( "unchecked" )
        public static final <T extends FFSystem> FFSystemTypeKey<T> create( Class<T> type ) {
            return Indexer.getIndexedTypeKey( FFSystemTypeKey.class, type );
        }
    }

}
