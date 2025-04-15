package ch.jtaf;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.Test;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

public class ArchitectureTest {

	private static final String UI = "UI";

	private static final String SERVICE = "Service";

	private static final String SECURITY = "Security";

	private static final String REPORTING = "Reporting";

	private static final String MODEL = "Model";

	private static final String UTIL = "Util";

	private static final String DB = "DB";

	private final JavaClasses classes = new ClassFileImporter().importPackages("ch.jtaf");

	@Test
	public void check_layered_architecture() {
		layeredArchitecture().consideringAllDependencies()

			.layer(UI)
			.definedBy("..ui..")
			.layer(SERVICE)
			.definedBy("..service..")
			.layer(SECURITY)
			.definedBy("..security..")
			.layer(REPORTING)
			.definedBy("..reporting..")
			.layer(MODEL)
			.definedBy("..model..")
			.layer(UTIL)
			.definedBy("..util..")
			.layer(DB)
			.definedBy("..db..")

			.whereLayer(UI)
			.mayNotBeAccessedByAnyLayer()
			.whereLayer(SERVICE)
			.mayOnlyBeAccessedByLayers(UI)
			.whereLayer(REPORTING)
			.mayOnlyBeAccessedByLayers(SERVICE)
			.whereLayer(DB)
			.mayOnlyBeAccessedByLayers(UI, SERVICE, SECURITY, REPORTING, UTIL)
			.whereLayer(MODEL)
			.mayOnlyBeAccessedByLayers(UI, REPORTING)

			.check(classes);
	}

	@Test
	public void check_cycles() {
		slices().matching("ch.jtaf.(*)..").should().beFreeOfCycles().check(classes);
	}

}
