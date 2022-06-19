package pl.kurs.trafficoffence.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDate;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PersonHaveBanDrivingLicenseException extends RuntimeException {

    private String pesel;
    private LocalDate date;

    public PersonHaveBanDrivingLicenseException(String pesel, LocalDate dateOfBan) {
        super("Driving license ban, pesel: " + pesel + ", from: " + dateOfBan.toString() );
        this.pesel = pesel;
        this.date = date;
    }
}
