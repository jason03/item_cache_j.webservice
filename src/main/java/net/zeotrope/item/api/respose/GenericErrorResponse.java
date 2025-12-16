package net.zeotrope.item.api.respose;

import java.time.Instant;

public record GenericErrorResponse(
        Instant timestamp,
        Integer status,
        String error,
        String message
)  implements ErrorResponse{}
