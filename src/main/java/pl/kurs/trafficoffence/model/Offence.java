package pl.kurs.trafficoffence.model;


import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "Offence")
public class Offence implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_offence")
    private Long id;

    @Column(nullable = false)
    private LocalDateTime time;

    @Column(nullable = false)
    private Integer points;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal penalty;

    @Column(nullable = false)
    private String faultDescription;

    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @Version
    private Integer version;

    public Offence() {
    }

    public Offence(LocalDateTime date, Integer points, BigDecimal penalty, String faultDescription, Person person) {
        this.time = date;
        this.points = points;
        this.penalty = penalty;
        this.faultDescription = faultDescription;
        this.person = person;
    }

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

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Integer getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Offence offence = (Offence) o;
        return Objects.equals(id, offence.id) && Objects.equals(time, offence.time) && Objects.equals(points, offence.points) && Objects.equals(penalty, offence.penalty) && Objects.equals(faultDescription, offence.faultDescription) && Objects.equals(person, offence.person) && Objects.equals(version, offence.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, time, points, penalty, faultDescription, person, version);
    }

    @Override
    public String toString() {
        return "Offence{" +
                "id=" + id +
                ", date=" + time +
                ", points=" + points +
                ", penalty=" + penalty +
                ", faultDescription='" + faultDescription + '\'' +
                ", person=" + person +
                '}';
    }
}
