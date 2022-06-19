package pl.kurs.trafficoffence.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EmptyPeselNumberException extends RuntimeException {

    private String pesel;

    public EmptyPeselNumberException() {
        super("null");
    }

    public EmptyPeselNumberException(String pesel) {
        super("Wrong pesel number: " + pesel);
        this.pesel = pesel;
    }

    public String getPesel() {
        return pesel;
    }
}
