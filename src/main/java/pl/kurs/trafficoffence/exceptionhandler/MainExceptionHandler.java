package pl.kurs.trafficoffence.exceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.kurs.trafficoffence.exception.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class MainExceptionHandler {

    @ExceptionHandler({NoEntityException.class, EmptyIdException.class, NoEmptyIdException.class, PersonHaveBanDrivingLicenseException.class, EmptyPeselNumberException.class,BadQueryException.class})
    public ResponseEntity<ExceptionResponse> handleCustomExceptions(Exception e) {
        ExceptionResponse response = new ExceptionResponse(List.of(e.getMessage()), e.getClass().getSimpleName(), "BAD_REQUEST", LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ExceptionResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {

        List<String> messages = ex.getFieldErrors().stream()
                .map(e -> "Property: " + e.getField() + "; value: '" + e.getRejectedValue() + "'; message: " + e.getDefaultMessage()).collect(Collectors.toList());
        messages.addAll(
                ex.getAllErrors().stream()
                        .filter(objectError -> !(objectError instanceof FieldError))
                        .map(objectError -> "Property: " + objectError.getObjectName() + "'; message: " + objectError.getDefaultMessage()).collect(Collectors.toList())
        );
        ExceptionResponse response = new ExceptionResponse(messages, ex.getClass().getSimpleName(), "BAD_REQUEST", LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<ExceptionResponse> handleValidationExceptions(ConstraintViolationException ex) {
        List<String> messages = ex.getConstraintViolations().stream()
                .map(e -> "Property: " + e.getPropertyPath().toString() + "; value: '" + e.getInvalidValue() + "'; message: " + e.getMessage()).collect(Collectors.toList());
        ExceptionResponse response = new ExceptionResponse(messages, ex.getClass().getSimpleName(), "BAD_REQUEST", LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity<ExceptionResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        ExceptionResponse response = new ExceptionResponse(List.of(ex.getMessage()), ex.getClass().getSimpleName(), "NOT_FOUND", LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

}
