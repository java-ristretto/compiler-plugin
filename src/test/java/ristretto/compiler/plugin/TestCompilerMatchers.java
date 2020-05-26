package ristretto.compiler.plugin;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

final class TestCompilerMatchers {

    private TestCompilerMatchers() {
    }

    static Matcher<TestCompiler.Result> hasOutput(String... value) {
        return new OutputMatcher(value);
    }

    private static class OutputMatcher extends TypeSafeMatcher<TestCompiler.Result> {

        private final String expectedOutput;

        OutputMatcher(String... expectedOutput) {
            this.expectedOutput = String.join(System.lineSeparator(), expectedOutput);
        }

        @Override
        public void describeTo(Description description) {
            description
                .appendText("the compiler's output to contain")
                .appendText(System.lineSeparator())
                .appendText(expectedOutput);
        }

        @Override
        protected void describeMismatchSafely(TestCompiler.Result result, Description mismatchDescription) {
            mismatchDescription
                .appendText("the compiler's output was actually")
                .appendText(System.lineSeparator())
                .appendText(result.additionalOutput);
        }

        @Override
        protected boolean matchesSafely(TestCompiler.Result result) {
            return result.additionalOutput.contains(expectedOutput);
        }
    }
}
