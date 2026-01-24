package cs.youtrade.ytrest.gson.adapter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LocalTimeAdapter extends ClassicTemporalAccessorAdapter<LocalTime> {
    @Override
    protected DateTimeFormatter getFormatter() {
        return DateTimeFormatter.ISO_LOCAL_TIME;
    }

    @Override
    protected LocalTime parseString(String str) {
        return LocalTime.parse(str, getFormatter());
    }
}
