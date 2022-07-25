package pl.kurs.trafficoffence.command;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public class QueryPersonCommand {
    private String name;

    private String lastname;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthdayFrom;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthdayTo;

    private Integer pointsFrom;

    private Integer pointsTo;

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

    public LocalDate getBirthdayFrom() {
        return birthdayFrom;
    }

    public void setBirthdayFrom(LocalDate birthdayFrom) {
        this.birthdayFrom = birthdayFrom;
    }

    public LocalDate getBirthdayTo() {
        return birthdayTo;
    }

    public void setBirthdayTo(LocalDate birthdayTo) {
        this.birthdayTo = birthdayTo;
    }

    public Integer getPointsFrom() {
        return pointsFrom;
    }

    public void setPointsFrom(Integer pointsFrom) {
        this.pointsFrom = pointsFrom;
    }

    public Integer getPointsTo() {
        return pointsTo;
    }

    public void setPointsTo(Integer pointsTo) {
        this.pointsTo = pointsTo;
    }
}
