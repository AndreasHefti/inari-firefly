package com.inari.firefly.physics.animation;

import java.util.Set;

import com.inari.commons.JavaUtils;
import com.inari.commons.lang.list.DynArray;
import com.inari.commons.lang.list.DynArrayRO;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.entity.EntityComponent;

public class EAnimation extends EntityComponent {
    
    public static final EntityComponentTypeKey<EAnimation> TYPE_KEY = EntityComponentTypeKey.create( EAnimation.class );
    
    public static final AttributeKey<DynArray<AnimationMapping>> ANIMATION_MAPPING = AttributeKey.createDynArray( "animationMapping", EAnimation.class, AnimationMapping.class );
    public static final Set<AttributeKey<?>> ATTRIBUTE_KEYS = JavaUtils.<AttributeKey<?>>unmodifiableSet(
        ANIMATION_MAPPING
    );
    
    private final DynArray<AnimationMapping> animationMapping;
    
    public EAnimation() {
        super( TYPE_KEY );
        animationMapping = DynArray.create( AnimationMapping.class, 5, 5 );
    }

    public final void resetAttributes() {
        animationMapping.clear();
    }
    
    public final DynArrayRO<AnimationMapping> getAnimationMappings() {
        return animationMapping;
    }
    
    public final Set<AttributeKey<?>> attributeKeys() {
        return ATTRIBUTE_KEYS;
    }
    
    public final void fromAttributes( AttributeMap attributes ) {
        animationMapping.clear();
        if ( attributes.contains( ANIMATION_MAPPING ) ) {
            animationMapping.addAll( attributes.getValue( ANIMATION_MAPPING ) );
        }
    }

    public final void toAttributes( AttributeMap attributes ) {
        attributes.put( ANIMATION_MAPPING, animationMapping );
    }

}
