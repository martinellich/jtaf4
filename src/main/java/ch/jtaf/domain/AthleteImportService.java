package ch.jtaf.domain;

import ch.jtaf.db.tables.records.AthleteRecord;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import static ch.jtaf.db.tables.Athlete.ATHLETE;

/**
 * Reads athlete registrations from an Excel file and enrols them into a series.
 * <p>
 * See {@code docs/use_cases/uc-074-import-athletes-from-excel.md}.
 */
@Service
public class AthleteImportService {

	private static final int COLUMN_LAST_NAME = 0;

	private static final int COLUMN_FIRST_NAME = 1;

	private static final int COLUMN_YEAR_OF_BIRTH = 2;

	private static final int COLUMN_GENDER = 3;

	private static final int MIN_YEAR = 1900;

	private static final int MAX_YEAR = 2100;

	private static final Pattern YEAR_PATTERN = Pattern.compile("(\\d{4})");

	private final AthleteDAO athleteDAO;

	private final CategoryDAO categoryDAO;

	private final CategoryAthleteDAO categoryAthleteDAO;

	public AthleteImportService(AthleteDAO athleteDAO, CategoryDAO categoryDAO, CategoryAthleteDAO categoryAthleteDAO) {
		this.athleteDAO = athleteDAO;
		this.categoryDAO = categoryDAO;
		this.categoryAthleteDAO = categoryAthleteDAO;
	}

	/**
	 * Reads the rows of the first sheet without touching the database.
	 */
	public List<AthleteImportRow> parse(byte[] xlsx) {
		var rows = new ArrayList<AthleteImportRow>();
		try (var in = new ByteArrayInputStream(xlsx); var workbook = new XSSFWorkbook(in)) {
			if (workbook.getNumberOfSheets() == 0) {
				return rows;
			}
			var sheet = workbook.getSheetAt(0);
			var headerRowIndex = findHeaderRowIndex(sheet);
			if (headerRowIndex < 0) {
				return rows;
			}
			for (var rowIndex = headerRowIndex + 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
				var row = sheet.getRow(rowIndex);
				var lastName = stringValue(row, COLUMN_LAST_NAME);
				var firstName = stringValue(row, COLUMN_FIRST_NAME);
				if (lastName.isEmpty() && firstName.isEmpty()) {
					break;
				}
				var yearOfBirth = yearOfBirth(row);
				var gender = gender(row);
				var status = lastName.isEmpty() || firstName.isEmpty() || yearOfBirth == null || gender == null
						? ImportStatus.INVALID : ImportStatus.NEW;
				rows.add(new AthleteImportRow(rowIndex + 1, lastName, firstName, yearOfBirth, gender, status));
			}
		}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		return rows;
	}

	/**
	 * Reads the rows and determines for each of them what an import would do.
	 */
	public List<AthleteImportRow> analyze(byte[] xlsx, long organizationId, long seriesId) {
		return parse(xlsx).stream().map(row -> row.withStatus(status(row, organizationId, seriesId))).toList();
	}

	/**
	 * Creates the missing athletes and assigns all readable rows to the matching category
	 * of the series.
	 */
	@Transactional
	public AthleteImportResult importAthletes(List<AthleteImportRow> rows, long organizationId, long seriesId) {
		var created = 0;
		var existing = 0;
		var assigned = 0;
		var withoutCategory = new ArrayList<String>();
		var invalid = new ArrayList<String>();

		for (var row : rows) {
			var yearOfBirth = row.yearOfBirth();
			var gender = row.gender();
			if (row.status() == ImportStatus.INVALID || yearOfBirth == null || gender == null) {
				invalid.add(fullName(row));
				continue;
			}

			var athlete = athleteDAO
				.findByOrganizationIdAndNameAndYearOfBirthAndGender(organizationId, row.lastName(), row.firstName(),
						yearOfBirth, gender)
				.orElse(null);
			if (athlete == null) {
				athlete = createAthlete(organizationId, row, yearOfBirth, gender);
				created++;
			}
			else {
				existing++;
			}

			var categoryId = categoryDAO.findIdBySeriesIdAndGenderAndYearOfBirth(seriesId, gender, yearOfBirth)
				.orElse(null);
			if (categoryId == null) {
				withoutCategory.add(fullName(row));
			}
			else if (!categoryAthleteDAO.isAssignedToSeries(athlete.getId(), seriesId)) {
				categoryAthleteDAO.createCategoryAthlete(athlete.getId(), categoryId);
				assigned++;
			}
		}

		return new AthleteImportResult(created, existing, assigned, withoutCategory, invalid);
	}

