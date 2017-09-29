package com.inari.firefly.control.maintenance;

import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.commons.lang.indexed.IIndexedTypeKey;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.component.SystemComponent;
import com.inari.firefly.system.utils.Disposable;
import com.inari.firefly.system.utils.Initiable;
import com.inari.firefly.system.utils.Trigger;
import com.inari.firefly.system.utils.Triggerer;

public abstract class Init extends SystemComponent implements Initiable, Disposable {
    
    public static final SystemComponentKey<Init> TYPE_KEY = SystemComponentKey.create( Init.class );
    public static final AttributeKey<Trigger> INIT_TRIGGER = AttributeKey.createTrigger( "initTrigger", Init.class );
    public static final AttributeKey<Trigger> CLEANUP_TRIGGER = AttributeKey.createTrigger( "cleanupTrigger", Init.class );
    public static final Set<AttributeKey<?>> ATTRIBUTE_KEYS = JavaUtils.<AttributeKey<?>>unmodifiableSet(
        INIT_TRIGGER,
        CLEANUP_TRIGGER
    );
    
    private static final Triggerer INIT_TRIGGERER = new Triggerer() {
        public final void trigger( FFContext context, int componentId ) {
            context.getSystem( MaintenanceSystem.SYSTEM_KEY ).init( componentId );
        }
    };
    private static final Triggerer CLEANUP_TRIGGERER = new Triggerer() {
        public final void trigger( FFContext context, int componentId ) {
            context.getSystem( MaintenanceSystem.SYSTEM_KEY ).cleanup( componentId );
        }
    };
    
    private boolean initialised;
    private Trigger initTrigger;
    private Trigger cleanupTrigger;

    protected Init( int index ) {
        super( index );
        initialised = false;
        initTrigger = null;
        cleanupTrigger = null;
    }

    public final IIndexedTypeKey indexedTypeKey() {
        return TYPE_KEY;
    }
    
    public final boolean isInitialised() {
        return initialised;
    }

    public final Trigger getInitTrigger() {
        return initTrigger;
    }

    public final FFContext setInitTrigger( final Trigger initTrigger ) {
        if ( this.initTrigger != null ) {
            this.initTrigger.dispose( context );
        }
        
        this.initTrigger = initTrigger;
        if ( initTrigger == null ) {
            return context;
        }
        this.initTrigger.register( context, index, INIT_TRIGGERER );
        
        return context;
    }

    public final Trigger getCleanupTrigger() {
        return cleanupTrigger;
    }

    public final FFContext setCleanupTrigger( final Trigger cleanupTrigger ) {
        if ( this.cleanupTrigger != null ) {
            this.cleanupTrigger.dispose( context );
        }
        
        this.cleanupTrigger = cleanupTrigger;
        if ( cleanupTrigger == null ) {
            return context;
        }
        this.cleanupTrigger.register( context, index, CLEANUP_TRIGGERER );
        
        return context;
    }

    public final Disposable init( final FFContext context ) {
        if ( initialised ) {
            return this;
        }
        
        initialise();
        
        initialised = true;
        return this;
    }
    
    public void dispose( final FFContext context ) {
        if ( !initialised ) {
            return;
        }
        
        cleanup();
        initialised = false;
    }
    
    protected abstract void initialise();
    protected abstract void cleanup();
    
    public final Set<AttributeKey<?>> attributeKeys() {
        return JavaUtils.unmodifiableSet( super.attributeKeys(), ATTRIBUTE_KEYS );
    }

    public final void fromAttributes( AttributeMap attributes ) {
        super.fromAttributes( attributes );
        
        if ( attributes.contains( INIT_TRIGGER ) ) {
            setInitTrigger( attributes.getValue( INIT_TRIGGER ) );
        }
        if ( attributes.contains( CLEANUP_TRIGGER ) ) {
            setCleanupTrigger( attributes.getValue( CLEANUP_TRIGGER ) );
        }
    }

    public final void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        
        if ( initTrigger != null ) {
            attributes.put( INIT_TRIGGER, initTrigger );
        }
        
        if ( cleanupTrigger != null ) {
            attributes.put( CLEANUP_TRIGGER, cleanupTrigger );
        }
    }

}
