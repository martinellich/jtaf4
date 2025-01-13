package ch.jtaf.domain;

public record CategoryEventVO(String abbreviation, String name, String gender, String eventType, String a, String b,
        String c, Integer position, Long categoryId, Long eventId) {
}
