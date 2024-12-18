package ch.jtaf.ui.dialog;

import ch.jtaf.db.tables.records.SeriesRecord;
import ch.jtaf.domain.SeriesDAO;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;

public class CopyCategoriesDialog extends Dialog {

    public CopyCategoriesDialog(long organizationId, long currentSeriesId, SeriesDAO seriesDAO) {
        setHeaderTitle(getTranslation("Copy.Categories"));

        var close = new Button(VaadinIcon.CLOSE_SMALL.create());
        close.addClickListener(event -> close());
        getHeader().add(close);

        var seriesSelection = new ComboBox<SeriesRecord>(getTranslation("Select.series.to.copy"));
        seriesSelection.setId("series-selection");
        seriesSelection.setWidth("300px");
        seriesSelection.setItemLabelGenerator(SeriesRecord::getName);
        seriesSelection.setItems(query ->
            seriesDAO.findByOrganizationIdAndSeriesId(organizationId, currentSeriesId,
            query.getOffset(), query.getLimit()).stream());

        add(seriesSelection);

        var copy = new Button(getTranslation("Copy"));
        copy.setId("copy-categories-copy");
        copy.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        copy.addClickListener(event -> {
            seriesDAO.copyCategories(seriesSelection.getValue().getId(), currentSeriesId);
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
