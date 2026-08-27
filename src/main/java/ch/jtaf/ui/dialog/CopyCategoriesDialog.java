package ch.jtaf.ui.dialog;

import ch.jtaf.db.tables.records.SeriesRecord;
import ch.jtaf.domain.SeriesDAO;
import ch.jtaf.ui.component.MaterialSymbol;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.IntegerField;

public class CopyCategoriesDialog extends Dialog {

	public CopyCategoriesDialog(long organizationId, long currentSeriesId, SeriesDAO seriesDAO) {
		setHeaderTitle(getTranslation("Copy.Categories"));

		var close = new Button(MaterialSymbol.CLOSE.create());
		close.addClickListener(event -> close());
		getHeader().add(close);

		var seriesSelection = new ComboBox<SeriesRecord>(getTranslation("Select.series.to.copy"));
		seriesSelection.setId("series-selection");
		seriesSelection.setWidth("300px");
		seriesSelection.setItemLabelGenerator(SeriesRecord::getName);
		seriesSelection.setItems(query -> seriesDAO
			.findByOrganizationIdAndSeriesId(organizationId, currentSeriesId, query.getOffset(), query.getLimit())
			.stream());

		add(seriesSelection);

		var increaseYears = new Checkbox(getTranslation("Increase.years"));
		increaseYears.setId("increase-years");

		var yearOffset = new IntegerField(getTranslation("Year.offset"));
		yearOffset.setId("year-offset");
		yearOffset.setValue(1);
		yearOffset.setMin(1);
		yearOffset.setMax(10);
		yearOffset.setStepButtonsVisible(true);
		yearOffset.setEnabled(false);

		increaseYears.addValueChangeListener(event -> yearOffset.setEnabled(Boolean.TRUE.equals(event.getValue())));

		add(increaseYears, yearOffset);

		var copy = new Button(getTranslation("Copy"));
		copy.setId("copy-categories-copy");
		copy.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		copy.setEnabled(false);
		seriesSelection.addValueChangeListener(event -> copy.setEnabled(event.getValue() != null));
		copy.addClickListener(event -> {
			var selectedSeries = seriesSelection.getValue();
			if (selectedSeries == null) {
				return;
			}

			var offset = Boolean.TRUE.equals(increaseYears.getValue()) && yearOffset.getValue() != null
					? yearOffset.getValue() : 0;
			seriesDAO.copyCategories(selectedSeries.getId(), currentSeriesId, offset);
			Notification.show(getTranslation("Categories.copied"), 6000, Notification.Position.TOP_END);

			fireEvent(new AfterCopyEvent(this));
			close();
		});

		var cancel = new Button(getTranslation("Cancel"));
		cancel.addClickListener(event -> close());

		getFooter().add(copy, cancel);
	}

	public void addAfterCopyListener(ComponentEventListener<AfterCopyEvent> listener) {
		addListener(AfterCopyEvent.class, listener);
	}

	public static class AfterCopyEvent extends ComponentEvent<CopyCategoriesDialog> {

		public AfterCopyEvent(CopyCategoriesDialog source) {
			super(source, false);
		}

	}

}
