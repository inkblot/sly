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

import com.jnape.palatable.lambda.adt.Try;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.lambda.adt.Try.failure;
import static com.jnape.palatable.lambda.adt.Try.success;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.movealong.sly.hamcrest.DescriptionOf.descriptionOf;
import static org.movealong.sly.matchers.lambda.TryMatcher.*;

class TryMatcherTest {
    @Test
    void successfulTries() {
        Try<String> subject = success("test");

        assertTrue(successfulTry().matches(subject));
        assertTrue(successfulTryOf("test").matches(subject));
        assertTrue(successfulTryThat(greaterThan("junit")).matches(subject));
        assertFalse(failedTry().matches(subject));
    }

    @Test
    void failedTries() {
        RuntimeException exception = new RuntimeException("nope");
        Try<String>      subject   = failure(exception);

        assertTrue(failedTry().matches(subject));
        assertTrue(failedTryOf(exception).matches(subject));
        assertTrue(failedTryThat(sameInstance(exception)).matches(subject));
        assertFalse(successfulTry().matches(subject));
    }

    @Test
    void successDescription() {
        Matcher<String> dependentMatcher = greaterThan("junit");
        assertThat(descriptionOf(successfulTryThat(dependentMatcher)),
                   allOf(containsString("success"),
                         containsString(descriptionOf(dependentMatcher))));
    }

    @Test
    void failureDescription() {
        Matcher<Throwable> dependentMatcher = sameInstance(new RuntimeException("nope"));
        assertThat(descriptionOf(failedTryThat(dependentMatcher)),
                   allOf(containsString("failure"),
                         containsString(descriptionOf(dependentMatcher))));
    }
}