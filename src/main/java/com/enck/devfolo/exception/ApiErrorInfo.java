package com.enck.devfolo.exception;


import lombok.Data;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

@Data
public class ApiErrorInfo {


    private  String message;
    private  LocalDate date;
    private  String  error;
   // private String path;

    public ApiErrorInfo( Exception ex , ResponseEntity responseEntity) {
        this.message = ex.getLocalizedMessage();
        this.date= LocalDate.now();
        this.error = responseEntity.getStatusCode().toString();
      //  this.path = path;
    }
}
