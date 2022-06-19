package pl.kurs.trafficoffence.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class OffenceDto {


    private Long id;

    private LocalDateTime time;

    private Integer points;

    private BigDecimal penalty;

    private String faultDescription;

    private String personPesel;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OffenceDto that = (OffenceDto) o;
        return Objects.equals(id, that.id) && Objects.equals(time, that.time) && Objects.equals(points, that.points) && Objects.equals(penalty, that.penalty) && Objects.equals(faultDescription, that.faultDescription) && Objects.equals(personPesel, that.personPesel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, time, points, penalty, faultDescription, personPesel);
    }
}
