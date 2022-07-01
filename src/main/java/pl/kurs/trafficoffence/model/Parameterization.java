package pl.kurs.trafficoffence.model;

import javax.persistence.*;

@Entity
@Table(name = "Parameterization")
public class Parameterization {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_parameterization")
    private Long id;

    @Column(nullable = false)
    private String keyParam;

    @Column(nullable = false)
    private String valueParam;

    public Parameterization() {
    }

    public Parameterization(String keyParam, String valueParam) {
        this.keyParam = keyParam;
        this.valueParam = valueParam;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKeyParam() {
        return keyParam;
    }

    public void setKeyParam(String key) {
        this.keyParam = key;
    }

    public String getValueParam() {
        return valueParam;
    }

    public void setValueParam(String value) {
        this.valueParam = value;
    }

}
