package pl.kurs.trafficoffence.exceptionhandler;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public class ExceptionResponse {

    private List<String> errorMessages;
    private String exceptionTypeName;
    private String errorCode;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;


    public ExceptionResponse(List<String> errorMessages, String exceptionTypeName, String errorCode, LocalDateTime timestamp) {
        this.errorMessages = errorMessages;
        this.exceptionTypeName = exceptionTypeName;
        this.errorCode = errorCode;
        this.timestamp = timestamp;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }

    public String getExceptionTypeName() {
        return exceptionTypeName;
    }

    public void setExceptionTypeName(String exceptionTypeName) {
        this.exceptionTypeName = exceptionTypeName;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
