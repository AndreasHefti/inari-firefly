package com.inari.firefly.physics.animation.timeline;

import java.util.Arrays;
import java.util.Set;

import com.inari.commons.lang.list.DynArray;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.physics.animation.IntAnimation;

public final class IntTimelineAnimation extends IntAnimation {
    
    public static final AttributeKey<DynArray<IntTimelineData>> TIMELINE = AttributeKey.createDynArray( "timeline", IntTimelineAnimation.class, IntTimelineData.class );
    private static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] {
        TIMELINE
    };
    
    private IntTimelineData[] timeline;
    
    private long lastUpdate;
    private int currentIndex;

    protected IntTimelineAnimation( int id ) {
        super( id );
        reset();
    }

    @Override
    public final void reset() {
        super.reset();
        lastUpdate = -1;
        currentIndex = 0;
    }

    public final IntTimelineData[] getTimeline() {
        return timeline;
    }

    public final void setTimeline( IntTimelineData[] timeline ) {
        this.timeline = timeline;
    }
    
    @Override
    public final void update() {
        long updateTime = context.getTime();
        
        if ( lastUpdate < 0 ) {
            lastUpdate = updateTime;
            return;
        }
        
        if ( updateTime - lastUpdate < timeline[ currentIndex ].time ) {
            return;
        }
        
        lastUpdate = updateTime;
        currentIndex++;
        
        if ( currentIndex >= timeline.length ) {
            currentIndex = 0;
            if ( !looping ) {
                finish();
            } 
        } 
    }
    
    @Override
    public final int getInitValue() {
        if ( timeline == null || timeline.length < 1 ) {
            return -1;
        }
        
        return getValue( -1, -1 );
    }

    @Override
    public final int getValue( int component, int currentValue ) {
        return timeline[ currentIndex ].value;
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
        
        DynArray<IntTimelineData> timelineData = attributes.getValue( TIMELINE );
        if ( timelineData != null ) {
            timeline = timelineData.getArray();
        }
        
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        
        if ( timeline != null ) {
            DynArray<IntTimelineData> result = DynArray.create( IntTimelineData.class );
            for ( int i = 0; i < timeline.length; i++ ) {
                result.set( i, timeline[ i ] );
            }
            attributes.put( TIMELINE, result );
        }
        
    }

}
