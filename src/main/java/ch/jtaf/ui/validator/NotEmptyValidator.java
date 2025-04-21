package ch.jtaf.ui.validator;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.data.validator.StringLengthValidator;

import java.io.Serial;

public class NotEmptyValidator extends StringLengthValidator {

	@Serial
	private static final long serialVersionUID = 1L;

	public NotEmptyValidator(Component component) {
		super(component.getTranslation("May.not.be.empty"), 1, null);
	}

}
