package pl.kurs.trafficoffence.dto;

public class PersonDtoWithOffencesSummary {

    private Long id;

    private String name;

    private String lastname;

    private String pesel;

    private Integer points;

    private Integer totalOffences;

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

    public String getPesel() {
        return pesel;
    }

    public void setPesel(String pesel) {
        this.pesel = pesel;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }


    public Integer getTotalOffences() {
        return totalOffences;
    }

    public void setTotalOffences(Integer totalOffences) {
        this.totalOffences = totalOffences;
    }

    @Override
    public String toString() {
        return "PersonDtoWithOffencesSummary{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lastname='" + lastname + '\'' +
                ", pesel='" + pesel + '\'' +
                ", points=" + points +
                ", totalOffences=" + totalOffences +
                '}';
    }
}
