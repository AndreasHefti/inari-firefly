package com.inari.firefly.component.build;

public class ComponentCreationException extends RuntimeException {

    private static final long serialVersionUID = 7615820147159196514L;

    public ComponentCreationException() {
        super();
    }

    public ComponentCreationException( String message, Throwable cause ) {
        super( message, cause );
    }

    public ComponentCreationException( String message ) {
        super( message );
    }

}
