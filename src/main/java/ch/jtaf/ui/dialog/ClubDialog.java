package ch.jtaf.ui.dialog;

import ch.jtaf.db.tables.records.ClubRecord;
import ch.jtaf.domain.ClubDAO;
import ch.jtaf.ui.validator.NotEmptyValidator;
import com.vaadin.flow.component.textfield.TextField;

import java.io.Serial;

public class ClubDialog extends EditDialog<ClubRecord> {

    @Serial
    private static final long serialVersionUID = 1L;

    public ClubDialog(String title, ClubDAO clubDAO) {
        super(title, "600px", clubDAO);
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
            .bind(ClubRecord::getAbbreviation, ClubRecord::setAbbreviation);

        var name = new TextField(getTranslation("Name"));
        name.setAutoselect(true);
        name.setRequiredIndicatorVisible(true);

        binder.forField(name)
            .withValidator(new NotEmptyValidator(this))
            .bind(ClubRecord::getName, ClubRecord::setName);

        formLayout.add(abbreviation, name);
    }
}
