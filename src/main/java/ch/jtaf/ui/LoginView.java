package ch.jtaf.ui;

import ch.jtaf.configuration.security.SecurityContext;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.router.*;

import java.io.Serial;

@Route
@PageTitle("JTAF - Login")
public class LoginView extends LoginOverlay implements AfterNavigationObserver, BeforeEnterObserver {

    @Serial
    private static final long serialVersionUID = 1L;

    private final transient SecurityContext securityContext;

    public LoginView(SecurityContext securityContext) {
        this.securityContext = securityContext;

        var i18n = LoginI18n.createDefault();

        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("JTAF");
        i18n.getHeader().setDescription("Track and Field");
        i18n.setAdditionalInformation(null);

        i18n.setForm(new LoginI18n.Form());
        i18n.getForm().setSubmit(getTranslation("Sign.in"));
        i18n.getForm().setTitle(getTranslation("Sign.in"));
        i18n.getForm().setUsername(getTranslation("Email"));
        i18n.getForm().setPassword(getTranslation("Password"));

        i18n.getErrorMessage().setTitle(getTranslation("Auth.ErrorTitle"));
        i18n.getErrorMessage().setMessage(getTranslation("Auth.ErrorMessage"));

        setI18n(i18n);

        setForgotPasswordButtonVisible(false);

        setAction("login");

        UI.getCurrent().getPage().executeJs("document.getElementById('vaadinLoginUsername').focus();");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (securityContext.isUserLoggedIn()) {
            event.forwardTo(DashboardView.class);
        }
        else {
            setOpened(true);
        }
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
    }

}
