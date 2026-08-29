package ch.jtaf.ui.usecase.report;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.domain.data.FastestRunnersData;
import ch.jtaf.ui.AbstractViewTest;
import ch.jtaf.ui.DashboardView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

/**
 * UC-100: View fastest runners.
 * <p>
 * See {@code docs/use_cases/uc-100-view-fastest-runners.md}.
 */
class UC100ViewFastestRunnersTest extends AbstractViewTest {

	@BeforeEach
	void login() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));
		navigate(DashboardView.class);
	}

	@Test
	@SuppressWarnings("unchecked")
	void fastest_runners_are_ranked_per_gender() {
		find(Button.class).id("fastest-runners-1-1").click();

		H1 h1 = find(H1.class).id("view-title");
		assertThat(h1.getText()).startsWith("Fastest Runners");

		Grid<FastestRunnersData.RankedRunner> maleGrid = find(Grid.class).id("fastest-runners-m");
		Grid<FastestRunnersData.RankedRunner> femaleGrid = find(Grid.class).id("fastest-runners-f");

		assertThat(test(maleGrid).size()).isPositive();
		assertThat(test(femaleGrid).size()).isPositive();

		var first = test(maleGrid).getRow(0);
		var second = test(maleGrid).getRow(1);
		assertThat(first.rank()).isEqualTo(1);
		assertThat(first.runner().gender()).isEqualTo("M");
		assertThat(first.runner().distance()).isIn(60, 80);
		assertThat(first.runner().normalizedTime().orElseThrow())
			.isLessThanOrEqualTo(second.runner().normalizedTime().orElseThrow());

		assertThat(test(femaleGrid).getRow(0).runner().gender()).isEqualTo("F");
	}

	@Test
	void refresh_reloads_the_ranking() {
		find(Button.class).id("fastest-runners-1-1").click();

		assertThatNoException().isThrownBy(() -> find(Button.class).id("refresh").click());
	}

}
