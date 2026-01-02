package com.lendwise.merchantservice.exceptions;

import lombok.Getter;
import lombok.Setter;

/*
    @created 5/9/2025 3:56 PM
    @project expense-distributor
    @author biplaw.chaudhary
*/
@Getter
@Setter
public class GenericException extends RuntimeException {
    private String urn;
    private String message;
    private Integer code;

    public GenericException(String urn, String message, Integer code) {
        super(message);
        this.urn = urn;
        this.message = message;
        this.code = code;
    }
}
