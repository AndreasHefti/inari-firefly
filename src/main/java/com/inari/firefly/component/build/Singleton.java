package com.inari.firefly.component.build;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target( TYPE )
@Retention( RetentionPolicy.RUNTIME )
@Inherited
public @interface Singleton {}
