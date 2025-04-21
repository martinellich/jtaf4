package ch.jtaf.ui.view;

import ch.jtaf.configuration.security.OrganizationProvider;
import ch.jtaf.configuration.security.Role;
import ch.jtaf.db.tables.records.OrganizationRecord;
import ch.jtaf.ui.KaribuTest;
import ch.jtaf.ui.OrganizationsView;
import ch.jtaf.ui.dialog.ConfirmDialog;
import ch.jtaf.ui.dialog.OrganizationDialog;
import com.github.mvysny.fakeservlet.FakeRequest;
import com.github.mvysny.kaributesting.v10.GridKt;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinServletRequest;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.mvysny.kaributesting.v10.LocatorJ._assert;
import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static org.assertj.core.api.Assertions.assertThat;

class OrganizationsViewTest extends KaribuTest {

	@BeforeEach
	public void login() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));
	}

	@Test
	void add_organization() {
		UI.getCurrent().navigate(OrganizationsView.class);

		H1 h1 = _get(H1.class, spec -> spec.withId("view-title"));
		assertThat(h1.getText()).isEqualTo("Organizations");

		// Check content of organizations grid
		Grid<OrganizationRecord> organizationGrid = _get(Grid.class, spec -> spec.withId("organizations-grid"));
		assertThat(GridKt._size(organizationGrid)).isEqualTo(2);
		assertThat(GridKt._get(organizationGrid, 0).getOrganizationKey()).isEqualTo("CIS");

		// Add organization
		_get(Button.class, spec -> spec.withId("add-button")).click();
		_assert(OrganizationDialog.class, 1);

		_get(TextField.class, spec -> spec.withLabel("Key")).setValue("AAA");
		_get(TextField.class, spec -> spec.withLabel("Name")).setValue("Test");
		_get(Button.class, spec -> spec.withText("Save")).click();

		// Check if organization was added
		assertThat(GridKt._size(organizationGrid)).isEqualTo(3);
		assertThat(GridKt._get(organizationGrid, 2).getOrganizationKey()).isEqualTo("AAA");

		// Remove organization
		GridKt._getCellComponent(organizationGrid, 2, "edit-column")
			.getChildren()
			.filter(component -> component instanceof Button button && button.getText().equals("Delete"))
			.findFirst()
			.map(Button.class::cast)
			.ifPresent(Button::click);

		ConfirmDialog confirmDialog = _get(ConfirmDialog.class);
		assertThat(confirmDialog.isOpened()).isTrue();
		_get(Button.class, spec -> spec.withId("delete-organization-confirm-dialog-confirm")).click();

		// Check if organization was removed
		assertThat(GridKt._size(organizationGrid)).isEqualTo(2);
	}

	@Test
	void with_cookie() {
		FakeRequest request = (FakeRequest) VaadinServletRequest.getCurrent().getRequest();
		request.addCookie(new Cookie(OrganizationProvider.JTAF_ORGANIZATION_ID, "1"));

		UI.getCurrent().navigate(OrganizationsView.class);

		// Check if series from Cookie was loaded
		RouterLink routerLink = _get(RouterLink.class, spec -> spec.withId("series-list-link"));
		assertThat(routerLink.getText()).isEqualTo("CIS");
	}

}
