package com.iprody.leonidm.paymentserviceapp.dto;

import java.time.Instant;
import java.util.UUID;

public record ErrorDto(
    String errorMessage,
    Instant timestamp,
    UUID guid,
    String operation
) {

    public ErrorDto(String errorMessage, Instant timestamp) {
        this(errorMessage, timestamp, null, null);
    }
}
