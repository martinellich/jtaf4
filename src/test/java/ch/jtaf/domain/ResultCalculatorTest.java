package ch.jtaf.domain;

import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static ch.jtaf.db.tables.Event.EVENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ResultCalculatorTest {

	@MockitoBean
	private JavaMailSender javaMailSender;

	@Autowired
	private ResultCalculator resultCalculator;

	@Autowired
	private DSLContext dsl;

	@ParameterizedTest
	@CsvSource(textBlock = """
			60 m,    1, 12.10,  62
			60 f,    2, 12.10,  74
			ball m, 11, 21.33, 224
			ball f, 12, 21.33, 304
			weit m, 16,  3.11, 261
			weit f, 17,  3.11, 340
			""")
	void correct_result(String event, long eventId, String result, int expectedPoints) {
		var eventRecord = dsl.selectFrom(EVENT).where(EVENT.ID.eq(eventId)).fetchOne();

		var calculatePoints = resultCalculator.calculatePoints(eventRecord, result);

		assertThat(calculatePoints).isEqualTo(expectedPoints);
	}

	@Test
	void invalid_result() {
		var eventRecord = dsl.selectFrom(EVENT).where(EVENT.ID.eq(1L)).fetchOne();

		assertThrows(NumberFormatException.class, () -> resultCalculator.calculatePoints(eventRecord, "12.2.2"));
	}

}
