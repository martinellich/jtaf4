package ch.jtaf.domain;

import java.util.ArrayList;
import java.util.List;

public enum Gender {

	M, F;

	public static List<String> valuesAsStrings() {
		var valuesAsStrings = new ArrayList<String>();
		for (var value : values()) {
			valuesAsStrings.add(value.name());
		}
		return valuesAsStrings;
	}

}
