package pl.kurs.trafficoffence.command;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.validator.constraints.pl.PESEL;
import pl.kurs.trafficoffence.validator.FaultsExist;
import pl.kurs.trafficoffence.validator.PersonExist;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;
import java.util.List;

public class CreateOffenceCommand {

    @PastOrPresent
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;

    @NotEmpty(message = "List of faults cannot be empty")
    @FaultsExist(message = "List contains not exists fault: [{list}]")
    private List<Long> faults;

    @PESEL
    @PersonExist
    private String personPesel;

    public CreateOffenceCommand() {
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getPersonPesel() {
        return personPesel;
    }

    public void setPersonPesel(String personPesel) {
        this.personPesel = personPesel;
    }

    public List<Long> getFaults() {
        return faults;
    }

    public void setFaults(List<Long> faults) {
        this.faults = faults;
    }

    @Override
    public String toString() {
        return "CreateOffenceCommand{" +
                "time=" + time +
                ", faults=" + faults +
                ", personPesel='" + personPesel + '\'' +
                '}';
    }
}
