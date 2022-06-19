package pl.kurs.trafficoffence.event;

import org.springframework.context.ApplicationEvent;
import pl.kurs.trafficoffence.model.Person;

import java.time.LocalDate;

public class OnInformPersonEvent extends ApplicationEvent {

    private Person person;
    private LocalDate dateOfBan;

    public OnInformPersonEvent(Person person, LocalDate dateOfBan) {
        super(person);
        this.person = person;
        this.dateOfBan = dateOfBan;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public LocalDate getDateOfBan() {
        return dateOfBan;
    }

    public void setDateOfBan(LocalDate dateOfBan) {
        this.dateOfBan = dateOfBan;
    }
}
