package ristretto.compiler.plugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

final class MetricsCollector<S, T> {

  private final Map<S, Map<T, Integer>> eventCount = new HashMap<>();

  void count(S eventSource, T eventType) {
    eventCount
      .computeIfAbsent(eventSource, newEventSource -> new HashMap<>())
      .merge(eventType, 1, Integer::sum);
  }

  Optional<Percentages<T>> calculate(S eventSource) {
    return Percentages.calculate(eventCount.getOrDefault(eventSource, Collections.emptyMap()));
  }
}
