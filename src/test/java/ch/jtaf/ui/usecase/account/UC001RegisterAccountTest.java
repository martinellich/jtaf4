package ch.jtaf.ui.usecase.account;

import ch.jtaf.ui.AbstractViewTest;
import ch.jtaf.ui.RegisterView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UC-001: Register account.
 * <p>
 * See {@code docs/use_cases/uc-001-register-account.md}.
 */
class UC001RegisterAccountTest extends AbstractViewTest {

	@Test
	void register() {
		setupVaadin();

		navigate(RegisterView.class);

		test(find(TextField.class).withCaption("First Name").single()).setValue("John");
		test(find(TextField.class).withCaption("Last Name").single()).setValue("Doe");
		test(find(EmailField.class).withCaption("Email").single()).setValue("john@doe.dev");
		test(find(PasswordField.class).withCaption("Password").single()).setValue("pass");
		test(find(Button.class).withText("Register").single()).click();

		Notification notification = find(Notification.class).single();
		assertThat(test(notification).getText())
			.isEqualTo("Thanks for registering. An email was sent to your address. Please check your inbox.");
	}

}
