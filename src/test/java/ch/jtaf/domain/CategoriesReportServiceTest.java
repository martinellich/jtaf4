package ch.jtaf.domain;

import ch.jtaf.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Locale;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class CategoriesReportServiceTest {

	@SuppressWarnings("unused")
	@MockitoBean
	private JavaMailSender javaMailSender;

	@Autowired
	private CategoriesReportService categoriesReportService;

	@Test
	void create_categories_sheet() {
		byte[] pdf = categoriesReportService.createCategoriesSheet(1L, Locale.of("de", "CH"));

		assertThat(pdf).isNotEmpty();
	}

	@Test
	void unknown_series_fails() {
		assertThatThrownBy(() -> categoriesReportService.createCategoriesSheet(-1L, Locale.ENGLISH))
			.isInstanceOf(NoSuchElementException.class);
	}

}
