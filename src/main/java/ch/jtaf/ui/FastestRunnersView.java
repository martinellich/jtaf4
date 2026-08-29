package ch.jtaf.ui;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.domain.CompetitionDAO;
import ch.jtaf.domain.CompetitionRankingService;
import ch.jtaf.domain.data.FastestRunnersData;
import ch.jtaf.ui.component.MaterialSymbol;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.io.Serial;
import java.util.List;
import java.util.Locale;

import static ch.jtaf.db.tables.Competition.COMPETITION;

/**
 * Shows the fastest runners of a competition across the 80 m and 60 m sprints, separately
 * for men and women. 60 m times are levelled to 80 m (× 80/60) so both events can be
 * ranked together, e.g. to select the participants of the sprint finals.
 */
@RolesAllowed({ Role.USER, Role.ADMIN })
@Route
public class FastestRunnersView extends VerticalLayout implements HasDynamicTitle, HasUrlParameter<String> {

	@Serial
	private static final long serialVersionUID = 1L;

	private static final String MALE = "M";

	private static final String FEMALE = "F";

	private final transient CompetitionRankingService competitionRankingService;

	private final transient CompetitionDAO competitionDAO;

	private final Grid<FastestRunnersData.RankedRunner> maleGrid = createGrid("fastest-runners-m");

	private final Grid<FastestRunnersData.RankedRunner> femaleGrid = createGrid("fastest-runners-f");

	private long competitionId;

	public FastestRunnersView(CompetitionRankingService competitionRankingService, CompetitionDAO competitionDAO) {
		this.competitionRankingService = competitionRankingService;
		this.competitionDAO = competitionDAO;

		var refresh = new Button(getTranslation("Refresh"), MaterialSymbol.REFRESH.create());
		refresh.setId("refresh");
		refresh.addClickListener(event -> load());
		add(refresh);

		add(new H3(getTranslation("Runners.Male")), maleGrid);
		add(new H3(getTranslation("Runners.Female")), femaleGrid);
	}

	private Grid<FastestRunnersData.RankedRunner> createGrid(String id) {
		var grid = new Grid<FastestRunnersData.RankedRunner>();
		grid.setId(id);
		grid.setAllRowsVisible(true);

		grid.addColumn(FastestRunnersData.RankedRunner::rank)
			.setHeader(getTranslation("Rank"))
			.setAutoWidth(true)
			.setFlexGrow(0)
			.setKey("rank");
		grid.addColumn(ranked -> ranked.runner().lastName())
			.setHeader(getTranslation("Last.Name"))
			.setAutoWidth(true)
			.setKey("lastName");
		grid.addColumn(ranked -> ranked.runner().firstName())
			.setHeader(getTranslation("First.Name"))
			.setAutoWidth(true)
			.setKey("firstName");
		grid.addColumn(ranked -> ranked.runner().yearOfBirth())
			.setHeader(getTranslation("Year"))
			.setAutoWidth(true)
			.setFlexGrow(0)
			.setKey("yearOfBirth");
		grid.addColumn(ranked -> ranked.runner().category())
			.setHeader(getTranslation("Category"))
			.setAutoWidth(true)
			.setFlexGrow(0)
			.setKey("category");
		grid.addColumn(ranked -> ranked.runner().club())
			.setHeader(getTranslation("Club"))
			.setAutoWidth(true)
			.setKey("club");
		grid.addColumn(ranked -> ranked.runner().eventAbbreviation())
			.setHeader(getTranslation("Event"))
			.setAutoWidth(true)
			.setFlexGrow(0)
			.setKey("event");
		grid.addColumn(ranked -> ranked.runner().result())
			.setHeader(getTranslation("Time"))
			.setAutoWidth(true)
			.setFlexGrow(0)
			.setTextAlign(ColumnTextAlign.END)
			.setKey("time");
		grid.addColumn(ranked -> formatTime(ranked.runner()))
			.setHeader(getTranslation("Time.80m"))
			.setAutoWidth(true)
			.setFlexGrow(0)
			.setTextAlign(ColumnTextAlign.END)
			.setKey("normalizedTime");

		return grid;
	}

	private static String formatTime(FastestRunnersData.Runner runner) {
		var normalizedTime = runner.normalizedTime();
		return normalizedTime.isPresent() ? String.format(Locale.ROOT, "%.2f", normalizedTime.getAsDouble()) : "";
	}

	private void load() {
		var data = competitionRankingService.getFastestRunners(competitionId);
		maleGrid.setItems(data.map(d -> d.ranking(MALE)).orElseGet(List::of));
		femaleGrid.setItems(data.map(d -> d.ranking(FEMALE)).orElseGet(List::of));
	}

	@Override
	public String getPageTitle() {
		return competitionDAO.findProjectionById(competitionId)
			.map(record -> "%s | %s - %s".formatted(getTranslation("Fastest.Runners"),
					record.get(COMPETITION.series().NAME), record.get(COMPETITION.NAME)))
			.orElseGet(() -> getTranslation("Fastest.Runners"));
	}

	@Override
	public void setParameter(BeforeEvent event, String parameter) {
		competitionId = Long.parseLong(parameter);
		load();
	}

}
