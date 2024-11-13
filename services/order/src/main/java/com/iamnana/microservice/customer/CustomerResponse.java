package com.iamnana.microservice.customer;

public record CustomerResponse(
        String id,
        String firstName,
        String lastName,
        String email
) {
}
