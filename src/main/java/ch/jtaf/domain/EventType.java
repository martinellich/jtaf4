package ch.jtaf.domain;

import java.util.ArrayList;
import java.util.List;

public enum EventType {

    JUMP_THROW, RUN, RUN_LONG;

    public static List<String> valuesAsStrings() {
        List<String> valuesAsStrings = new ArrayList<>();
        for (var value : values()) {
            valuesAsStrings.add(value.name());
        }
        return valuesAsStrings;
    }

}
