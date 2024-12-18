package ch.jtaf.ui.dialog;

import ch.jtaf.db.tables.records.EventRecord;
import ch.jtaf.domain.EventDAO;
import ch.jtaf.domain.EventType;
import ch.jtaf.domain.Gender;
import ch.jtaf.ui.validator.NotEmptyValidator;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.converter.StringToDoubleConverter;

import java.io.Serial;

public class EventDialog extends EditDialog<EventRecord> {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String MUST_BE_A_NUMBER = "Must.be.a.number";

    public EventDialog(String title, EventDAO eventDAO) {
        super(title, "600px", eventDAO);
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void createForm() {
        var abbreviation = new TextField(getTranslation("Abbreviation"));
        abbreviation.setAutoselect(true);
        abbreviation.setAutofocus(true);
        abbreviation.setRequiredIndicatorVisible(true);

        binder.forField(abbreviation)
            .withValidator(new NotEmptyValidator(this))
            .bind(EventRecord::getAbbreviation, EventRecord::setAbbreviation);

        var name = new TextField(getTranslation("Name"));
        name.setAutoselect(true);
        name.setRequiredIndicatorVisible(true);

        binder.forField(name)
            .withValidator(new NotEmptyValidator(this))
            .bind(EventRecord::getName, EventRecord::setName);

        var gender = new Select<String>();
        gender.setLabel(getTranslation("Gender"));
        gender.setRequiredIndicatorVisible(true);
        gender.setItems(Gender.valuesAsStrings());

        binder.forField(gender)
            .bind(EventRecord::getGender, EventRecord::setGender);

        var eventType = new Select<String>();
        eventType.setLabel(getTranslation("Event.Type"));
        eventType.setRequiredIndicatorVisible(true);
        eventType.setItems(EventType.valuesAsStrings());

        binder.forField(eventType)
            .withValidator(new NotEmptyValidator(this))
            .bind(EventRecord::getEventType, EventRecord::setEventType);

        var a = new TextField("A");
        a.setAutoselect(true);
        a.setRequiredIndicatorVisible(true);

        binder.forField(a)
            .withConverter(new StringToDoubleConverter(getTranslation(MUST_BE_A_NUMBER)))
            .withNullRepresentation(0.0d)
            .bind(EventRecord::getA, EventRecord::setA);

        var b = new TextField("B");
        b.setAutoselect(true);
        b.setRequiredIndicatorVisible(true);

        binder.forField(b)
            .withConverter(new StringToDoubleConverter(getTranslation(MUST_BE_A_NUMBER)))
            .withNullRepresentation(0.0d)
            .bind(EventRecord::getB, EventRecord::setB);

        var c = new TextField("C");
        c.setAutoselect(true);
        c.setRequiredIndicatorVisible(true);

        binder.forField(c)
            .withConverter(new StringToDoubleConverter(getTranslation(MUST_BE_A_NUMBER)))
            .withNullRepresentation(0.0d)
            .bind(EventRecord::getC, EventRecord::setC);

        formLayout.add(abbreviation, name, gender, eventType, a, b, c);
    }
}
