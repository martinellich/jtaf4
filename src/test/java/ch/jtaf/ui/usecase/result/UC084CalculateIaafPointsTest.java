package ch.jtaf.ui.usecase.result;

import ch.jtaf.TestcontainersConfiguration;
import ch.jtaf.db.tables.records.EventRecord;
import ch.jtaf.domain.ResultCalculator;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static ch.jtaf.db.tables.Event.EVENT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * UC-084: Calculate IAAF points.
 * <p>
 * See {@code docs/use_cases/uc-084-calculate-iaaf-points.md}.
 */
@Import(TestcontainersConfiguration.class)
@SpringBootTest
class UC084CalculateIaafPointsTest {

	@SuppressWarnings("unused")
	@MockitoBean
	private JavaMailSender javaMailSender;

	@Autowired
	private ResultCalculator resultCalculator;

	@Autowired
	private DSLContext dsl;

	@Test
	void br054_run_uses_time_formula() {
		// 60 m M (id=1): A=6.30895, B=1460, C=2.5 — RUN parses seconds.centiseconds.
		EventRecord run = event(1L);

		assertThat(resultCalculator.calculatePoints(run, "12.10")).isEqualTo(62);
	}

	@Test
	void br054_run_long_parses_minutes_then_seconds() {
		// 600 m M (id=3): RUN_LONG splits on '.' as <minutes>.<seconds>.
		EventRecord runLong = event(3L);

		// "2" is treated as 2 minutes (no seconds part), so time = 120s.
		int twoMinutes = resultCalculator.calculatePoints(runLong, "2");
		// "1.60" is 1 minute 60 seconds = 120s — same total time, same points.
		int oneMinSixtySec = resultCalculator.calculatePoints(runLong, "1.60");
		assertThat(twoMinutes).isEqualTo(oneMinSixtySec).isPositive();

		// A faster time must yield more points than a slower one.
		int faster = resultCalculator.calculatePoints(runLong, "1.30");
		assertThat(faster).isGreaterThan(twoMinutes);
	}

	@Test
	void br054_jump_throw_uses_distance_formula() {
		// Weitsprung M (id=16): A=136.08157, B=130, C=1.1 — JUMP_THROW parses meters.
		EventRecord jumpThrow = event(16L);

		assertThat(resultCalculator.calculatePoints(jumpThrow, "3.11")).isEqualTo(261);

		// Ball M (id=11) — same JUMP_THROW formula, different coefficients.
		EventRecord ball = event(11L);
		assertThat(resultCalculator.calculatePoints(ball, "21.33")).isEqualTo(224);
	}

	@Test
	void br055_underperformance_clamps_to_zero() {
		EventRecord run = event(1L); // 60 m M, B=1460 cs (= 14.60 s).
		// Time slower than B yields a negative base; points are clamped to 0.
		assertThat(resultCalculator.calculatePoints(run, "20.00")).isZero();

		EventRecord jumpThrow = event(16L); // Weitsprung M, B=130 cm.
		// Distance below B gives a negative base; points are clamped to 0.
		assertThat(resultCalculator.calculatePoints(jumpThrow, "1.00")).isZero();
	}

	private EventRecord event(long id) {
		return dsl.selectFrom(EVENT).where(EVENT.ID.eq(id)).fetchOne();
	}

}
