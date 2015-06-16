package com.inari.firefly.state;

import com.inari.firefly.component.NamedIndexedComponent;

public final class State extends NamedIndexedComponent {

    State( int stateId ) {
        super( stateId );
    }

    @Override
    public final String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "State [name=" );
        builder.append( name );
        builder.append( "]" );
        return builder.toString();
    }

}
