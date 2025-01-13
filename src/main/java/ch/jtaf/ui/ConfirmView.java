package ch.jtaf.ui;

import ch.jtaf.domain.UserService;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@SuppressWarnings("unused")
@AnonymousAllowed
@Route(value = "confirm", layout = MainLayout.class)
public class ConfirmView extends VerticalLayout implements HasDynamicTitle, BeforeEnterObserver {

    private final transient UserService userService;

    private final VerticalLayout okDiv;

    private final H1 failure;

    public ConfirmView(UserService userService) {
        this.userService = userService;

        okDiv = new VerticalLayout();
        okDiv.add(new H1(getTranslation("Confirm.success")));
        okDiv.add(new RouterLink("Login", OrganizationsView.class));
        okDiv.setVisible(false);
        add(okDiv);

        failure = new H1(getTranslation("Confirm.failure"));
        add(failure);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        var queryParameters = beforeEnterEvent.getLocation().getQueryParameters();
        if (queryParameters.getParameters().containsKey("cf")) {
            boolean confirmed = userService.confirm(queryParameters.getParameters().get("cf").getFirst());

            if (confirmed) {
                okDiv.setVisible(true);
                failure.setVisible(false);
            }
            else {
                okDiv.setVisible(false);
                failure.setVisible(true);
            }
        }
    }

    @Override
    public String getPageTitle() {
        return getTranslation("Confirm");
    }

}
