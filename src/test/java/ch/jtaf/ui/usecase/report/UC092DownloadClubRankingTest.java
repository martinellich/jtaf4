package ch.jtaf.ui.usecase.report;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.ui.AbstractViewTest;
import ch.jtaf.ui.DashboardView;
import com.vaadin.flow.component.html.Anchor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatNoException;

/**
 * UC-092: Download club ranking.
 * <p>
 * See {@code docs/use_cases/uc-092-download-club-ranking.md}.
 */
@SuppressWarnings("java:S2699")
class UC092DownloadClubRankingTest extends AbstractViewTest {

	@BeforeEach
	void login() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));
		navigate(DashboardView.class);
	}

	@Test
	void club_ranking() {
		assertThatNoException()
			.isThrownBy(() -> test(find(Anchor.class).id("club-ranking-1")).download(new ByteArrayOutputStream()));
	}

}
