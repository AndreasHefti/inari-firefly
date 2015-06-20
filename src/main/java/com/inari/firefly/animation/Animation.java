package com.inari.firefly.animation;

import java.util.Arrays;
import java.util.Set;

import com.inari.firefly.component.AttributeKey;
import com.inari.firefly.component.AttributeMap;
import com.inari.firefly.component.NamedIndexedComponent;

public abstract class Animation extends NamedIndexedComponent {
    
    public static final AttributeKey<Long> START_TIME = new AttributeKey<Long>( "name", Long.class, Animation.class );
    public static final AttributeKey<Boolean> LOOPING = new AttributeKey<Boolean>( "name", Boolean.class, Animation.class );
    public static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] {
        START_TIME,
        LOOPING
    };
    
    private long startTime;
    private boolean looping;
    protected boolean active;
    protected boolean finished;

    Animation( int id ) {
        super( id );
    }

    @Override
    public final Class<Animation> getComponentType() {
        return Animation.class;
    }

    @Override
    public final Class<Animation> getIndexedObjectType() {
        return Animation.class;
    }

    public final long getStartTime() {
        return startTime;
    }

    public final void setStartTime( long startTime ) {
        this.startTime = startTime;
    }

    public final boolean isLooping() {
        return looping;
    }

    public final void setLooping( boolean looping ) {
        this.looping = looping;
    }

    public final boolean isActive() {
        return active;
    }
    
    public final boolean isFinished() {
        return finished;
    }

    @Override
    public Set<AttributeKey<?>> attributeKeys() {
        Set<AttributeKey<?>> attributeKeys = super.attributeKeys();
        attributeKeys.addAll( Arrays.asList( ATTRIBUTE_KEYS ) );
        return attributeKeys;
    }

    @Override
    public void fromAttributeMap( AttributeMap attributes ) {
        super.fromAttributeMap( attributes );
        startTime = attributes.getValue( START_TIME, startTime );
        looping = attributes.getValue( LOOPING, looping );
    }

    @Override
    public void toAttributeMap( AttributeMap attributes ) {
        super.toAttributeMap( attributes );
        attributes.put( NAME, name );
        attributes.put( START_TIME, startTime );
        attributes.put( LOOPING, looping );
    }


    public abstract void update( long time );

}