	private AthleteRecord createAthlete(long organizationId, AthleteImportRow row, int yearOfBirth, String gender) {
		var athlete = ATHLETE.newRecord();
		athlete.setLastName(row.lastName());
		athlete.setFirstName(row.firstName());
		athlete.setYearOfBirth(yearOfBirth);
		athlete.setGender(gender);
		athlete.setOrganizationId(organizationId);
		athleteDAO.save(athlete);
		return athlete;
	}

	private ImportStatus status(AthleteImportRow row, long organizationId, long seriesId) {
		var yearOfBirth = row.yearOfBirth();
		var gender = row.gender();
		if (row.status() == ImportStatus.INVALID || yearOfBirth == null || gender == null) {
			return ImportStatus.INVALID;
		}
		if (categoryDAO.findIdBySeriesIdAndGenderAndYearOfBirth(seriesId, gender, yearOfBirth).isEmpty()) {
			return ImportStatus.NO_CATEGORY;
		}
		var athlete = athleteDAO
			.findByOrganizationIdAndNameAndYearOfBirthAndGender(organizationId, row.lastName(), row.firstName(),
					yearOfBirth, gender)
			.orElse(null);
		if (athlete == null) {
			return ImportStatus.NEW;
		}
		return categoryAthleteDAO.isAssignedToSeries(athlete.getId(), seriesId) ? ImportStatus.ALREADY_ASSIGNED
				: ImportStatus.EXISTING;
	}

	private static String fullName(AthleteImportRow row) {
		return row.lastName() + " " + row.firstName();
	}

	private static int findHeaderRowIndex(Sheet sheet) {
		for (var rowIndex = sheet.getFirstRowNum(); rowIndex <= sheet.getLastRowNum(); rowIndex++) {
			var row = sheet.getRow(rowIndex);
			var first = stringValue(row, COLUMN_LAST_NAME).toLowerCase(Locale.ROOT);
			var second = stringValue(row, COLUMN_FIRST_NAME).toLowerCase(Locale.ROOT);
			if (first.startsWith("name") && second.startsWith("vorname")) {
				return rowIndex;
			}
		}
		return -1;
	}

	private static @Nullable Integer yearOfBirth(@Nullable Row row) {
		var cell = cell(row, COLUMN_YEAR_OF_BIRTH);
		if (cell == null) {
			return null;
		}
		if (cell.getCellType() == CellType.NUMERIC) {
			if (DateUtil.isCellDateFormatted(cell)) {
				return cell.getLocalDateTimeCellValue().getYear();
			}
			var numericValue = cell.getNumericCellValue();
			if (numericValue >= MIN_YEAR && numericValue <= MAX_YEAR) {
				return (int) numericValue;
			}
			return DateUtil.isValidExcelDate(numericValue) ? cell.getLocalDateTimeCellValue().getYear() : null;
		}
		var matcher = YEAR_PATTERN.matcher(stringValue(row, COLUMN_YEAR_OF_BIRTH));
		Integer year = null;
		while (matcher.find()) {
			var candidate = Integer.parseInt(matcher.group(1));
			if (candidate >= MIN_YEAR && candidate <= MAX_YEAR) {
				year = candidate;
			}
		}
		return year;
	}

	private static @Nullable String gender(@Nullable Row row) {
		var value = stringValue(row, COLUMN_GENDER).toLowerCase(Locale.ROOT);
		if (value.startsWith("m")) {
			return Gender.M.name();
		}
		if (value.startsWith("w") || value.startsWith("f")) {
			return Gender.F.name();
		}
		return null;
	}

	private static String stringValue(@Nullable Row row, int column) {
		var cell = cell(row, column);
		if (cell == null) {
			return "";
		}
		return switch (cell.getCellType()) {
			case STRING -> cell.getStringCellValue().trim();
			case NUMERIC -> numericAsString(cell);
			case BOOLEAN -> Boolean.toString(cell.getBooleanCellValue());
			default -> "";
		};
	}

	private static String numericAsString(Cell cell) {
		if (DateUtil.isCellDateFormatted(cell)) {
			return cell.getLocalDateTimeCellValue().toString();
		}
		var numericValue = cell.getNumericCellValue();
		return numericValue == Math.rint(numericValue) ? Long.toString((long) numericValue)
				: Double.toString(numericValue);
	}

	private static @Nullable Cell cell(@Nullable Row row, int column) {
		if (row == null) {
			return null;
		}
		var cell = row.getCell(column, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
		if (cell == null) {
			return null;
		}
		return cell.getCellType() == CellType.FORMULA ? null : cell;
	}

}
