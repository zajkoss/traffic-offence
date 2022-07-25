package pl.kurs.trafficoffence.predicate;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import liquibase.pro.packaged.B;
import liquibase.pro.packaged.Q;
import pl.kurs.trafficoffence.model.QOffence;
import pl.kurs.trafficoffence.model.QPerson;

import java.time.LocalDate;
import java.util.Optional;

public class SearchPersonQuery {

    private String name;
    private String lastname;
    private LocalDate birthdayFrom;
    private LocalDate birthdayTo;
    private Integer pointsFrom;
    private Integer pointsTo;

    public Predicate toPredicate() {
        BooleanBuilder conditions = new BooleanBuilder();
        Optional.ofNullable(name).map(QPerson.person.name::containsIgnoreCase).ifPresent(conditions::and);
        Optional.ofNullable(lastname).map(QPerson.person.lastname::containsIgnoreCase).ifPresent(conditions::and);
        Optional.ofNullable(birthdayFrom).map(this::getPESELNumberFromDate).map(QPerson.person.pesel::goe).ifPresent(conditions::and);
        Optional.ofNullable(birthdayTo).map(this::getPESELNumberFromDate).map(QPerson.person.pesel::loe).ifPresent(conditions::and);

        BooleanBuilder pointsConditions = new BooleanBuilder();
        Optional.ofNullable(pointsFrom).map(pointsFrom -> QPerson.person.pesel.contains(
                JPAExpressions.selectFrom(QOffence.offence)
                        .select(QOffence.offence.person.pesel)
                        .groupBy(QOffence.offence.person.pesel)
                        .having(QOffence.offence.points.sum().goe(pointsFrom))
        )).ifPresent(pointsConditions::and);
        Optional.ofNullable(pointsTo).map(pointsFrom -> QPerson.person.pesel.contains(
                JPAExpressions.selectFrom(QOffence.offence)
                        .select(QOffence.offence.person.pesel)
                        .groupBy(QOffence.offence.person.pesel)
                        .having(QOffence.offence.points.sum().loe(pointsTo))
        )).ifPresent(pointsConditions::and);

        BooleanBuilder emptyPointsConditions = new BooleanBuilder();
        emptyPointsConditions.or(pointsConditions);

        if((pointsFrom == null && pointsTo != null)
        || (pointsFrom != null && pointsFrom <= 0))
            emptyPointsConditions.or(QPerson.person.offences.isEmpty());

        conditions.and(emptyPointsConditions);
        return conditions;
    }

    private String getPESELNumberFromDate(LocalDate date) {
        StringBuilder buildPesel = new StringBuilder();

        //year
        String year = String.valueOf(date.getYear()).substring(2, 3);

        //month
        int monthPesel = date.getMonthValue();
        if (date.isAfter(LocalDate.of(1999, 12, 31)))
            monthPesel += 20;
        String month = String.valueOf(monthPesel);
        if (monthPesel < 10)
            month = "0" + monthPesel;

        //day
        String day = String.valueOf(date.getDayOfMonth());
        if (date.getDayOfMonth() < 10)
            day = "0" + date.getDayOfMonth();

        buildPesel.append(String.valueOf(date.getYear()).substring(2, 4))
                .append(month)
                .append(day)
                //fill pesel to 11 chars
                .append("00000");
        return buildPesel.toString();
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
