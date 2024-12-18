package ch.jtaf.ui.dialog;

import ch.jtaf.configuration.security.OrganizationProvider;
import ch.jtaf.db.tables.records.AthleteRecord;
import ch.jtaf.db.tables.records.ClubRecord;
import ch.jtaf.domain.AthleteDAO;
import ch.jtaf.domain.ClubDAO;
import ch.jtaf.domain.Gender;
import ch.jtaf.ui.converter.JtafStringToIntegerConverter;
import ch.jtaf.ui.validator.NotEmptyValidator;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

import java.io.Serial;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AthleteDialog extends EditDialog<AthleteRecord> {

    @Serial
    private static final long serialVersionUID = 1L;

    private final transient OrganizationProvider organizationProvider;
    private final transient ClubDAO clubDAO;

    private Map<Long, ClubRecord> clubRecordMap = new HashMap<>();

    public AthleteDialog(String title, AthleteDAO athleteDAO, ClubDAO clubDAO, OrganizationProvider organizationProvider) {
        super(title, "600px", athleteDAO);
        this.clubDAO = clubDAO;
        this.organizationProvider = organizationProvider;
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void createForm() {
        var lastName = new TextField(getTranslation("Last.Name"));
        lastName.setAutoselect(true);
        lastName.setAutofocus(true);
        lastName.setRequiredIndicatorVisible(true);
        lastName.focus();

        binder.forField(lastName)
            .withValidator(new NotEmptyValidator(this))
            .bind(AthleteRecord::getLastName, AthleteRecord::setLastName);

        var firstName = new TextField(getTranslation("First.Name"));
        firstName.setAutoselect(true);
        firstName.setRequiredIndicatorVisible(true);

        binder.forField(firstName)
            .withValidator(new NotEmptyValidator(this))
            .bind(AthleteRecord::getFirstName, AthleteRecord::setFirstName);

        var gender = new Select<String>();
        gender.setLabel(getTranslation("Gender"));
        gender.setRequiredIndicatorVisible(true);
        gender.setItems(Gender.valuesAsStrings());

        binder.forField(gender)
            .bind(AthleteRecord::getGender, AthleteRecord::setGender);

        var yearOfBirth = new TextField(getTranslation("Year"));
        yearOfBirth.setAutoselect(true);
        lastName.setRequiredIndicatorVisible(true);

        binder.forField(yearOfBirth)
            .withConverter(new JtafStringToIntegerConverter(getTranslation("Must.be.a.number")))
            .withNullRepresentation(0)
            .bind(AthleteRecord::getYearOfBirth, AthleteRecord::setYearOfBirth);

        var club = new Select<ClubRecord>();
        club.setLabel(getTranslation("Club"));
        club.setItemLabelGenerator(item -> "%s %s".formatted(item.getAbbreviation(), item.getName()));
        club.setItems(getClubs());

        binder.forField(club)
            .withConverter(new Converter<ClubRecord, Long>() {
                @Override
                public Result<Long> convertToModel(ClubRecord value, ValueContext context) {
                    return Result.ok(value == null ? null : value.getId());
                }

                @Override
                public ClubRecord convertToPresentation(Long value, ValueContext context) {
                    return clubRecordMap.get(value);
                }
            })
            .bind(AthleteRecord::getClubId, AthleteRecord::setClubId);

        var year = new TextField("Year");
        year.setAutoselect(true);
        year.setRequiredIndicatorVisible(true);

        binder.forField(year)
            .withConverter(new JtafStringToIntegerConverter(getTranslation("Must.be.a.number")))
            .withNullRepresentation(0)
            .bind(AthleteRecord::getYearOfBirth, AthleteRecord::setYearOfBirth);

        formLayout.add(lastName, firstName, gender, year, club);
    }

    private List<ClubRecord> getClubs() {
        var organizationRecord = organizationProvider.getOrganization();

        if (organizationRecord == null) {
            return Collections.emptyList();
        } else {
            var clubs = clubDAO.findByOrganizationId(organizationRecord.getId());
            clubRecordMap = clubs.stream().collect(Collectors.toMap(ClubRecord::getId, clubRecord -> clubRecord));
            return clubs;
        }
    }

}
