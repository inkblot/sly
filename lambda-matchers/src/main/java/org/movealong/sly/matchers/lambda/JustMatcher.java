/*
 * Copyright (c) 2023 Nate Riffe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.movealong.sly.matchers.lambda;

import com.jnape.palatable.lambda.adt.Maybe;
import lombok.AllArgsConstructor;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import static lombok.AccessLevel.PRIVATE;
import static org.hamcrest.core.IsAnything.anything;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Is the {@link Maybe} present, and does its carrier satisfy a nested matcher?
 *
 * @param <A> the carrier type
 */
@AllArgsConstructor(access = PRIVATE)
public class JustMatcher<A> extends TypeSafeDiagnosingMatcher<Maybe<A>> {

    private final Matcher<? super A> dependentMatcher;

    @Override
    protected boolean matchesSafely(Maybe<A> item, Description mismatch) {
        A value = item.orElse(null);
        if (value == null) {
            mismatch.appendText("was nothing");
            return false;
        } else if (!dependentMatcher.matches(value)) {
            dependentMatcher.describeMismatch(value, mismatch.appendText("was Just "));
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Just <")
                   .appendDescriptionOf(dependentMatcher)
                   .appendText(">");
    }

    /**
     * Creates a matcher that matches an instance of {@link Maybe} when the
     * carrier is present (i.e. "just"), and satisfies the nested matcher.
     *
     * <pre>
     * assertThat(just("foo"), isJustThat(startsWith("f"))); // passes
     * assertThat(just("bar"), isJustThat(startsWith("f"))); // fails
     * assertThat(nothing(), isJustThat(startsWith("f")));   // fails
     * </pre>
     *
     * @param dependentMatcher the nested matcher
     * @param <A>              the carrier type
     * @return A matcher of {@link Maybe}
     */
    public static <A> JustMatcher<A> isJustThat(Matcher<? super A> dependentMatcher) {
        return new JustMatcher<>(dependentMatcher);
    }

    /**
     * Creates a matcher that matches an instance of {@link Maybe} when the
     * carrier is present (i.e. "just"), and is equal to the given instance
     * of the carrier type according to {@link Object#equals} method of the
     * examined carrier value.
     *
     * <pre>
     * assertThat(just("foo"), isJustOf("foo")); // passes
     * assertThat(just("bar"), isJustOf("foo")); // fails
     * assertThat(nothing(), isJustOf("foo"));   // fails
     * </pre>
     *
     * @param a   the matching carrier value
     * @param <A> the carrier type
     * @return A matcher of {@link Maybe}
     */
    public static <A> JustMatcher<A> isJustOf(A a) {
        return isJustThat(equalTo(a));
    }

    /**
     * Creates a matcher that matches an instance of {@link} when the carrier
     * is present (i.e. "just), with no other opinion about the carrier.
     *
     * <pre>
     * assertThat(just("foo"), isJust()); // passes
     * assertThat(just(242), isJust());   // passes
     * assertThat(nothing(), isJust());   // fails
     * </pre>
     *
     * @return A matcher of {@link Maybe}
     */
    public static JustMatcher<?> isJust() {
        return isJustThat(anything());
    }
}
