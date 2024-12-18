package ch.jtaf.ui.dialog;

import ch.jtaf.db.tables.records.CompetitionRecord;
import ch.jtaf.domain.CompetitionDAO;
import ch.jtaf.ui.validator.NotEmptyValidator;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.textfield.TextField;

import java.io.Serial;

public class CompetitionDialog extends EditDialog<CompetitionRecord> {

    @Serial
    private static final long serialVersionUID = 1L;

    public CompetitionDialog(String title, CompetitionDAO competitionDAO) {
        super(title, "600px", competitionDAO);
    }

    @Override
    public void createForm() {
        var name = new TextField(getTranslation("Name"));
        name.setAutoselect(true);
        name.setAutofocus(true);
        name.setRequiredIndicatorVisible(true);

        binder.forField(name)
            .withValidator(new NotEmptyValidator(this))
            .bind(CompetitionRecord::getName, CompetitionRecord::setName);

        var date = new DatePicker(getTranslation("Date"));
        name.setRequiredIndicatorVisible(true);

        binder.forField(date)
            .bind(CompetitionRecord::getCompetitionDate, CompetitionRecord::setCompetitionDate);

        var alwaysFirstThreeMedals = new Checkbox(getTranslation("Always.first.three.medals"));

        binder.forField(alwaysFirstThreeMedals)
            .bind(CompetitionRecord::getAlwaysFirstThreeMedals, CompetitionRecord::setAlwaysFirstThreeMedals);

        formLayout.add(name, date);
    }
}
