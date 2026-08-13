package ch.jtaf.ui;

import ch.jtaf.domain.UserAlreadyExistException;
import ch.jtaf.domain.UserService;
import ch.jtaf.ui.validator.NotEmptyValidator;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@AnonymousAllowed
@Route(value = "register")
public class RegisterView extends VerticalLayout implements HasDynamicTitle {

	public RegisterView(UserService userService) {
		addClassName("p-xl");

		var formLayout = new FormLayout();
		add(formLayout);

		var firstName = new TextField(getTranslation("First.Name"));
		firstName.setAutoselect(true);
		firstName.setAutofocus(true);
		firstName.setRequired(true);

		var lastName = new TextField(getTranslation("Last.Name"));
		lastName.setAutoselect(true);
		lastName.setRequired(true);

		var email = new EmailField(getTranslation("Email"));
		email.setRequiredIndicatorVisible(true);
		email.setAutoselect(true);

		var password = new PasswordField(getTranslation("Password"));
		password.setAutoselect(true);
		password.setRequired(true);

		var binder = new Binder<Registration>();
		binder.forField(firstName)
			.withValidator(new NotEmptyValidator(this))
			.bind(Registration::getFirstName, Registration::setFirstName);
		binder.forField(lastName)
			.withValidator(new NotEmptyValidator(this))
			.bind(Registration::getLastName, Registration::setLastName);
		binder.forField(email)
			.withValidator(new NotEmptyValidator(this))
			.withValidator(new EmailValidator(getTranslation("Invalid.email")))
			.bind(Registration::getEmail, Registration::setEmail);
		binder.forField(password)
			.withValidator(new NotEmptyValidator(this))
			.bind(Registration::getPassword, Registration::setPassword);
		binder.setBean(new Registration());

		var register = new Button(getTranslation("Register"), e -> {
			if (!binder.validate().isOk()) {
				return;
			}
			try {
				var registration = binder.getBean();
				userService.createUser(registration.getFirstName(), registration.getLastName(), registration.getEmail(),
						registration.getPassword(), getLocale());

				Notification.show(getTranslation("Email.sent"), 5000, Notification.Position.TOP_END);
				UI.getCurrent().navigate(DashboardView.class);
			}
			catch (UserAlreadyExistException _) {
				Notification.show(getTranslation("User.already.exist"), 5000, Notification.Position.TOP_END);
			}
		});

		formLayout.add(firstName, lastName, email, password, register);

		firstName.focus();
	}

	@Override
	public String getPageTitle() {
		return getTranslation("Register");
	}

	private static class Registration {

		private String firstName = "";

		private String lastName = "";

		private String email = "";

		private String password = "";

		String getFirstName() {
			return firstName;
		}

		void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		String getLastName() {
			return lastName;
		}

		void setLastName(String lastName) {
			this.lastName = lastName;
		}

		String getEmail() {
			return email;
		}

		void setEmail(String email) {
			this.email = email;
		}

		String getPassword() {
			return password;
		}

		void setPassword(String password) {
			this.password = password;
		}

	}

}
