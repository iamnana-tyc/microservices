package com.iamnana.microservice.handler;

import java.util.Map;

public record ErrorObject(
        Map<String, String> errors
) {
}
