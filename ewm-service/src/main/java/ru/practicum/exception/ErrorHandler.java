package ru.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(final NotFoundException e) {
        return ApiError.builder().message(e.getMessage()).reason(e.getCause().toString())
                .errors(List.of(Arrays.toString(e.getStackTrace()))).status("404 NOT_FOUND")
                .timestamp(LocalDateTime.now().toString()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleUnknownError(final Exception e) {
        return ApiError.builder().message(e.getMessage()).reason(e.getCause().toString())
                .errors(List.of(Arrays.toString(e.getStackTrace()))).status(HttpStatusCode.valueOf(500).toString())
                .timestamp(LocalDateTime.now().toString()).build();
    }
}


