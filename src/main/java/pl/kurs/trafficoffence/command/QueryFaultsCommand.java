package pl.kurs.trafficoffence.command;

import java.math.BigDecimal;

public class QueryFaultsCommand {

    private String name;

    private Integer minPoints;

    private Integer maxPoints;

    private BigDecimal minPenalty;

    private BigDecimal maxPenalty;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMinPoints() {
        return minPoints;
    }

    public void setMinPoints(Integer minPoints) {
        this.minPoints = minPoints;
    }

    public Integer getMaxPoints() {
        return maxPoints;
    }

    public void setMaxPoints(Integer maxPoints) {
        this.maxPoints = maxPoints;
    }

    public BigDecimal getMinPenalty() {
        return minPenalty;
    }

    public void setMinPenalty(BigDecimal minPenalty) {
        this.minPenalty = minPenalty;
    }

    public BigDecimal getMaxPenalty() {
        return maxPenalty;
    }

    public void setMaxPenalty(BigDecimal maxPenalty) {
        this.maxPenalty = maxPenalty;
    }
}
