package ch.jtaf.reporting.report;

import ch.jtaf.domain.EventType;
import ch.jtaf.reporting.data.NumbersAndSheetsAthlete;
import ch.jtaf.reporting.data.NumbersAndSheetsCompetition;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.lowagie.text.Element.ALIGN_RIGHT;
import static com.lowagie.text.PageSize.A5;

public class SheetsReport extends AbstractReport {

	private static final Logger LOGGER = LoggerFactory.getLogger(SheetsReport.class);

	private static final float INFO_LINE_HEIGHT = 40f;

	private static final float FONT_SIZE_INFO = 8f;

	private static final float FONT_SIZE_TEXT = 16f;

	@Nullable
	private Document document;

	@Nullable
	private PdfWriter pdfWriter;

	@Nullable
	private final NumbersAndSheetsCompetition competition;

	private final List<NumbersAndSheetsAthlete> athletes;

	private final byte[] logo;

	public SheetsReport(NumbersAndSheetsAthlete athlete, byte[] logo, Locale locale) {
		super(locale);
		this.competition = null;
		this.athletes = new ArrayList<>();
		this.athletes.add(athlete);
		this.logo = logo;
	}

	public SheetsReport(NumbersAndSheetsCompetition competition, NumbersAndSheetsAthlete athlete, byte[] logo,
			Locale locale) {
		super(locale);
		this.competition = competition;
		this.athletes = new ArrayList<>();
		this.athletes.add(athlete);
		this.logo = logo;
	}

	public SheetsReport(NumbersAndSheetsCompetition competition, List<NumbersAndSheetsAthlete> athletes, byte[] logo,
			Locale locale) {
		super(locale);
		this.competition = competition;
		this.athletes = athletes;
		this.logo = logo;
	}

	public byte[] create() {
		try (var byteArrayOutputStream = new ByteArrayOutputStream()) {
			float oneCm = cmToPixel(1f);
			document = new Document(A5, oneCm, oneCm, cmToPixel(4.5f), oneCm);
			pdfWriter = PdfWriter.getInstance(document, byteArrayOutputStream);
			document.open();
			var first = true;
			var number = 1;
			for (var athlete : athletes) {
				if (!first) {
					document.newPage();
				}
				createLogo();
				createCategory(athlete);
				createAthleteInfo(athlete, number);
				createCompetitionRow();
				createEventTable(athlete);
				first = false;
				number++;
			}
			document.close();
			pdfWriter.flush();
			return byteArrayOutputStream.toByteArray();
		}
		catch (DocumentException | IOException e) {
			LOGGER.error(e.getMessage(), e);
			return new byte[0];
		}
	}

	private void createLogo() throws DocumentException {
		try {
			var image = Image.getInstance(logo);
			image.setAbsolutePosition(cmToPixel(1f), cmToPixel(17.5f));
			image.scaleToFit(120, 60);
			if (document != null) {
				document.add(image);
			}
		}
		catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	private void createCategory(NumbersAndSheetsAthlete athlete) {
		var table = new PdfPTable(1);
		table.setWidthPercentage(100);
		addCategoryAbbreviationCell(table, athlete.categoryAbbreviation());
		addCategoryNameCell(table, athlete.categoryName());

		if (document != null && pdfWriter != null) {
			var page = document.getPageSize();
			table.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
			table.writeSelectedRows(0, 2, document.leftMargin(), cmToPixel(20.5f), pdfWriter.getDirectContent());
		}
	}

	private void createAthleteInfo(NumbersAndSheetsAthlete athlete, int number) throws DocumentException {
		var table = new PdfPTable(2);
		table.setWidthPercentage(100);
		table.setSpacingBefore(cmToPixel(1f));

		if (athlete.id() != null) {
			addInfoCell(table, String.valueOf(number));
			addCell(table, athlete.id().toString());
		}
		else {
			addCell(table, " ");
			addCell(table, " ");
		}
		if (athlete.lastName() == null) {
			addInfoCellWithBorder(table, messages.getString("Last.Name"));
		}
		else {
			addInfoCell(table, athlete.lastName());
		}
		if (athlete.firstName() == null) {
			addInfoCellWithBorder(table, messages.getString("First.Name"));
		}
		else {
			addInfoCell(table, athlete.firstName());
		}
		if (athlete.yearOfBirth() == null) {
			addInfoCellWithBorder(table, messages.getString("Year"));
		}
		else {
			addInfoCell(table, String.valueOf(athlete.yearOfBirth()));
		}
		if (athlete.club() == null) {
			if (athlete.id() == null) {
				addInfoCellWithBorder(table, messages.getString("Club"));
			}
			else {
				addInfoCell(table, "");
			}
		}
		else {
			addInfoCell(table, athlete.club());
		}

		if (document != null) {
			document.add(table);
		}
	}

	private void createCompetitionRow() throws DocumentException {
		var table = new PdfPTable(1);
		table.setWidthPercentage(100);
		table.setSpacingBefore(cmToPixel(0.5f));
		table.setSpacingAfter(cmToPixel(0.5f));

		addCompetitionCell(table, competition == null ? ""
				: "%s %s".formatted(competition.name(), DATE_TIME_FORMATTER.format(competition.competitionDate())));

		if (document != null) {
			document.add(table);
		}
	}

	private void createEventTable(NumbersAndSheetsAthlete athlete) throws DocumentException {
		var table = new PdfPTable(4);
		table.setWidthPercentage(100);
		table.setSpacingBefore(cmToPixel(1f));

		for (var event : athlete.events()) {
			if (event.type().equals(EventType.JUMP_THROW.name())) {
				addInfoCell(table, event.name());
				addInfoCellWithBorder(table, "");
				addInfoCellWithBorder(table, "");
				addInfoCellWithBorder(table, "");
			}
			else {
				addInfoCellWithColspan(table, event.name(), 3);
				addInfoCellWithBorder(table, "");
			}
		}

		if (document != null) {
			document.add(table);
		}
	}

	protected void addCategoryAbbreviationCell(PdfPTable table, String text) {
		var cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 80f)));
		cell.setBorder(0);
		cell.setHorizontalAlignment(ALIGN_RIGHT);
		table.addCell(cell);
	}

	protected void addCategoryNameCell(PdfPTable table, String text) {
		var cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA, 14f)));
		cell.setBorder(0);
		cell.setPaddingRight(7f);
		cell.setHorizontalAlignment(ALIGN_RIGHT);
		table.addCell(cell);
	}

	private void addCompetitionCell(PdfPTable table, String text) {
		var cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, FONT_SIZE_TEXT)));
		cell.setBorder(0);
		table.addCell(cell);
	}

	private void addInfoCell(PdfPTable table, String text) {
		var cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA, FONT_SIZE_TEXT)));
		cell.setBorder(0);
		cell.setMinimumHeight(INFO_LINE_HEIGHT);
		table.addCell(cell);
	}

	private void addInfoCellWithColspan(PdfPTable table, String text,
			@SuppressWarnings("SameParameterValue") int colspan) {
		var cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA, FONT_SIZE_TEXT)));
		cell.setBorder(0);
		cell.setColspan(colspan);
		cell.setMinimumHeight(INFO_LINE_HEIGHT);
		table.addCell(cell);
	}

	private void addInfoCellWithBorder(PdfPTable table, String text) {
		var cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA, FONT_SIZE_INFO)));
		cell.setMinimumHeight(INFO_LINE_HEIGHT);
		cell.setBorderWidth(1);
		table.addCell(cell);
	}

}
