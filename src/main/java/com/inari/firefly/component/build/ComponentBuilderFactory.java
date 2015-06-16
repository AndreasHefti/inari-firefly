package com.inari.firefly.component.build;

public interface ComponentBuilderFactory {
    
    <C> ComponentBuilder<C> getComponentBuilder( Class<C> type );

}
