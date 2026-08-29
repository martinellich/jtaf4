package ch.jtaf.ui.dialog;

import ch.jtaf.db.tables.records.AthleteRecord;
import ch.jtaf.domain.AthleteDAO;
import ch.jtaf.ui.validator.NotEmptyValidator;
import com.vaadin.flow.component.textfield.TextField;

import java.io.Serial;

/**
 * Lets the user correct a misspelled name during result entry. Gender, year of birth and
 * club are deliberately not editable here because they determine the category the athlete
 * is enrolled in.
 */
public class AthleteNameDialog extends EditDialog<AthleteRecord> {

	@Serial
	private static final long serialVersionUID = 1L;

	public AthleteNameDialog(String title, AthleteDAO athleteDAO) {
		super(title, "400px", athleteDAO);
	}

	@Override
	public void createForm() {
		var lastName = new TextField(getTranslation("Last.Name"));
		lastName.setId("athlete-last-name");
		lastName.setAutoselect(true);
		lastName.setAutofocus(true);
		lastName.setRequiredIndicatorVisible(true);
		lastName.focus();

		binder.forField(lastName)
			.withValidator(new NotEmptyValidator(this))
			.bind(AthleteRecord::getLastName, AthleteRecord::setLastName);

		var firstName = new TextField(getTranslation("First.Name"));
		firstName.setId("athlete-first-name");
		firstName.setAutoselect(true);
		firstName.setRequiredIndicatorVisible(true);

		binder.forField(firstName)
			.withValidator(new NotEmptyValidator(this))
			.bind(AthleteRecord::getFirstName, AthleteRecord::setFirstName);

		formLayout.add(lastName, firstName);
	}

}
