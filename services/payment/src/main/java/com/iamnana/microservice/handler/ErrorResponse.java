package com.iamnana.microservice.handler;

import java.util.Map;

public record ErrorResponse (
        Map<String,String> errors
){

}
