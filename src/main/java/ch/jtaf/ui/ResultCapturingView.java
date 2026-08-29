package ch.jtaf.ui;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.db.tables.records.CompetitionRecord;
import ch.jtaf.db.tables.records.ResultRecord;
import ch.jtaf.domain.AthleteDAO;
import ch.jtaf.domain.CategoryAthleteDAO;
import ch.jtaf.domain.CategoryAthleteId;
import ch.jtaf.domain.ClubDAO;
import ch.jtaf.domain.CompetitionDAO;
import ch.jtaf.domain.EventDAO;
import ch.jtaf.domain.ResultCalculator;
import ch.jtaf.domain.ResultDAO;
import ch.jtaf.domain.SeriesDAO;
import ch.jtaf.ui.dialog.AthleteNameDialog;
import ch.jtaf.ui.dialog.ConfirmDialog;
import ch.jtaf.ui.dialog.SearchAthleteDialog;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import jakarta.annotation.security.RolesAllowed;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Record5;
import org.jooq.impl.DSL;
import org.jspecify.annotations.Nullable;

import java.io.Serial;

import static ch.jtaf.db.tables.Athlete.ATHLETE;
import static ch.jtaf.db.tables.Category.CATEGORY;
import static ch.jtaf.db.tables.Competition.COMPETITION;
import static ch.jtaf.db.tables.Result.RESULT;
import static org.jooq.impl.DSL.upper;

@RolesAllowed({ Role.USER, Role.ADMIN })
@Route
public class ResultCapturingView extends VerticalLayout implements HasDynamicTitle, HasUrlParameter<String> {

	@Serial
	private static final long serialVersionUID = 1L;

	private static final String REMOVE_RESULTS = "Remove.results";

	private final transient ResultCalculator resultCalculator;

	private final transient ResultDAO resultDAO;

	private final transient CategoryAthleteDAO categoryAthleteDAO;

	private final transient CompetitionDAO competitionDAO;

	private final transient EventDAO eventDAO;

	private final transient AthleteDAO athleteDAO;

	private final transient ClubDAO clubDAO;

	private final transient SeriesDAO seriesDAO;

	private final Grid<Record5<Long, String, String, String, Long>> grid = new Grid<>();

	private final Div form = new Div();

	private final TextField filter = new TextField();

	private final Button editAthlete = new Button();

	private ConfigurableFilterDataProvider<Record5<Long, String, String, String, Long>, Void, String> dataProvider;

	@Nullable private TextField resultTextField;

	private long competitionId;

	@SuppressWarnings("java:S107")
	public ResultCapturingView(ResultCalculator resultCalculator, ResultDAO resultDAO,
			CategoryAthleteDAO categoryAthleteDAO, AthleteDAO athleteDAO, CompetitionDAO competitionDAO,
			EventDAO eventDAO, ClubDAO clubDAO, SeriesDAO seriesDAO) {
		this.resultCalculator = resultCalculator;
		this.resultDAO = resultDAO;
		this.categoryAthleteDAO = categoryAthleteDAO;
		this.competitionDAO = competitionDAO;
		this.eventDAO = eventDAO;
		this.athleteDAO = athleteDAO;
		this.clubDAO = clubDAO;
		this.seriesDAO = seriesDAO;

		this.dataProvider = createDataProvider(athleteDAO);

		createFilter();
		var assignAthlete = new Button(getTranslation("Assign.Athlete"));
		assignAthlete.setId("assign-athlete");
		assignAthlete.addClickListener(event -> openSearchAthleteDialog());

		editAthlete.setText(getTranslation("Edit.Athlete"));
		editAthlete.setId("edit-athlete");
		editAthlete.setEnabled(false);
		editAthlete.addClickListener(event -> openAthleteDialog());

		var filterBar = new HorizontalLayout(filter, assignAthlete, editAthlete);
		filterBar.setAlignItems(FlexComponent.Alignment.BASELINE);
		add(filterBar);

		createGrid();
		add(grid);

		add(form);

		grid.asSingleSelect().addValueChangeListener(event -> {
			editAthlete.setEnabled(event.getValue() != null);
			createForm(event);
		});
	}

