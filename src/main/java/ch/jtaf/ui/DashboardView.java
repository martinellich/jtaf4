package ch.jtaf.ui;

import ch.jtaf.configuration.security.SecurityContext;
import ch.jtaf.domain.CompetitionDAO;
import ch.jtaf.domain.CompetitionRankingService;
import ch.jtaf.domain.SeriesDAO;
import ch.jtaf.domain.SeriesRankingService;
import ch.jtaf.ui.component.MaterialSymbol;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.io.Serial;
import java.time.format.DateTimeFormatter;

import static ch.jtaf.ui.util.LogoUtil.resizeLogo;

/**
 * Realizes UC-090 (View dashboard) and the public report downloads UC-091 (series
 * ranking), UC-092 (club ranking) and UC-093 (competition ranking); signed-in users
 * additionally get UC-094 (diplomas), UC-095 (event ranking) and the entry points to
 * UC-080 and UC-100.
 */
@AnonymousAllowed
@Route(value = "")
public class DashboardView extends VerticalLayout implements HasDynamicTitle {

	@Serial
	private static final long serialVersionUID = 1L;

	private static final String NAME_MIN_WIDTH = "300px";

	private static final String BUTTON_WIDTH = "220px";

	@SuppressWarnings("java:S1192")
	public DashboardView(SeriesRankingService seriesRankingService, CompetitionRankingService competitionRankingService,
			SeriesDAO seriesDAO, CompetitionDAO competitionDAO, SecurityContext securityContext) {
		getClassNames().add("dashboard");

		var verticalLayout = new VerticalLayout();
		add(verticalLayout);

		var seriesIndex = 1;
		var seriesRecords = seriesDAO.findAllOrderByCompetitionDate();
		for (var series : seriesRecords) {
			var seriesLayout = new HorizontalLayout();
			seriesLayout.getClassNames().add("series-layout");
			verticalLayout.add(seriesLayout);

			var logo = resizeLogo(series);
			var divLogo = new Div(logo);
			divLogo.setWidth("100px");
			seriesLayout.add(divLogo);

			var pSeriesName = new Paragraph(series.getName());
			pSeriesName.setMinWidth(NAME_MIN_WIDTH);
			seriesLayout.add(pSeriesName);

			var buttonLayout = new HorizontalLayout();
			buttonLayout.getClassNames().add("button-layout");
			seriesLayout.add(buttonLayout);

			var seriesRankingAnchor = new Anchor(event -> {
				event.setContentType("application/pdf");
				event.inline("series_ranking" + series.getId() + ".pdf");
				event.getOutputStream().write(seriesRankingService.getSeriesRankingAsPdf(series.getId(), getLocale()));
			}, "");
			seriesRankingAnchor.setId("series-ranking-" + seriesIndex);
			seriesRankingAnchor.setTarget("_blank");

			var seriesRankingButton = new Button(getTranslation("Series.Ranking"), MaterialSymbol.DOCS.create());
			seriesRankingButton.setWidth(BUTTON_WIDTH);
			seriesRankingButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
			seriesRankingButton.addClassName(LumoUtility.FontWeight.MEDIUM);
			seriesRankingAnchor.add(seriesRankingButton);

			var seriesRankingDiv = new Div(seriesRankingAnchor);
			buttonLayout.add(seriesRankingDiv);

			var clubRankingAnchor = new Anchor(event -> {
				event.setContentType("application/pdf");
				event.inline("club_ranking" + series.getId() + ".pdf");
				event.getOutputStream().write(seriesRankingService.getClubRankingAsPdf(series.getId(), getLocale()));
			}, "");
			clubRankingAnchor.setId("club-ranking-" + seriesIndex);
			clubRankingAnchor.setTarget("_blank");

			var clubRankingButton = new Button(getTranslation("Club.Ranking"), MaterialSymbol.DOCS.create());
			clubRankingButton.setWidth(BUTTON_WIDTH);
			clubRankingButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
			clubRankingButton.addClassName(LumoUtility.FontWeight.MEDIUM);
			clubRankingAnchor.add(clubRankingButton);

			var clubRankingDiv = new Div(clubRankingAnchor);
			buttonLayout.add(clubRankingDiv);

			var competitionIndex = 1;
			var competitionRecords = competitionDAO.findBySeriesId(series.getId());
			for (var competition : competitionRecords) {
				var competitionLayout = new HorizontalLayout();
				competitionLayout.getClassNames().add("competition-layout");
				competitionLayout.setWidthFull();
				verticalLayout.add(competitionLayout);

				var fakeLogo = new Paragraph();
				fakeLogo.setWidth("100px");
				competitionLayout.add(fakeLogo);

				var dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
				var pCompetition = new Paragraph("%s %s".formatted(competition.getName(),
						dateTimeFormatter.format(competition.getCompetitionDate())));
				pCompetition.setMinWidth(NAME_MIN_WIDTH);
				competitionLayout.add(pCompetition);

				var links = new HorizontalLayout();
				links.getClassNames().add("links-layout");
				competitionLayout.add(links);

				var competitionRankingAnchor = new Anchor(event -> {
					event.setContentType("application/pdf");
					event.inline("competition_ranking" + competition.getId() + ".pdf");
					event.getOutputStream()
						.write(competitionRankingService.getCompetitionRankingAsPdf(competition.getId(), getLocale()));
				}, "");
				competitionRankingAnchor.setId("competition-ranking-" + seriesIndex + "-" + competitionIndex);
				competitionRankingAnchor.setTarget("_blank");

				var competitionRankingButton = new Button(getTranslation("Competition.Ranking"),
						MaterialSymbol.DOCS.create());
				competitionRankingButton.setWidth(BUTTON_WIDTH);
				competitionRankingButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
				competitionRankingButton.addClassName(LumoUtility.FontWeight.MEDIUM);
				competitionRankingAnchor.add(competitionRankingButton);

				var competitionRankingDiv = new Div(competitionRankingAnchor);
				links.add(competitionRankingDiv);

				if (securityContext.isUserLoggedIn()) {
					var diplomaAnchor = new Anchor(event -> {
						event.setContentType("application/pdf");
						event.inline("diploma" + competition.getId() + ".pdf");
						event.getOutputStream()
							.write(competitionRankingService.getDiplomasAsPdf(competition.getId(), getLocale()));
					}, "");
					diplomaAnchor.setId("diploma-" + seriesIndex + "-" + competitionIndex);
					diplomaAnchor.setTarget("_blank");

					var diplomaButton = new Button(getTranslation("Diploma"), MaterialSymbol.DOCS.create());
					diplomaButton.setWidth(BUTTON_WIDTH);
					diplomaButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
					diplomaButton.addClassName(LumoUtility.FontWeight.MEDIUM);
					diplomaAnchor.add(diplomaButton);

					var diplomaDiv = new Div(diplomaAnchor);
					links.add(diplomaDiv);

					var eventRankingAnchor = new Anchor(event -> {
						event.setContentType("application/pdf");
						event.inline("event_ranking" + competition.getId() + ".pdf");
						event.getOutputStream()
							.write(competitionRankingService.getEventRankingAsPdf(competition.getId(), getLocale()));
					}, "");
					eventRankingAnchor.setId("event-ranking-" + seriesIndex + "-" + competitionIndex);
					eventRankingAnchor.setTarget("_blank");

					var eventRankingButton = new Button(getTranslation("Event.Ranking"), MaterialSymbol.DOCS.create());
					eventRankingButton.setWidth(BUTTON_WIDTH);
					eventRankingButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
					eventRankingButton.addClassName(LumoUtility.FontWeight.MEDIUM);
					eventRankingAnchor.add(eventRankingButton);

					var eventRankingDiv = new Div(eventRankingAnchor);
					links.add(eventRankingDiv);

					var fastestRunners = new Button(getTranslation("Fastest.Runners"), MaterialSymbol.SPRINT.create());
					fastestRunners.setId("fastest-runners-" + seriesIndex + "-" + competitionIndex);
					fastestRunners.setWidth(BUTTON_WIDTH);
					fastestRunners.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
					fastestRunners.addClassName(LumoUtility.FontWeight.MEDIUM);
					fastestRunners.addClickListener(event -> UI.getCurrent()
						.navigate(FastestRunnersView.class, competition.getId().toString()));
					links.add(new Div(fastestRunners));

					var enterResults = new Button(getTranslation("Enter.Results"), MaterialSymbol.KEYBOARD.create());
					enterResults.setId("enter-results-" + seriesIndex + "-" + competitionIndex);
					enterResults.addThemeVariants(ButtonVariant.LUMO_ERROR);
					enterResults.setWidth(BUTTON_WIDTH);
					enterResults.addClickListener(event -> UI.getCurrent()
						.navigate(ResultCapturingView.class, competition.getId().toString()));
					var enterResultsDiv = new Div(enterResults);
					competitionRankingDiv.add(enterResultsDiv);
				}

				competitionIndex++;
			}
			var hr = new Hr();
			hr.setClassName("dashboard-separator");
			verticalLayout.add(hr);

			seriesIndex++;
		}
	}

	@Override
	public String getPageTitle() {
		return getTranslation("Dashboard");
	}

}
