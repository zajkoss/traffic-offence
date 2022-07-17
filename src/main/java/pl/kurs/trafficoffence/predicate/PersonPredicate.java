package pl.kurs.trafficoffence.predicate;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;
import pl.kurs.trafficoffence.model.Person;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;


public class PersonPredicate {

    private SearchCriteria criteria;

    public PersonPredicate(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    public BooleanExpression getPredicate() {
        PathBuilder<Person> entityPath = new PathBuilder<>(Person.class, "person");

        if (isNumeric(criteria.getValue().toString()) && !criteria.getKey().equalsIgnoreCase("pesel")) {
            NumberPath<Integer> path = entityPath.getNumber(criteria.getKey(), Integer.class);
            int value = Integer.parseInt(criteria.getValue().toString());
            switch (criteria.getOperation()) {
                case ":":
                    return path.eq(value);
                case ">":
                    return path.goe(value);
                case "<":
                    return path.loe(value);
            }

        } else {
            StringPath path = entityPath.getString(criteria.getKey());
            if (criteria.getKey().equalsIgnoreCase("birthday")) {
                path = entityPath.getString("pesel");
                try {
                    LocalDate localDate = LocalDate.parse((CharSequence) criteria.getValue());
                    String peselNumberFromDate = getPESELNumberFromDate(localDate);
                    switch (criteria.getOperation()) {
                        case ">":
                            return path.goe(peselNumberFromDate);
                        case "<":
                            return path.loe(peselNumberFromDate);
                    }
                } catch (DateTimeParseException de) {
                    return null;
                }

            } else if (criteria.getOperation().equalsIgnoreCase(":")) {
                return path.containsIgnoreCase(criteria.getValue().toString());
            }
        }
        return null;
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

        buildPesel.append(String.valueOf(String.valueOf(date.getYear()).substring(2, 4)))
                .append(month)
                .append(day)
                //fill pesel to 11 chars
                .append("00000");
        return buildPesel.toString();
    }

    private static boolean isNumeric(final String str) {
        try {
            Integer.parseInt(str);
        } catch (final NumberFormatException e) {
            return false;
        }
        return true;
    }
}
