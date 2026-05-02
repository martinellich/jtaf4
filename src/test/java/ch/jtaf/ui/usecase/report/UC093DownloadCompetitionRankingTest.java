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
 * UC-093: Download competition ranking.
 * <p>
 * See {@code docs/use_cases/uc-093-download-competition-ranking.md}.
 */
@SuppressWarnings("java:S2699")
class UC093DownloadCompetitionRankingTest extends AbstractViewTest {

	@BeforeEach
	void login() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));
		navigate(DashboardView.class);
	}

	@Test
	void competition_ranking() {
		assertThatNoException().isThrownBy(
				() -> test($(Anchor.class).id("competition-ranking-1-1")).download(new ByteArrayOutputStream()));
	}

}
