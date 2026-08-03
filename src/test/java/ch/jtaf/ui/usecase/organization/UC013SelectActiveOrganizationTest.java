package ch.jtaf.ui.usecase.organization;

import ch.jtaf.configuration.security.OrganizationProvider;
import ch.jtaf.configuration.security.Role;
import ch.jtaf.ui.AbstractViewTest;
import ch.jtaf.ui.OrganizationsView;
import com.vaadin.browserless.mocks.MockRequest;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinServletRequest;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletRequestWrapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UC-013: Select active organization.
 * <p>
 * See {@code docs/use_cases/uc-013-select-active-organization.md}.
 */
class UC013SelectActiveOrganizationTest extends AbstractViewTest {

	@BeforeEach
	void login() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));
	}

	@Test
	void with_cookie() {
		MockRequest mockRequest = unwrapMockRequest(VaadinServletRequest.getCurrent().getRequest());
		mockRequest.addCookie(new Cookie(OrganizationProvider.JTAF_ORGANIZATION_ID, "1"));

		navigate(OrganizationsView.class);

		// Check if series from Cookie was loaded
		RouterLink routerLink = find(RouterLink.class).id("series-list-link");
		assertThat(routerLink.getText()).isEqualTo("CIS");
	}

	private static MockRequest unwrapMockRequest(ServletRequest request) {
		ServletRequest current = request;
		while (current != null) {
			if (current instanceof MockRequest mock) {
				return mock;
			}
			if (current instanceof ServletRequestWrapper wrapper) {
				current = wrapper.getRequest();
			}
			else {
				break;
			}
		}
		throw new IllegalStateException("Could not find underlying MockRequest");
	}

}
