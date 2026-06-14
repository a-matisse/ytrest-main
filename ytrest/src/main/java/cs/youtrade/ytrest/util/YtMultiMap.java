package cs.youtrade.ytrest.util;

import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@NoArgsConstructor
public class YtMultiMap<K, V> {
    private final Map<K, List<V>> params = new LinkedHashMap<>();

    private YtMultiMap(Map<K, List<V>> params) {
        this.params.putAll(params);
    }

    public boolean isEmpty() {
        return params.isEmpty();
    }

    public void add(K key, V value) {
        params.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
    }

    public void addAll(K key, Iterable<V> values) {
        values.forEach(v -> add(key, v));
    }

    public List<V> get(K key) {
        return params.getOrDefault(key, List.of());
    }

    public void forEach(BiConsumer<? super K, ? super V> action) {
        params.entrySet().stream().flatMap(entry -> {
            List<V> values = entry.getValue();
            return values.stream().map(v -> new Wrapper<>(entry.getKey(), v));
        }).forEach(wrapper -> action.accept(wrapper.key(), wrapper.value));
    }

    public String toQueryString() {
        StringBuilder query = new StringBuilder();
        for (Map.Entry<K, List<V>> entry : params.entrySet())
            for (V value : entry.getValue())
                query.append("&").append(entry.getKey()).append("=").append(value);
        return query.isEmpty() ? "" : query.substring(1);
    }

    public static <K, V> YtMultiMap<K, V> fromMap(Map<K, V> map) {
        Map<K, List<V>> newMap = map.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> createMutableList(e.getValue())
        ));
        return new YtMultiMap<>(newMap);
    }

    private static <V> List<V> createMutableList(V value) {
        var list = new ArrayList<V>();
        list.add(value);
        return list;
    }

    private record Wrapper<K, V>(K key, V value) {
    }
}
