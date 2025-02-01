package ch.jtaf.domain;

import ch.jtaf.db.tables.records.EventRecord;
import org.springframework.stereotype.Service;

@Service
public class ResultCalculator {

    public int calculatePoints(EventRecord event, String result) {
        var points = 0.0d;
        if (EventType.valueOf(event.getEventType()) == EventType.RUN) {
            points = event.getA() * Math.pow((event.getB() - Double.parseDouble(result) * 100) / 100, event.getC());
        }
        else if (EventType.valueOf(event.getEventType()) == EventType.RUN_LONG) {
            String[] parts = result.split("\\.");
            double time;
            if (parts.length == 1) {
                time = Double.parseDouble(parts[0]) * 60;
            }
            else {
                time = Double.parseDouble(parts[0]) * 60 + Double.parseDouble(parts[1]);
            }
            points = event.getA() * Math.pow((event.getB() - time * 100) / 100, event.getC());
        }
        else if (EventType.valueOf(event.getEventType()) == EventType.JUMP_THROW) {
            points = event.getA() * Math.pow((Double.parseDouble(result) * 100 - event.getB()) / 100, event.getC());
        }
        return (int) Math.round(points);
    }

}
