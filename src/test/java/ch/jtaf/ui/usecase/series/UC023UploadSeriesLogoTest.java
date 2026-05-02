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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

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

		TextField name = $(TextField.class).single();
		assertThat(name.getValue()).isEqualTo("CIS 2019");
	}

	@Test
	void logo_upload() {
		try {
			URL imageUrl = getClass().getClassLoader().getResource("images/logo.png");
			if (imageUrl == null) {
				fail("Image not found");
			}
			else {
				Path path = Paths.get(imageUrl.toURI());
				byte[] logoData = Files.readAllBytes(path);

				Upload upload = $(Upload.class).id("logo-upload");
				test(upload).upload("logo.png", "image/png", logoData);
			}
		}
		catch (URISyntaxException | IOException e) {
			fail(e.getMessage(), e);
		}
	}

}
