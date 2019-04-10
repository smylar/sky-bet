package com.skyb.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception that defines the status code to return.
 * 
 * N.B. I've put most of my exceptions through this
 * a full system would have more specific errors
 * 
 * @author paul
 *
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RequestException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 6898275835669421847L;

    public RequestException(String string) {
        super(string);
    }

    public RequestException(Exception e) {
        super(e);
    }

}
