package ch.jtaf.ui.view;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.ui.KaribuTest;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.mvysny.kaributesting.v10.DownloadKt._download;
import static com.github.mvysny.kaributesting.v10.LocatorJ._assert;
import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

@SuppressWarnings("java:S2699")
class DashboardViewTest extends KaribuTest {

	@BeforeEach
	public void login() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));
		UI.getCurrent().getPage().reload();
	}

	@Test
	void title_is_dashboard() {
		H1 h1 = _get(H1.class, spec -> spec.withId("view-title"));
		assertThat(h1.getText()).isEqualTo("Dashboard");
	}

	@Test
	void series_are_displayed() {
		_assert(HorizontalLayout.class, 4, spec -> spec.withClasses("series-layout"));
	}

	@Test
	void series_ranking() {
		assertThatNoException()
			.isThrownBy(() -> _download(_get(Anchor.class, spec -> spec.withId("series-ranking-1"))));
	}

	@Test
	void club_ranking() {
		assertThatNoException().isThrownBy(() -> _download(_get(Anchor.class, spec -> spec.withId("club-ranking-1"))));
	}

	@Test
	void competition_ranking() {
		assertThatNoException()
			.isThrownBy(() -> _download(_get(Anchor.class, spec -> spec.withId("competition-ranking-1-1"))));
	}

	@Test
	void diploma() {
		assertThatNoException().isThrownBy(() -> _download(_get(Anchor.class, spec -> spec.withId("diploma-1-1"))));
	}

	@Test
	void event_ranking() {
		assertThatNoException()
			.isThrownBy(() -> _download(_get(Anchor.class, spec -> spec.withId("event-ranking-1-1"))));
	}

}
