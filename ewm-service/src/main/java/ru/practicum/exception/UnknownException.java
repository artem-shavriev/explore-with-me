package ru.practicum.exception;

public class UnknownException extends RuntimeException {
    public UnknownException(String message) {
        super(message);
    }
}
