package pl.kurs.trafficoffence.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "Fault")
public class Fault implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_fault")
    private Long id;

    @Column(nullable = false)
    private Integer points;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal penalty;

    @ManyToMany
    private Set<Offence> offences;

    @Version
    private Integer version;

    public Fault() {
    }

    public Fault(Integer points, BigDecimal penalty, Set<Offence> offences, Integer version) {
        this.points = points;
        this.penalty = penalty;
        this.offences = offences;
        this.version = version;
    }

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

    public Set<Offence> getOffences() {
        return offences;
    }

    public void setOffences(Set<Offence> offences) {
        this.offences = offences;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fault fault = (Fault) o;
        return Objects.equals(id, fault.id) && Objects.equals(points, fault.points) && Objects.equals(penalty, fault.penalty) && Objects.equals(offences, fault.offences) && Objects.equals(version, fault.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, points, penalty, offences, version);
    }

    @Override
    public String toString() {
        return "Fault{" +
                "id=" + id +
                ", points=" + points +
                ", penalty=" + penalty +
                ", version=" + version +
                '}';
    }
}
