package ch.jtaf.usecase;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Links a test method to the use case specification it verifies
 * ({@code docs/use_cases/uc-xxx-*.md}). The {@code scenario} and {@code businessRules}
 * values must match the headings in the specification.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UseCase {

	String id();

	String scenario() default "Main Success Scenario";

	String[] businessRules() default {};

}
