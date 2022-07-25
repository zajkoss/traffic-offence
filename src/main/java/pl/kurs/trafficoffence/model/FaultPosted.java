package pl.kurs.trafficoffence.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "Fault_posted")
public class FaultPosted implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_fault_posted")
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Integer points;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal penalty;

    @ManyToOne
    @JoinColumn(name = "offence_id", nullable = false, referencedColumnName = "id_offence")
    private Offence offence;

    @Version
    private Integer version;

    public FaultPosted() {
    }

    public FaultPosted(String name, Integer points, BigDecimal penalty) {
        this.name = name;
        this.points = points;
        this.penalty = penalty;
    }

    public FaultPosted(String name, Integer points, BigDecimal penalty, Offence offence) {
        this.name = name;
        this.points = points;
        this.penalty = penalty;
        this.offence = offence;
    }

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

    public Offence getOffence() {
        return offence;
    }

    public void setOffence(Offence offence) {
        this.offence = offence;
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
        FaultPosted that = (FaultPosted) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(points, that.points) && Objects.equals(penalty, that.penalty) && Objects.equals(offence, that.offence) && Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, points, penalty, offence, version);
    }

    @Override
    public String toString() {
        return "FaultPosted{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", points=" + points +
                ", penalty=" + penalty +
                ", offence=" + offence +
                ", version=" + version +
                '}';
    }
}
