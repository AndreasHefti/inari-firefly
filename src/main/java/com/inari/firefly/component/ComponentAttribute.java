package com.inari.firefly.component;

public @interface ComponentAttribute {
    
    public String name() default "";
    public Class<?> type() default Object.class;
    public String group() default "";
    public Class<?> groupType() default Object.class;
    
}
