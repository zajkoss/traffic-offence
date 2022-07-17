package pl.kurs.trafficoffence.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadQueryException extends RuntimeException {

    private String message;

    public BadQueryException() {
        super("null");
    }

    public BadQueryException(String message) {
        super(message);
        this.message = message;
    }

}
