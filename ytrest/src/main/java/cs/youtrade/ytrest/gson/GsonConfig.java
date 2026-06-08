package cs.youtrade.ytrest.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cs.youtrade.ytrest.gson.adapter.LocalDateAdapter;
import cs.youtrade.ytrest.gson.adapter.LocalDateTimeAdapter;
import cs.youtrade.ytrest.gson.adapter.LocalTimeAdapter;
import lombok.Getter;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class GsonConfig {
    @Getter
    private static final Map<Type, Object> adapters = new HashMap<>();

    static {
        addAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        addAdapter(LocalDate.class, new LocalDateAdapter());
        addAdapter(LocalTime.class, new LocalTimeAdapter());
    }

    public static GsonBuilder getGsonBuilder() {
        return fillGsonBuilder(new GsonBuilder());
    }

    public static GsonBuilder fillGsonBuilder(GsonBuilder builder) {
        adapters.forEach(builder::registerTypeAdapter);
        return builder;
    }

    public static Gson createGson() {
        return getGsonBuilder().create();
    }

    public static <T> void addAdapter(Class<T> type, Object adapter) {
        adapters.put(type, adapter);
    }
}
