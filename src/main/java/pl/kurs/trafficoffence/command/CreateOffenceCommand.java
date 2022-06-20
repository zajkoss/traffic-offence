package pl.kurs.trafficoffence.command;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.validator.constraints.pl.PESEL;
import pl.kurs.trafficoffence.validator.PersonExist;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CreateOffenceCommand {

    @PastOrPresent
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;

    @PositiveOrZero
    @Max(15)
    private Integer points;

    @PositiveOrZero
    @Max(5000)
    private BigDecimal penalty;

    @NotBlank
    private String faultDescription;

    @PESEL
    @PersonExist
    private String personPesel;

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public BigDecimal getPenalty() {
        return penalty;
    }

    public void setPenalty(BigDecimal penalty) {
        this.penalty = penalty;
    }

    public String getFaultDescription() {
        return faultDescription;
    }

    public void setFaultDescription(String faultDescription) {
        this.faultDescription = faultDescription;
    }

    public String getPersonPesel() {
        return personPesel;
    }

    public void setPersonPesel(String personPesel) {
        this.personPesel = personPesel;
    }

    @Override
    public String toString() {
        return "CreateOffenceCommand{" +
                "time=" + time +
                ", points=" + points +
                ", penalty=" + penalty +
                ", faultDescription='" + faultDescription + '\'' +
                ", personPesel='" + personPesel + '\'' +
                '}';
    }
}
