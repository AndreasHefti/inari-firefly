package com.inari.firefly.animation;

import java.util.Arrays;
import java.util.Set;

import com.inari.firefly.component.NamedIndexedComponent;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;

public abstract class Animation extends NamedIndexedComponent {
    
    public static final AttributeKey<Long> START_TIME = new AttributeKey<Long>( "startTime", Long.class, Animation.class );
    public static final AttributeKey<Long> END_TIME = new AttributeKey<Long>( "endTime", Long.class, Animation.class );
    public static final AttributeKey<Boolean> LOOPING = new AttributeKey<Boolean>( "looping", Boolean.class, Animation.class );
    public static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] {
        START_TIME,
        END_TIME,
        LOOPING,
    };
    
    private long startTime;
    private long endTime;
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

    public final long getEndTime() {
        return endTime;
    }

    public final void setEndTime( long endTime ) {
        this.endTime = endTime;
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
    public void fromAttributes( AttributeMap attributes ) {
        super.fromAttributes( attributes );
        startTime = attributes.getValue( START_TIME, startTime );
        endTime = attributes.getValue( END_TIME, endTime );
        looping = attributes.getValue( LOOPING, looping );
    }

    @Override
    public void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        attributes.put( START_TIME, startTime );
        attributes.put( END_TIME, endTime );
        attributes.put( LOOPING, looping );
    }


    public abstract void update( long time );

}
