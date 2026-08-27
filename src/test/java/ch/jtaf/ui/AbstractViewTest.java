package ch.jtaf.ui;

import ch.jtaf.TestcontainersConfiguration;
import ch.jtaf.db.tables.records.OrganizationRecord;
import ch.jtaf.db.tables.records.SeriesRecord;
import com.vaadin.browserless.SpringBrowserlessTest;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
public abstract class AbstractViewTest extends SpringBrowserlessTest {

	@SuppressWarnings("unused")
	@MockitoBean
	private JavaMailSender javaMailSender;

	@BeforeAll
	static void setDefaultLocale() {
		Locale.setDefault(Locale.ENGLISH);
	}

	@Override
	protected Set<String> scanPackages() {
		return Set.of("ch.jtaf.ui");
	}

	@Override
	@BeforeEach
	protected void initVaadinEnvironment() {
		// Defer Vaadin setup so subclasses can establish the security context
		// before the framework navigates to the initial route.
	}

	@AfterEach
	void clearSecurityContext() {
		SecurityContextHolder.clearContext();
	}

	protected void login(String user, String pass, List<String> roles) {
		List<SimpleGrantedAuthority> authorities = roles.stream()
			.map(it -> new SimpleGrantedAuthority("ROLE_" + it))
			.toList();

		UserDetails userDetails = new User(user, pass, authorities);
		UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(userDetails, pass,
				authorities);
		SecurityContextHolder.getContext().setAuthentication(authReq);

		setupVaadin();
	}

	protected void setupVaadin() {
		super.initVaadinEnvironment();
	}

	protected static Button gridHeaderButton(Grid<?> grid, String columnKey) {
		return (Button) grid.getColumnByKey(columnKey).getHeaderComponent();
	}

	/**
	 * Returns the button with the given id from a column header that holds several
	 * buttons.
	 */
	protected static Button gridHeaderButton(Grid<?> grid, String columnKey, String buttonId) {
		return grid.getColumnByKey(columnKey)
			.getHeaderComponent()
			.getChildren()
			.filter(Button.class::isInstance)
			.map(Button.class::cast)
			.filter(button -> button.getId().filter(buttonId::equals).isPresent())
			.findFirst()
			.orElseThrow(() -> new AssertionError("No button with id " + buttonId + " in header of " + columnKey));
	}

	protected Grid<SeriesRecord> navigateToSeriesList() {
		navigate(OrganizationsView.class);

		H1 h1 = find(H1.class).id("view-title");
		assertThat(h1.getText()).isEqualTo("Organizations");

		Grid<OrganizationRecord> organizationsGrid = find(Grid.class).id("organizations-grid");
		assertThat(test(organizationsGrid).size()).isEqualTo(2);

		test(organizationsGrid).getCellComponent(0, "edit-column")
			.getChildren()
			.filter(Button.class::isInstance)
			.findFirst()
			.map(Button.class::cast)
			.ifPresent(Button::click);

		h1 = find(H1.class).id("view-title");
		assertThat(h1.getText()).isEqualTo("Series");

		Grid<SeriesRecord> seriesGrid = find(Grid.class).id("series-grid");
		assertThat(test(seriesGrid).size()).isEqualTo(2);

		SeriesRecord seriesRecord = test(seriesGrid).getRow(0);
		assertThat(seriesRecord.getName()).isEqualTo("CIS 2019");

		return seriesGrid;
	}

}
