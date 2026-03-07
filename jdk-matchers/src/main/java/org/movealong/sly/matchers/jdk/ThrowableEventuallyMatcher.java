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

import lombok.RequiredArgsConstructor;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import static lombok.AccessLevel.PRIVATE;

/**
 * Match a {@link Throwable} by recursively checking its causal chain.
 * The matcher will return true if any throwable in the causal chain matches the delegate matcher.
 */
@RequiredArgsConstructor(access = PRIVATE)
public class ThrowableEventuallyMatcher extends TypeSafeDiagnosingMatcher<Throwable> {

    private final Matcher<? super Throwable> delegate;

    @Override
    public void describeTo(Description description) {
        description.appendText("exception with a causal chain containing ")
                   .appendDescriptionOf(delegate);
    }

    @Override
    protected boolean matchesSafely(Throwable item, Description mismatchDescription) {
        if (item == null) {
            mismatchDescription.appendText("was null");
            return false;
        }

        Throwable current = item;
        while (current != null) {
            if (delegate.matches(current)) {
                return true;
            }

            // Move to the next cause
            current = current.getCause();
            if (current == item) {
                // Handle circular references
                break;
            }
        }

        // If we get here, no throwable in the chain matched
        mismatchDescription.appendText("no throwable in causal chain matched the delegate matcher");

        return false;
    }

    /**
     * Creates a matcher that matches when any throwable in the causal chain of the examined {@link Throwable}
     * satisfies the specified matcher.
     *
     * @param matcher the matcher to apply to each throwable in the causal chain of the examined {@link Throwable}
     * @return a matcher that matches when any throwable in the causal chain satisfies the specified matcher
     */
    public static Matcher<Throwable> hasEventualCauseThat(Matcher<? super Throwable> matcher) {
        return new ThrowableEventuallyMatcher(matcher);
    }
}
