package com.techelevator.tenmo.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class InvalidEntryException extends RuntimeException {

    public InvalidEntryException(){
        super("Invalid Entry");
    }
}
