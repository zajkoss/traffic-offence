package pl.kurs.trafficoffence.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
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
    private String name;

    @Column(nullable = false)
    private Integer points;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal penalty;

    @ManyToMany
    private Set<Offence> offences = new HashSet<>();

    @Column
    private boolean deleted = false;

    @Column
    private LocalDate endDate;

    @Version
    private Integer version;

    public Fault() {
    }

    public Fault(Long id, String name, Integer points, BigDecimal penalty, Set<Offence> offences, boolean deleted, LocalDate endDate) {
        this.id = id;
        this.name = name;
        this.points = points;
        this.penalty = penalty;
        this.offences = offences;
        this.deleted = deleted;
        this.endDate = endDate;
    }

    public Fault(String name, Integer points, BigDecimal penalty, Set<Offence> offences, boolean deleted) {
        this.name = name;
        this.points = points;
        this.penalty = penalty;
        this.offences = offences;
        this.deleted = deleted;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fault fault = (Fault) o;
        return deleted == fault.deleted && Objects.equals(id, fault.id) && Objects.equals(name, fault.name) && Objects.equals(points, fault.points) && Objects.equals(penalty, fault.penalty) && Objects.equals(endDate, fault.endDate) && Objects.equals(version, fault.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, points, penalty, deleted, endDate, version);
    }

    @Override
    public String toString() {
        return "Fault{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", points=" + points +
                ", penalty=" + penalty +
                ", deleted=" + deleted +
                ", endDate=" + endDate +
                ", version=" + version +
                '}';
    }
}
