package ch.jtaf.ui.usecase.series;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.db.tables.records.SeriesRecord;
import ch.jtaf.ui.AbstractViewTest;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UC-023: Upload series logo.
 * <p>
 * See {@code docs/use_cases/uc-023-upload-series-logo.md}.
 */
class UC023UploadSeriesLogoTest extends AbstractViewTest {

	@BeforeEach
	void login() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));

		Grid<SeriesRecord> seriesGrid = navigateToSeriesList();
		test(seriesGrid).clickRow(0);

		TextField name = find(TextField.class).single();
		assertThat(name.getValue()).isEqualTo("CIS 2019");
	}

	@Test
	void logo_upload() throws URISyntaxException, IOException {
		URL imageUrl = getClass().getClassLoader().getResource("images/logo.png");
		assertThat(imageUrl).as("Image not found").isNotNull();

		Path path = Paths.get(Objects.requireNonNull(imageUrl).toURI());
		byte[] logoData = Files.readAllBytes(path);

		Upload upload = find(Upload.class).id("logo-upload");
		test(upload).upload("logo.png", "image/png", logoData);
	}

}
