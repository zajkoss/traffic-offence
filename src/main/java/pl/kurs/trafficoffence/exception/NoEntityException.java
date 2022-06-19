package pl.kurs.trafficoffence.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NoEntityException extends RuntimeException {

    private Long id;

    public NoEntityException() {
        super("null");
    }

    public NoEntityException(Long id) {
        super("id:" + id);
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