	private void createGrid() {
		grid.addColumn(athleteRecord -> athleteRecord.get(ATHLETE.ID))
			.setHeader("ID")
			.setSortable(true)
			.setAutoWidth(true)
			.setKey(ATHLETE.ID.getName());
		grid.addColumn(athleteRecord -> athleteRecord.get(ATHLETE.LAST_NAME))
			.setHeader(getTranslation("Last.Name"))
			.setSortable(true)
			.setAutoWidth(true)
			.setKey(ATHLETE.LAST_NAME.getName());
		grid.addColumn(athleteRecord -> athleteRecord.get(ATHLETE.FIRST_NAME))
			.setHeader(getTranslation("First.Name"))
			.setSortable(true)
			.setAutoWidth(true)
			.setKey(ATHLETE.FIRST_NAME.getName());
		grid.addColumn(athleteRecord -> athleteRecord.get(CATEGORY.ABBREVIATION))
			.setHeader(getTranslation("Category"))
			.setSortable(true)
			.setAutoWidth(true)
			.setKey(CATEGORY.ABBREVIATION.getName());
		grid.setItems(dataProvider);
		grid.setHeight("200px");
	}

	private void createFilter() {
		filter.setId("filter");
		filter.setAutoselect(true);
		filter.setAutofocus(true);
		filter.setValueChangeMode(ValueChangeMode.EAGER);
		filter.addValueChangeListener(event -> dataProvider.setFilter(event.getValue()));
	}

	private void openSearchAthleteDialog() {
		// The athletes belong to the organization of the series of this competition
		var series = competitionDAO.findById(competitionId)
			.flatMap(competition -> seriesDAO.findById(competition.getSeriesId()))
			.orElse(null);
		if (series == null) {
			return;
		}

		var dialog = new SearchAthleteDialog(athleteDAO, clubDAO, series.getOrganizationId(), series.getId(),
				this::onAthleteSelect);
		if (!StringUtils.isNumeric(filter.getValue())) {
			dialog.setFilterValue(filter.getValue());
		}
		dialog.open();
	}

	/**
	 * Opens the name form for the selected athlete to correct a misspelled name without
	 * leaving the result entry. Gender, year of birth and club stay untouched because
	 * they determine the category.
	 */
	private void openAthleteDialog() {
		var selected = grid.asSingleSelect().getValue();
		if (selected == null) {
			return;
		}

		athleteDAO.findById(selected.get(ATHLETE.ID)).ifPresent(athleteRecord -> {
			var dialog = new AthleteNameDialog(getTranslation("Edit.Athlete"), athleteDAO);
			dialog.open(athleteRecord, saved -> selectAthlete(saved.getId()));
		});
	}

	private void onAthleteSelect(SearchAthleteDialog.AthleteSelectedEvent event) {
		var athleteRecord = event.getAthleteRecord();
		var seriesId = competitionDAO.findById(competitionId).map(CompetitionRecord::getSeriesId).orElse(null);
		if (seriesId == null) {
			return;
		}

		if (categoryAthleteDAO.createCategoryAthlete(athleteRecord, seriesId).isEmpty()) {
			Notification.show(getTranslation("No.matching.category"), 6000, Notification.Position.TOP_END);
			return;
		}

		selectAthlete(athleteRecord.getId());
	}

	/**
	 * Filtering by the athlete number yields exactly one row which is auto-selected.
	 */
	private void selectAthlete(long athleteId) {
		filter.setValue(String.valueOf(athleteId));
		dataProvider.refreshAll();
	}

	private ConfigurableFilterDataProvider<Record5<Long, String, String, String, Long>, Void, String> createDataProvider(
			AthleteDAO athleteDAO) {
		return new CallbackDataProvider<>(query -> {
			var athletes = athleteDAO.getAthletes(competitionId, createCondition(query), query.getOffset(),
					query.getLimit());
			if (athletes.size() == 1) {
				grid.select(athletes.getFirst());
				if (resultTextField != null) {
					resultTextField.focus();
				}
			}
			return athletes.stream();
		}, (Query<Record5<Long, String, String, String, Long>, String> query) -> {
			int count = athleteDAO.countAthletes(competitionId, createCondition(query));
			if (count == 0) {
				// Clears the form and disables the athlete editing via the selection
				// listener
				grid.deselectAll();
			}
			return count;
		}, athleteRecord -> athleteRecord.get(ATHLETE.ID)).withConfigurableFilter();
	}

