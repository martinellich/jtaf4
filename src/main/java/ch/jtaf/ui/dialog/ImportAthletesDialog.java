package ch.jtaf.ui.dialog;

import ch.jtaf.domain.AthleteImportRow;
import ch.jtaf.domain.AthleteImportService;
import ch.jtaf.domain.ImportStatus;
import ch.jtaf.ui.component.MaterialSymbol;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.server.streams.UploadHandler;

import java.util.Comparator;
import java.util.List;

/**
 * Imports athlete registrations from an Excel file into a series.
 * <p>
 * See {@code docs/use_cases/uc-074-import-athletes-from-excel.md}.
 */
public class ImportAthletesDialog extends Dialog {

	private static final String XLSX_MIME_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

	private final transient AthleteImportService athleteImportService;

	private final long organizationId;

	private final long seriesId;

	private final Grid<AthleteImportRow> previewGrid = new Grid<>();

	private final Span summary = new Span();

	private final Button importButton = new Button();

	private transient List<AthleteImportRow> rows = List.of();

	public ImportAthletesDialog(long organizationId, long seriesId, AthleteImportService athleteImportService) {
		this.organizationId = organizationId;
		this.seriesId = seriesId;
		this.athleteImportService = athleteImportService;

		setHeaderTitle(getTranslation("Import.Athletes"));
		setWidth("800px");

		var close = new Button(MaterialSymbol.CLOSE.create());
		close.addClickListener(event -> close());
		getHeader().add(close);

		add(new VerticalLayout(createUpload(), summary, previewGrid));

		createPreviewGrid();

		summary.setId("import-summary");

		importButton.setText(getTranslation("Import"));
		importButton.setId("import-athletes-import");
		importButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		importButton.setEnabled(false);
		importButton.addClickListener(event -> importAthletes());

		var cancel = new Button(getTranslation("Cancel"));
		cancel.addClickListener(event -> close());

		getFooter().add(importButton, cancel);
	}

	private Upload createUpload() {
		var uploadHandler = UploadHandler.inMemory((metadata, data) -> analyze(data));

		var upload = new Upload(uploadHandler);
		upload.setId("import-athletes-upload");
		upload.setMaxFiles(1);
		upload.setAcceptedFileTypes(".xlsx", XLSX_MIME_TYPE);

		var uploadButton = new Button(getTranslation("Athletes.upload"));
		uploadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		upload.setUploadButton(uploadButton);

		upload.setDropLabel(new Span(getTranslation("Athletes.drop.here")));

		return upload;
	}

	private void createPreviewGrid() {
		previewGrid.setId("import-preview-grid");
		previewGrid.setAllRowsVisible(false);
		previewGrid.setHeight("400px");

		previewGrid.addColumn(AthleteImportRow::lastName)
			.setHeader(getTranslation("Last.Name"))
			.setSortable(true)
			.setAutoWidth(true)
			.setKey("last-name");
		previewGrid.addColumn(AthleteImportRow::firstName)
			.setHeader(getTranslation("First.Name"))
			.setSortable(true)
			.setAutoWidth(true)
			.setKey("first-name");
		previewGrid.addColumn(AthleteImportRow::gender)
			.setComparator(nullsLast(AthleteImportRow::gender))
			.setHeader(getTranslation("Gender"))
			.setSortable(true)
			.setAutoWidth(true)
			.setKey("gender");
		previewGrid.addColumn(AthleteImportRow::yearOfBirth)
			.setComparator(nullsLast(AthleteImportRow::yearOfBirth))
			.setHeader(getTranslation("Year"))
			.setSortable(true)
			.setAutoWidth(true)
			.setKey("year-of-birth");
		previewGrid.addColumn(row -> getTranslation(translationKey(row.status())))
			.setComparator(Comparator.comparing(AthleteImportRow::status))
			.setHeader(getTranslation("Import.status"))
			.setSortable(true)
			.setAutoWidth(true)
			.setKey("status");

		// The rows start out in the order of the file
		previewGrid.setMultiSort(true);
	}

	private static <V extends Comparable<? super V>> Comparator<AthleteImportRow> nullsLast(
			ValueProvider<AthleteImportRow, V> valueProvider) {
		return Comparator.comparing(valueProvider, Comparator.nullsLast(Comparator.naturalOrder()));
	}

	private void analyze(byte[] data) {
		rows = athleteImportService.analyze(data, organizationId, seriesId);

		previewGrid.setItems(rows);

		var newAthletes = count(ImportStatus.NEW);
		var existing = count(ImportStatus.EXISTING) + count(ImportStatus.ALREADY_ASSIGNED);
		var withoutCategory = count(ImportStatus.NO_CATEGORY);
		summary.setText(getTranslation("Import.summary", newAthletes, existing, withoutCategory));

		importButton.setEnabled(!rows.isEmpty());
	}

	private void importAthletes() {
		var result = athleteImportService.importAthletes(rows, organizationId, seriesId);

		Notification.show(getTranslation("Athletes.imported", result.assigned(), result.created(),
				result.withoutCategory().size()), 6000, Notification.Position.TOP_END);

		fireEvent(new AfterImportEvent(this));
		close();
	}

	private long count(ImportStatus status) {
		return rows.stream().filter(row -> row.status() == status).count();
	}

	private static String translationKey(ImportStatus status) {
		return switch (status) {
			case NEW -> "Import.status.new";
			case EXISTING -> "Import.status.existing";
			case ALREADY_ASSIGNED -> "Import.status.assigned";
			case NO_CATEGORY -> "Import.status.no.category";
			case INVALID -> "Import.status.invalid";
		};
	}

	public void addAfterImportListener(ComponentEventListener<AfterImportEvent> listener) {
		addListener(AfterImportEvent.class, listener);
	}

	public static class AfterImportEvent extends ComponentEvent<ImportAthletesDialog> {

		public AfterImportEvent(ImportAthletesDialog source) {
			super(source, false);
		}

	}

}
