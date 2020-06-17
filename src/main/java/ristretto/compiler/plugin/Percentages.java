package ristretto.compiler.plugin;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toUnmodifiableMap;

final class Percentages<K> {

  private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

  private final int total;
  private final Map<K, BigDecimal> percentages;

  private Percentages(int total, Map<K, BigDecimal> percentages) {
    this.total = total;
    this.percentages = percentages;
  }

  static <K> Optional<Percentages<K>> calculate(Map<K, Integer> stats) {
    int total = stats.values()
      .stream()
      .mapToInt(Integer::intValue)
      .sum();

    if (total == 0) {
      return Optional.empty();
    }

    Map<K, BigDecimal> percentages = stats.entrySet()
      .stream()
      .collect(toUnmodifiableMap(Map.Entry::getKey, entry -> percentage(entry.getValue(), total)));

    return Optional.of(new Percentages<>(total, percentages));
  }

  private static BigDecimal percentage(int count, int total) {
    return BigDecimal.valueOf(count)
      .divide(BigDecimal.valueOf(total), 4, RoundingMode.FLOOR)
      .multiply(HUNDRED)
      .setScale(2, RoundingMode.FLOOR);
  }

  int getTotal() {
    return total;
  }

  BigDecimal percentage(K key) {
    return percentages.getOrDefault(key, BigDecimal.ZERO);
  }
}
