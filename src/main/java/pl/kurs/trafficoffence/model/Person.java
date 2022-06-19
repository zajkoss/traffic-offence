package pl.kurs.trafficoffence.model;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "Person")
public class Person implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_person")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String lastname;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length = 11, unique = true)
    private String pesel;

    @OneToMany(mappedBy = "person")
    private Set<Offence> offences = new HashSet<>();

    @Column
    private LocalDate dataOfBanDrivingLicense;

    @Version
    private Integer version;

    public Person() {
    }

    public Person(String name, String lastname, String email, String pesel, Set<Offence> offences, LocalDate dataOfBanDrivingLicense) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.pesel = pesel;
        this.offences = offences;
        this.dataOfBanDrivingLicense = dataOfBanDrivingLicense;
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

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPesel() {
        return pesel;
    }

    public void setPesel(String pesel) {
        this.pesel = pesel;
    }

    public Integer getVersion() {
        return version;
    }

    public Set<Offence> getOffences() {
        return offences;
    }

    public void setOffences(Set<Offence> offences) {
        this.offences = offences;
    }

    public LocalDate getDataOfBanDrivingLicense() {
        return dataOfBanDrivingLicense;
    }

    public void setDataOfBanDrivingLicense(LocalDate dataOfBanDrivingLicense) {
        this.dataOfBanDrivingLicense = dataOfBanDrivingLicense;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(id, person.id) && Objects.equals(name, person.name) && Objects.equals(lastname, person.lastname) && Objects.equals(email, person.email) && Objects.equals(pesel, person.pesel) && Objects.equals(offences, person.offences) && Objects.equals(dataOfBanDrivingLicense, person.dataOfBanDrivingLicense) && Objects.equals(version, person.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, lastname, email, pesel, offences, dataOfBanDrivingLicense, version);
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", pesel='" + pesel + '\'' +
                ", offences=" + offences +
                ", dataOfBanDrivingLicense=" + dataOfBanDrivingLicense +
                ", version=" + version +
                '}';
    }
}
