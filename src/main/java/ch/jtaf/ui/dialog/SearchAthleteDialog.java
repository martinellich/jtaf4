package ch.jtaf.ui.dialog;

import ch.jtaf.db.tables.records.AthleteRecord;
import ch.jtaf.db.tables.records.ClubRecord;
import ch.jtaf.domain.AthleteDAO;
import ch.jtaf.domain.ClubDAO;
import ch.jtaf.ui.component.MaterialSymbol;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.impl.DSL;
import org.jspecify.annotations.Nullable;

import java.io.Serial;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static ch.jtaf.db.tables.Athlete.ATHLETE;
import static ch.jtaf.ui.component.GridBuilder.addActionColumnAndSetSelectionListener;
import static org.jooq.impl.DSL.lower;

public class SearchAthleteDialog extends Dialog {

	@Serial
	private static final long serialVersionUID = 1L;

	public static final String FULLSCREEN = "fullscreen";

	private boolean isFullScreen = false;

	private final Div content;

	private final Button toggle;

	private final Map<Long, ClubRecord> clubRecordMap;

	private final ConfigurableFilterDataProvider<AthleteRecord, Void, String> dataProvider;

	private final TextField filter;

	@Nullable private AthleteRecord pendingNewRecord;

	public SearchAthleteDialog(AthleteDAO athleteDAO, ClubDAO clubDAO, long organizationId, long seriesId,
			ComponentEventListener<AthleteSelectedEvent> athleteSelectedListener) {
		setDraggable(true);
		setResizable(true);

		addListener(AthleteSelectedEvent.class, athleteSelectedListener);

		setHeaderTitle(getTranslation("Athletes"));

		toggle = new Button(MaterialSymbol.MAXIMIZE.create());
		toggle.setId("toggle");
		toggle.addClickListener(event -> toggle());

		var close = new Button(MaterialSymbol.CLOSE.create());
		close.addClickListener(event -> close());

		getHeader().add(toggle, close);

		var dialog = new AthleteDialog(getTranslation("Athlete"), athleteDAO, clubDAO, organizationId);

		filter = new TextField(getTranslation("Filter"));
		filter.setAutoselect(true);
		filter.setAutofocus(true);
		filter.setValueChangeMode(ValueChangeMode.EAGER);

		var clubs = clubDAO.findByOrganizationId(organizationId);
		clubRecordMap = clubs.stream().collect(Collectors.toMap(ClubRecord::getId, clubRecord -> clubRecord));

		CallbackDataProvider<AthleteRecord, String> callbackDataProvider = DataProvider.fromFilteringCallbacks(
				query -> athleteDAO
					.findByOrganizationIdAndSeriesId(organizationId, seriesId, createCondition(query),
							query.getOffset(), query.getLimit())
					.stream(),
				query -> athleteDAO.countByOrganizationIdAndSeriesId(organizationId, seriesId, createCondition(query)));

		dataProvider = callbackDataProvider.withConfigurableFilter();

		var grid = new Grid<AthleteRecord>();
		grid.setId("search-athletes-grid");
		grid.setItems(dataProvider);
		grid.setHeight("calc(100% - 60px");

		grid.addColumn(AthleteRecord::getLastName)
			.setHeader(getTranslation("Last.Name"))
			.setSortable(true)
			.setAutoWidth(true)
			.setKey(ATHLETE.LAST_NAME.getName());
		grid.addColumn(AthleteRecord::getFirstName)
			.setHeader(getTranslation("First.Name"))
			.setSortable(true)
			.setAutoWidth(true)
			.setKey(ATHLETE.FIRST_NAME.getName());
		grid.addColumn(AthleteRecord::getGender)
			.setHeader(getTranslation("Gender"))
			.setSortable(true)
			.setAutoWidth(true)
			.setKey(ATHLETE.GENDER.getName());
		grid.addColumn(AthleteRecord::getYearOfBirth)
			.setHeader(getTranslation("Year"))
			.setSortable(true)
			.setAutoWidth(true)
			.setKey(ATHLETE.YEAR_OF_BIRTH.getName());
		grid.addColumn(athleteRecord -> {
			if (athleteRecord.getClubId() != null) {
				var clubRecord = clubRecordMap.get(athleteRecord.getClubId());
				if (clubRecord != null) {
					return clubRecord.getAbbreviation();
				}
			}
			return null;
		}).setHeader(getTranslation("Club")).setAutoWidth(true);

		addActionColumnAndSetSelectionListener(athleteDAO, grid, dialog, this::afterSave, () -> {
			pendingNewRecord = ATHLETE.newRecord();
			pendingNewRecord.setOrganizationId(organizationId);
			return pendingNewRecord;
		}, getTranslation("Assign.Athlete"), this::select, dataProvider::refreshAll);

		filter.addValueChangeListener(event -> dataProvider.setFilter(event.getValue()));

		content = new Div(filter, grid);
		content.setSizeFull();
		add(content);

		toggle();

		filter.focus();
	}

	/**
	 * Sets the filter text, e.g. to continue a search started in the calling view.
	 */
	public void setFilterValue(String value) {
		filter.setValue(value);
	}

	/**
	 * A newly created athlete is selected right away; an edited athlete only refreshes
	 * the grid.
	 */
	private void afterSave(AthleteRecord athleteRecord) {
		if (pendingNewRecord != null && Objects.equals(athleteRecord.getId(), pendingNewRecord.getId())) {
			pendingNewRecord = null;
			select(athleteRecord);
		}
		else {
			dataProvider.refreshAll();
		}
	}

	private void select(AthleteRecord athleteRecord) {
		fireEvent(new AthleteSelectedEvent(this, athleteRecord));
		close();
	}

	private void initialSize() {
		toggle.setIcon(MaterialSymbol.MAXIMIZE.create());
		getElement().getThemeList().remove(FULLSCREEN);
		setHeight("auto");
		setWidth("600px");
	}

	private void toggle() {
		if (isFullScreen) {
			initialSize();
		}
		else {
			toggle.setIcon(MaterialSymbol.MINIMIZE.create());
			getElement().getThemeList().add(FULLSCREEN);
			setSizeFull();
			content.setVisible(true);
		}
		isFullScreen = !isFullScreen;
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
				return lower(ATHLETE.LAST_NAME).like(filterString.toLowerCase() + "%")
					.or(lower(ATHLETE.FIRST_NAME).like(filterString.toLowerCase() + "%"));
			}
		}
		else {
			return DSL.condition("1 = 2");
		}
	}

	public static class AthleteSelectedEvent extends ComponentEvent<SearchAthleteDialog> {

		private final AthleteRecord athleteRecord;

		public AthleteSelectedEvent(SearchAthleteDialog source, AthleteRecord athleteRecord) {
			super(source, false);

			this.athleteRecord = athleteRecord;
		}

		public AthleteRecord getAthleteRecord() {
			return athleteRecord;
		}

	}

}
