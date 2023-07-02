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
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.core.StringContains.containsStringIgnoringCase;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.movealong.sly.hamcrest.DescribeMismatch.describeMismatch;
import static org.movealong.sly.hamcrest.DescriptionOf.descriptionOf;
import static org.movealong.sly.matchers.lambda.JustMatcher.isJust;
import static org.movealong.sly.matchers.lambda.JustMatcher.isJustThat;

class JustMatcherTest {

    @Test
    void describesWell() {
        Matcher<Integer>     dependentMatcher = greaterThan(5);
        JustMatcher<Integer> sut              = isJustThat(dependentMatcher);
        assertThat(descriptionOf(sut),
                   allOf(containsStringIgnoringCase("just"),
                         containsString(descriptionOf(dependentMatcher))));
    }

    @Test
    void whenAbsent() {
        JustMatcher<?> sut = isJust();
        assertFalse(sut.matches(nothing()));
        assertThat(describeMismatch(sut, nothing()),
                   containsStringIgnoringCase("was nothing"));
    }

    @Test
    void presentMatchingDependent() {
        Matcher<Integer>     dependentMatcher = greaterThan(5);
        JustMatcher<Integer> sut              = isJustThat(dependentMatcher);
        assertTrue(sut.matches(just(6)));
    }

    @Test
    void presentMismatchingDependent() {
        int                  carrier          = 4;
        Maybe<Integer>       subject          = just(carrier);
        Matcher<Integer>     dependentMatcher = greaterThan(5);
        JustMatcher<Integer> sut              = isJustThat(dependentMatcher);

        assertFalse(sut.matches(subject));
        assertThat(describeMismatch(sut, subject),
                   allOf(containsStringIgnoringCase("just"),
                         containsString(describeMismatch(dependentMatcher, carrier))));
    }
}