package com.inari.firefly.graphics.scene;

import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.commons.lang.functional.Callback;
import com.inari.commons.lang.indexed.IIndexedTypeKey;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.component.SystemComponent;

public abstract class Scene extends SystemComponent {
    
    public static final SystemComponentKey<Scene> TYPE_KEY = SystemComponentKey.create( Scene.class );
    
    public static final AttributeKey<Boolean> RUN_AGAIN = AttributeKey.createBoolean( "runAgain", Scene.class );
    public static final Set<AttributeKey<?>> ATTRIBUTE_KEYS = JavaUtils.<AttributeKey<?>>unmodifiableSet(
        RUN_AGAIN
    );
    
    boolean runAgain;
    
    Callback callback;
    boolean running = false;
    boolean paused = false;

    protected Scene( int index ) {
        super( index );
        runAgain = true;
    }

    @Override
    public final IIndexedTypeKey indexedTypeKey() {
        return TYPE_KEY;
    }

    public final boolean isRunAgain() {
        return runAgain;
    }

    public final void setRunAgain( boolean runAgain ) {
        this.runAgain = runAgain;
    }

    public abstract void run( final FFContext context );
    
    public abstract void reset( final FFContext context );

    public abstract void update( final FFContext context );

    public void dispose( final FFContext context ) {
        reset( context );
        dispose();
    }
    
    @Override
    public Set<AttributeKey<?>> attributeKeys() {
        return JavaUtils.unmodifiableSet( super.attributeKeys(), ATTRIBUTE_KEYS );
    }

    @Override
    public void fromAttributes( AttributeMap attributes ) {
        super.fromAttributes( attributes );
        
        runAgain = attributes.getValue( RUN_AGAIN, runAgain );
    }

    @Override
    public void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        
        attributes.put( RUN_AGAIN, runAgain );
    }
    
    

}
