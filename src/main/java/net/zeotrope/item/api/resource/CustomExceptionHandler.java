package net.zeotrope.item.api.resource;

import jakarta.servlet.http.HttpServletRequest;
import net.zeotrope.item.api.respose.ErrorResponse;
import net.zeotrope.item.api.respose.GenericErrorResponse;
import net.zeotrope.item.exceptions.InvalidStatusException;
import net.zeotrope.item.exceptions.ItemNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpServerErrorException.BadGateway;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomExceptionHandler  {
    public static final Logger LOGGER = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @ExceptionHandler(InternalServerError.class)
    public ResponseEntity<ErrorResponse> handleInternalServerError(InternalServerError exception, HttpServletRequest request) {
        var errorResponse = new GenericErrorResponse(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                String.format("Internal Server Error for request: %s", request.getRequestURI())
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(BadGateway.class)
    public ResponseEntity<ErrorResponse> handleBadGatewayException(BadGateway exception, HttpServletRequest request) {
        return new ResponseEntity<>(
                new GenericErrorResponse(
                        Instant.now(),
                        HttpStatus.BAD_GATEWAY.value(),
                        HttpStatus.BAD_GATEWAY.getReasonPhrase(),
                        String.format("Bad Gateway for request: %s", request.getRequestURI())
                ), HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException exception, HttpServletRequest request) {
        return new ResponseEntity<>(
                new GenericErrorResponse(
                        Instant.now(),
                        HttpStatus.NOT_FOUND.value(),
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        String.format("Resource Not Found for request: %s", request.getRequestURI())
                ), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleItemNotFoundException(ItemNotFoundException exception, HttpServletRequest request) {
        return new ResponseEntity<>(
                new GenericErrorResponse(
                        Instant.now(),
                        HttpStatus.NOT_FOUND.value(),
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        String.format("Item not found for request: %s", request.getRequestURI())
                ), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidStatusException.class)
    public ResponseEntity<ErrorResponse> handleInvalidStatusException(InvalidStatusException exception, HttpServletRequest request) {
        return new ResponseEntity<>(
                new GenericErrorResponse(
                        Instant.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        String.format("Item request is invalid: %s?%s", request.getRequestURI(), request.getQueryString())
                ), HttpStatus.BAD_REQUEST);
    }
}

@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
class DefaultExceptionHandler {
    public static final Logger LOGGER = LoggerFactory.getLogger(DefaultExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception exception, HttpServletRequest request) {
        return new ResponseEntity<>(
                new GenericErrorResponse(
                        Instant.now(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                        String.format("Internal server error for request: %s", request.getRequestURI())
                ), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
