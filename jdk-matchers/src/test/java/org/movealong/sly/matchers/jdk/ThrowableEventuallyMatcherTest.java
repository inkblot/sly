/*
 * Copyright (c) 2025 Nate Riffe
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
package org.movealong.sly.matchers.jdk;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.movealong.sly.hamcrest.DescribeMismatch.describeMismatch;
import static org.movealong.sly.hamcrest.DescriptionOf.descriptionOf;
import static org.movealong.sly.matchers.jdk.ThrowableEventuallyMatcher.hasEventualCauseThat;
import static org.movealong.sly.matchers.jdk.ThrowableMessageMatcher.hasMessageOf;

class ThrowableEventuallyMatcherTest {

    @Test
    void matchesDirectly() {
        Matcher<Throwable> matcher = hasEventualCauseThat(hasMessageOf("direct match"));
        RuntimeException   subject = new RuntimeException("direct match");
        assertTrue(matcher.matches(subject));
    }

    @Test
    void matchesInCausalChain() {
        Matcher<Throwable> matcher   = hasEventualCauseThat(hasMessageOf("nested match"));
        RuntimeException   innermost = new RuntimeException("nested match");
        RuntimeException   middle    = new RuntimeException("middle", innermost);
        RuntimeException   outer     = new RuntimeException("outer", middle);
        assertTrue(matcher.matches(outer));
    }

    @Test
    void doesNotMatchWhenNoMatchInChain() {
        Matcher<Throwable> matcher   = hasEventualCauseThat(hasMessageOf("not found"));
        RuntimeException   innermost = new RuntimeException("innermost");
        RuntimeException   middle    = new RuntimeException("middle", innermost);
        RuntimeException   outer     = new RuntimeException("outer", middle);
        assertFalse(matcher.matches(outer));
    }

    @Test
    void describesWell() {
        assertThat(descriptionOf(hasEventualCauseThat(hasMessageOf("expected message"))),
                   equalTo("exception with a causal chain containing exception with message \"expected message\""));
    }

    @Test
    void describesMismatchWell() {
        Matcher<Throwable> matcher   = hasEventualCauseThat(hasMessageOf("not found"));
        RuntimeException   innermost = new RuntimeException("innermost");
        RuntimeException   middle    = new RuntimeException("middle", innermost);
        RuntimeException   outer     = new RuntimeException("outer", middle);
        assertFalse(matcher.matches(outer));
        assertThat(describeMismatch(matcher, outer),
                   equalTo("no throwable in causal chain matched the delegate matcher"));
    }

    @Test
    void handlesNullThrowable() {
        Matcher<Throwable> matcher = hasEventualCauseThat(hasMessageOf("any"));
        assertFalse(matcher.matches(null));
        assertThat(describeMismatch(matcher, null),
                   equalTo("was null"));
    }

    @Test
    void handlesDeepCausalChain() {
        Matcher<Throwable> matcher = hasEventualCauseThat(hasMessageOf("deep match"));

        // Create a deep chain of exceptions
        RuntimeException deepest = new RuntimeException("deep match");
        RuntimeException level4  = new RuntimeException("level4", deepest);
        RuntimeException level3  = new RuntimeException("level3", level4);
        RuntimeException level2  = new RuntimeException("level2", level3);
        RuntimeException level1  = new RuntimeException("level1", level2);

        assertTrue(matcher.matches(level1));
    }
}
