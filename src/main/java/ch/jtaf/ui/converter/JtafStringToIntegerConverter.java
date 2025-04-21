package ch.jtaf.ui.converter;

import com.vaadin.flow.data.converter.StringToIntegerConverter;

import java.text.NumberFormat;
import java.util.Locale;

public class JtafStringToIntegerConverter extends StringToIntegerConverter {

	public JtafStringToIntegerConverter(String errorMessage) {
		super(errorMessage);
	}

	@Override
	protected NumberFormat getFormat(Locale locale) {
		var format = super.getFormat(locale);
		format.setGroupingUsed(false);
		return format;
	}

}