	@SuppressWarnings("java:S3776")
	private void createForm(
			AbstractField.ComponentValueChangeEvent<Grid<Record5<Long, String, String, String, Long>>, Record5<Long, String, String, String, Long>> event) {
		form.removeAll();

		if (event.getValue() != null) {
			var formLayout = new FormLayout();
			form.add(formLayout);

			var events = eventDAO.findByCategoryIdOrderByPosition(event.getValue().get(CATEGORY.ID));

			var first = true;
			var position = 0;
			for (var eventRecord : events) {
				var result = new TextField(eventRecord.getName());
				result.setId("result-" + position);
				formLayout.add(result);

				if (first) {
					this.resultTextField = result;
					first = false;
				}

				var points = new TextField();
				points.setId("points-" + position);
				points.setReadOnly(true);
				points.setEnabled(false);
				formLayout.add(points);

				var optionalResultRecord = resultDAO.getResults(competitionId, event.getValue().get(ATHLETE.ID),
						event.getValue().get(CATEGORY.ID), eventRecord.getId());

				ResultRecord resultRecord;
				if (optionalResultRecord.isPresent()) {
					resultRecord = optionalResultRecord.get();
					result.setValue(resultRecord.getResult());
					points.setValue(resultRecord.getPoints() == null ? "" : resultRecord.getPoints().toString());
				}
				else {
					resultRecord = RESULT.newRecord();
					resultRecord.setPosition(position);
					resultRecord.setEventId(eventRecord.getId());
					resultRecord.setAthleteId(event.getValue().get(ATHLETE.ID));
					resultRecord.setCategoryId(event.getValue().get(CATEGORY.ID));
					resultRecord.setCompetitionId(competitionId);
				}

				var finalResultRecord = resultRecord;
				result.addValueChangeListener(ve -> {
					var resultValue = ve.getValue();

					int calculatedPoints;
					try {
						calculatedPoints = resultCalculator.calculatePoints(eventRecord, resultValue);
					}
					catch (NumberFormatException _) {
						Notification.show(getTranslation("Invalid.result"), 6000, Notification.Position.TOP_END);
						return;
					}

					finalResultRecord.setResult(resultValue);
					finalResultRecord.setPoints(calculatedPoints);
					points.setValue(
							finalResultRecord.getPoints() == null ? "" : finalResultRecord.getPoints().toString());

					resultDAO.save(finalResultRecord);
				});
				position++;
			}

			var dnf = new Checkbox(getTranslation("Dnf"));
			dnf.addValueChangeListener(e -> {
				try {
					categoryAthleteDAO.setDnf(event.getValue().get(ATHLETE.ID), event.getValue().get(CATEGORY.ID),
							e.getValue());
				}
				catch (IllegalStateException _) {
					Notification.show(getTranslation("Set.dnf.unsuccessful"), 6000, Notification.Position.TOP_END);
				}
			});

			categoryAthleteDAO
				.findById(new CategoryAthleteId(event.getValue().get(CATEGORY.ID), event.getValue().get(ATHLETE.ID)))
				.ifPresent(categoryAthleteRecord -> dnf.setValue(categoryAthleteRecord.getDnf()));

			form.add(dnf);

			var removeResults = new Button(getTranslation(REMOVE_RESULTS));
			removeResults.addClassName(Margin.Top.MEDIUM);
			removeResults.addClickListener(e -> new ConfirmDialog("remove-results", getTranslation(REMOVE_RESULTS),
					getTranslation(REMOVE_RESULTS), getTranslation("Confirm"), ev -> {
						dnf.setValue(false);

						resultDAO.delete(RESULT.ATHLETE_ID.eq(event.getValue().get(ATHLETE.ID))
							.and(RESULT.COMPETITION_ID.eq(competitionId)));

						createForm(event);
					}, getTranslation("Cancel"), ev -> {
					})
				.open());
			form.add(removeResults);
		}
	}

	@SuppressWarnings("StringCaseLocaleUsage")
	private Condition createCondition(Query<?, ?> query) {
		var optionalFilter = query.getFilter();
		if (optionalFilter.isPresent()) {
			var filterString = (String) optionalFilter.get();
			if (StringUtils.isNumeric(filterString)) {
				return ATHLETE.ID.eq(Long.valueOf(filterString));
			}
			else {
				return upper(ATHLETE.LAST_NAME).like(filterString.toUpperCase() + "%")
					.or(upper(ATHLETE.FIRST_NAME).like(filterString.toUpperCase() + "%"));
			}
		}
		else {
			return DSL.condition("1 = 2");
		}
	}

	@Override
	public String getPageTitle() {
		return competitionDAO.findProjectionById(competitionId)
			.map(stringStringRecord2 -> "%s | %s - %s".formatted(getTranslation("Enter.Results"),
					stringStringRecord2.get(COMPETITION.series().NAME), stringStringRecord2.get(COMPETITION.NAME)))
			.orElseGet(() -> getTranslation("Enter.Results"));
	}

	@Override
	public void setParameter(BeforeEvent event, String parameter) {
		competitionId = Long.parseLong(parameter);
		dataProvider.refreshAll();
	}

}
