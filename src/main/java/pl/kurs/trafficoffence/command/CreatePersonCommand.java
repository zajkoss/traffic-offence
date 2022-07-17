package pl.kurs.trafficoffence.command;

import org.hibernate.validator.constraints.pl.PESEL;
import pl.kurs.trafficoffence.validator.PersonEmailUnique;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class CreatePersonCommand {

    @NotBlank
    private String name;

    @NotBlank
    private String lastname;

    @NotBlank
    @Email
    @PersonEmailUnique
    private String email;

    @PESEL
    private String pesel;

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

    @Override
    public String toString() {
        return "CreatePersonCommand{" +
                "name='" + name + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", pesel='" + pesel + '\'' +
                '}';
    }
}
