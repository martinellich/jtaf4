package ch.jtaf;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

public class ArchitectureTest {

	private static final String UI = "UI";

	private static final String SECURITY = "Security";

	private static final String REPORTING = "Reporting";

	private static final String DOMAIN = "Domain";

	private static final String UTIL = "Util";

	private static final String DB = "DB";

	private final JavaClasses classes = new ClassFileImporter().importPackages("ch.jtaf");

	@Test
	public void check_layered_architecture() {
		layeredArchitecture().consideringAllDependencies()

			.layer(UI)
			.definedBy("..ui..")
			.layer(SECURITY)
			.definedBy("..security..")
			.layer(REPORTING)
			.definedBy("..reporting..")
			.layer(DOMAIN)
			.definedBy("..domain..")
			.layer(UTIL)
			.definedBy("..util..")
			.layer(DB)
			.definedBy("..db..")

			.whereLayer(UI)
			.mayNotBeAccessedByAnyLayer()
			.whereLayer(REPORTING)
			.mayOnlyBeAccessedByLayers(DOMAIN)
			.whereLayer(DB)
			.mayOnlyBeAccessedByLayers(UI, DOMAIN, SECURITY, REPORTING, UTIL)
			.whereLayer(DOMAIN)
			.mayOnlyBeAccessedByLayers(UI, REPORTING)

			.check(classes);
	}

	@Test
	public void check_cycles() {
		slices().matching("ch.jtaf.(*)..").should().beFreeOfCycles().check(classes);
	}

}
