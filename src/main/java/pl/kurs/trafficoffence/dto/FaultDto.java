package pl.kurs.trafficoffence.dto;

import java.math.BigDecimal;
import java.util.Objects;

public class FaultDto {

    private Long id;

    private String name;

    private Integer points;

    private BigDecimal penalty;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FaultDto faultDto = (FaultDto) o;
        return Objects.equals(id, faultDto.id) && Objects.equals(name, faultDto.name) && Objects.equals(points, faultDto.points) && Objects.equals(penalty, faultDto.penalty);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, points, penalty);
    }

    @Override
    public String toString() {
        return "FaultDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", points=" + points +
                ", penalty=" + penalty +
                '}';
    }
}
