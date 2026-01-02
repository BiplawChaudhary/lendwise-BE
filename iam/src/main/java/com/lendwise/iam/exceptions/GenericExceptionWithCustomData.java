package com.lendwise.iam.exceptions;

import lombok.Getter;
import lombok.Setter;

/*
    @created 5/9/2025 3:58 PM
    @project expense-distributor
    @author biplaw.chaudhary
*/
@Getter
@Setter
public class GenericExceptionWithCustomData extends RuntimeException{
    private String urn;
    private String message;
    private Integer code;
    private Object data;

    public GenericExceptionWithCustomData(String urn, String message, Integer code, Object data) {
        super(message);
        this.urn = urn;
        this.message = message;
        this.code = code;
        this.data = data;
    }
}
