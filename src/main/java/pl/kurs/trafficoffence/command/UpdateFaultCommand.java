package pl.kurs.trafficoffence.command;

import pl.kurs.trafficoffence.validator.FaultNameUnique;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@FaultNameUnique(message = "Not unique fault name")
public class UpdateFaultCommand {

    private Long id;

    @NotBlank
    private String name;

    @PositiveOrZero
    @Max(15)
    private Integer points;

    @PositiveOrZero
    @Max(5000)
    private BigDecimal penalty;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
