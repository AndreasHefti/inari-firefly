package com.inari.firefly.system;

public class FFInitException extends RuntimeException {

    private static final long serialVersionUID = 3326546859441672813L;

    public FFInitException() {
        super();
    }

    public FFInitException( String message, Throwable cause ) {
        super( message, cause );
    }

    public FFInitException( String message ) {
        super( message );
    }

}
