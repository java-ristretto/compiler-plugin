package ristretto.compiler.plugin;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class PercentagesTest {

  @Test
  void indicates_when_there_are_no_percentages() {
    assertThat(Percentages.calculate(Map.of()), is(Optional.empty()));
    assertThat(Percentages.calculate(Map.of("key", 0)), is(Optional.empty()));
  }

  @Test
  void calculates_percentages_when_available() {
    var stats = Map.of(
      "added", 3,
      "annotated", 2,
      "already-present", 1
    );

    var percentages = Percentages.calculate(stats).orElseThrow();

    assertThat(percentages.getTotal(), is(6));
    assertThat(percentages.percentage("added"), is(new BigDecimal("50.00")));
    assertThat(percentages.percentage("already-present"), is(new BigDecimal("16.66")));
    assertThat(percentages.percentage("annotated"), is(new BigDecimal("33.33")));
  }

}