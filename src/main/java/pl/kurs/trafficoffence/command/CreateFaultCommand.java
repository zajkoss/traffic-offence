package pl.kurs.trafficoffence.command;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public class CreateFaultCommand {

    @NotBlank
    private String name;

    @PositiveOrZero
    @Max(15)
    private Integer points;

    @PositiveOrZero
    @Max(5000)
    private BigDecimal penalty;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
